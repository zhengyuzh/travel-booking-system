package com.zjy.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.beans.Transient;
import java.util.Date;

@Data
@TableName("s_order")
public class SOrder {
    @TableId
    private Integer id;
    private Integer cid;
    private Integer sid;
    private Integer count;
    private Double total;
    @TableField(fill = FieldFill.INSERT) //插入时填充字段
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;
    @TableField(fill = FieldFill.INSERT) //插入时填充字段
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;

    @TableField(exist = false)
    private Customer customer;
    @TableField(exist = false)
    private Scenic scenic;

    private Integer status;

    @TableField(exist = false) // 该字段不会参与数据库的查询或插入
    private String statusShow;//状态展示

    @TableField(exist = false)
    private String totalMoney;
    @TableField(exist = false)
    private String scenicId;

    /**
     * 用户电话
     **/
    private String phone;
    /**
     * 用户身份证
     **/
    private String idNumber;
}
