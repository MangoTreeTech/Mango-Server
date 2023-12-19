package com.mango.schedule;
import com.mango.config.Constant;
import com.mango.entity.Blog;
import com.mango.service.BlogService;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RestController
@Slf4j
public class DynamicScheduler implements ApplicationContextAware {
    // 初始设定为每分钟执行，可根据需求调整
    private String cronExpression = "0 * * * * ?";

    private static ApplicationContext context;

    @Autowired
    private BlogService blogService;

    @Autowired
    private Constant constant;

    @Autowired
    private TencentTextModerationService tencentTextModerationService;

    @Autowired
    private TencentImageModerationService tencentImageModerationService;

    @Autowired
    private DetectImageLabelsService detectImageLabelsService;

    @Autowired
    private DetectTextLabelsService detectTextLabelsService;

    /**
     *
     * 定时审核进程，功能为扫描数据库中未审核的推文，对其打上标签并进行审核
     */
    @Scheduled(fixedRate = 2000)//上个任务结束后一秒再开始
    public void performTask() throws IOException, TencentCloudSDKException {
        // 这里放置任务逻辑
        System.out.println("开始审核");
        //获取未审核的队列
        List<Blog> blogList = blogService.selectBlogsUnPassed();
        for (Blog blog: blogList) {
            //文字审核
            String textResult = tencentTextModerationService.performTextModeration(blog.getDescription());
            //String result = tencentTextModerationService.performTextModeration("我要色色");
            if(textResult.equals("0")){
                System.out.println("文本审核正常，文本内容：" + blog.getDescription());
            }
            else{
                System.out.println("文本审核不正常");
                blog.setPassStatus(2);//是否通过审核。0为待审核，1为通过审核，2为审核不通过
                blogService.updateById(blog);
                continue;
            }
            //图片审核
            String imageResult = tencentImageModerationService.performImageModeration(blog.getImage());
            if(imageResult.equals("0")){
                System.out.println("图片审核正常");
                blog.setPassStatus(1);//是否通过审核。0为待审核，1为通过审核，2为审核不通过
                //blogService.updateById(blog);
            }
            else{
                System.out.println("图片审核不正常");
                blog.setPassStatus(2);//是否通过审核。0为待审核，1为通过审核，2为审核不通过
                blogService.updateById(blog);
                continue;
            }
            //TODO 文本特征读取

            //图片特征提取
            blog.setImageLabels(detectImageLabelsService.detectImageLabels(blog.getImage()));
            blogService.updateById(blog);
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

    //在spring容器自动注入失败时,使用ApplicationContext表示主动获取bean对象
    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
    public static <T> T getBean(Class<T> clazz) {
        return context != null ? context.getBean(clazz) : null;
    }
}
