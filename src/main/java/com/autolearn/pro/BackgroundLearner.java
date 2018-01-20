package com.autolearn.pro;

import com.autolearn.pro.Log.DefaultLoggerFactory;
import com.autolearn.pro.http.HttpWebClient;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackgroundLearner {
    static final ExecutorService learner = Executors.newCachedThreadPool();

    static final ConcurrentHashMap<String,LearnState> learnStates= new ConcurrentHashMap<>();

    public static void addLearnTask(HttpWebClient webClient , String userID) {
        learner.execute(()-> {
            List<NameValuePair> formParams = new ArrayList<>();
            DefaultLoggerFactory.getDefaultLogger().info("user:" + userID + " try get course list!");
            String content = null;
            LearnState state =new LearnState(userID);
            BackgroundLearner.learnStates.put(userID,state);
            while(true) {
                try {
                    //获取学习列表
                    content = webClient
                            .pageGet("http://yuhang.learning.gov.cn/study/index.php?act=studycourselist");
                    DefaultLoggerFactory.getDefaultLogger().info("user:" + userID + " course list get!");
                    int index = content.indexOf("act=detail&courseid=");
                    if (index<0) break;//index=0,代表没有可以学习的课程，退出学习
                    //获取首堂课的id
                    String FirstCourseId = content.substring(index + 20, index + 30);
                    state.setCurrentCourseId(FirstCourseId);
                    DefaultLoggerFactory.getDefaultLogger().info("user:" + userID + " first course id:" + FirstCourseId);
                    //设置课程对话
                    formParams.add(new BasicNameValuePair("act", "set_course_session"));
                    formParams.add(new BasicNameValuePair("courseId", FirstCourseId));
                    formParams.add(new BasicNameValuePair("delay", "1200000"));
                    webClient.formAjaxPost("http://yuhang.learning.gov.cn/study/ajax.php", formParams);
                    //启动课程学习对话，获取logid
                    formParams.clear();
                    formParams.add(new BasicNameValuePair("act", "insert"));
                    formParams.add(new BasicNameValuePair("courseId", FirstCourseId));
                    String jsonstr = webClient.formAjaxPost("http://yuhang.learning.gov.cn/study/ajax.php", formParams);
                    JSONObject jsonObject = new JSONObject(jsonstr);
                    String logId = jsonObject.get("logId").toString();
                    DefaultLoggerFactory.getDefaultLogger().info("user:" +
                            userID + " begin study course id:" + FirstCourseId +
                            " logId:"+logId
                    );
                    //开始更新学习状态
                    formParams.clear();
                    formParams.add(new BasicNameValuePair("act", "update"));
                    formParams.add(new BasicNameValuePair("courseId", FirstCourseId));
                    formParams.add(new BasicNameValuePair("logId", logId));
                    while (true)
                    {
                        DefaultLoggerFactory.getDefaultLogger().info("user:" + userID + " update study state!");
                        String updateResult = webClient.formAjaxPost("http://yuhang.learning.gov.cn/study/ajax.php", formParams);
                        jsonObject = new JSONObject(updateResult);
                        if(jsonObject.get("err").toString().equals("1")) break;
                        try {
                            state.setLearnedTime(jsonObject.get("playTime").toString());
                            Thread.sleep(180000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    DefaultLoggerFactory.getDefaultLogger().info("user:" + userID + " finish course:"+FirstCourseId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            BackgroundLearner.learnStates.remove(userID);
        });
    }

    public static Collection<LearnState> getCurrentLearners()
    {
        return learnStates.values();
    }
    public static class LearnState
    {
        public LearnState(String username)
        {
            this.userName = username;
        }
        private String userName;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
        private String currentCourseId;

        public String getCurrentCourseId() {
            return currentCourseId;
        }

        public void setCurrentCourseId(String currentCourseId) {
            this.currentCourseId = currentCourseId;
        }
        private String learnedTime;

        public String getLearnedTime() {
            return learnedTime;
        }

        public void setLearnedTime(String learnedTime) {
            this.learnedTime = learnedTime;
        }
        @Override
        public String toString()
        {
            return String.format("user:%s studying course id:%s, has been study for %s minutes!",
                    userName,currentCourseId, learnedTime);
        }
    }
}
