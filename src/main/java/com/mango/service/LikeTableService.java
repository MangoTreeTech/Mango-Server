package com.mango.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mango.entity.LikeTable;

import java.util.List;

public interface LikeTableService extends IService<LikeTable> {
    boolean existLike(Integer userId,Integer likeId, Integer isBlog);

    void cancelLike(Integer userId,Integer likeId, Integer isBlog);

    List<LikeTable> selectLikeByUserId(Integer userId);

    void removeByLikeId(Integer likeId, Integer i);
}
