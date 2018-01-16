package com.autolearn.pro.controller;

import com.autolearn.pro.http.HttpWebClient;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
public class LoginController {
    @RequestMapping(value="/login/",method = RequestMethod.GET)
    public String login(HttpSession httpSession){
        HttpWebClient webclient = new HttpWebClient();
        try {
            HttpClient client = webclient.clientPost("http://yuhang.learning.gov.cn/study/login.php");
            httpSession.setAttribute("client",webclient);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "login";
    }
    @RequestMapping(value="/login/",method = RequestMethod.POST)
    public  String login(String userID,String pwd,String authKey ,HttpSession httpSession)
    {
        HttpWebClient webclient = (HttpWebClient) httpSession.getAttribute("client");
        try {
            webclient.loginPost("http://yuhang.learning.gov.cn/study/login.php",
                    userID,pwd,authKey);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "result";
    }
}
