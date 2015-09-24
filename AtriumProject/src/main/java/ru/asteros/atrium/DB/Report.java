package ru.asteros.atrium.DB;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Timofey on 24.09.2015.
 */
public class Report {

    public static void addWarning(int idOrder, String idRecord, String nameField, String message) throws Exception {

        File file = new File("src/main/resources/config/log_" + idOrder + ".log");
        boolean fileExists = file.exists();
        FileWriter fw = new FileWriter(file, true);

        if(!fileExists){
            file.createNewFile();
            fw.write("idRecord     nameField     message");
        }
        fw.write("\r\n" + idRecord + "   " + nameField + "   " + message);

        fw.close();

    }



}
