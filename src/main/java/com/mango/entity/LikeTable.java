package com.mango.entity;

import lombok.Data;

import java.io.Serializable;
@Data
public class LikeTable implements Serializable {
    //主键
    private Integer id;

    //用户id
    private Integer userId;

    //被点赞的推文或评论id
    private Integer likeId;

    //为0表示评论，为非零表示推文
    private Integer isBlog;
}
