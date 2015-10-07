package atrium.fx.ru;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by A.Gabdrakhmanov on 08.09.2015.
 * Implemented by Koshkin Andrey on 19.09.2015
 */
/*Файл конфигурации приложения*/

// !!!Config файл должен находиться в AtriumProject/src/main/webapp/resources/config/AtriumFXConfig.properties!!!
public class AppConfiguration {

    private static String APP_URL;
    private static String PROPERTIES_FILE_PATH = "AtriumFXConfig.properties";

    private static Map<String, String> configData = new HashMap<String, String>();
    private static boolean isConfigRead = false;

    // Запускаем локально или на сервере? true - локально.
    public static boolean LOCAL = false;   ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public AppConfiguration(){
    }

    /***
     * Функция возвращает значение параметра конфигурации, полученное при инифиализации приложения
     *
     * @param paramName имя параметра
     * @return значение параметра
     */
    public static String get(String paramName){
        if(!isConfigRead) {
            readConfigFile();
        }
        if(configData.containsKey(paramName)){
            return configData.get(paramName);
        } else{
            return null;
        }
    }

    /***
     * Функция возвращает значение параметра конфигурации
     * возвращает данные, которе в настоящее время содержатся в конфигурационном файле
     * может влиять на производительность
     *
     * @param paramName имя параметра
     * @return значение параметра
     */

    public static String getFromFile(String paramName){
        FileInputStream inputStream = null;

        try {
            File configFile = new File(getPropertiesFileUrl());
            inputStream = new FileInputStream(configFile);
            Properties prop = new Properties();

            if (inputStream != null) {
                prop.load(inputStream);
            } else{
                LogDB.error("Can't load configuration file. ");
                return null;
            }

            return prop.getProperty(paramName);

        } catch (Exception e) {
            System.out.println("Exception while reading configuration file: " + e);
        } finally {
            try {
                inputStream.close();
            }catch(Exception e){
                LogDB.warn("Can't close configuration's fileInputStream");
            }
        }
        return null;
    }

    /***
     * Функция чтения даннх из кофигурационного файла
     * в случае успешного чтения, устанавливает свойство isConfigRead = true
     * иначе isConfigRead = false
     */
    private static void readConfigFile(){

        URL configFile = null;
        File configFile2 = null;   ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        InputStream inputStream = null;
        Properties prop = new Properties();

        try {
            if (!LOCAL) {            ////////////////////////////////////////////////////////////////////////////////////////////////////////////
                configFile = new URL(getPropertiesFileUrl());
//                System.out.println(configFile.getPath());
                inputStream = configFile.openStream();
            } else {
                configFile2 = new File(PROPERTIES_FILE_PATH);
//                System.out.println(configFile2.getAbsolutePath());
                inputStream = new FileInputStream(configFile2);
            }

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                LogDB.error("Can't load configuration file. ");
                // вывод тестовых данных в файл
                prop.setProperty("exampleKey", "exampleValue");
                prop.store(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getPropertiesFileUrl()), "UTF-8")), null);
                return;
            }

            for(String key : prop.stringPropertyNames()) {
                String value = prop.getProperty(key);
                configData.put(key, value);
            }
            configData.put ("PATH_TO_OUT_FOLDER", "//" + configData.get("SERVER_IP") + "/" + configData.get("FOLDER_FILE") + "/");
            configData.put("NAME_ROOT_ELEMENT", "Макрорегионы");
            // станавливаем флаг успешного чтения данных из файла
            isConfigRead = true;

        } catch (Exception e) {
            try {
                prop.setProperty("exampleKey", "exampleValue");
                prop.store(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getPropertiesFileUrl()), "UTF-8")), null);
            } catch(Exception r){

            }
            isConfigRead = false;
            if(!LOCAL) {   ////////////////////////////////////////////////////////////////////////////////////////////////////////////
                LogDB.error("Exception while reading configuration file: " + configFile.getPath() + e);
            } else {
                LogDB.error("Exception while reading configuration file: " + configFile2.getAbsolutePath() + e);
            }

        } finally {
            try {
                inputStream.close();
            }catch(Exception e){
                if(!LOCAL) {    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    LogDB.warn("Can't close configuration's fileInputStream" + configFile.getPath());
                } else {
                    LogDB.warn("Can't close configuration's fileInputStream" + configFile2.getAbsolutePath());
                }
            }
        }
    }

    /***
     * функция возвращает статус прочтения конфигурационного файла
     * @return истина, в случае успешного чтения, лож в случае ошибки
     */
    public static boolean isConfigLoad(){
        return isConfigRead;
    }

    public static void setAppUrl(String url) {
        APP_URL = url;
    }

    private static String getPropertiesFileUrl() {
        if (!LOCAL) {   ////////////////////////////////////////////////////////////////////////////////////////////////////////////
            return APP_URL.substring(0, APP_URL.length() - 3) + "static/resources/config/AtriumFXConfig.properties";
        }
        return PROPERTIES_FILE_PATH;
    }

}
