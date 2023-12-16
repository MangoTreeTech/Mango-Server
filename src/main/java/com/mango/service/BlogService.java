package com.mango.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mango.entity.Blog;

import java.util.List;

public interface BlogService extends IService<Blog> {
    List<Blog> selectBlogs(Integer userId);

    void addBlogLikeAmount(Integer id);

    boolean subBlogLikeAmount(Integer id);
}
