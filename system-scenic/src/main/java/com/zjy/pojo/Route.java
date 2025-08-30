package com.zjy.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("route")
public class Route {
    private Integer id;
    private String rname;
    private String rtime;
    private Integer day;
    private String descr;
    private String rimage;
    private Integer sid;
}
