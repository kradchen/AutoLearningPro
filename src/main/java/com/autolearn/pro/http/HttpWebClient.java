package com.autolearn.pro.http;

import java.awt.image.BufferedImage;
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

import javax.imageio.ImageIO;


public class HttpWebClient {

    public HttpWebClient() {
        CookieStore cookieStore = new BasicCookieStore();
        LaxRedirectStrategy redirectStrategy = new LaxRedirectStrategy();
        httpclient = HttpClients.custom()
                .setRedirectStrategy(redirectStrategy)
                .setDefaultCookieStore(cookieStore)
                .build();
    }

    public void close() throws IOException {
        httpclient.close();
    }

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

    public String pageGet(String pageUrl) throws IOException {
        HttpGet httpget = new HttpGet(pageUrl);
        CloseableHttpResponse response = httpclient.execute(httpget);
        int code = response.getStatusLine().getStatusCode();
        if (code == 200) {
            String content = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
            response.close();
            return content;
        } else {
            return "";
        }
    }

    public Boolean resourceGet(String resUrl, String savePath, String fileName) throws Exception {
        Boolean result = false;
        HttpGet httpget = new HttpGet(resUrl);
        CloseableHttpResponse response = null;
        try {
            System.out.println("begin get  resource url:" + resUrl);
            response = httpclient.execute(httpget);
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                result = true;
                HttpEntity entity = response.getEntity();
                InputStream stream = entity.getContent();
                System.out.println("saving image!");
                writeImageToDisk(readInputStream(stream), savePath, fileName);
                System.out.println("Image saved!");
            }

        } finally {
            response.close();
        }
        return result;
    }

    public BufferedImage resourceGet(String resUrl) throws Exception {
        HttpGet httpget = new HttpGet(resUrl);
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpget);
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                HttpEntity entity = response.getEntity();
                InputStream stream = entity.getContent();
                //直接用URL获取图片
                BufferedImage image = ImageIO.read(stream);
                return image;
            }

        } finally {
            response.close();
        }
        return null;
    }

    public Boolean formPost(String url, List<NameValuePair> params) throws IOException {
        Boolean result = false;
        HttpPost post = new HttpPost(url);

        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, Consts.UTF_8);
        post.setEntity(entity);
        System.out.println("do post!");
        CloseableHttpResponse response = httpclient.execute(post);
        int code = response.getStatusLine().getStatusCode();
        if (code == 200) {
            result = true;
        }
        return result;
    }

    public String formAjaxPost(String url, List<NameValuePair> params) throws IOException
    {

        HttpPost post = new HttpPost(url);
        post.addHeader("X-Requested-With","XMLHttpRequest");
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, Consts.UTF_8);
        post.setEntity(entity);
        System.out.println("do post!");
        CloseableHttpResponse response = httpclient.execute(post);
        int code = response.getStatusLine().getStatusCode();
        if (code == 200) {
            String content = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
            System.out.println("ajax content:");
            System.out.println(content);
            return content;
        }
        return null;
    }

    private byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }

    private void writeImageToDisk(byte[] img, String path, String filename) {
        try {
            File file = new File(path);
            if (!file.exists() && file.isDirectory()) file.mkdir();
            String fullpath = path + filename;
            file = new File(fullpath);
            if (!file.exists()) {
                file.createNewFile();
            } else {
                file.delete();
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
