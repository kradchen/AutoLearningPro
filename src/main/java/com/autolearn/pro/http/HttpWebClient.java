package com.autolearn.pro.http;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ResponseCache;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


public class HttpWebClient {

    private CloseableHttpClient httpclient;

    void post() {
        HttpURLConnection connection = null;
        URL url;
        try {
            url = new URL("");

            connection = (HttpURLConnection) url.openConnection();
            /**
             * 然后把连接设为输出模式。URLConnection通常作为输入来使用，比如下载一个Web页。
             * 通过把URLConnection设为输出，你可以把数据向你个Web页传送。下面是如何做：
             */
            connection.setDoOutput(true);  //打开输出，向服务器输出参数（POST方式、字节）（写参数之前应先将参数的字节长度设置到配置"Content-Length"<字节长度>）
            connection.setDoInput(true);//打开输入，从服务器读取返回数据
            connection.setRequestMethod("POST"); //设置登录模式为POST（字符串大写）
            connection.setInstanceFollowRedirects(false);
            connection.connect();
            /**
             * 最后，为了得到OutputStream，简单起见，把它约束在Writer并且放入POST信息中，例如： ...
             */
            OutputStreamWriter out = new OutputStreamWriter(connection
                    .getOutputStream(), "utf-8");
            //其中的loginName和loginPassword也是阅读html代码得知的，即为表单中对应的参数名称
            out.write("loginName=admin&loginPassword=*****&validateCode=autoupdate"); // post的关键所在！
            //remember to clean up
            out.flush();
            out.close();

        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }


    public HttpClient clientPost(String url) throws Exception {
        HttpGet httpget = new HttpGet(url);
        CloseableHttpResponse response = null;
        CookieStore cookieStore = new BasicCookieStore();
        LaxRedirectStrategy redirectStrategy = new LaxRedirectStrategy();

        httpclient = HttpClients.custom()
                .setRedirectStrategy(redirectStrategy)
                .setDefaultCookieStore(cookieStore)
                .build();
        try {
            response = httpclient.execute(httpget);
            httpget = new HttpGet("http://www.learning.gov.cn/system/akey_img.php?" + Math.random());
            response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            String classpath = Thread.currentThread()
                    .getContextClassLoader()
                    .getResource("../").toString();
            String fileDirPath =  classpath.substring(5);
            writeImageToDisk(readInputStream(stream),fileDirPath);
        }
        finally {
            response.close();
        }
        return httpclient;
    }
    public boolean loginPost(String url ,String username,String pwd,String authKey) throws IOException {
        boolean result =false;
        HttpPost post = new HttpPost(url);
        List<NameValuePair> formParams = new ArrayList<>();
        formParams.add(new BasicNameValuePair("username",username));
        formParams.add(new BasicNameValuePair("password",pwd));
        formParams.add(new BasicNameValuePair("authKey",authKey));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formParams, Consts.UTF_8);
        post.setEntity(entity);
        CloseableHttpResponse response = httpclient.execute(post);
        int code = response.getStatusLine().getStatusCode();
        String content = EntityUtils.toString(response.getEntity());
        HttpGet get = new HttpGet("http://yuhang.learning.gov.cn/study/");
        response = httpclient.execute(get);
        content = EntityUtils.toString(response.getEntity());
        return  true;
    }
    byte[] readInputStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while( (len=inStream.read(buffer)) != -1 ){
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }
    void writeImageToDisk(byte[] img, String fileName){
        try {
            String path = fileName+"images/";
            File file = new File(path);
            if (!file.exists() && file.isDirectory()) file.mkdir();
            path = path + "temp.png";
            file = new File(path);
            if (!file.exists())
            {
                file.createNewFile();
            }
            FileOutputStream fops = new FileOutputStream(file);
            fops.write(img);
            fops.flush();
            fops.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
