package ru.asteros.atrium.soap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.asteros.atrium.AppConfiguration;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Andrey.A.Koshkin on 15.09.2015.
 */
public class SOAPHandler {

    private static Logger log = LoggerFactory.getLogger(SOAPHandler.class);


    /**
     * Возвращает Soap-сообщение по XML, имени шаблона и формату для заказа отчёта
     *
     * @param dataSourceXML XML с данными
     * @param templateName  Имя шаблона
     * @param format        Формат шаблона
     * @return Soap-сообщение
     */
    public static String GetSoapMessage(String dataSourceXML, String templateName, String format) {
        log.trace("Call method GetSoapMessage("+dataSourceXML+","+templateName+","+format+")");
        try {
            return CreateEnvelope(dataSourceXML, templateName, format + " to Caller");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error while formatting SOAP-message for request ", e);
        }
    }

    /**
     * Формирует Envelope часть SOAP-сообщения
     * @param dataSource    Текст датасорса
     * @param documentName  Имя документа в xPression
     * @param outputProfile Имя output-профайла xPression
     * @return Строку содержащую Envelope чать SOAP-сообщения
     */
    public static String CreateEnvelope(String dataSource, String documentName, String outputProfile) {
        String soapUserName = AppConfiguration.get("XPRESSION_USER");
        String soapUserPassword = AppConfiguration.get("XPRESSION_PASSWORD");
        String header = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.framework.xprs.dsc.com\">\t<soapenv:Header/>\t<soapenv:Body>\t\t<web:publishAndReturnDocument>\t\t\t<!--Optional:-->\t\t\t<web:requestContext><![CDATA[<RequestContext><Credentials method=\"UserID and Password\"><UserID>" + soapUserName + "</UserID><Password>" + soapUserPassword + "</Password></Credentials><ApplicationName>xPression Revise</ApplicationName></RequestContext>]]></web:requestContext>\t\t\t<!--Optional:-->\t\t\t<web:documentName>" + documentName + "</web:documentName>\t\t\t<!--Optional:-->\t\t\t<web:customerData><![CDATA[<ROOT>";
        String footer = "</ROOT>]]></web:customerData>			<!--Optional:-->			<web:outputProfileName>" + outputProfile + "</web:outputProfileName>			<web:assignToUserName>sidorov</web:assignToUserName>		</web:publishAndReturnDocument>	</soapenv:Body></soapenv:Envelope>";
        return header + dataSource + footer;
    }

    /**
     * Отправляет Soap-сообщение и возврашает результат в виде base64-строки
     *
     * @param soapMessage Soap-сообщение
     * @return Ответ в формате base64
     */
    public static String SendAndRecvSoap(String soapMessage) {
        log.trace("Calling method SendAndRecvSoap("+soapMessage+")");
        try {
            return SendAndRecvMessage(soapMessage);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error while sending SOAP-message for request ", e);
        }
    }

    /**
     * Отправляет SOAP-сообещиние серверу и возвращает ответ от сервера
     *
     * @param soapMessage Посылаемое SOAP-сообщение
     * @return ответ сервера на SOAP-запрос
     * @throws IOException
     */
    public static String SendAndRecvMessage(String soapMessage) {
        String endpoint = AppConfiguration.get("XPRESSION_URL") + "/xFramework/services/QuickDoc";
        try {

            byte[] data = soapMessage.getBytes("UTF-8");
            URL url = new URL(endpoint);
            URLConnection uc = url.openConnection();

            uc.setDoOutput(true);
            uc.setDoInput(true);
            uc.setUseCaches(false);

            uc.setRequestProperty("Content-Type", "text/xml;charset=\"utf-8\"");
            uc.setRequestProperty("Content-Length", "" + soapMessage.length());
            uc.connect();

            DataOutputStream dos = new DataOutputStream(uc.getOutputStream());
            dos.write(data, 0, data.length);
            dos.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream(), "UTF-8"));

            String res = br.readLine();
            br.close();

            return res.substring(res.indexOf("<ns:return>") + 11, res.indexOf("</ns:return>"));

        } catch (Exception e) {
            if (e.getMessage().contains("response code: 500")){
                log.error("Internal error xPression os the server  " + endpoint, e);
            } else {
                log.error("Error while working with xPression", e);
            }
        }

        throw new RuntimeException("Error while working with xPression");
    }
}
