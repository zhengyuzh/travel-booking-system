package com.zjy.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


/**
 * 用户密保答案表
 */
@Data
@TableName("UserSecurityAnswers")
public class UserSecurityAnswers {
    /**
     * 主键
     **/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 用户主键--姓名
     **/
    private String customerName;
    /**
     * 密保问题1
     **/
    private String questionOne;
    /**
     * 密保1答案
     **/
    private String questionOneAnswer;
    /**
     * 密保问题2
     **/
    private String questionTwo;
    /**
     * 密保2答案
     **/
    private String questionTwoAnswer;

}
