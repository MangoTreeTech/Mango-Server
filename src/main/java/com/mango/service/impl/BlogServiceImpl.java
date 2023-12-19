package com.mango.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mango.entity.Blog;
import com.mango.mapper.BlogMapper;
import com.mango.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {
    @Autowired
    private BlogService blogService;

    @Override
    public List<Blog> selectBlogs(Integer userId) {
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);

        //List<Blog> blogList = blogService.list(queryWrapper);
        return blogService.list(queryWrapper);
    }

    @Override
    public void addBlogLikeAmount(Integer id) {
        Blog blog = blogService.getById(id);
        UpdateWrapper<Blog> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id).set("like_amount", blog.getLikeAmount() + 1);
        blogService.update(null, updateWrapper);
    }

    @Override
    public boolean subBlogLikeAmount(Integer id) {
        Blog blog = blogService.getById(id);
        UpdateWrapper<Blog> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id).set("like_amount", blog.getLikeAmount() - 1);
        blogService.update(null, updateWrapper);
        return false;
    }

    @Override
    public List<Blog> selectBlogsUnPassed(){
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pass_status", 0);

        return blogService.list(queryWrapper);
    }
}
