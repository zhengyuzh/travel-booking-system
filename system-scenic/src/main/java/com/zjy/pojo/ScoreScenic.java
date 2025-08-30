package com.zjy.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@TableName("score_scenic")
public class ScoreScenic {

    private Integer userId;

    private Integer scenicId;

    private Integer score;
    private Date time;

}
