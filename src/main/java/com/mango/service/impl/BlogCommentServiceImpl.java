package com.mango.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mango.entity.Blog;
import com.mango.entity.BlogComment;
import com.mango.mapper.BlogCommentMapper;
import com.mango.service.BlogCommentService;
import com.mango.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlogCommentServiceImpl extends ServiceImpl<BlogCommentMapper, BlogComment> implements BlogCommentService {
    @Autowired
    private BlogCommentService blogCommentService;

    @Override
    public List<BlogComment> selectBlogCommentByBlogId(Integer blogId) {
        QueryWrapper<BlogComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("blog_id", blogId);

        //List<Blog> blogList = blogService.list(queryWrapper);
        return blogCommentService.list(queryWrapper);
    }

    @Override
    public void addBlogCommentLikeAmount(Integer id) {
        BlogComment blogComment = blogCommentService.getById(id);
        UpdateWrapper<BlogComment> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id).set("like_amount", blogComment.getLikeAmount() + 1);
        blogCommentService.update(null, updateWrapper);
    }

    @Override
    public void subBlogCommentLikeAmount(Integer id) {
        BlogComment blogComment = blogCommentService.getById(id);
        UpdateWrapper<BlogComment> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id).set("like_amount", blogComment.getLikeAmount() - 1);
        blogCommentService.update(null, updateWrapper);
    }
}
