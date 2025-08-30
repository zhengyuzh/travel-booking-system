package com.zjy.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@TableName("h_order")
public class Horder {
    @TableId
    private Integer id;
    private Integer cid;
    private Integer rid;
    private Integer count;
    private Double total;
    @TableField(fill = FieldFill.INSERT) //插入时填充字段
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;
    @TableField(fill = FieldFill.INSERT) //插入时填充字段
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;
    private Integer status;

    @TableField(exist = false)
    private String totalMoney;
    @TableField(exist = false)
    private String hname;
    @TableField(exist = false)
    private String cname;
    @TableField(exist = false)
    private String rname;
    @TableField(exist = false)
    private String cimage;
    @TableField(exist = false)
    private String himage;
    @TableField(exist = false)
    private String rimage;
    @TableField(exist = false)
    private double price;
    @TableField(exist = false)
    private String address;
    @TableField(exist = false)
    private String city;
    @TableField(exist = false)
    private Integer bed;
    @TableField(exist = false)
    private Integer big;

    /**
     * 用户电话
     **/
    private String phone;
    /**
     * 用户身份证
     **/
    private String idNumber;
}
