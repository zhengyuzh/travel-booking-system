package com.zjy.pojo;

import lombok.Data;

/**
 * @BelongsProject: system-scenic
 * @BelongsPackage: com.zjy.pojo
 * @Description: TODO 支付订单相关信息
 * @Version: 1.0
 */
@Data
public class AlipayBean {
    /**
     * 商户订单号
     */
    private String out_trade_no;

    /**
     * 订单名称
     */
    private String subject;

    /**
     * 付款金额
     */
    private String total_amount;

    /**
     * 商品描述
     */
    private String body;

    /**
     * 产品编号，PC网页支付，这个是必传参数
     */
    private String product_code = "FAST_INSTANT_TRADE_PAY";
}
