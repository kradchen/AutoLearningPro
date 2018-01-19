package com.autolearn.pro;

import com.autolearn.pro.Log.DefaultLoggerFactory;
import com.autolearn.pro.http.HttpWebClient;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackgroundLearner {
    static final ExecutorService learner = Executors.newCachedThreadPool();
    public static void addLearnTask(HttpWebClient webClient , String userID) {
        learner.execute(()-> {
            List<NameValuePair> formParams = new ArrayList<>();
            DefaultLoggerFactory.getDefaultLogger().info("user:" + userID + " try get course list!");
            String content = null;
            while(true) {
                try {
                    content = webClient
                            .pageGet("http://yuhang.learning.gov.cn/study/index.php?act=studycourselist");
                    DefaultLoggerFactory.getDefaultLogger().info("user:" + userID + " course list get!");
                    int index = content.indexOf("act=detail&courseid=");
                    if (index<0) return;//index=0,代表没有可以学习的课程，退出学习
                    String FirstCourseId = content.substring(index + 20, index + 30);
                    DefaultLoggerFactory.getDefaultLogger().info("user:" + userID + " first course id:" + FirstCourseId);
                    formParams.add(new BasicNameValuePair("act", "set_course_session"));
                    formParams.add(new BasicNameValuePair("courseId", FirstCourseId));
                    formParams.add(new BasicNameValuePair("delay", "1200000"));
                    webClient.formAjaxPost("http://yuhang.learning.gov.cn/study/ajax.php", formParams);
                    formParams.clear();
                    formParams.add(new BasicNameValuePair("act", "insert"));
                    formParams.add(new BasicNameValuePair("courseId", FirstCourseId));
                    String jsonstr = webClient.formAjaxPost("http://yuhang.learning.gov.cn/study/ajax.php", formParams);
                    JSONObject jsonObject = new JSONObject(jsonstr);
                    String logid = jsonObject.get("logId").toString();
                    formParams.clear();
                    formParams.add(new BasicNameValuePair("act", "update"));
                    formParams.add(new BasicNameValuePair("courseId", FirstCourseId));
                    formParams.add(new BasicNameValuePair("logId", logid));
                    while (true)
                    {
                        String updateResult = webClient.formAjaxPost("http://yuhang.learning.gov.cn/study/ajax.php", formParams);
                        jsonObject = new JSONObject(updateResult);
                        if(jsonObject.get("err ").toString().equals("1")) break;
                        try {
                            Thread.sleep(600000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    DefaultLoggerFactory.getDefaultLogger().info("user:" + userID + " finish course:"+FirstCourseId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
