package atrium.fx.ru.file;

import atrium.fx.ru.AppConfiguration;

import java.io.File;

/**
 * Created by A.Gabdrakhmanov on 06.09.2015.
 * Хранение структуры имени файла в виде массива String:
 * mas[0] - имя файла с расширением;
 * mas[1]...mas[n] - последовательность папок, в которые вложен файл, начиная с mas[1], в котором
 * находится файл.
 */
public class FileInfoForThree {
    public String[] absolutePath;

    public int getCountFolder() {
        return countFolder;
    }

    private int countFolder;
    /*Создание структуры информации о файле
    * @param file - полный путь файла для преобразования
    * @countFolder - количество родительских папок для отображения*/
    public FileInfoForThree(File file){
        countFolder = getCountFolder(file);
        File pointOnFolder = new File(file.getPath());
        absolutePath = new String[countFolder];
        for (int i = 0; i < countFolder; i++)
        {
            absolutePath[i] = pointOnFolder.getName();
            pointOnFolder = pointOnFolder.getParentFile();
        }
    }

    /*Получаем количество родительских папок от AppConfiguration.FOLDER_FILE*/
    private int getCountFolder(File file){
        File fileCurrent = file.getParentFile();
        int countFolder = 1;
        while (true){
            if (fileCurrent.getName().equals(AppConfiguration.get("FOLDER_FILE"))){
                return countFolder;
            }
            fileCurrent = fileCurrent.getParentFile();
            countFolder ++;
        }
    }
}
