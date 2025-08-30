package com.zjy.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @BelongsProject: system-scenic
 * @BelongsPackage: com.zjy.pojo
 * @Author: author
 * @CreateTime: 2024-12-14  11:51
 * @Description: TODO
 * @Version: 1.0
 */
@Data
@TableName("scenic_route")
public class ScenicRoute {

    /**
     * 主键
     **/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 路线名称
     **/
    private String title;
    /**
     * 路线内容
     **/
    private String context;
    /**
     * 图片
     **/
    private String simage;
    /**
     * 热度
     **/
    private Integer hot;


    /**
     * 更新时间
     **/
    private Date updateTime;


}
