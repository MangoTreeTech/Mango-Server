package com.mango.controller;

import com.mango.common.R;
import com.mango.config.Constant;
import com.mango.entity.BlogComment;
import com.mango.service.BlogCommentService;
import com.mango.service.LikeTableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comment")
@Slf4j
public class BlogCommentController {
    @Autowired
    private BlogCommentService blogCommentService;

    @Autowired
    private Constant constant;

    @Autowired
    private LikeTableService likeTableService;

    /**
     * 实现发布评论功能，需求参数：userId，blogId，isChild，parentId，comment
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/makecomment")
    public R<String> makeComment(@RequestBody Map map, HttpSession session) {
        log.info(map.toString());

        BlogComment blogComment= new BlogComment();
        //获取各种数据
        int userId = Integer.parseInt(map.get("userId").toString());
        int blogId = Integer.parseInt(map.get("blogId").toString());
        int isChild = Integer.parseInt(map.get("isChild").toString());
        if (isChild != 0) {
            int parentId = Integer.parseInt(map.get("parentId").toString());
            blogComment.setParentId(parentId);
        }
        String comment = map.get("comment").toString();

        blogComment.setUserId(userId);
        blogComment.setBlogId(blogId);
        blogComment.setIsChild(isChild);
        blogComment.setLikeAmount(0);
        blogComment.setComment(comment);
        //获取时间并存入
        LocalDateTime dateTime = LocalDateTime.now();
        blogComment.setCreateTime(dateTime);

        blogCommentService.save(blogComment);

        return R.success("发布评论成功");
    }

    /**
     * 实现删除评论功能，需求参数：id
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/deletecomment")
    public R<String> deleteComment(@RequestBody Map map, HttpSession session){
        log.info(map.toString());

        int id = Integer.parseInt(map.get("id").toString());
        blogCommentService.removeById(id);
        likeTableService.removeByLikeId(id ,0);
        return R.success("删除成功");
    }

    /**
     * 实现展示评论功能，需求参数：BlogId
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/showcomment")
    public R<List> showComment(@RequestBody Map map, HttpSession session){
        log.info(map.toString());

        int blogId = Integer.parseInt(map.get("blogId").toString());
        return R.success(blogCommentService.selectBlogCommentByBlogId(blogId));
    }
}
