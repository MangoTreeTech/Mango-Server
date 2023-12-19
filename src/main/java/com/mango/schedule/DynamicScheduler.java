package com.mango.schedule;
import com.mango.config.Constant;
import com.mango.entity.Blog;
import com.mango.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class DynamicScheduler {
    // 初始设定为每分钟执行，可根据需求调整
    private String cronExpression = "0 * * * * ?";

    @Autowired
    private BlogService blogService;

    @Autowired
    private Constant constant;

    /**
     *
     * 定时审核进程，功能为扫描数据库中未审核的推文，对其打上标签并进行审核
     */
    @Scheduled(fixedRate = 1000)
    public void performTask() throws IOException {
        // 这里放置任务逻辑
        System.out.println("开始审核");
        //获取未审核的队列
        List<Blog> blogList = blogService.selectBlogsUnPassed();
        for (Blog blog: blogList) {
            //python程序测试
            String urlString = "http://localhost:8989/endpoint"; // 替换为你的服务器IP和端点，即接口位置
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // 发送数据
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(blog.getDescription());
            wr.flush();
            wr.close();

            // 获取响应
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // 输出响应结果
            System.out.println("Response: " + response.toString());
        }

    }

    // 动态调整任务频率的方法
    public void setCronExpression(long newPeriod, TimeUnit newTimeUnit) {
        // 根据传入的时间间隔和单位生成新的cron表达式
        // 这里需要根据传入的参数重新计算cron表达式
        this.cronExpression = calculateNewCronExpression(newPeriod, newTimeUnit);
    }

    // 用于生成新的cron表达式的方法，根据需求自行实现
    private String calculateNewCronExpression(long newPeriod, TimeUnit newTimeUnit) {
        // 生成新的cron表达式逻辑
        String newCronExpression = "0 * * * * ?";
        return newCronExpression;
    }

    // 供@Scheduled注解引用的方法，返回动态的cron表达式
    public String getCronExpression() {
        return cronExpression;
    }
}
