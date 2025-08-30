package com.zjy.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @BelongsProject: system-scenic
 * @BelongsPackage: com.zjy.pojo
 * @Author: author
 * @CreateTime: 2024-12-14  20:19
 * @Description: TODO
 * @Version: 1.0
 */
@Data
@TableName("scenic_images")
public class ScenicImage {
    /**
     * 主键
     **/
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 景色主键UUID
     **/
    private String scenicId;

    /**
     * 图片地址
     **/
    private String imagePath;
}
