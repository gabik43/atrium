package atrium.fx.ru.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by A.Gabdrakhmanov on 06.09.2015.
 */
public class FileHandler {

    /*Получаем список папок и подпапок, находящихся в папке
    * @param pathForSearch-  путь для поиска файлов*/
    public static List<File> getFolderList(String pathForSearch){
        List<File> fileList = new ArrayList<File>();
        getFolderInFolder(pathForSearch, fileList);
        return fileList;
    }

    /*Получаем список файлов, находящихся в папке
    * @param pathForSearch-  путь для поиска файлов*/
    public static List<File> getFileList(String pathForSearch){
        List<File> fileList = new ArrayList<File>();
        getFileInFolder(pathForSearch, fileList);
        return fileList;
    }

    /*Получение списка .pdf файлов в каталоге.
   * @param pathForSearch - путь к каталогу, в котором происходид поиск
   * @param foundFiles - коллекция, содержащая список найденных имен в виде полного пути к файлам*/
    private static void getFileInFolder(String pathForSearch,  List<File> foundFiles){
        File directoryForSearch = new File(pathForSearch);
        String[] allFoundDirectoryOrFile = directoryForSearch.list();
        if (allFoundDirectoryOrFile == null){
            return; // Пустая папка, ничего не возвращаем;
        }
        for (int i = 0; i < allFoundDirectoryOrFile.length; i++) {
            File pdfFile = new File(pathForSearch + "/" + allFoundDirectoryOrFile[i]);
            if (pdfFile.getAbsolutePath().contains(".pdf"))
                foundFiles.add(pdfFile);
        }
    }

    /*Рекурсивные поиск папок в каталоге и его подкаталогах.
    * @param pathForSearch - путь к каталогу, в котором происходид поиск
    * @param foundFolder - коллекция, содержащая список найденных имен в виде полного пути к папке и имя папки*/
    private static void getFolderInFolder(String pathForSearch, List<File> foundFolder){
        File directoryForSearch = new File(pathForSearch);
        String[] listFoundDirectoryAndFile = directoryForSearch.list();
        if (listFoundDirectoryAndFile == null){
            return; // Пустая папка, выходим;
        }
        if (listFoundDirectoryAndFile.length > 15){
            int i = 0;
        }
        for (int i = 0; i < listFoundDirectoryAndFile.length; i++){
            File currDirOrFile = new File(pathForSearch + "/" + listFoundDirectoryAndFile[i]);
            if (currDirOrFile.isDirectory()){
                    String[] list = currDirOrFile.list();
                    if (list == null){  // Если нет доступа к папке
                        continue;
                    }
                    if (list.length == 0){  // Если папка пуста
                        continue;
                    }
                    if (list[0].contains(".pdf")){
                        foundFolder.add(currDirOrFile);
                    } else {
                        getFolderInFolder(currDirOrFile.getPath(), foundFolder);
                        continue;
                    }
            }
        }
    }
}

