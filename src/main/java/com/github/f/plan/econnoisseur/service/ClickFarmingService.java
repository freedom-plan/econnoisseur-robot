package com.github.f.plan.econnoisseur.service;

import com.github.f.plan.econnoisseur.dto.MiningInfo;
import com.github.f.plan.econnoisseur.dto.PreTradeInfo;
import com.github.f.plan.econnoisseur.exchanges.coinex.service.CoinexApi;
import com.github.f.plan.econnoisseur.exchanges.common.dto.*;
import com.github.f.plan.econnoisseur.exchanges.common.iface.IApi;
import com.github.f.plan.econnoisseur.exchanges.common.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 *
 * ClickFarmingService
 *
 * @author Kevin Huang
 * @since version
 * 2018年06月27日 11:20:00
 */
@Service
public class ClickFarmingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClickFarmingService.class);
    @Autowired
    @Qualifier("coinexApi")
    private IApi api;
    @Autowired(required = false)
    private DingTalkService dingTalkService;
    @Autowired
    private ExecutorService taskExecutor;

    private static final CurrencyPair CURRENT_CURRENCY_PAIR = CurrencyPair.LFT_BCH;

    // 平台币
    private static final Currency PLATFORM_CURRENCY = Currency.CET;
    // 开启挖矿
    private static final Boolean MINING = Boolean.FALSE;

    private static final BigDecimal MIN_PLATFORM_CURRENCY_AMOUNT = new BigDecimal("200");
    // 交易币
    private static final BigDecimal MAX_HOLD_AMOUNT = new BigDecimal("6000");
    private static final BigDecimal MIN_AMOUNT = new BigDecimal("50");

    private static final BigDecimal SAFE_WIDE = new BigDecimal("0.00000002");

    // 暂停指数
    private static BigDecimal LAST_TRADE_PRICE = null;
    private static MiningInfo MINING_INFO = null;

    @Scheduled(fixedDelayString="6000")
    public void run() throws InterruptedException, ExecutionException {
        LOGGER.info("开始执行");
        if (MINING) {
            if (Calendar.getInstance().get(Calendar.MINUTE) <= 35) {
                LOGGER.info("未到挖矿时间点\n\n");
                return;
            }

            if (null != MINING_INFO && MINING_INFO.isOverrun(MIN_AMOUNT)) {
                LOGGER.info("当前小时可挖矿：{}，已挖矿：{}, 可交易数量：{}", MINING_INFO.getDifficulty(), MINING_INFO.getPrediction(), MINING_INFO.getAmount());
                LOGGER.info("挖矿达到最大值\n\n");
                return;
            }
        }

        String priorityId = null;
        String secondId = null;
        OrderStatus priorityStatus = null;
        OrderStatus secondStatus = null;

        PreTradeInfo tradeInfo = this.getPreTradeInfo(this.api, CURRENT_CURRENCY_PAIR);
        BigDecimal price = tradeInfo.getPrice();

        if (tradeInfo.isSuspend()) {
            return;
        }

        if (null != price) {
            BigDecimal amount = tradeInfo.getAmount();

            OrderOperation priority = tradeInfo.getPriority();
            LOGGER.info("挂{}单执行价: {}, 执行数量: {}", priority, price, amount);
            Future<Order> priorityFuture = taskExecutor.submit(() -> api.limit(CURRENT_CURRENCY_PAIR, priority, price, amount));
            OrderOperation second = tradeInfo.getSecond();
            LOGGER.info("挂{}单执行价: {}, 执行数量: {}", second, price, amount);
            Future<Order> secondFuture = taskExecutor.submit(() -> api.limit(CURRENT_CURRENCY_PAIR, second, price, amount));
            Order priorityOrder = priorityFuture.get();
            Order secondOrder = secondFuture.get();

            if (check(priorityOrder)) {
                priorityId = priorityOrder.getId();
                priorityStatus = priorityOrder.getStatus();
            }
            if (check(secondOrder)) {
                secondId = secondOrder.getId();
                secondStatus = secondOrder.getStatus();
            }

            if (null == priorityId && null != secondId) {
                LOGGER.info("{}单请求失败!", priority);
                Order cancel = api.cancelOrder(CURRENT_CURRENCY_PAIR, secondId);
                LOGGER.info("取消{}单 : {}\n\n", second, cancel.getCode());
                return;
            }
            if (null != priorityId && null == secondId) {
                LOGGER.info("{}单请求失败!", second);
                Order cancel = api.cancelOrder(CURRENT_CURRENCY_PAIR, priorityId);
                LOGGER.info("取消{}单 : {}\n\n", priority, cancel.getCode());
                return;
            }
            if (null == priorityId && null == secondId) {
                LOGGER.info("下单失败\n\n");
                return;
            }

            if (api instanceof CoinexApi) {
                LOGGER.info("默认下单完成\n\n");
                return;
            }

            int count = 0;
            do {
                if (OrderStatus.FILLED != priorityStatus) {
                    priorityStatus = this.getOrderStatus(CURRENT_CURRENCY_PAIR, priority, priorityId);
                }
                if (OrderStatus.FILLED != secondStatus) {
                    secondStatus = this.getOrderStatus(CURRENT_CURRENCY_PAIR, second, secondId);
                }

                if (OrderStatus.FILLED == priorityStatus && OrderStatus.FILLED == secondStatus) {
                    LOGGER.info("{}单完成，{}单完成", priority, second);
                    break;
                } else if (OrderStatus.FILLED == priorityStatus) {
                    Thread.sleep(1000);
                    Order cancel = api.cancelOrder(CURRENT_CURRENCY_PAIR, secondId);
                    LOGGER.info("{}单完成，取消{}单: {}", priority, second, cancel.getCode());
                    break;
                } else if (OrderStatus.FILLED == secondStatus) {
                    Thread.sleep(1000);
                    Order cancel = api.cancelOrder(CURRENT_CURRENCY_PAIR, priorityId);
                    LOGGER.info("{}单完成，取消{}单: {}", second, priority, cancel.getCode());
                    break;
                }
                count++;
            } while (count < 2);

            if (OrderStatus.FILLED != priorityStatus || OrderStatus.FILLED != secondStatus) {
                if (null != dingTalkService) {
                    OrderOperation operation = OrderStatus.FILLED == priorityStatus ? priority : second;
                    waitOrExitAndNotify(1000D, false, operation + "订单被吞", "#### " + operation + "订单被吞\n\n"
                            + " * " + priority + "单状态: **" + priorityStatus + "**\n"
                            + " * " + second + "单状态: **" + secondStatus + "**\n"
                            + " * 委托价格: **" + price + "**\n"
                            + " * 委托数量: **" + amount + "**");
                }
            }
            LOGGER.info("本次结束\n\n");
        }
    }

    /**
     * 获取交易价格 和 交易数量
     * @param api
     * @param pair
     * @return
     */
    private PreTradeInfo getPreTradeInfo(IApi api, CurrencyPair pair) throws InterruptedException {
        PreTradeInfo preTradeInfo = new PreTradeInfo()
                .setPriority(OrderOperation.SELL)
                .setSecond(OrderOperation.BUY);

        // 使用参考价来评 可买入的币的数量
        if (null == LAST_TRADE_PRICE) {
            Ticker ticker = api.ticker(pair);
            LAST_TRADE_PRICE = this.getPrice(ticker, preTradeInfo.getPriority());
        }

        if (null != LAST_TRADE_PRICE) {
            Balances balances = api.balances();
            if (check(balances)) {
                boolean flag = true;
                if (!MINING) {
                    Balance platform = balances.getBalance(PLATFORM_CURRENCY);
                    if (null == platform || platform.getAvailable().compareTo(MIN_PLATFORM_CURRENCY_AMOUNT) < 0) {
                        flag = false;
                        waitOrExitAndNotify(60000D , false, "没有足够的平台币", "#### 没有足够的平台币\n\n"
                                + " * " + PLATFORM_CURRENCY + " : ** " + (null != platform ? platform.getAvailable() : 0) + " **");
                    }
                }

                 if (flag) {
                    Balance base = balances.getBalance(pair.getBase());
                    Balance counter = balances.getBalance(pair.getCounter());
                    BigDecimal baseAmount = base.getAvailable();
                    BigDecimal counterAmount = counter.getAvailable();
                    LOGGER.info("账号 {} 状态, available: {}, frozen: {}", pair.getBase(), base.getAvailable(), base.getFrozen().doubleValue());
                    LOGGER.info("账号 {} 状态, available: {}, frozen: {}", pair.getCounter(), counter.getAvailable(), counter.getFrozen().doubleValue());
                    preTradeInfo.setBaseAmount(baseAmount).setCounterAmount(counterAmount);

                    BigDecimal buyVol = counterAmount.divide(LAST_TRADE_PRICE, RoundingMode.HALF_UP);
                    // 计算交易量
                    BigDecimal amount = baseAmount.min(buyVol);
                    LOGGER.info("本次能执行最大刷单数量: {}", amount);

                    if (amount.compareTo(MIN_AMOUNT) > 0) {
                        amount = amount.multiply(new BigDecimal(
                                    MINING ? new Random().nextDouble() : (0.8 - new Random().nextDouble() * 0.15)
                                )).setScale(0, BigDecimal.ROUND_DOWN)
                                .max(MIN_AMOUNT);
                        LOGGER.info("本次执行的刷单数量: {}", amount);
                        preTradeInfo.setAmount(amount);

                        // 评估操作顺序
                        BigDecimal doubleBase = baseAmount.multiply(new BigDecimal(2));
                        if (doubleBase.compareTo(MAX_HOLD_AMOUNT) < 0 && doubleBase.compareTo(buyVol) < 0) {
                            LOGGER.info("交换交易顺序，{} <----> {}", OrderOperation.BUY, OrderOperation.SELL);
                            preTradeInfo.setPriority(OrderOperation.BUY)
                                    .setSecond(OrderOperation.SELL);
                        }
                    } else {
                        waitOrExitAndNotify(60000D , false, "没有足够的币种交易", "#### 没有足够的币种交易\n\n"
                                + " * " + pair.getBase() + " : ** " + preTradeInfo.getBaseAmount() + " **\n"
                                + " * " + pair.getCounter() + " : ** " + preTradeInfo.getCounterAmount() + " **");
                    }

                }

            } else {
                LOGGER.info("超API的频率休息60s\n\n");
                Thread.sleep(60000);
            }
        }

        // 评估价格
        if (null != preTradeInfo.getAmount()) {
            Ticker ticker = api.ticker(pair);
            BigDecimal price = this.getPrice(ticker, preTradeInfo.getPriority());
            if (null != price) {
                LAST_TRADE_PRICE = price;
                if (!this.checkMining(preTradeInfo.getAmount(), MIN_AMOUNT)) {
                    preTradeInfo.setSuspend(true);
                    LOGGER.info("——————————> 休息60s\n\n");
                    Thread.sleep(60000);
                }

                preTradeInfo.setPrice(price);
            }
        }
        return preTradeInfo;
    }

    // 最大挖矿折合交易币种数量
    private boolean checkMining(BigDecimal preAmount, BigDecimal minAmount) {
        boolean result = true;
        if (MINING) {
            if (null == MINING_INFO) {
                updateMining();
            }

            LOGGER.info("当前小时可挖矿：{}，已挖矿：{}, 可交易数量：{}", MINING_INFO.getDifficulty(), MINING_INFO.getPrediction(), MINING_INFO.getAmount());

            if (MINING_INFO.isOverrun(preAmount)) {
                result = false;
            } else {
                if (preAmount.compareTo(minAmount) >= 0) {
                    MINING_INFO.subtract(preAmount);
                }
            }
        }
        return result;
    }

    @Scheduled(cron = "09/20 * * * * ?")
    public synchronized void updateMining() {
        if (MINING) {
            MiningDifficulty miningDifficulty = ((CoinexApi) api).miningDifficulty();
            if (check(miningDifficulty)) {
                // 除2是有买卖两次交易
                BigDecimal rate = null;
                Ticker ticker = api.ticker(CurrencyPair.get(PLATFORM_CURRENCY, CURRENT_CURRENCY_PAIR.getCounter()));
                if (check(ticker)) {
                    BigDecimal platform = ticker.getLast();

                    if (null == LAST_TRADE_PRICE) {
                        Ticker price = api.ticker(CURRENT_CURRENCY_PAIR);
                        LAST_TRADE_PRICE = price.getLast();
                    }

                    if (null != LAST_TRADE_PRICE) {
                        rate = platform.multiply(new BigDecimal(1000 / 2 * 0.95)).divide(LAST_TRADE_PRICE, RoundingMode.HALF_DOWN);
                        if (null == MINING_INFO) {
                            MINING_INFO = new MiningInfo(miningDifficulty, rate);
                        } else {
                            MINING_INFO.reset(miningDifficulty, rate);
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取订单状态
     * @param pair
     * @param orderOperation
     * @param orderId
     * @return
     */
    private OrderStatus getOrderStatus(CurrencyPair pair, OrderOperation orderOperation, String orderId) {
        OrderStatus status = null;
        Order order = api.getOrder(pair, orderId);
        if (check(order)) {
            status = order.getStatus();
        }
        LOGGER.info("获取{}单状态, Status: {}", orderOperation, status);
        return status;
    }

    private BigDecimal getPrice(Ticker ticker, OrderOperation operation) {
        BigDecimal price = null;
        boolean result = false;

        if (check(ticker)) {
            BigDecimal sell = ticker.getAsk();
            BigDecimal buy = ticker.getBid();

            if (sell.compareTo(buy.add(SAFE_WIDE)) >= 0) {
//              BigDecimal step = sell.subtract(buy).divide(new BigDecimal(3), RoundingMode.HALF_UP);
//              BigDecimal amount = OrderOperation.SELL == operation ? sell.subtract(step) : buy.add(step);
//              price = amount.setScale(8, BigDecimal.ROUND_HALF_UP);

                result = true;
                price = sell.add(buy).divide(new BigDecimal(2), RoundingMode.HALF_UP).setScale(8, BigDecimal.ROUND_HALF_UP);
            }
        }
        if (result) {
            LOGGER.info("检查盘口宽度是否合适做单：{}", result);
        } else {
            LOGGER.info("检查盘口宽度是否合适做单：{}\n\n", result);
        }
        return price;
    }

    private static boolean check(BaseResp resp) {
        boolean status = false;
        if (null != resp && Code.OK == resp.getCode()) {
            status = true;
        }
        return status;
    }

    private void waitOrExitAndNotify(Double num, boolean exit, String title, String msg) {
        long millis = num.longValue();
        try {
            LOGGER.info("{}, 暂停{}秒, 系统是否退出{}", title, millis / 1000, exit);
            if (null != dingTalkService) {
                dingTalkService.send(new DingTalkService.DingTalkMsg(title + ", 暂停 " + millis / 1000 + " 秒", msg));
            }
            Thread.sleep(millis);
            if (exit) {
                System.exit(0);
            }
            if (null != dingTalkService) {
                dingTalkService.send(new DingTalkService.DingTalkMsg("报警解除", title + "，报警解除"));
            }
        } catch (InterruptedException e) {
            LOGGER.error("暂停或者退出异常", e);
        }
    }

    @Deprecated
    public void status() {
        BigDecimal bigDecimal = new BigDecimal(0);
        Orders orders = api.finishedOrders(CURRENT_CURRENCY_PAIR);
        if (check(orders)) {
            for (Order order : orders.getOrders()) {
                bigDecimal = bigDecimal.add(order.getAmount());
            }
        }
        LOGGER.info("$$$$$ 获取交易总量状态, sum: {}\n\n", bigDecimal);
    }

    @Deprecated
    public void op() throws InterruptedException {
        BigDecimal amount = new BigDecimal(1500);
        OrderOperation operation = OrderOperation.SELL;
        Ticker ticker = api.ticker(CURRENT_CURRENCY_PAIR);

        BigDecimal price = this.getPrice(ticker, operation);
        if (null == price) {
            LOGGER.info("\n\n");
            return;
        }

        Order order = api.limit(CURRENT_CURRENCY_PAIR, operation, price, amount);
        if (check(order)) {
            String id = order.getId();
            OrderStatus status = order.getStatus();

            if (null == status) {
                Thread.sleep(1000);
                status = this.getOrderStatus(CURRENT_CURRENCY_PAIR, operation, id);
            }

            if (OrderStatus.FILLED != status) {
                Order cancel = api.cancelOrder(CURRENT_CURRENCY_PAIR, id);
                LOGGER.info("取消{}单: {}\n\n", operation, cancel.getCode());
            }

            if (OrderStatus.FILLED == status || OrderStatus.PARTIALLY_FILLED == status) {
                LOGGER.info("本次{}结束\n\n", operation);
                System.exit(0);
            }
        }
    }

}