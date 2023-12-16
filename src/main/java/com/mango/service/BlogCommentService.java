package com.mango.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mango.entity.BlogComment;

import java.util.List;

public interface BlogCommentService extends IService<BlogComment> {
    List<BlogComment> selectBlogCommentByBlogId(Integer blogId);

    boolean addBlogCommentLikeAmount(Integer id);

    boolean subBlogCommentLikeAmount(Integer id);
}
