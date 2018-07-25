<%--
  Created by IntelliJ IDEA.
  User: u
  Date: 2018/7/23
  Time: 9:38
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    订单号是：${orderid}<br>
    <img src="/weixinzhifu/imageservlet">
<span id="result"></span>
</body>
<script type="text/javascript">

    window.onload=connect();
    var websocket=null;
    function  connect() {

        var ordierid=document.getElementById("ordierid").innerHTML;//需要判断接收者
        //先判断浏览器是否支持websocket
        if ('WebSocket' in window) {
            websocket=new WebSocket("ws://"+document.location.host+"/weixinzhifu/websocket/"+ordierid);
        }else{
            alert("世界上最遥远的距离是就是我在websocket这一端,而你没有websocket");
        }
        if (websocket) {


            websocket.onerror=function () {
                setMessage('出现错误');
            }

            websocket.onopen=function () {
                setMessage('建立链接');
            }
            websocket.onclose=function () {
                setMessage('关闭连接');
            }
            websocket.onmessage=function (event) {
                setMessage(event.data);
            }
            window.onbeforeunload=function () {
                if (websocket) {
                    websocket.close();
                }
            }
        }

    }

    var span = document.getElementById("result");
    function  setMessage(message) {
        span.innerHTML=message;
    }

</script>
</html>
