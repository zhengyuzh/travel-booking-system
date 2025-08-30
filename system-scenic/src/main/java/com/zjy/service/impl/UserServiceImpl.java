package com.zjy.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjy.mapper.UserMapper;
import com.zjy.mapper.UserSecurityAnswersMapper;
import com.zjy.pojo.User;
import com.zjy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserSecurityAnswersMapper userSecurityAnswersMapper;
    @Override
    public boolean login(String username, String password) {
        QueryWrapper<User>qw=new QueryWrapper<>(); //User为pojo层的User
        qw.eq("username",username); //与username一样值的在User里的username
        User user = userMapper.selectOne(qw); //根据提供的查询条件（qw）从数据库中选择一个用户记录
        if (user==null){
            return false;
        }
        String s = DigestUtil.md5Hex(password); //对输入的密码进行加密码
        String userPassword = user.getPassword(); //数据库里的密码
        if (Objects.equals(s,userPassword)){ //对两个密码进行对比
            return true;
        }else {
            return false;
        }

    }
}
