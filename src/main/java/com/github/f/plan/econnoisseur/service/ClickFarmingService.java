package com.github.f.plan.econnoisseur.service;

import com.github.f.plan.econnoisseur.exchanges.common.dto.*;
import com.github.f.plan.econnoisseur.exchanges.common.iface.IApi;
import com.github.f.plan.econnoisseur.exchanges.common.model.Code;
import com.github.f.plan.econnoisseur.exchanges.common.model.CurrencyPair;
import com.github.f.plan.econnoisseur.exchanges.common.model.OrderOperation;
import com.github.f.plan.econnoisseur.exchanges.common.model.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

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
    @Qualifier("kkcoinApi")
    private IApi api;
    @Autowired(required = false)
    private DingTalkService dingTalkService;

    private static final CurrencyPair CURRENT_CURRENCY_PAIR = CurrencyPair.IOST_ETH;
    private static final BigDecimal SAFE_WIDE = new BigDecimal("0.00000002");
    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal("200");
    private static final BigDecimal MIN_AMOUNT = new BigDecimal("200");
    private static final BigDecimal MAX_HOLD_AMOUNT = new BigDecimal("2000");

    private static Boolean REVERSE_TRADE = false;

    //    @Scheduled(cron = "0/3 * * * * ?")
    @Scheduled(fixedDelayString="5000")
    public void run() {
        Balances balances = api.balances();
        BigDecimal baseAmount = null;
        BigDecimal counterAmount = null;
        if (check(balances)) {
            Balance base = balances.getBalance(CURRENT_CURRENCY_PAIR.getBase());
            baseAmount = base.getAvailable();
            LOGGER.info("账号 {} 状态, available: {}, frozen: {}", CURRENT_CURRENCY_PAIR.getBase(), base.getAvailable(), base.getFrozen());

            Balance counter = balances.getBalance(CURRENT_CURRENCY_PAIR.getCounter());
            counterAmount = counter.getAvailable();
            LOGGER.info("账号 {} 状态, available: {}, frozen: {}", CURRENT_CURRENCY_PAIR.getCounter(), counter.getAvailable(), counter.getFrozen());
        }

        Ticker ticker = api.ticker(CURRENT_CURRENCY_PAIR);
        BigDecimal price = isSafeWide(ticker);
        BigDecimal amount = (null != baseAmount && null != counterAmount && null != price) ? getAmount(baseAmount, counterAmount, price) : DEFAULT_AMOUNT;

        if (null != price) {
            int count = 0;
            LOGGER.info("挂卖单执行价: {}, 执行数量: {}", price, amount);
            Order sell = api.limit(CURRENT_CURRENCY_PAIR, OrderOperation.SELL, price, amount);
            String sellId = null;
            String buyId = null;
            OrderStatus sellStatus = null;
            OrderStatus buyStatus = null;
            if (check(sell)) {
                sellId = sell.getId();
                sellStatus = sell.getStatus();
            }
            if (null == sellId) {
                LOGGER.info("卖单请求失败!\n\n");
                return;
            }

            if (OrderStatus.FILLED == sellStatus) {
                LOGGER.info("卖单被吞!\n\n");
                return;
            }

            LOGGER.info("挂买单执行价: {}, 执行数量: {}", price, amount);
            Order buy = api.limit(CURRENT_CURRENCY_PAIR, OrderOperation.BUY, price, amount);
            if (check(buy)) {
                buyId = buy.getId();
                buyStatus = buy.getStatus();
            }
            if (null == buyId) {
                Order cancel = api.cancelOrder(CURRENT_CURRENCY_PAIR, sellId);
                LOGGER.info("取消卖单 {}\n\n", cancel.getCode());
                return;
            }

            do {
                if (OrderStatus.FILLED != sellStatus) {
                    sell = api.getOrder(CURRENT_CURRENCY_PAIR, sellId);
                    if (check(sell)) {
                        sellStatus = sell.getStatus();
                    }
                    LOGGER.info("获取 sell 状态, sellStatus: {}", sellStatus);
                }
                if (OrderStatus.FILLED != buyStatus) {
                    buy = api.getOrder(CURRENT_CURRENCY_PAIR, buyId);
                    if (check(buy)) {
                        buyStatus = buy.getStatus();
                    }
                    LOGGER.info("获取 buy 状态, buyStatus: {}", buyStatus);
                }

                if (OrderStatus.FILLED == sellStatus && OrderStatus.FILLED == buyStatus) {
                    LOGGER.info("卖单完成，买单完成");
                    break;
                } else if (OrderStatus.FILLED == sellStatus) {
                    Order cancel = api.cancelOrder(CURRENT_CURRENCY_PAIR, buyId);
                    LOGGER.info("卖单完成，取消买单 {}", cancel.getCode());
                    break;
                } else if (OrderStatus.FILLED == buyStatus) {
                    Order cancel = api.cancelOrder(CURRENT_CURRENCY_PAIR, sellId);
                    LOGGER.info("买单完成，取消卖单 {}", cancel.getCode());
                    break;
                }

                count++;
            } while (count < 10);

            if (OrderStatus.FILLED != sellStatus || OrderStatus.FILLED != buyStatus) {
                if (null != dingTalkService) {
                    dingTalkService.send(new DingTalkService.DingTalkMsg("高危订单出现，请关注", "#### Order Status\n\n"
                            + " * 卖单状态: **" + sellStatus + "**\n"
                            + " * 买单状态: **" + buyStatus + "**\n"
                            + " * 委托价格: **" + price + "**\n"
                            + " * 委托数量: **" + amount + "**"));
                }
            }
            LOGGER.info("本次结束\n\n");
        }

    }

//    @Scheduled(fixedDelayString="120000")
    public void check() {
        BigDecimal bigDecimal = new BigDecimal(0);
        Orders orders = api.finishedOrders(CURRENT_CURRENCY_PAIR);
        if (check(orders)) {
            for (Order order : orders.getOrders()) {
                bigDecimal = bigDecimal.add(order.getAmount());
            }
        }
        LOGGER.info("$$$$$ 获取交易总量状态, sum: {}\n\n", bigDecimal);
    }


    private BigDecimal getAmount(BigDecimal base, BigDecimal counter, BigDecimal price) {
        BigDecimal amount = base.min(counter.divide(price, RoundingMode.HALF_UP));
        LOGGER.info("本次能执行最大刷单数量: {}", amount);
        if (amount.compareTo(MIN_AMOUNT) <= 0) {
            LOGGER.info("没有足够的币种交易");
            if (null != dingTalkService) {
                dingTalkService.send(new DingTalkService.DingTalkMsg("没有足够的币种交易，请关注", "#### Balance Status\n\n"
                        + " * " + CURRENT_CURRENCY_PAIR.getBase() + ": **" + base + "**\n"
                        + " * " + CURRENT_CURRENCY_PAIR.getCounter() + ": **" + counter + "**")
                );
            }
            System.exit(0);
        }

        amount = amount.multiply(new BigDecimal(0.8 - new Random().nextDouble() * 0.15))
                .setScale(0, BigDecimal.ROUND_DOWN)
                .max(DEFAULT_AMOUNT);
        LOGGER.info("本次执行的刷单数量: {}", amount);
        return amount;
    }

    private BigDecimal isSafeWide(Ticker ticker) {
        BigDecimal price = null;
        if (check(ticker)) {
            BigDecimal sell = ticker.getAsk();
            BigDecimal buy = ticker.getBid();
            if (sell.compareTo(buy.add(SAFE_WIDE)) >= 0) {
                price = getAverage(sell, buy);
            }
        }

        LOGGER.info("检查盘口宽度是否合适做单：{}, 单价: {}", null != price, price);
        return price;
    }

    private static BigDecimal getAverage(BigDecimal a, BigDecimal b) {
//        BigDecimal step = new BigDecimal(new Random().nextDouble() * b.subtract(a).doubleValue());
//       return a.add(step).setScale(8, BigDecimal.ROUND_HALF_UP);
        return a.add(b).divide(new BigDecimal(2)).setScale(8, BigDecimal.ROUND_HALF_UP);
    }

    private static boolean check(BaseResp resp) {
        boolean status = false;
        if (null != resp && Code.OK == resp.getCode()) {
            status = true;
        }
        return status;
    }



}
