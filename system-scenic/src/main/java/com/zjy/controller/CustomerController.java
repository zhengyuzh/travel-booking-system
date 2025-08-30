package com.zjy.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjy.pojo.Comment;
import com.zjy.pojo.Customer;
import com.zjy.service.CommentService;
import com.zjy.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("customer")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private CommentService commentService;

    @Value("${location}")
    private String location;
    @RequestMapping("listCustomer")
    public String listCustomer(@RequestParam(value = "pageNum",defaultValue = "1",required = false)Integer pageNum,
                               @RequestParam(value = "pageSize",defaultValue = "6",required = false)Integer pageSize, Model model, Customer customer){
        if (pageNum<0||pageNum.equals("")||pageNum==null){
            pageNum=1;
        }
        if (pageSize<0||pageSize.equals("")||pageSize==null){
            pageSize=6;
        }
        PageHelper.startPage(pageNum,pageSize);  //分页一页6个
        QueryWrapper<Customer>qw=new QueryWrapper<>();
        if (customer.getCustomerName()!=null){
            qw.like("customer_name",customer.getCustomerName());
        }

        List<Customer> list = customerService.list(qw);
        PageInfo<Customer>pageInfo=new PageInfo<>(list);
        model.addAttribute("pageInfo",pageInfo);
        return "admin-customer-list";
    }

    /**
     * @description: 评论审核管理
     * @param: [pageNum, pageSize, model, customer]
     * @return: java.lang.String
     **/

    @RequestMapping("commentList")
    public String listComment(@RequestParam(value = "pageNum",defaultValue = "1",required = false)Integer pageNum,
                               @RequestParam(value = "pageSize",defaultValue = "6",required = false)Integer pageSize, Model model, Comment comment){
        if (pageNum<0||pageNum.equals("")||pageNum==null){
            pageNum=1;
        }
        if (pageSize<0||pageSize.equals("")||pageSize==null){
            pageSize=6;
        }
        PageHelper.startPage(pageNum,pageSize);  //分页一页6个
        QueryWrapper<Comment> qw = new QueryWrapper<>();
        if (comment.getCustomer()!=null){
            qw.like("customer",comment.getCustomer());
        }
        List<Comment> list = commentService.list(qw);
        PageInfo<Comment>pageInfo=new PageInfo<>(list);
        model.addAttribute("pageInfo",pageInfo);
        return "admin-comment-list";
    }

    @RequestMapping("preUpdateComment/{id}")
    public String preUpdateComment(@PathVariable Integer id,Model model){
        Comment comment = commentService.getById(id);
        model.addAttribute("comment",comment);
        return "admin-comment-update";
    }

    @RequestMapping("updateComment")
    public String updateComment(Comment comment){
        boolean b = commentService.updateById(comment);
        return "redirect:/customer/commentList";
    }
    /**
     * 删除评论
     **/
    @RequestMapping("delComment/{id}")
    public String delCommnet(@PathVariable Integer id){
        boolean b = commentService.removeById(id);
        return "redirect:/customer/commentList";
    }

    @RequestMapping("preSaveCustomer")
    public String preSaveCustomer(){
        return "admin-customer-save";
    }


    @RequestMapping("saveCustomer")
    public String saveCustomer(Customer customer, MultipartFile file){

        if (!file.isEmpty()){
            transfile(customer,file);
        }
        String s = DigestUtil.md5Hex(customer.getPassword());
        customer.setPassword(s);
        boolean save = customerService.save(customer);  //添加到
        return "redirect:/customer/listCustomer";  //redirect重定向到/customer/listCustomer
    }

    private void transfile(Customer customer, MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        int index = originalFilename.lastIndexOf(".");  //获取图片后缀
        String suffix = originalFilename.substring(index);
        String prefix=System.nanoTime()+"";  //以当前时间作为前缀
        String path=prefix+suffix;  //拼接
        File file1 = new File(location);
        if (!file1.exists()){  //如果location的目录不存在，创建目录
            file1.mkdirs();
        }
        File file2 = new File(file1, path);  //将改好名的图片存入
        try {
            file.transferTo(file2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        customer.setCimage(path);
    }

    @RequestMapping("preUpdateCustomer/{id}")
    public String preUpdateCustomer(@PathVariable Integer id,Model model){
        Customer customer = customerService.getById(id);
        model.addAttribute("customer",customer);
        return "admin-customer-update";
    }

    @RequestMapping("updateCustomer")
    public String updateCustomer(Customer customer,MultipartFile file){
        if (!file.isEmpty()){
            transfile(customer,file);
        }
        boolean b = customerService.updateById(customer);

        return "redirect:/customer/listCustomer";

    }

    @RequestMapping("delCustomer/{id}")
    public String delCustomer(@PathVariable Integer id){
        boolean b = customerService.removeById(id);
        return "redirect:/customer/listCustomer";
    }


    @ResponseBody
    @RequestMapping("batchDeleteCustomer")
    public String batchDeleteCustomer(String idList){

        String[] split = StrUtil.split(idList, ",");//传过来是1,2，....这里将，号去除
        List<Integer>list=new ArrayList<>();
        for (String s : split) {//遍历split，将数据存入list
            if (!s.isEmpty()){
                list.add(Integer.valueOf(s));
            }
        }

        boolean b = customerService.removeByIds(list);//ByIds根据id批量删除
        if (b){
            return "OK";
        }else {
            return "error";
        }

    }
}
