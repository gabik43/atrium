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
    <title>Atrium. Редактирование регионов</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" type="text/css" href="<c:url value="/static/resources/css/region_edit.css"/>"/>
    <link rel="stylesheet" href="css/normalize.css">
    <link rel="stylesheet" href="css/main.css">
</head>
<body>
<div class = "header material_shadow">
    <img id = logo src="http://ru.tele2.ru/img/logo.png"></img>
</div>

<div class = "middle">
    <div class = "start-block">
        <div id = "description-text">Панель управления регионами</div>
    </div>

    <script>
        function onClickDeleteButton(obj){
            document.location.href = "./delete?id_in_db=" + obj.id;
        }
        function onClickUpdateButton(obj){
            document.location.href = "./update?id_in_db=" + obj.id +
                    "&id=" + document.getElementById("id_" + obj.id).innerHTML +
                    "&region_eng=" + document.getElementById("region_eng_" + obj.id).innerHTML +
                    "&region_rus=" + document.getElementById("region_rus_" + obj.id).innerHTML +
                    "&macro_region_eng=" + document.getElementById("macro_region_eng_" + obj.id).innerHTML +
                    "&macro_region_rus=" + document.getElementById("macro_region_rus_" + obj.id).innerHTML +
                    "&priority=" + document.getElementById("priority_" + obj.id).innerHTML +
                    "&email=" + document.getElementById("email_" + obj.id).innerHTML +
                    "&logo_id=" + document.getElementById("logo_id_" + obj.id).innerHTML +
                    "&status=" + document.getElementById("status_" + obj.id).innerHTML +
                    "&phone_b2c=" + document.getElementById("phone_b2c_" + obj.id).innerHTML +
                    "&phone_b2b=" + document.getElementById("phone_b2b_" + obj.id).innerHTML +
                    "&delivery_group_status=" + document.getElementById("delivery_group_status_" + obj.id).innerHTML;
        }
        function onClickAddButton(){
            document.location.href = "./add?" +
                    "id=" + document.getElementById("new_id").innerHTML +
                    "&region_eng=" + document.getElementById("new_region_eng").innerHTML +
                    "&region_rus=" + document.getElementById("new_region_rus").innerHTML +
                    "&macro_region_eng=" + document.getElementById("new_macro_region_eng").innerHTML +
                    "&macro_region_rus=" + document.getElementById("new_macro_region_rus").innerHTML +
                    "&priority=" + document.getElementById("new_priority").innerHTML +
                    "&email=" + document.getElementById("new_email").innerHTML +
                    "&logo_id=" + document.getElementById("new_logo_id").innerHTML +
                    "&status=" + document.getElementById("new_status").innerHTML +
                    "&phone_b2c=" + document.getElementById("new_phone_b2c").innerHTML +
                    "&phone_b2b=" + document.getElementById("new_phone_b2b").innerHTML +
                    "&delivery_group_status=" + document.getElementById("new_delivery_group_status").innerHTML;
        }
    </script>

    <table id="order-table">
        <thead>
        <tr class = "tr-head">
            <th align="left">&nbsp;№&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>
            <th align="left">&nbsp;ID&nbsp;региона&nbsp;&nbsp;</th>
            <th align="left">&nbsp;Регион</th>
            <th align="left">&nbsp;Регион(рус)</th>
            <th align="left">&nbsp;Макрорегион&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>
            <th align="left">&nbsp;Макрорегион(рус)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>
            <th align="left">&nbsp;Приоритет&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>
            <th align="left">&nbsp;E-mail</th>
            <th align="left">&nbsp;Логотип&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>
            <th align="left">&nbsp;Активность<br>&nbsp;региона</th>
            <th align="left"><nobr>&nbsp;Короткий номер</nobr><br><nobr>&nbsp;для клиентов B2C&nbsp;</nobr></th>
            <th align="left"><nobr>&nbsp;Короткий номер</nobr><br><nobr>&nbsp;для клиентов B2B&nbsp;</nobr></th>
            <th align="left">&nbsp;Активность<br><nobr>&nbsp;групп доставки&nbsp;</nobr></th>
            <th align="left"></th>
            <th align="left"></th>
        </tr>
        </thead>
        <tbody>

            <%
        List<RegionInfo> records = RegionInfoUtility.getAllRecords();
        int n=0;
        for (RegionInfo record : records){
          out.println("<tr class = \"tr-order\">" +
          "<td align=\"left\">" + ++n + "</td>" +
          "<td class=\"number_field\" id=\"id_" + record.idInDB + "\" align=\"left\">" + record.id + "</td>" +
          "<td class=\"changed_field\" id=\"region_eng_" + record.idInDB + "\" align=\"left\">" + record.regionEng + "</td>" +
          "<td class=\"changed_field\" id=\"region_rus_" + record.idInDB + "\" align=\"left\">" + record.regionRus + "</td>" +
          "<td class=\"changed_field\" id=\"macro_region_eng_" + record.idInDB + "\" align=\"left\">" + record.macroRegionEng + "</td>" +
          "<td class=\"changed_field\" id=\"macro_region_rus_" + record.idInDB + "\" align=\"left\">" + record.macroRegionRus + "</td>" +
          "<td class=\"number_field\" id=\"priority_" + record.idInDB + "\" align=\"left\">" + record.priority + "</td>" +
          "<td class=\"changed_field\" id=\"email_" + record.idInDB + "\" align=\"left\">" + record.email + "</td>" +
          "<td class=\"number_field\" id=\"logo_id_" + record.idInDB + "\" align=\"left\">" + record.logoId + "</td>" +
          "<td class=\"number_field\" id=\"status_" + record.idInDB + "\" align=\"left\">" + record.status + "</td>" +
          "<td class=\"changed_field\" id=\"phone_b2c_" + record.idInDB + "\" align=\"left\">" + record.phoneB2C + "</td>" +
          "<td class=\"changed_field\" id=\"phone_b2b_" + record.idInDB + "\" align=\"left\">" + record.phoneB2B + "</td>" +
          "<td class=\"changed_field\" id=\"delivery_group_status_" + record.idInDB + "\" align=\"left\">" + record.deliveryGroupStatus + "</td>" +
          "<td> <button id=\"" + record.idInDB + "\" onClick=\"onClickDeleteButton(this)\">" +
          "<div id=\"delete-button\" >Удалить</div>" + "</button> </td>" +
          "<td> <button id=\"" + record.idInDB + "\" onClick=\"onClickUpdateButton(this)\">" +
          "<div id=\"delete-button\" >Изменить</div>" + "</button>" + "</td>" +
          "</tr>");
        }
        out.println("<td align=\"left\"></td>" +
        "<td class=\"number_field\" id=\"new_id\" align=\"left\">0</td>" +
        "<td class=\"changed_field\" id=\"new_region_eng\" align=\"left\">-</td>" +
        "<td class=\"changed_field\" id=\"new_region_rus\" align=\"left\">-</td>" +
        "<td class=\"changed_field\" id=\"new_macro_region_eng\" align=\"left\">-</td>" +
        "<td class=\"changed_field\" id=\"new_macro_region_rus\" align=\"left\">-</td>" +
        "<td class=\"number_field\" id=\"new_priority\" align=\"left\">0</td>" +
        "<td class=\"changed_field\" id=\"new_email\" align=\"left\">-</td>" +
        "<td class=\"number_field\" id=\"new_logo_id\" align=\"left\">0</td>" +
        "<td class=\"number_field\" id=\"new_status\" align=\"left\">0</td>" +
        "<td class=\"changed_field\" id=\"new_phone_b2c\" align=\"left\">-</td>" +
        "<td class=\"changed_field\" id=\"new_phone_b2b\" align=\"left\">-</td>" +
        "<td class=\"changed_field\" id=\"new_delivery_group_status\" align=\"left\">-</td>" +
        "<td> <button onClick=\"onClickAddButton()\">" +
            "<div id=\"delete-button\" >Добавить</div>" + "</button>" + "</td>"
        );

    %>

        <script type="text/javascript">
            $(function()	{
                $(".changed_field").click(function(e)	{
                    //ловим элемент, по которому кликнули
                    var t = e.target || e.srcElement;
                    //получаем название тега
                    var elm_name = t.tagName.toLowerCase();
                    //если это инпут - ничего не делаем
                    if(elm_name == 'input')	{return false;}
                    var val = $(this).html();
                    var code = '<input type="text" id="edit" value="'+val+'" />';
                    $(this).empty().append(code);
                    $('#edit').focus();
                    $('#edit').blur(function()	{
                        var val = $(this).val();
                        $(this).parent().empty().html(val).css("color", "darkgoldenrod");
                    });
                });
            });
            $(function()	{
                $(".number_field").click(function(e)	{
                    //ловим элемент, по которому кликнули
                    var t = e.target || e.srcElement;
                    //получаем название тега
                    var elm_name = t.tagName.toLowerCase();
                    //если это инпут - ничего не делаем
                    if(elm_name == 'input')	{return false;}
                    var val = $(this).html();
                    var code = '<input type="number" id="edit" value="'+val+'" />';
                    $(this).empty().append(code);
                    $('#edit').focus();
                    $('#edit').blur(function()	{
                        var val = $(this).val();
                        $(this).parent().empty().html(val).css("color", "darkgoldenrod");
                    });
                });
            });
            $(window).keydown(function(event){
                //ловим событие нажатия клавиши
                if(event.keyCode == 13) {	//если это Enter
                    $('#edit').blur();	//снимаем фокус с поля ввода
                }
            });
        </script>

    </table>

</div>

</body>
</html>