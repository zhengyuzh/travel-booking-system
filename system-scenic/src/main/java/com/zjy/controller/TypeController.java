package com.zjy.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjy.pojo.Type;
import com.zjy.service.TypeService;
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
@RequestMapping("type")
public class TypeController {
    @Autowired
    private TypeService typeService;

    @RequestMapping("listType")
    public String listType(@RequestParam(value = "pageNum",defaultValue = "1",required = false)Integer pageNum,
                           @RequestParam(value = "pageSize",defaultValue = "6",required = false)Integer pageSize, Model model, Type type){
        if (pageNum<0||pageNum.equals("")||pageNum==null){
            pageNum=1;
        }
        if (pageSize<0||pageSize.equals("")||pageSize==null){
            pageSize=6;
        }
        PageHelper.startPage(pageNum,pageSize);
        QueryWrapper<Type>qw=new QueryWrapper<>();
        if (type.getType()!=null){
            qw.like("type",type.getType());
        }
        List<Type> list = typeService.list(qw);
        PageInfo<Type>pageInfo=new PageInfo<>(list);
        model.addAttribute("pageInfo",pageInfo);
        return "admin-type-list";
    }

    @RequestMapping("preSaveType")
    public String preSaveType(){
        return "admin-type-save";
    }

    @RequestMapping("saveType")
    public String saveType(Type type){
        boolean save = typeService.save(type);
        return "redirect:/type/listType";
    }

    @RequestMapping("preUpdateType/{id}")
    public String preUpdateType(@PathVariable Integer id,Model model){
        Type byId = typeService.getById(id);
        model.addAttribute("type",byId);
        return "admin-type-update";
    }

    @RequestMapping("updateType")
    public String updateType(Type type){
        boolean b = typeService.updateById(type);
        return "redirect:/type/listType";
    }

    @RequestMapping("delType/{id}")
    public String delType(@PathVariable Integer id){
        boolean b = typeService.removeById(id);
        return "redirect:/type/listType";
    }

    @ResponseBody
    @RequestMapping("batchDeleteType")
    public String batchDeleteType(String idList){
        String[] split = StrUtil.split(idList, ",");
        List<Integer>list=new ArrayList<>();
        for (String s : split) {
            if (!s.isEmpty()){
                list.add(Integer.valueOf(s));
            }
        }
        boolean b = typeService.removeByIds(list);
        if (b){
            return "OK";
        }else {
            return "error";
        }
    }
}
