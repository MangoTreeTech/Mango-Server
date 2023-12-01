package com.mango.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.mango.common.R;
import com.mango.config.Constant;
import com.mango.entity.User;
import com.mango.mapper.UserMapper;
import com.mango.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private Constant constant;

    /**
     * 实现用户注册功能，接收参数password和name即可
     *
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/register")
    public R<String> register(@RequestBody Map map, HttpSession session) {
        log.info(map.toString());

        //获取用户昵称
        String name = map.get("name").toString();
        //获取用户密码
        String password = map.get("password").toString();

        User user = new User();
        //生成唯一性id
        int id;
        while (true) {
            id = (int) (Math.random() * 1000000000);
            //确保生成用户id为九位数
            while (id < 100000000) {
                id = (int) (Math.random() * 1000000000);
            }
            if (userService.isIdUnique(id)) {
                break;
            }
        }
        //初始化user并插入数据库
        user.setPassword(password);
        user.setId(id);
        user.setName(name);
        user.setIcon("1.jpg");
        user.setPosX(0);
        user.setPosY(0);
        userService.save(user);

        return R.success(String.valueOf(id));
    }

    /**
     * 实现登录功能，需求参数：用户id和用户密码password
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<String> login(@RequestBody Map map, HttpSession session) {
        log.info(map.toString());

        //获取用户id
        int id = Integer.parseInt(map.get("id").toString());
        //获取用户密码
        int password = Integer.parseInt(map.get("password").toString());

        //判断用户id和用户密码是否对应上
        if (userService.existIdAndPassword(id, password)) {
            return R.success("登陆成功");
        } else {
            return R.error("登陆失败");
        }
    }


    /**
     * 实现用户头像上传/更换功能，接收用户id和图片文件file
     * @param file
     * @param id
     * @return
     * @throws IOException
     */
    @PostMapping("/uploadhead")
    public R<String> uploadHead(@RequestParam("file") MultipartFile file, @RequestParam("id") Integer id) throws IOException {
        log.info(String.valueOf(file), id);

        // 验证文件是否为图片格式，这是最安全的做法，检查后缀名可能会被绕过
        if (!Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
            return R.error("上传的文件不是图片");
        }

        // 获取文件后缀
        String fileExtension = getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));

        // 生成时间戳作为文件名
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String fileName = timeStamp + "." + fileExtension;

        // 保存文件到指定路径
        Path path = Paths.get(constant.dir + "/" + fileName);
        Files.write(path, file.getBytes());

        // 存储文件名到用户的icon属性中
        userService.uploadHeadById(fileName, id);

        return R.success("头像上传成功");
    }


    // 获取文件后缀
    @NotNull
    private String getFileExtension(String fileName) {
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }

    @PostMapping("/uploadlocation")
    public R<String> uploadLocation(@RequestBody Map map, HttpSession session) {
        log.info(map.toString());

        //获取用户id
        int id = Integer.parseInt(map.get("id").toString());
        //获取用户位置
        int posX = Integer.parseInt(map.get("posX").toString());
        int posY = Integer.parseInt(map.get("posY").toString());

        userService.uploadLocationById(id, posX, posY);

        return R.success("成功更新位置");
    }
}
