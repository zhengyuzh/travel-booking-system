package com.zjy.controller;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.zjy.mapper.HorderMapper;
import com.zjy.mapper.HotelMapper;
import com.zjy.mapper.ScenicMapper;
import com.zjy.mapper.SorderMapper;
import com.zjy.pojo.*;
import com.zjy.service.HorderService;
import com.zjy.service.HotelService;
import com.zjy.service.RoomService;
import com.zjy.service.SorderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.Random;

/**
 * @BelongsProject: system-scenic
 * @BelongsPackage: com.zjy.controller
 * @Description: TODO 支付宝沙箱支付
 * @Version: 1.0
 */
@Controller
public class AlipayController {
    //获取配置文件中的配置信息
    /**
     * 应用ID
     **/
    @Value("${appId}")
    private String appId;
    /**
     * 商户私钥
     **/
    @Value("${privateKey}")
    private String privateKey;
    /**
     * 支付宝公钥
     **/
    @Value("${publicKey}")
    private String publicKey;
    /**
     * 服务器异步通知页面路径
     **/
    @Value("${notifyUrl}")
    private String notifyUrl;
    /**
     * 页面跳转同步通知页面路径
     **/
    @Value("${returnUrl}")
    private String returnUrl;
    /**
     * 签名方式
     **/
    @Value("${signType}")
    private String signType;
    /**
     * 字符编码格式
     **/
    @Value("${charset}")
    private String charset;
    /**
     * 支付宝网关
     **/
    @Value("${gatewayUrl}")
    private String gatewayUrl;

    private final String format = "json";

    @Autowired
    private SorderService sorderService;

    @Autowired
    SorderMapper sorderMapper;

    @Autowired
    ScenicMapper scenicMapper;

    @Autowired
    private HorderService horderservice;

    @Autowired
    HorderMapper horderMapper;

    @Autowired
    HotelMapper hotelMapper;

    @Autowired
    HotelService hotelService;

    @Autowired
    RoomService roomService;

    /**
     * @description: 支付宝支付  /alipay/payMoney
     * @param: map
     * @param: model
     * @return: java.lang.String
     **/
    @RequestMapping(value = "aliPayMoney/{id}", method = RequestMethod.GET)
    public String onAlipaymoney(@PathVariable Integer id, Model model)  {
        LambdaQueryWrapper<SOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SOrder::getId,id);
        SOrder sOrder = sorderMapper.selectOne(lambdaQueryWrapper);

        LambdaQueryWrapper<Scenic> scenicLambdaQueryWrapper = new LambdaQueryWrapper<>();
        scenicLambdaQueryWrapper.eq(Scenic::getId,sOrder.getSid());
        Scenic scenic = scenicMapper.selectOne(scenicLambdaQueryWrapper);

        //数据组合
        AlipayBean alipayBean = new AlipayBean();
        alipayBean.setOut_trade_no(sOrder.getId().toString());
        alipayBean.setSubject(scenic.getSname());
        alipayBean.setTotal_amount(sOrder.getTotal().toString());
        alipayBean.setBody(scenic.getDescr());
        alipayBean.setProduct_code("FAST_INSTANT_TRADE_PAY");


        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl, appId, privateKey, format, charset, publicKey, signType);

        //PC网页支付使用AlipayTradePagePayRequest传参，下面调用的是pageExecute方法
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(returnUrl);
        alipayRequest.setNotifyUrl(notifyUrl);
        alipayRequest.setBizContent(JSON.toJSONString(alipayBean));
        System.out.println("封装请求支付宝付款参数为:{}" + JSON.toJSONString(alipayRequest));

        // 执行请求，拿到响应的结果，返回给浏览器
        String form = "";

        try{
            // 调用SDK生成表单
            form = alipayClient.pageExecute(alipayRequest).getBody();
            System.out.println("请求支付宝付款返回参数为:{}" + form);


            //修改状态为1---已支付
            UpdateWrapper<SOrder> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", sOrder.getId())
                    .set("status", 1);
            sorderMapper.update(sOrder, updateWrapper);

            model.addAttribute("formhtml",form);


        }catch (Exception e){
            System.out.println("异常信息：" + e.getMessage());
        }
        return "payHtml";

    }

    /**
     * @description: 支付宝支付  /alipay/payMoney
     * @param: map
     * @param: model
     * @return: java.lang.String
     **/
    @RequestMapping(value = "aliPayMoneyHorder/{id}", method = RequestMethod.GET)
    public String onAlipaymoneyHorder(@PathVariable Integer id, Model model)  {
        LambdaQueryWrapper<Horder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Horder::getId,id);
        Horder HOrder = horderMapper.selectOne(lambdaQueryWrapper);

        //数据组合
        AlipayBean alipayBean = new AlipayBean();
        alipayBean.setOut_trade_no("000"+HOrder.getId().toString());
        alipayBean.setSubject(hotelService.getById(roomService.getById(HOrder.getRid()).getHid()).getHname().toString());
        alipayBean.setTotal_amount(HOrder.getTotal().toString());
        alipayBean.setBody(HOrder.getEndTime().toString()+HOrder.getStartTime().toString());
        alipayBean.setProduct_code("FAST_INSTANT_TRADE_PAY");


        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl, appId, privateKey, format, charset, publicKey, signType);

        //PC网页支付使用AlipayTradePagePayRequest传参，下面调用的是pageExecute方法
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(returnUrl);
        alipayRequest.setNotifyUrl(notifyUrl);
        alipayRequest.setBizContent(JSON.toJSONString(alipayBean));
        System.out.println("封装请求支付宝付款参数为:{}" + JSON.toJSONString(alipayRequest));

        // 执行请求，拿到响应的结果，返回给浏览器
        String form = "";

        try{
            // 调用SDK生成表单
            form = alipayClient.pageExecute(alipayRequest).getBody();
            System.out.println("请求支付宝付款返回参数为:{}" + form);


            //修改状态为1---已支付
            UpdateWrapper<Horder> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", HOrder.getId())
                    .set("status", 1);
            horderMapper.update(HOrder, updateWrapper);

            model.addAttribute("formhtml",form);

        }catch (Exception e){
            System.out.println("异常信息：" + e.getMessage());
        }
        return "payHtml";

    }
}
