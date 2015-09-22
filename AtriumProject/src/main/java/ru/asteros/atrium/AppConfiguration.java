package ru.asteros.atrium;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by A.Gabdrakhmanov on 11.08.2015.
 * Extends by A.Koshkin on 27.08.2015.
 */
@Component("appConfiguration")
@Scope("singleton")
public class AppConfiguration {
    // параметр хранит имя кофигурационного файла
    public final static String PROPERTIES_FILE_NAME = "AtriumConfig.properties";

    public final static String DONE_NOT_CUSTOMER = "Нет клиентов";
    public final static String DONE_GENERATE_DOCUMENT_COMPLETE = "Документы сформированны";
    public final static String DONE_FILE_UPLOAD = "Документы загружены";

    public final static String PERFORMED_GET_REGION_INFO = "Получение информации о регионе";
    public final static String PERFORMED_GET_CUSTOMER_DATA = "Получение данных клиентов";
    public final static String PERFORMED_SAVE_XML = "Сохранение данных клиентов";
    public final static String PERFORMED_START_FILE_THREAD = "Запуск процесса обработки документов";
    public final static String PERFORMED_GENERATE_DOCUMENT = "Генерация документов";
    public final static String PERFORMED_UPLOAD_FILE = "Загрузка документов";

    private static Map<String, String> configData = new HashMap<>();
    private static Logger log = LoggerFactory.getLogger(AppConfiguration.class);
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
                log.error("Can't load configuration file. ");
                return null;
            }

            return prop.getProperty(paramName);

        } catch (Exception e) {
            System.out.println("Exception while reading configuration file: " + e);
        } finally {
            try {
                inputStream.close();
            }catch(Exception e){
                log.warn("Can't close configuration's fileInputStream");
            }
        }
        return null;
    }

    /***
     * Функция чтения даннх из кофигурационного файла
     * в случае успешного чтения, устанавливает свойство isConfigRead = true
     * иначе isConfigRead = false
     */
    @PostConstruct
    public static void readConfigFile(){
        FileInputStream inputStream = null;
        File configFile = new File(PROPERTIES_FILE_NAME);
        try {
            inputStream = new FileInputStream(configFile);
            Properties prop = new Properties();

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                log.error("Can't load configuration file. ");


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
            isConfigRead = false;
            log.error("Exception while reading configuration file: " + configFile.getAbsolutePath() + e);
        } finally {
            try {
                inputStream.close();
            }catch(Exception e){
                log.warn("Can't close configuration's fileInputStream" + configFile.getAbsolutePath());
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
