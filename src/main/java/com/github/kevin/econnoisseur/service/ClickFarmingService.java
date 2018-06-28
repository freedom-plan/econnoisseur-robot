package com.github.kevin.econnoisseur.service;

import com.github.kevin.econnoisseur.exchanges.coinex.dto.*;
import com.github.kevin.econnoisseur.exchanges.coinex.model.MARKET;
import com.github.kevin.econnoisseur.exchanges.coinex.service.StockApi;
import com.github.kevin.econnoisseur.model.OrderType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
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
    private StockApi stockApi;
    @Autowired(required = false)
    private DingTalkService dingTalkService;
    private static final MARKET CURRENT_MARKET = MARKET.MARKET_NANOBTC;
    private static final BigDecimal SAFE_WIDE = new BigDecimal("0.00000002");
    private static final BigDecimal AMOUNT = new BigDecimal("2");

    private static final String STATUS_DONE = "done";

//    @Scheduled(cron = "0/5 * * * * ?")
    @Scheduled(fixedDelayString="1000")
    public void run() {
        ResponseDto<TickerDto> ticker = stockApi.ticker(CURRENT_MARKET);

        BigDecimal price = isSafeWide(ticker);
        if (null != price) {
//            BigDecimal AMOUNT = new BigDecimal(new Random().nextDouble() * 5 + 5)
//                    .setScale(8, BigDecimal.ROUND_HALF_UP);
            int count = 0;
            LOGGER.info("挂卖单执行价: {}, 执行数量: {}", price, AMOUNT);
            ResponseDto<OrderDto> sell = stockApi.putLimitOrder(CURRENT_MARKET, OrderType.ORDER_TYPE_SELL, AMOUNT, price);
            Long sellId = null;
            Long buyId = null;
            String sellStatus = null;
            String buyStatus = null;
            if (checkResponse(sell)) {
                sellId = sell.getData().getId();
                sellStatus = sell.getData().getStatus();
            }
            if (null == sellId) {
                LOGGER.info("卖单请求失败!\n\n");
                return;
            }
            if (STATUS_DONE.equals(sellStatus)) {
                LOGGER.info("卖单被吞!\n\n");
                System.exit(0);
            }

            LOGGER.info("挂买单执行价: {}, 执行数量: {}", price, AMOUNT);
            ResponseDto<OrderDto> buy = stockApi.putLimitOrder(CURRENT_MARKET, OrderType.ORDER_TYPE_BUY, AMOUNT, price);
            if (checkResponse(buy)) {
                buyId = buy.getData().getId();
                buyStatus = buy.getData().getStatus();
            }
            if (null == buyId) {
                LOGGER.info(stockApi.cancelOrder(CURRENT_MARKET, sellId));
                LOGGER.info("取消卖单 {}\n\n", sellId);
                return;
            }

            do {
                if (StringUtils.isBlank(sellStatus) || !STATUS_DONE.equals(sellStatus)) {
                    sell = stockApi.getOrder(CURRENT_MARKET, sellId);
                    if (checkResponse(sell)) {
                        sellStatus = sell.getData().getStatus();
                    }
                    LOGGER.info("获取 sell 状态, sellStatus: {}", sellStatus);
                }
                if (StringUtils.isBlank(buyStatus) || !STATUS_DONE.equals(buyStatus)) {
                    buy = stockApi.getOrder(CURRENT_MARKET, buyId);
                    if (checkResponse(buy)) {
                        buyStatus = buy.getData().getStatus();
                    }
                    LOGGER.info("获取 buy 状态, buyStatus: {}", buyStatus);
                }

                if (STATUS_DONE.equals(buyStatus) && STATUS_DONE.equals(sellStatus)) {
                    break;
                }
                count++;
            } while (count < 10);

            if (!STATUS_DONE.equals(sellStatus) || !STATUS_DONE.equals(buyStatus)) {
                if (null != dingTalkService) {
                    dingTalkService.send(new DingTalkService.DingTalkMsg("高危订单出现，请关注", "#### Order Status\n\n"
                            + " * 卖单状态: **" + sellStatus + "**\n"
                            + " * 买单状态: **" + buyStatus + "**\n"
                            + " * 委托价格: **" + price + "**\n"
                            + " * 委托数量: **" + AMOUNT + "**"));
                }
                System.exit(0);
            }
            LOGGER.info("本次结束\n\n");
        }

    }

    @Scheduled(fixedDelayString="120000")
    public void check() {
        ResponseDto<Map<String, BalanceDto>> account = stockApi.account();
        if (checkResponse(account)) {
            BalanceDto BTC = account.getData().get("BTC");
            LOGGER.info("账号 BTC 状态, available: {}, frozen: {}", BTC.getAvailable(), BTC.getFrozen());

            BalanceDto NANO = account.getData().get("NANO");
            LOGGER.info("账号 NANO 状态, available: {}, frozen: {}", NANO.getAvailable(), NANO.getFrozen());
        }

        BigDecimal bigDecimal = new BigDecimal(0);

        int pageNum = 1;
        PageDto page = null;
        do {
            ResponseDto<PageDto> finished = stockApi.finishedOrder(CURRENT_MARKET, pageNum, 100);
            if (checkResponse(finished)) {
                page = finished.getData();
                if (null != page) {
                    List<OrderDto> orders = page.getData();
                    if (!CollectionUtils.isEmpty(orders)) {
                        for (OrderDto order : orders) {
                            bigDecimal = bigDecimal.add(order.getAmount());
                        }
                    }
                }
            }
            pageNum++;
        } while (null != page && page.getHasNext());
        LOGGER.info("$$$$$ 获取交易总量状态, sum: {}\n\n", bigDecimal);
    }

    private BigDecimal isSafeWide(ResponseDto<TickerDto> tickerResponse) {
        BigDecimal price = null;
        if (checkResponse(tickerResponse)) {
            TickerDto.Ticker ticker = tickerResponse.getData().getTicker();
//            LOGGER.info("检查盘口宽度是否合适做单：开始 {}", JacksonUtil.toJson(ticker));
            BigDecimal sell = ticker.getSell();
            BigDecimal buy = ticker.getBuy();
            if (sell.compareTo(buy.add(SAFE_WIDE)) >= 0) {
                price = getAverage(sell, buy);
            }
        }
        LOGGER.info("检查盘口宽度是否合适做单：结束 {}", price);
        return price;
    }

    private static BigDecimal getAverage(BigDecimal a, BigDecimal b) {
//        BigDecimal step = new BigDecimal(new Random().nextDouble() * b.subtract(a).doubleValue());
//       return a.add(step).setScale(8, BigDecimal.ROUND_HALF_UP);
        return a.add(b).divide(new BigDecimal(2)).setScale(8, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 检查返回值
     * @param responseDto 返回值
     * @return
     */
    private static boolean checkResponse(ResponseDto<?> responseDto) {
        boolean status = false;
        if (null != responseDto && 0 == responseDto.getCode() && null != responseDto.getData()) {
            status = true;
        }
        return status;
    }


    public static void main(String[] args) {
        System.out.println(new BigDecimal(new Random().nextDouble() * 2).setScale(8, BigDecimal.ROUND_HALF_UP));
    }
}
