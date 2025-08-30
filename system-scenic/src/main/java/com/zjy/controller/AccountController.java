package com.zjy.controller;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zjy.pojo.User;
import com.zjy.service.CustomerService;
import com.zjy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;

@Controller
public class AccountController {
    @Autowired
    private UserService userService;
    @Autowired
    private CustomerService customerService;

    @RequestMapping("login")
    public String login(){
        return "user-login";
    }

    @RequestMapping("adminlogin")
    public String adminlogin(String username, String password, Model model, HttpSession session){
        boolean b=userService.login(username,password);//session除了关闭项目不然信息一直存在，model只在这个页面跳转了就没了
        if (b){
            QueryWrapper<User>qw=new QueryWrapper<>(); //创建一个查询包装器，用于构建数据库查询条件
            qw.eq("username",username); //查找与传入的用户名匹配的用户记录
            User user=userService.getOne(qw); //从数据库中获取与用户名匹配的用户对象
            session.setAttribute("currentUser",username); //将当前用户名存入会话中
            session.setAttribute("userId",user.getId()); //将用户ID存入会话中....
            session.setAttribute("image",user.getImage());
            session.setAttribute("email",user.getEmail());
            session.setAttribute("phone",user.getPhone());
            session.setAttribute("password",password);
            return "admin-home";
        }else {
            model.addAttribute("msg","用户名或密码错误！");
            return "index";
        }
    }



    @RequestMapping("count")
    public String count(){
        return "admin-count";
    }


    @RequestMapping("profile")
    public String profile(HttpSession session,Model model ){
        String currentUser = (String) session.getAttribute("currentUser");
        String  password = (String) session.getAttribute("password");
        QueryWrapper<User>qw=new QueryWrapper<>();
        qw.eq("username",currentUser);
        User one = userService.getOne(qw);
        one.setPassword(password);
        model.addAttribute("user",one);
        return "admin-profile";
    }

    @RequestMapping("updateAdminProfile")
    public String updateAdminProfile(User user, MultipartFile file){
        if (!file.isEmpty()){
            transFile(user,file);
        }
        String s = DigestUtil.md5Hex(user.getPassword());
        user.setPassword(s);
        boolean b = userService.updateById(user);
        return "redirect:/profile";
    }
    @Value("${location}")
    private String location;
    private void transFile(User user, MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        int index = originalFilename.lastIndexOf(".");
        String suffix = originalFilename.substring(index);
        String prefix=System.nanoTime()+"";
        String path=prefix+suffix;
        File file1 = new File(location);
        if (!file1.exists()){
            file1.mkdirs();
        }
        File file2 = new File(file1, path);
        try {
            file.transferTo(file2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        user.setImage(path);
    }

    //登出
    @RequestMapping("logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "index";
    }
}
