package com.zjy.controller;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjy.pojo.Customer;
import com.zjy.pojo.SOrder;
import com.zjy.pojo.Scenic;
import com.zjy.service.CustomerService;
import com.zjy.service.ScenicService;
import com.zjy.service.SorderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("sorder")
public class SorderController {
    @Autowired
    private SorderService sorderService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private ScenicService scenicService;
    @RequestMapping("listSorder")
    public String listSorder(@RequestParam(value = "pageNum",defaultValue = "1",required = false)Integer pageNum,
                             @RequestParam(value = "pageSize",defaultValue = "6",required = false)Integer pageSize, SOrder sOrder, Model model){
        if (pageNum<0||pageNum.equals("")||pageNum==null){
            pageNum=1;
        }
        if (pageSize<0||pageSize.equals("")||pageSize==null){
            pageSize=6;
        }
        PageHelper.startPage(pageNum,pageSize);

        List<SOrder>list=sorderService.listSorder(sOrder.getId());
        PageInfo<SOrder>pageInfo=new PageInfo<>(list);
        model.addAttribute("pageInfo",pageInfo);
        return "admin-sorder-list";
    }

    @RequestMapping("preSaveSorder")
    public String preSaveSorder(Model model){
        List<Customer> list = customerService.list(null);
        List<Scenic> list1 = scenicService.list(null);
        model.addAttribute("customerList",list);
        model.addAttribute("scenicList",list1);
        return "admin-sorder-save";
    }

    @RequestMapping("saveSorder")
    public String saveSorder(SOrder sOrder){

        Scenic scenic = scenicService.getById(sOrder.getSid());
        double a=scenic.getPrice()*sOrder.getCount();
        sOrder.setTotal(a);
        boolean save = sorderService.save(sOrder);
        return "redirect:/sorder/listSorder";
    }

    @RequestMapping("delSorder/{id}")
    public String delSorder(@PathVariable Integer id){
        boolean b = sorderService.removeById(id);

        return "redirect:/sorder/listSorder";
    }

    @ResponseBody
    @RequestMapping("batchDeleteSorder")
    public String batchDeleteSorder(String idList){
        String[] split = StrUtil.split(idList, ",");
        List<Integer>list=new ArrayList<>();
        for (String s : split) {
            if (!s.isEmpty()){
                list.add(Integer.valueOf(s));
            }
        }

        boolean b = sorderService.removeByIds(list);
        if (b){
            return "OK";
        }else {
            return "error";
        }

    }
}
