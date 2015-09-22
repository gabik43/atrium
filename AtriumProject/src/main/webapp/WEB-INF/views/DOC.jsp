<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html><head>
    <link href="//fonts.googleapis.com/css?family=Roboto:100italic,100,300italic,300,400italic,400,500italic,500,700italic,700,900italic,900" rel="stylesheet" type="text/css">
    <meta charset="utf-8">
    <meta http-equiv="x-ua-compatible" content="ie=edge">
    <title>Работа с документами</title>
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" type="text/css" href="<c:url value="/static/resources/css/screen.css"/>"/>
    <link rel="stylesheet" href="css/normalize.css">
    <link rel="stylesheet" href="css/main.css">
    <SCRIPT src="http://java.com/js/dtjava.js"></SCRIPT>
    <script>
        function launchApplication(jnlpfile) {
            dtjava.launch(            {
                        url : 'href="<c:url value="/static/resources/JavaFXApp.jnlp"/>"'
                    },
                    {
                        javafx : '2.2+'
                    },
                    {}
            );
            return false;
        }
    </script>

    <script>
        function javafxEmbed_JavaFXApp_id() {
            dtjava.embed(
                    {
                        id : 'JavaFXApp_id',
                        url : href="<c:url value="/static/resources/JavaFXApp.jnlp"/>",
                        placeholder : 'javafx-app-placeholder',
                        width : '100%',
                        height : '100%'
                    },
                    {
                        javafx : '2.2+'
                    },
                    {}
            );
        }
        <!-- Embed FX application into web page once page is loaded -->
        dtjava.addOnloadCallback(javafxEmbed_JavaFXApp_id);
    </script>

</head><body bgcolor="#8B8484">
<div class = "header material_shadow">
    <img id = logo src="http://ru.tele2.ru/img/logo.png"></img>
</div>

<div class = "middle">
    <div class = "start-block">
        <div class="fx" id='javafx-app-placeholder'></div>
    </div>
</div>
</body></html>
