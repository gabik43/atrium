package ru.asteros.atrium.file;

import java.io.File;

/**
 * Created by A.Gabdrakhmanov on 24.08.2015.
 */
public class FileTools {

    /*Очистка папок и подпапок от файлов*/
    public static void clearFolder(File folder) {
        if (folder.isDirectory()) {
            File[] list = folder.listFiles();
            if (list != null) {
                for (int i = 0; i < list.length; i++) {
                    File tmpF = list[i];
                    if (tmpF.isDirectory()) {
                        clearFolder(tmpF);
                    }
                    if (tmpF.isFile()) {
                        tmpF.delete();
                    }
                }
            }
        }
    }

}
