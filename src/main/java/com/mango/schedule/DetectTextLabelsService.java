package com.mango.schedule;

import com.mango.config.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DetectTextLabelsService {

    @Autowired
    private Constant constant;

    public String detectTextLabels(String text){

         /*//python程序测试
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
            System.out.println("Response: " + response.toString());*/

        return text;
    }
}
