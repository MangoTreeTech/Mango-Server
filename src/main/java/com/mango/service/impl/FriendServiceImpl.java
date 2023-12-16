package com.mango.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mango.entity.Friend;
import com.mango.entity.User;
import com.mango.mapper.FriendMapper;
import com.mango.service.FriendService;
import com.mango.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendServiceImpl extends ServiceImpl<FriendMapper, Friend> implements FriendService {

    @Autowired
    private FriendService friendService;

    @Autowired
    private UserService userService;

    @Override
    public boolean existFollow(Integer userId, Integer friendId) {
        QueryWrapper<Friend> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("friend_id", friendId); // 设置查询条件

        int count = (int) friendService.count(queryWrapper); // 查询符合条件的记录数量
        return count > 0;
    }

    @Override
    public void removeFollow(Integer userId, Integer friendId) {
        QueryWrapper<Friend> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("friend_id", friendId);
        friendService.remove(queryWrapper);
    }

    @Override
    public List<User> selectFriends(Integer userId) {
        QueryWrapper<Friend> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);

        //先查出来friend数据表，然后筛选出friend_id列
        List<Integer> list = friendService.list(queryWrapper).stream().map(Friend::getFriendId).collect(Collectors.toList());

        userService.listByIds(list);
        return userService.listByIds(list);
    }
}
