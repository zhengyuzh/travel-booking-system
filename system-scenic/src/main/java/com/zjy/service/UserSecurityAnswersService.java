package com.zjy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjy.pojo.UserSecurityAnswers;

/**
 * @Description:
 */
public interface UserSecurityAnswersService  extends IService<UserSecurityAnswers> {
    UserSecurityAnswers selectByUserName(String userName);
}
