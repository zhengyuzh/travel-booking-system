package com.zjy.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjy.mapper.CustomerMapper;
import com.zjy.mapper.UserSecurityAnswersMapper;
import com.zjy.pojo.Customer;
import com.zjy.pojo.User;
import com.zjy.pojo.UserSecurityAnswers;
import com.zjy.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer>implements CustomerService {

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    UserSecurityAnswersMapper userSecurityAnswersMapper;
    @Override
    public boolean login(String username, String password) {
        QueryWrapper<Customer>qw=new QueryWrapper<>();
        qw.eq("customer_name",username);
        Customer customer = customerMapper.selectOne(qw);
        if (customer==null){
            return false;
        }

        String s = DigestUtil.md5Hex(password);
        if (Objects.equals(s,customer.getPassword())){
            return true;
        }else {
            return false;
        }

    }

    @Override
    public UserSecurityAnswers selectByUserName(String username) {
        LambdaQueryWrapper<UserSecurityAnswers> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserSecurityAnswers::getCustomerName,username);
        UserSecurityAnswers securityAnswers = userSecurityAnswersMapper.selectOne(lambdaQueryWrapper);
        return securityAnswers;
    }

    @Override
    public boolean selectUser(String username) {
        QueryWrapper<Customer>qw=new QueryWrapper<>();
        qw.eq("customer_name",username);
        Customer customer = customerMapper.selectOne(qw);
        if (customer==null){
            return false;
        }
        return true;
    }

    @Override
    public Customer selectByUserId(Integer userId) {
        return customerMapper.selectById(userId);
    }
}
