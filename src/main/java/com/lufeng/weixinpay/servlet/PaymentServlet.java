package com.lufeng.weixinpay.servlet;

import com.lufeng.weixinpay.util.PayCommonUtil;
import com.lufeng.weixinpay.util.ZxingUtil;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

/**
 * Created by LUFENG.
 */
//@WebServlet(urlPatterns = "weixinzhifu/paymentservlet")
public class PaymentServlet extends HttpServlet {

    private Random random = new Random();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //获取要购买商品的名字，实际开发中用户传递过来的是商品的id，从服务器内部获取信息
        request.setCharacterEncoding("utf-8");
        String body = request.getParameter("body");
        //价格默认1分
        String price  = "1";
        ///牵扯到tomcat版本问题，所以要注意字符集编码问题
        String serverInfo = request.getServletContext().getServerInfo();
        System.out.println(serverInfo);           //版本7需要注意 ，8不需
        int orderid = random.nextInt(1000000000);
        //将请求参数封装好并按照要求排序签名
        try {
            //通过网络请求 请求微信下单服务器，获取二维码的字符串
            String url = PayCommonUtil.weixin_pay(price, body, orderid+"");
            //然后将二维码字符串转换成图片并放到session中
            BufferedImage image = ZxingUtil.createImage(url, 300, 300);
            request.getSession().setAttribute("orderid",orderid);
            request.getSession().setAttribute("image",image);
            //跳转页面
            response.sendRedirect("/pay.jsp");
        } catch (Exception e) {
            
            e.printStackTrace();
        }
    }
}
