package com.zjy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjy.pojo.User;

public interface UserService extends IService<User> {
    boolean login(String username, String password);
}
