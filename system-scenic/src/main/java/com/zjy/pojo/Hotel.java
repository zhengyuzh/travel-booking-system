package com.zjy.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

@Data
@TableName("hotel")
public class Hotel {
    @TableId
    private Integer id;
    private String hname;
    private String city;
    private String address;
    private Integer sid;
    private String himage;
    private String descr;

    @TableField(exist = false)
    private String sname;
    @TableField(exist = false)
    private List<Room> roomList;


    /**
     * UUID和图片合集关联
     **/
    private String idTwo;
    /**
     * 图片合集
     **/
    @TableField(exist = false)
    private List<HotelImage> hotelImageList;
}
