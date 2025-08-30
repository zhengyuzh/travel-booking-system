package com.zjy.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("room")
public class Room {
    @TableId
    private Integer id;
    private String rname;
    private Integer wifi;
    private Integer bed;
    private Integer big;
    private Integer weiyu;
    private Integer tv;
    private Integer hid;
    private String rimage;
    private double price;
}
