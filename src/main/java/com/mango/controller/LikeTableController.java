package com.mango.controller;

import com.mango.common.R;
import com.mango.config.Constant;
import com.mango.entity.Blog;
import com.mango.entity.LikeTable;
import com.mango.service.BlogCommentService;
import com.mango.service.BlogService;
import com.mango.service.LikeTableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/like")
@Slf4j
public class LikeTableController {
    @Autowired
    private LikeTableService likeTableService;

    @Autowired
    private BlogService blogService;

    @Autowired
    private BlogCommentService blogCommentService;


    @Autowired
    private Constant constant;

    /**
     * 给推文点赞，需求参数：userId，likeId
     *
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/likeblog")
    public R<String> likeBlog(@RequestBody Map map, HttpSession session) {
        log.info(map.toString());

        int userId = Integer.parseInt(map.get("userId").toString());
        int likeId = Integer.parseInt(map.get("likeId").toString());

        if (likeTableService.existLike(userId, likeId, 1)) {
            return R.error("已经点赞过了");
        }

        LikeTable likeTable = new LikeTable();
        likeTable.setUserId(userId);
        likeTable.setLikeId(likeId);
        likeTable.setIsBlog(1);
        //获取时间并存入
        LocalDateTime dateTime = LocalDateTime.now();
        likeTable.setCreateTime(dateTime);
        likeTableService.save(likeTable);
        //更新推文表中的相应数据
        blogService.addBlogLikeAmount(likeId);
        return R.success("点赞成功");
    }

    /**
     * 给评论点赞，需求参数：userId，likeId
     *
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/likeblogcomment")
    public R<String> likeBlogComment(@RequestBody Map map, HttpSession session) {
        log.info(map.toString());

        int userId = Integer.parseInt(map.get("userId").toString());
        int likeId = Integer.parseInt(map.get("likeId").toString());

        if (likeTableService.existLike(userId, likeId, 0)) {
            return R.error("已经点赞过了");
        }

        LikeTable likeTable = new LikeTable();
        likeTable.setUserId(userId);
        likeTable.setLikeId(likeId);
        likeTable.setIsBlog(0);
        //获取时间并存入
        LocalDateTime dateTime = LocalDateTime.now();
        likeTable.setCreateTime(dateTime);
        likeTableService.save(likeTable);
        //更新推文表中的相应数据
        blogCommentService.addBlogCommentLikeAmount(likeId);
        return R.success("点赞成功");
    }

    /**
     * 取消给推文点赞，需求参数：userId，likeId
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/cancellikeblog")
    public R<String> cancelLikeBlog(@RequestBody Map map, HttpSession session){
        log.info(map.toString());

        int userId = Integer.parseInt(map.get("userId").toString());
        int likeId = Integer.parseInt(map.get("likeId").toString());
        likeTableService.cancelLike(userId,likeId,0);
        blogService.subBlogLikeAmount(likeId);
        return R.success("取消点赞成功");
    }

    /**
     * 取消给评论点赞，需求参数：userId，likeId
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/cancellikeblogcomment")
    public R<String> cancelLikeBlogComment(@RequestBody Map map, HttpSession session){
        log.info(map.toString());

        int userId = Integer.parseInt(map.get("userId").toString());
        int likeId = Integer.parseInt(map.get("likeId").toString());
        likeTableService.cancelLike(userId,likeId,0);
        blogCommentService.subBlogCommentLikeAmount(likeId);
        return R.success("取消点赞成功");
    }

    @GetMapping("/selectlike")
    public R<? extends List> selectLike(Integer userId){
        log.info(String.valueOf(userId));
        List<LikeTable> list = likeTableService.selectLikeByUserId(userId);
        return R.success(list);
    }
}
