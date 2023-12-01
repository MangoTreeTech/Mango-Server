package com.mango.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Friend implements Serializable {
    //主键
    private Integer id;

    //用户id
    private Integer userId;

    //好友id
    private Integer friendId;
}
