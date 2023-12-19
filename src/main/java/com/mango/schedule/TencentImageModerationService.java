package com.mango.schedule;

import com.mango.config.Constant;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.ciModel.auditing.BatchImageAuditingInputObject;
import com.qcloud.cos.model.ciModel.auditing.BatchImageAuditingRequest;
import com.qcloud.cos.model.ciModel.auditing.BatchImageAuditingResponse;
import com.qcloud.cos.model.ciModel.auditing.BatchImageJobDetail;
import com.qcloud.cos.region.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

@Service
public class TencentImageModerationService {
    @Autowired
    private Constant constant;

    public String performImageModeration(String image) throws IOException {//image需要做处理，可能包含多个图片路径
        // 初始化认证信息
        COSCredentials cred = new BasicCOSCredentials(constant.secretId, constant.secretKey);

        // 2 设置 bucket 的区域, COS 地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
        // clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分。
        Region region = new Region("ap-nanjing");
        ClientConfig clientConfig = new ClientConfig(region);
        // 3 生成 cos 客户端。
        COSClient client = new COSClient(cred, clientConfig);

        //1.创建任务请求对象
        BatchImageAuditingRequest request = new BatchImageAuditingRequest();
        //2.添加请求参数 参数详情请见 API 接口文档
        //2.1设置请求bucket
        request.setBucketName(constant.bucketName);
        //2.2添加请求内容
        List<BatchImageAuditingInputObject> inputList = request.getInputList();

        // 去除字符串两端的方括号并按照逗号和空格分割成字符串数组
        String[] fileNames = image.replaceAll("[\\[\\]]", "").split("\\s*,\\s*");

        for (String fileName : fileNames) {
            //读取图片
            String filePath = constant.dir + "/" + fileName; // 获取文件路径
            // 处理文件读取异常，比如记录日志或者跳过该文件的处理
            // 读取文件内容并添加到列表中
            byte[] fileContent = Files.readAllBytes(Path.of(filePath));
            // 使用Base64编码转换为字符串
            String encodedString = Base64.getEncoder().encodeToString(fileContent);

            BatchImageAuditingInputObject input = new BatchImageAuditingInputObject();
            input.setDataId(fileName);
            input.setContent(encodedString);
            inputList.add(input);
        }
        //2.2设置审核类型
        request.getConf().setDetectType("all");
        //request.setInputList(inputList);
        //3.调用接口,获取任务响应对象
        BatchImageAuditingResponse response = client.batchImageAuditing(request);
        List<BatchImageJobDetail> jobList = response.getJobList();
        //有更简单的方法实现result的判断，但是在可读性和性能间，我选择可读性，语法糖是坏文明  by wang
        for (BatchImageJobDetail jobDetail: jobList) {
            //该字段表示本次判定的审核结果，您可以根据该结果，进行后续的操作；建议您按照业务所需，对不同的审核结果进行相应处理。
            // 有效值：0（审核正常），1 （判定为违规敏感文件），2（疑似敏感，建议人工复核）。
            if(!jobDetail.getResult().equals("0")){
                // 关闭客户端(关闭后台线程)
                client.shutdown();
                return jobDetail.getResult();
            }
        }
        // 关闭客户端(关闭后台线程)
        client.shutdown();
        return "0";
    }
}
