<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page import = "ru.asteros.atrium.DB.*" %>
<%@ page import = "ru.asteros.atrium.*" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>

<html class="no-js" lang="">
<head>
    <link href="//fonts.googleapis.com/css?family=Roboto:100italic,100,300italic,300,400italic,400,500italic,500,700italic,700,900italic,900" rel="stylesheet" type="text/css">
    <meta charset="utf-8">
    <meta http-equiv="x-ua-compatible" content="ie=edge">
    <title>Atrium. Панель управления</title>
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" type="text/css" href="<c:url value="/static/resources/css/screen.css"/>"/>
    <link rel="stylesheet" href="css/normalize.css">
    <link rel="stylesheet" href="css/main.css">
</head>
<body>
<div class = "header material_shadow">
    <img id = logo src="http://ru.tele2.ru/img/logo.png"></img>
</div>

<div class = "middle">
    <div class = "start-block">
        <div id = "description-text">Панель заказа документов</div>
        <a rel="START" href="./start">
            <div id = "order-button" class = "material_shadow">ЗАКАЗАТЬ</div>
        </a>
        <div class = "addin-block">
            <a rel="REFRESH" href="./refresh">
                <div id = "refresh-button" class = "material_shadow">ОБНОВИТЬ</div>
            </a>
            <a rel="START" href="./clean">
                <div id = "clean-button" class = "material_shadow">ОЧИСТИТЬ</div>
            </a>
        </div>
        <div id = "version-text">0.1 alpha</div>
    </div>

    <table id="order-table">
        <thead>
        <tr class = "tr-head">
            <th>ID-заказа</th>
            <th>Наименование</th>
            <th>Статус</th>
            <th>Время создания</th>
        </tr>
        </thead>
        <tbody>

    <%
        List<Map<String, Object>> orderList = new ArrayList<Map<String, Object>>();
        orderList = OrderDB.GetOrderList();
        for (Map<String, Object> orderData : orderList)
        {
            //Заказ
            String orderId = orderData.get("id").toString();
            String orderName = "Заказ №" + orderData.get("id").toString();
            String orderLink = "http://ya.ru";
            String orderStatus = orderData.get("stats").toString();
            String orderTime;
            try{
                orderTime = orderData.get("createTime").toString();
                orderTime = orderTime.substring(0, orderTime.indexOf('.')); // небезопастно
            } catch(Exception e){
                orderTime = "";
            }
            out.println("<tr class = \"tr-order\"> <td> " + orderId + "</td>" + "<td> " + orderName + "</td>" + "<td>" +  orderStatus + "</td>" + "<td>" + orderTime + "</td> </tr>");

            //Подзаказы
            List<Map<String, Object>> subOrderList = SubOrderDB.GetSuborderList(Long.parseLong(orderId));
            for (Map<String, Object> subOrderData : subOrderList)
            {
                String subOrderId = subOrderData.get("id").toString();
                String subOrderName = "Подзаказ №" + subOrderData.get("id").toString() + " " + subOrderData.get("region").toString();
                String subOrderLink = "http://ya.ru";
                String subOrderStatus = subOrderData.get("statsMsg").toString();
                String subOrderTime;
                try{
                    subOrderTime = subOrderData.get("startTime").toString();
                    subOrderTime = subOrderTime.substring(0, subOrderTime.indexOf('.')); // небезопастно
                } catch(Exception e){
                    subOrderTime = "";
                }
                out.println("<tr class = \"tr-suborder\"> <td> " + subOrderId + "</td>" + "<td> " + subOrderName + "</td>" + "<td>"  + subOrderStatus + "</td>" + "<td>" + subOrderTime + "</td> </tr>");
            }
        }
    %>
    </table>

</div>

<div class = "footer material_shadow">

</div>
</body>
</html>