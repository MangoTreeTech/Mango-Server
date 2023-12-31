package com.mango.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Blog implements Serializable {
    //主键
    private Integer id;

    //用户id
    private Integer userId;

    //图片
    private String image;

    //推文描述
    private String description;

    //创建时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    //更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    //是否被锁上，1为上锁，0为没有上锁
    private Integer isLocked;

    //点赞数量
    private Integer likeAmount;

    //评论数量
    private Integer commentAmount;

    //文本标签
    private String  textLabels;

    //图像标签
    private String  imageLabels;

    //是否通过审核。0为待审核，1为通过审核，2为审核不通过
    private Integer passStatus;
    //推文位置
    //经度
    private double posX;
    //纬度
    private double posY;
}
