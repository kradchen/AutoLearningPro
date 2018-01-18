package com.autolearn.pro.controller;

import com.autolearn.pro.http.HttpWebClient;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
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
        HttpWebClient webclient = new HttpWebClient();
        try {
            String result = webclient.pageGet("http://yuhang.learning.gov.cn/study/login.php");
            if(result.length()>0) {
                httpSession.setAttribute("client", webclient);
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
            System.out.println("user:" + userID + " do login!");
            webClient.formPost("http://yuhang.learning.gov.cn/study/login.php",
                    formParams);
            System.out.println("user:" + userID + " try get course list!");
            String content = webClient
                    .pageGet("http://yuhang.learning.gov.cn/study/index.php?act=studycourselist");
            System.out.println("user:" + userID + " course list get!");
            System.out.println("content:");
            System.out.println(content);
            int index = content.indexOf("act=detail&courseid=");
            String FirstCourseId = content.substring(index + 20, index + 30);
            System.out.println("user:" + userID + " first course id:" + FirstCourseId);
            formParams = new ArrayList<>();
            formParams.add(new BasicNameValuePair("act", "set_course_session"));
            formParams.add(new BasicNameValuePair("courseId", FirstCourseId));
            formParams.add(new BasicNameValuePair("delay", "1200000"));
            webClient.formAjaxPost("http://yuhang.learning.gov.cn/study/ajax.php", formParams);
            formParams = new ArrayList<>();
            formParams.add(new BasicNameValuePair("act", "insert"));
            formParams.add(new BasicNameValuePair("courseId", FirstCourseId));
            String jsonstr = webClient.formAjaxPost("http://yuhang.learning.gov.cn/study/ajax.php", formParams);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "result";
    }
}
