<%--
  Created by IntelliJ IDEA.
  User: krad
  Date: 2018/1/14
  Time: 上午8:10
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
    <link rel='stylesheet' href='/stylesheets/style.css' />
</head>
<body>
<form  action="/autolearnpro/login/" method="post" >
    <label >身份证号：</label>
    <input type="text" name="userID" class="form-control" placeholder="身份证号" required autofocus>
    <label >密码：</label>
    <input type="password" id="pwd" name="pwd" class="form-control" placeholder="密码" required>
    <input type="text" id="authKey" name="authKey" placeholder="验证码" required >
    <img style="vertical-align: middle;" height="23" width="80" id="authKeyImag" src="../images/temp.png"   >
    <button type="submit">登录</button>
</form>
</body>
</html>