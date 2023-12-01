package com.mango.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mango.entity.Friend;
import com.mango.entity.User;

import java.util.List;

public interface FriendService extends IService<Friend> {
    /**
     *查询用户是否已经关注了另一个用户
     * @param userId
     * @param friendId
     * @return
     */
    boolean existFollow(Integer userId,Integer friendId);

    /**
     * 取消关注
     * @param userId
     * @param friendId
     */
    void removeFollow(Integer userId,Integer friendId);

    /**
     * 查询好友列表，返回User表
     * @param userId
     * @return
     */
    List<User> selectFriends(Integer userId);
}
