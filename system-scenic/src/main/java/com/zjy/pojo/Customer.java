package com.zjy.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("customer")
public class Customer {
    private Integer id;
    private String customerName;
    private String password;
    private String email;
    private String phone;
    private String address;
    private String cimage;

    /**
     * 身份证
     **/
    private String idNumber;
}
