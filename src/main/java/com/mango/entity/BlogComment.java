package com.mango.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class BlogComment implements Serializable {
    //主键
    private Integer id;

    //用户id
    private Integer userId;

    //推文id
    private Integer blogId;

    //评论内容
    private String comment;

    //点赞id
    private Integer likeAmount;

    //创建时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    //为0时表示没有回复其他用户，为非零数时表示这是子评论
    private Integer isChild;

    //回复评论的id，此id为评论的id
    private Integer parentId;
}
