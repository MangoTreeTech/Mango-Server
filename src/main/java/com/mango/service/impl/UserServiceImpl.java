package com.mango.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mango.entity.User;
import com.mango.mapper.UserMapper;
import com.mango.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserService userService;

    @Override
    public boolean uploadHeadById(String fileName, Integer id) {

        // 存储文件名到用户的icon属性中
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id).set("icon", fileName);
        return userService.update(null, updateWrapper);
    }

    @Override
    public boolean isIdUnique(Integer id) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id); // 设置查询条件

        int count = (int) userService.count(queryWrapper); // 查询符合条件的记录数量
        return count == 0; // 如果数量为0，表示数据库中不存在该用户名，可以作为唯一用户名
    }


    @Override
    public boolean isPasswordUnique(Integer password) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("password", password); // 设置查询条件

        int count = (int) userService.count(queryWrapper); // 查询符合条件的记录数量
        return count == 0;
    }

    @Override
    public boolean existIdAndPassword(Integer id, Integer password) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id).eq("password", password); // 设置查询条件

        int count = (int) userService.count(queryWrapper); // 查询符合条件的记录数量
        return count > 0;
    }

    @Override
    public boolean uploadLocationById(Integer id, double posX, double posY) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id).set("pos_x", posX).set("pos_y", posY);
        return userService.update(null, updateWrapper);
    }

    @Override
    public boolean changeVisibilityById(Integer id, Integer isVisible) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id).set("is_visible", isVisible);
        return userService.update(null, updateWrapper);
    }


}
