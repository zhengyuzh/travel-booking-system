package com.zjy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjy.mapper.TypeMapper;
import com.zjy.mapper.UserSecurityAnswersMapper;
import com.zjy.pojo.Type;
import com.zjy.pojo.UserSecurityAnswers;
import com.zjy.service.UserSecurityAnswersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @BelongsProject: system-scenic
 * @BelongsPackage: com.zjy.service.impl
 * @Author: author
 * @CreateTime: 2024-12-13  12:17
 * @Description: TODO
 * @Version: 1.0
 */
@Service
public class UserSecurityAnswersServiceImpl extends ServiceImpl<UserSecurityAnswersMapper, UserSecurityAnswers> implements UserSecurityAnswersService {

    @Autowired
    UserSecurityAnswersMapper userSecurityAnswersMapper;
    @Override
    public UserSecurityAnswers selectByUserName(String userName) {
        LambdaQueryWrapper<UserSecurityAnswers> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserSecurityAnswers::getCustomerName,userName);
        return userSecurityAnswersMapper.selectOne(lambdaQueryWrapper);
    }
}
