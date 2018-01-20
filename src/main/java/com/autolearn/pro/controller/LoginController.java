package com.autolearn.pro.controller;

import com.autolearn.pro.BackgroundLearner;
import com.autolearn.pro.Log.DefaultLoggerFactory;
import com.autolearn.pro.http.HttpWebClient;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class LoginController {
    @RequestMapping(value="/login/",method = RequestMethod.GET)
    public String login(HttpSession httpSession){
        HttpWebClient webClient = new HttpWebClient();
        try {
            String result = webClient.pageGet("http://yuhang.learning.gov.cn/study/login.php");
            if(result.length()>0) {
                httpSession.setAttribute("client", webClient);
                //暂时先不管失败怎么办
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "login";
    }
    @RequestMapping(value="/login/",method = RequestMethod.POST)
    public  String login(String userID,String pwd,String authKey ,HttpSession httpSession)
    {
        HttpWebClient webClient = (HttpWebClient) httpSession.getAttribute("client");
        try {
            List<NameValuePair> formParams = new ArrayList<>();
            formParams.add(new BasicNameValuePair("username", userID));
            formParams.add(new BasicNameValuePair("password", pwd));
            formParams.add(new BasicNameValuePair("authKey", authKey));
            //login
            DefaultLoggerFactory.getDefaultLogger().info("user:" + userID + " do login!");
            String content = webClient.formPost("http://yuhang.learning.gov.cn/study/login.php",
                    formParams);
            if (content.indexOf("学员登录")>0) return "fail";
            BackgroundLearner.addLearnTask(webClient,userID);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "success";
    }
}
