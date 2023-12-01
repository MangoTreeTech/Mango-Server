package com.mango.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mango.entity.Blog;
import com.mango.entity.BlogComment;

import java.util.List;

public interface BlogCommentService extends IService<BlogComment> {
    List<BlogComment> selectBlogCommentByBlogId(Integer blogId);

    void addBlogCommentLikeAmount(Integer id);

    void subBlogCommentLikeAmount(Integer id);
}
