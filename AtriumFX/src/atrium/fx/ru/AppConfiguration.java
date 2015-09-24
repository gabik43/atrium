package atrium.fx.ru;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by A.Gabdrakhmanov on 08.09.2015.
 * Implemented by Koshkin Andrey on 19.09.2015
 */
/*Файл конфигурации приложения*/

public class AppConfiguration {
    // параметр хранит имя кофигурационного файла
    public final static String PROPERTIES_FILE_NAME = "AtriumConfig.properties";

    public final static String SERVER_IP = "10.101.145.239";
    public final static String FOLDER_FILE = "DOC"; //OutputFilesForUser
    public final static String PATH_TO_OUT_FOLDER = "//" + SERVER_IP + "/" + FOLDER_FILE + "/";
    public final static int COUNT_PARENT_FOLDER_FILE = 5;
    public final static String TITLE = "Tele2";
    public final static String NAME_ROOT_ELEMENT = "Макрорегионы";


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
        if(!isConfigRead || configData.containsKey(paramName)){
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
            File configFile = new File(PROPERTIES_FILE_NAME);
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
    public static void readConfigFile(){
        FileInputStream inputStream = null;
        File configFile = new File(PROPERTIES_FILE_NAME);
        Properties prop = new Properties();
        try {
            inputStream = new FileInputStream(configFile);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                LogDB.error("Can't load configuration file. ");


                // вывод тестовых данных в файл
                prop.setProperty("exampleKey", "exampleValue");
                prop.store(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(PROPERTIES_FILE_NAME), "UTF-8")), null);

                return;
            }

            for(String key : prop.stringPropertyNames()) {
                String value = prop.getProperty(key);
                configData.put(key, value);
            }
            // станавливаем флаг успешного чтения данных из файла
            isConfigRead = true;

        } catch (Exception e) {
            try {
                prop.setProperty("exampleKey", "exampleValue");
                prop.store(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(PROPERTIES_FILE_NAME), "UTF-8")), null);
            } catch(Exception r){

            }
            isConfigRead = false;
            LogDB.error("Exception while reading configuration file: " + configFile.getAbsolutePath() + e);
        } finally {
            try {
                inputStream.close();
            }catch(Exception e){
                LogDB.warn("Can't close configuration's fileInputStream" + configFile.getAbsolutePath());
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

}
