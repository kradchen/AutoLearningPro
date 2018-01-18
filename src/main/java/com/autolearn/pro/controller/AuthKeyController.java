package com.autolearn.pro.controller;

import com.autolearn.pro.http.HttpWebClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;


@Controller
public class AuthKeyController {
    @RequestMapping("/authkey/")
    public void getAuthCodeImg(HttpServletResponse response, HttpSession httpSession)
            throws Exception
    {
        System.out.println("authkey excute!");
        HttpWebClient webClient = (HttpWebClient) httpSession.getAttribute("client");
        //设置response头信息
        //禁止缓存
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        BufferedImage image = webClient.resourceGet("http://www.learning.gov.cn/system/akey_img.php?" + Math.random());
        //写入输出流
        ImageIO.write(image, "PNG", response.getOutputStream());
        //输出所有缓存
        response.getOutputStream().flush();
    }
}
