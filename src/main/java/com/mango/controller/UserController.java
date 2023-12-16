package com.mango.controller;

import com.mango.common.R;
import com.mango.config.Constant;
import com.mango.entity.Blog;
import com.mango.entity.LikeTable;
import com.mango.entity.User;
import com.mango.service.BlogService;
import com.mango.service.LikeTableService;
import com.mango.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private LikeTableService likeTableService;

    @Autowired
    private BlogService blogService;

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
        user.setIsVisible(1);
        if (map.get("email") != null) {
            user.setEmail(map.get("email").toString());
        }
        if (map.get("phone") != null) {
            user.setPhone(map.get("phone").toString());
        }
        userService.save(user);

        return R.success(String.valueOf(id));
    }

    /**
     * 实现登录功能，需求参数：用户id和用户密码password
     *
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
     *
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
        if (!userService.uploadHeadById(fileName, id)) {
            return R.error("头像上传成功失败");
        }

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

    /**
     * 上传自己位置，需求参数：用户id，用户位置posX，posY
     *
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/uploadlocation")
    public R<String> uploadLocation(@RequestBody Map map, HttpSession session) {
        log.info(map.toString());

        //获取用户id
        int id = Integer.parseInt(map.get("id").toString());
        //获取用户位置
        int posX = Integer.parseInt(map.get("posX").toString());
        int posY = Integer.parseInt(map.get("posY").toString());

        if (!userService.uploadLocationById(id, posX, posY)) {
            return R.error("更新位置失败");
        }

        return R.success("更新位置成功");
    }

    /**
     * 实现修改可见性功能，需求参数：用户id和修改后的可见性（0不可见，1可见）
     *
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/changevisibility")
    public R<String> changeVisibility(@RequestBody Map map, HttpSession session) {
        log.info(map.toString());

        //获取用户id
        int id = Integer.parseInt(map.get("id").toString());
        //获取用户可见性
        int isVisible = Integer.parseInt(map.get("isVisible").toString());

        if (!userService.changeVisibilityById(id, isVisible)) {
            return R.error("修改可见性失败");
        }
        return R.success("修改可见性成功");
    }

    /**
     * 实现查找某位用户的位置，会返回这个用户除了密码外的所有东西，需求参数：被查找的用户的id
     *
     * @param id
     * @return
     */
    @GetMapping("/findauserlocation")
    public R<User> findAUserLocation(Integer id) throws IOException {
        log.info(String.valueOf(id));

        User user = userService.getById(id);
        if (user == null) {
            return R.error("该用户不存在");
        }
        if (user.getIsVisible() == 0) {
            return R.error("该用户不可见");
        }
        user.setPassword("0");
        //处理头像
        String filePath = constant.dir + "/" + user.getIcon(); // 获取文件路径
        // 处理文件读取异常，比如记录日志或者跳过该文件的处理
        // 读取文件内容并添加到列表中
        byte[] fileContent = Files.readAllBytes(Path.of(filePath));
        // 使用Base64编码转换为字符串
        String encodedString = Base64.getEncoder().encodeToString(fileContent);

        user.setIcon(encodedString);
        return R.success(user);
    }


    //TODO 大数据推送功能
    @PostMapping("/pytest")
    public R pyTest(@RequestBody Map map, HttpSession session) throws IOException, InterruptedException {
        log.info(map.toString());

        //获取用户id
        int id = Integer.parseInt(map.get("id").toString());
        //获取用户点赞的推文
        List<LikeTable> list = likeTableService.selectLikeCommentsByUserId(id);
        List<List> likeBlogContentList = new ArrayList<>();
        for (LikeTable likeTable : list) {
            List<String> DescriptionAndFileNameList = new ArrayList<>();
            //得到一个推文，一个推文可能有多个图片，在这里把一个推文的多个图片拆开来，分别存入列表
            Blog blog = blogService.getById(likeTable.getLikeId());
            //将文本传入列表
            DescriptionAndFileNameList.add(blog.getDescription());

            // 去除字符串两端的方括号并按照逗号和空格分割成字符串数组
            String[] fileNames = blog.getImage().replaceAll("[\\[\\]]", "").split("\\s*,\\s*");

            for (String fileName : fileNames) {
                String filePath = constant.dir + "/" + fileName; // 获取文件路径
                DescriptionAndFileNameList.add(filePath);
            }
            likeBlogContentList.add(DescriptionAndFileNameList);
        }
        //return R.success(likeBlogContentList);

        // 构建Python命令及参数列表
        String pythonScriptPath = "path/to/your/python_script.py";//TODO 正确的py脚本路径名
        String[] command = new String[]{"python", pythonScriptPath};
        for (List fileName : likeBlogContentList) {
            command = Arrays.copyOf(command, command.length + 1);
            command[command.length - 1] = fileName.toString();
        }

        // 创建ProcessBuilder对象
        ProcessBuilder processBuilder = new ProcessBuilder(command);

        // 启动进程
        Process process = processBuilder.start();

        // 获取Python脚本的输出
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line); // 输出Python脚本的执行结果
        }
        //TODO 预期执行结果为图片名列表
        /**
         * 这样就会涉及到一个问题，如何根据图片名得知图片属于哪个推文，从而实现精准推送
         * 我现在想到一个办法，将文件名前方添加推文Id，例如原本是1701430469780.png，之后改成2_1701430469780.png
         * 这样会损失一部分安全性，但是能够实现功能。如果这样做，python脚本的执行结果返回文件名列表，我读取文件名列表得知这个id用户可以被推送哪些推文
         * 可以另外建一个数据库用来存用户可以被推送的队列，实现起来比较简单，当用户想看推文时，从队列里取出10个推文id，然后获取推文内容推送给用户（举例）
         * 那么我期望python脚本接收图片名，对图片进行处理等操作，得到模型，可以根据实际情况进行优化，最终输出文件名列表，被我读取
         * 还有一个问题，如何实现这个方法被调用，客户端定时轮询？频率可以一天一次或者动态调整，如果要后端实现主动调用的话我这边可以添加新的框架来实现
         */

        // 等待Python脚本执行完毕并获取返回值
        int exitCode = process.waitFor();
        return R.success("Python脚本执行完毕，退出码：" + exitCode);


    }
}
