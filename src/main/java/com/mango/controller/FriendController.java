package com.mango.controller;

import com.mango.common.R;
import com.mango.config.Constant;
import com.mango.entity.Friend;
import com.mango.entity.User;
import com.mango.service.FriendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/friend")
@Slf4j
public class FriendController {
    @Autowired
    private FriendService friendService;

    @Autowired
    private Constant constant;

    /**
     * 关注某位用户，需求参数：关注者id和被关注者id
     *
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/follow")
    public R<String> follow(@RequestBody Map map, HttpSession session) {
        log.info(map.toString());

        //获取userId
        int userId = Integer.parseInt(map.get("userId").toString());
        //获取friendId
        int friendId = Integer.parseInt(map.get("friendId").toString());

        Friend friend = new Friend();
        friend.setUserId(userId);
        friend.setFriendId(friendId);
        //查询是否出现重复关注
        if (friendService.existFollow(userId, friendId)) {
            return R.error("不可重复关注");
        }
        friendService.save(friend);
        return R.success("关注成功");
    }

    /**
     * 取消关注某位用户，需求参数：关注者id和被关注者id
     *
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/unfollow")
    public R<String> unfollow(@RequestBody Map map, HttpSession session) {
        log.info(map.toString());

        //获取userId
        int userId = Integer.parseInt(map.get("userId").toString());
        //获取friendId
        int friendId = Integer.parseInt(map.get("friendId").toString());

        friendService.removeFollow(userId, friendId);

        return R.success("取消关注成功");
    }

    /**
     * 实现查询好友列表功能，需求参数：用户userId
     * @param userId
     * @return
     * @throws IOException
     */
    @GetMapping("/displayfriendlist")
    public R<? extends List> displayFriendList(Integer userId) throws IOException {
        log.info(String.valueOf(userId));

        List<User> list = friendService.selectFriends(userId);
        //List<byte[]> fileContents = new ArrayList<>();
        for (User user : list) {
            String filePath = constant.dir + "/" + user.getIcon(); // 获取文件路径
            // 处理文件读取异常，比如记录日志或者跳过该文件的处理
            // 读取文件内容并添加到列表中
            byte[] fileContent = Files.readAllBytes(Path.of(filePath));
            // 使用Base64编码转换为字符串
            String encodedString = Base64.getEncoder().encodeToString(fileContent);

            user.setIcon(encodedString);
        }

        return R.success(list);
    }

    /**
     * 同样实现关注功能，但是是不一样的写法，后续讨论选择一种
     * @param friend
     * @return
     */
    @PostMapping("/follow2")
    public R<String> follow2(@RequestBody Friend friend) {

        //获取userId
        int userId = friend.getUserId();
        //获取friendId
        int friendId = friend.getFriendId();

        Friend newfriend = new Friend();
        newfriend.setUserId(userId);
        newfriend.setFriendId(friendId);
        //查询是否出现重复关注
        if (friendService.existFollow(userId, friendId)) {
            return R.success("不可重复关注");
        }
        friendService.save(newfriend);
        return R.success("关注成功");
    }
}
