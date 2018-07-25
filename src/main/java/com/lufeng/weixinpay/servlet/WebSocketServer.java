package com.lufeng.weixinpay.servlet;

import net.sf.json.JSONObject;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LUFENG.
 */

/******
 * 此对象是一个多例对象，请求一次创建一次
 * name 用于区分链接
 */
@ServerEndpoint("/websocket/{orderid}")//websocket的请求地址
public class WebSocketServer {
    private String name;//当前链接的标记
    private Session session;//当前链接的链接对象,用于通信用的,也就是长连接对象
    //将每个链接存起来,在使用的时候进行查找获取
    private static Map<String, Session> allClients = new ConcurrentHashMap<>();

    public WebSocketServer() {
        System.out.println("构造方法执行了,有人进来了");
    }

    /**
     * 当有人和服务器建立链接的时候执行
     * @param name 用户建立链接的时候传递过来的标记
     * @param session 当前用户的链接
     */
    @OnOpen
    public void onOpen(@PathParam("orderid") String name, Session session) {
        this.name = name;
        this.session = session;
        allClients.put(name, session);
    }

    /**
     * 服务端收到消息了
     * @param session 代表哪个会话发送的消息,也就是哪个客户端
     * @param message 发送的消息内容
     *
     * 此方法是websocket提供的用于告诉我们有消息发过来了的监听,实际要做什么需要我们自己的业务逻辑来决定
     *
     * 一般情况下 我们可能是双方进通信,所以此处应该需要告诉我们接收方是谁,然后我们的服务端将消息转到接收方那边
     *  定制规范:发送过来的消息格式json,里面有一个值是接收者是谁
     *
     */
    @OnMessage
    public void onMessage(Session session,String message) {
        ///==============实际此处以自己的业务为准========
        ///获取消息内容
        JSONObject jsonObject= JSONObject.fromObject(message);
        //解析消息
        //找到接收者
        String toName = jsonObject.getString("toName");//接收者
        String content = jsonObject.getString("content");//发送的真正内容
        //发送消息给它
        sendMessage(toName,"来自"+name+"的消息,内容是:  "+content);
    }

    private void sendMessage(String toName, String s) {
        //通过接收者的名字找到它的session
        Session toSession = allClients.get(toName);
        if (toSession != null) {//有接收者
            toSession.getAsyncRemote().sendText(s);
            return;
        }
        //接收者不在线
        ///=========取决于业务逻辑决定=====
        //有的人可能需要将消息存起来,下次这个人上来后再发给他
        //此处的逻辑是如果没有链接就提示发送者,对方不在线
        session.getAsyncRemote().sendText("对方不在线,请稍后再试");
    }


    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
        if (session.isOpen()) {
            try {
                session.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            allClients.remove(name);
        }
    }
    @OnClose
    public void onClose(Session session) {
        allClients.remove(name);//移除当前链接
        if (session.isOpen()) {
            try {
                session.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 给订单系统发送消息用的
     * @param orderid
     * @param message
     */
    public static void sendResult(String orderid, String message) {
        Session session = allClients.get(orderid);
        if (session != null) {//有接收者
            session.getAsyncRemote().sendText(message);
            return;
        }
    }

}

