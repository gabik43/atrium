<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html><head>
    <meta charset="utf-8">
    <meta http-equiv="x-ua-compatible" content="ie=edge">
    <title>Работа с документами</title>
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" type="text/css" href="<c:url value="/static/resources/css/screen.css"/>"/>
    <link rel="stylesheet" href="css/normalize.css">
    <link rel="stylesheet" href="css/main.css">
    <SCRIPT src="<c:url value="/static/resources/css/dtjava.js"/>"></SCRIPT>
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
                        height : '80%'
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
</div>

<div class = "middle">
    <div class = "start-block">
        <div class="fx" id='javafx-app-placeholder'></div>
    </div>
</div>
</body></html>
