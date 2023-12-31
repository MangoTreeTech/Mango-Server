package com.mango.schedule;

import com.mango.config.Constant;
import com.qcloud.cos.model.ciModel.auditing.BatchImageAuditingInputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.tiia.v20190529.TiiaClient;
import com.tencentcloudapi.tiia.v20190529.models.*;
@Service
public class DetectImageLabelsService {

    @Autowired
    private Constant constant;

    public String detectImageLabels(String image) throws IOException, TencentCloudSDKException {
        // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
        // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
        // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
        Credential cred = new Credential(constant.secretId, constant.secretKey);
        // 实例化一个http选项，可选的，没有特殊需求可以跳过
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint("tiia.tencentcloudapi.com");
        // 实例化一个client选项，可选的，没有特殊需求可以跳过
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        // 实例化要请求产品的client对象,clientProfile是可选的
        TiiaClient client = new TiiaClient(cred, "ap-beijing", clientProfile);
        // 去除字符串两端的方括号并按照逗号和空格分割成字符串数组
        String[] fileNames = image.replaceAll("[\\[\\]]", "").split("\\s*,\\s*");
        List<String> respList = new ArrayList<>();
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
            // 实例化一个请求对象,每个接口都会对应一个request对象
            DetectLabelProRequest req = new DetectLabelProRequest();
            req.setImageBase64(encodedString);
            // 返回的resp是一个DetectLabelProResponse的实例，与请求对象对应
            DetectLabelProResponse resp = client.DetectLabelPro(req);
            // 输出json格式的字符串回包
            System.out.println(DetectLabelProResponse.toJsonString(resp));
            respList.add(DetectLabelProResponse.toJsonString(resp));
        }
        return respList.toString();
    }
}
