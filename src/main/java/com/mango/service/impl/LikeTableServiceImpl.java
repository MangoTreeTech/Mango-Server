package com.mango.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mango.entity.LikeTable;
import com.mango.mapper.LikeTableMapper;
import com.mango.service.LikeTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LikeTableServiceImpl extends ServiceImpl<LikeTableMapper, LikeTable> implements LikeTableService {
    @Autowired
    private LikeTableService likeTableService;

    @Override
    public boolean existLike(Integer userId, Integer likeId, Integer isBlog) {
        QueryWrapper<LikeTable> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("like_id", likeId).eq("is_blog", isBlog); // 设置查询条件

        int count = (int) likeTableService.count(queryWrapper); // 查询符合条件的记录数量
        return count != 0;
    }

    @Override
    public void cancelLike(Integer userId, Integer likeId, Integer isBlog) {
        QueryWrapper<LikeTable> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("like_id", likeId).eq("is_blog", isBlog); // 设置查询条件
        likeTableService.remove(queryWrapper);
    }

    @Override
    public List<LikeTable> selectLikeByUserId(Integer userId) {
        QueryWrapper<LikeTable> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return likeTableService.list(queryWrapper);
    }

    @Override
    public List<LikeTable> selectLikeCommentsByUserId(Integer userId) {
        QueryWrapper<LikeTable> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("is_blog", 1);
        return likeTableService.list(queryWrapper);
    }

    //删除评论或推文时，删除相关数据
    @Override
    public void removeByLikeId(Integer likeId, Integer i) {
        QueryWrapper<LikeTable> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("like_id", likeId).eq("is_blog", i); // 设置查询条件
        likeTableService.remove(queryWrapper);
    }
}
