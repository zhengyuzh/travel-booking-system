package com.zjy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjy.pojo.Customer;
import com.zjy.pojo.UserSecurityAnswers;

public interface CustomerService extends IService<Customer> {
    boolean login(String username, String password);

    UserSecurityAnswers selectByUserName(String username);

    boolean selectUser(String username);

    Customer selectByUserId(Integer userId);
}
