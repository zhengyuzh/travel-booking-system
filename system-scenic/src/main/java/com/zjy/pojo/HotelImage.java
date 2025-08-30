package com.zjy.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @BelongsProject: system-scenic
 * @BelongsPackage: com.zjy.pojo
 * @Author: author
 * @CreateTime: 2024-12-14  18:06
 * @Description: TODO
 * @Version: 1.0
 */
@Data
@TableName("hotel_images")
public class HotelImage {
    /**
     * 主键
     **/
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 酒店主键UUID
     **/
    private String hotelId;

    /**
     * 图片地址
     **/
    private String imagePath;


}
