package ru.asteros.atrium.Report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.asteros.atrium.DB.RegionInfoUtility;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Timofey on 24.09.2015.
 */
public class ReportDWH {

    private static Logger log = LoggerFactory.getLogger(RegionInfoUtility.class);

    /**
     * Записывается в файл информация об ошибке
     *
     * @param idOrder - id заказа
     * @param idRecord -  id клиента у которого возникла ошибка в данных
     * @param nameField - имя клиента у которого возникла ошибка
     * @param message - описание ошибки
     */
    public static void addWarning(String idOrder, String idRecord, String nameField, String message) {

        File fileForReport = null;
        FileWriter fw = null;
        try {
            fileForReport = new File("log_" + idOrder + ".log");
            boolean fileExists = fileForReport.exists();
            fw = new FileWriter(fileForReport, true);

            if (!fileExists) {
                fileForReport.createNewFile();
                fw.write("ITG_CLNT_ID     nameField (or GroupNameFields)     message");
            }
            fw.write("\r\n" + idRecord + "   " + nameField + "   " + message);

        } catch (IOException e) {
            log.error("Ошибка при работе с файлом");
            e.printStackTrace();
        } finally {
            try {
                if (fw!=null) {
                    fw.close();
                }
            } catch (IOException e) {
                log.error("Ошибка при закрытии потока записи в файл");
                e.printStackTrace();
            }
        }

    }



}
