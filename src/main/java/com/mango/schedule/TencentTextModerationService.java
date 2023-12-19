package com.mango.schedule;

import com.mango.config.Constant;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.ciModel.auditing.TextAuditingRequest;
import com.qcloud.cos.model.ciModel.auditing.TextAuditingResponse;
import com.qcloud.cos.model.ciModel.bucket.MediaBucketRequest;
import com.qcloud.cos.model.ciModel.bucket.MediaBucketResponse;
import com.qcloud.cos.region.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TencentTextModerationService {
    @Autowired
    private Constant constant;

    public String performTextModeration(String text) {
        // 初始化认证信息
        COSCredentials cred = new BasicCOSCredentials(constant.secretId, constant.secretKey);

        // 2 设置 bucket 的区域, COS 地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
        // clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分。
        Region region = new Region("ap-nanjing");
        ClientConfig clientConfig = new ClientConfig(region);
        // 3 生成 cos 客户端。
        COSClient client = new COSClient(cred, clientConfig);

        //请求示例，提交一个文本审核任务
        //1.创建任务请求对象
        TextAuditingRequest request = new TextAuditingRequest();
        //2.添加请求参数 参数详情请见 API 接口文档
        request.setBucketName(constant.bucketName);
        //2.1.1设置请求内容,文本内容的Base64编码
        // 将字符串转换为 Base64
        String encodedString = Base64.getEncoder().encodeToString(text.getBytes());
        request.getInput().setContent(encodedString);
        //3.调用接口,获取任务响应对象
        TextAuditingResponse response = client.createAuditingTextJobs(request);

        //该字段表示本次判定的审核结果，您可以根据该结果，进行后续的操作；建议您按照业务所需，对不同的审核结果进行相应处理。
        // 有效值：0（审核正常），1 （判定为违规敏感文件），2（疑似敏感，建议人工复核）。
        String result = response.getJobsDetail().getResult();

        // 关闭客户端(关闭后台线程)
        client.shutdown();
        // 处理审核结果
        return result; //根据实际需求获取相应的结果信息
    }
}
