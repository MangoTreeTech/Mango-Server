package com.mango.controller;

import com.mango.common.R;
import com.mango.config.Constant;
import com.mango.entity.Blog;
import com.mango.service.BlogService;
import com.mango.service.FriendService;
import com.mango.service.LikeTableService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/blog")
@Slf4j
public class BlogController {
    @Autowired
    private BlogService blogService;

    @Autowired
    private Constant constant;

    @Autowired
    private LikeTableService likeTableService;

    /**
     * 实现发布推文的功能，需求参数：files，userId，description
     *
     * @param files
     * @param userId
     * @param description
     * @return
     * @throws IOException
     */
    @PostMapping("/createblog")
    public R<String> createBlog(@RequestParam("files") MultipartFile[] files,
                                @RequestParam("userId") Integer userId,
                                @RequestParam("description") String description) throws IOException {
        log.info(Arrays.toString(files), userId, description);

        Blog blog = new Blog();
        blog.setUserId(userId);
        blog.setDescription(description);
        blog.setIsLocked(0);
        blog.setLikeAmount(0);
        blog.setCommentAmount(0);
        List<String> fileNames = new ArrayList<>();
        for (MultipartFile file : files) {
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

            // 将文件名存入列表
            fileNames.add(fileName);
        }
        blog.setImage(fileNames.toString());
        //获取时间并存入
        LocalDateTime dateTime = LocalDateTime.now();
        blog.setCreateTime(dateTime);
        blog.setUpdateTime(dateTime);
        blogService.save(blog);

        return R.success("发布成功");
    }

    @NotNull
    private String getFileExtension(String fileName) {
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }

    /**
     * 实现展示/查看一个用户所有推文的功能，不带评论
     *
     * @param userId
     * @return
     * @throws IOException
     */
    @GetMapping("/showblog")
    public R<? extends List> showBlog(Integer userId) throws IOException {
        log.info(String.valueOf(userId));

        //根据userId查询这个用户写出的推文
        List<Blog> blogList = blogService.selectBlogs(userId);
        //替换其中的image
        for (Blog blog : blogList) {
            // 去除字符串两端的方括号并按照逗号和空格分割成字符串数组
            String[] fileNames = blog.getImage().replaceAll("[\\[\\]]", "").split("\\s*,\\s*");
            //存储读取得到的，编码后的字符串
            List<String> fileContents = new ArrayList<>();
            for (String fileName : fileNames) {
                String filePath = constant.dir + "/" + fileName; // 获取文件路径
                // 处理文件读取异常，比如记录日志或者跳过该文件的处理
                // 读取文件内容并添加到列表中
                byte[] fileContent = Files.readAllBytes(Path.of(filePath));
                // 使用Base64编码转换为字符串
                String encodedString = Base64.getEncoder().encodeToString(fileContent);

                fileContents.add(encodedString);
            }
            blog.setImage(fileContents.toString());
        }
        return R.success(blogList);
    }


    //TODO 更新推文测试

    /**
     * 实现更新推文功能，需求参数写在函数头了
     * @param files
     * @param userId
     * @param id
     * @param description
     * @return
     * @throws IOException
     */
    @PostMapping("/updateblog")
    public R<String> updateBlog(@RequestParam("files") MultipartFile[] files,
                                @RequestParam("userId") Integer userId,
                                @RequestParam("id") Integer id,
                                @RequestParam("description") String description) throws IOException {
        log.info(Arrays.toString(files), userId, id, description);

        Blog blog = blogService.getById(id);
        // 去除字符串两端的方括号并按照逗号和空格分割成字符串数组
        String[] fileNames = blog.getImage().replaceAll("[\\[\\]]", "").split("\\s*,\\s*");

        for (String fileName : fileNames) {
            String filePath = constant.dir + "/" + fileName; // 获取文件路径
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        }
        List<String> newFileNames = new ArrayList<>();
        for (MultipartFile file : files) {
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

            // 将文件名存入列表
            newFileNames.add(fileName);
        }
        blog.setImage(newFileNames.toString());

        blog.setDescription(description);
        //获取时间并存入
        LocalDateTime dateTime = LocalDateTime.now();
        blog.setUpdateTime(dateTime);
        blogService.updateById(blog);
        return R.success("更新成功");
    }

    //TODO 删除推文测试
    /**
     * 删除推文，需求参数：id
     *
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/deleteblog")
    public R<String> deleteBlog(@RequestBody Map map, HttpSession session) {
        log.info(map.toString());

        int id = Integer.parseInt(map.get("id").toString());
        Blog blog = blogService.getById(id);
        // 去除字符串两端的方括号并按照逗号和空格分割成字符串数组
        String[] fileNames = blog.getImage().replaceAll("[\\[\\]]", "").split("\\s*,\\s*");

        for (String fileName : fileNames) {
            String filePath = constant.dir + "/" + fileName; // 获取文件路径
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        }
        blogService.removeById(id);
        likeTableService.removeByLikeId(id, 1);
        return R.success("删除成功");
    }
}
