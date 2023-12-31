package com.mango.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户信息
 */
@Data
public class User implements Serializable {
    //用户标识
    private Integer id;

    //用户密码
    private String password;

    //用户昵称
    private String name;

    //用户头像
    private String icon;

    //用户位置
    //经度
    private double posX;
    //纬度
    private double posY;

    //可见性，为1时可见，为0时不可见
    private Integer isVisible;

    //用户邮箱
    private String email;

    //用户手机号
    private String phone;

}
