package com.zjy.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("scenic")
public class Scenic {
    private Integer id;
    private String sname;
    private String country;
    private String address;
    private String stime;
    private double price;
    private String type;
    private String simage;
    private String descr;

    /**
     * 主键关联图片
     **/
    private String idTwo;
    /**
     * 路线地址
     **/
    private String routeAddress;

}
