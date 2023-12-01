package com.mango.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mango.entity.Blog;
import com.mango.entity.Friend;
import com.mango.entity.User;

import java.util.List;

public interface BlogService extends IService<Blog> {
    List<Blog> selectBlogs(Integer userId);

    void addBlogLikeAmount(Integer id);

    void subBlogLikeAmount(Integer id);
}
