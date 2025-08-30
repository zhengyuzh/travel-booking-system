package com.zjy.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@TableName("comment")//对应数据库的comment表
public class Comment {
    private Integer id;
    private Integer scenicId;
    private Integer star;
    private String customer;//评论人
    private String cimage;  //头像
    private String comment; //评论内容

    @TableField(fill = FieldFill.INSERT) //插入时填充字段
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date commentTime;//评论时间

    /**
     * 审核状态 0 -未审核 1 -审核通过 2 -审核未通过
     **/
    private Integer status;

}
