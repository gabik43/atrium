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

public class AppConfiguration {

//    private final static String SERVER_NAME = "10.101.145.239";
//    private final static String PATH = "config";
//    private final static String FILE_NAME = "AtriumFXConfig.properties";
//    private final static String PROPERTIES_FILE_URL = "//" + SERVER_NAME + "/" + PATH + "/" +FILE_NAME;
//    private final static String PROPERTIES_FILE_URL = "//10.101.145.239/config/AtriumFXConfig.properties";

    public static String APP_URL;
//    private static String PROPERTIES_FILE_URL = "AtriumFXConfig.properties";

    private static Map<String, String> configData = new HashMap<String, String>();
    private static boolean isConfigRead = false;

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
//        File configFile = null;
        InputStream inputStream = null;
        Properties prop = new Properties();

        try {
            configFile = new URL(getPropertiesFileUrl());
            System.out.println(configFile.getPath());
            inputStream = configFile.openStream();
//            configFile = new File(PROPERTIES_FILE_URL);
//            System.out.println(configFile.getAbsolutePath());
//            inputStream = new FileInputStream(configFile);

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
            LogDB.error("Exception while reading configuration file: " + configFile.getPath() + e);

        } finally {
            try {
                inputStream.close();
            }catch(Exception e){
                LogDB.warn("Can't close configuration's fileInputStream" + configFile.getPath());
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

    private static String getPropertiesFileUrl() {
        return APP_URL.substring(0, APP_URL.length()-3) + "static/resources/config/AtriumFXConfig.properties";
    }

}
