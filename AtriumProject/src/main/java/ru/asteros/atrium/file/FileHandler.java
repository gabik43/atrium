package ru.asteros.atrium.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.asteros.atrium.AppConfiguration;
import ru.asteros.atrium.DB.RegionInfo;
import ru.asteros.atrium.DB.RegionInfoUtility;
import ru.asteros.atrium.DB.SubOrderDB;
import ru.asteros.atrium.ProjectException.AtriumError;
import ru.asteros.atrium.infoarhive.InfoarhiveManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by A.Gabdrakhmanov on 13.08.2015.
 * Файловый перекладчик и по совместительству загрузчик xml в Infoarhive
 */
public class FileHandler extends Thread {
    private HashMap<String, Boolean> treatedRegion;
    private String path;
    private Date beginTimeThread;
    private RegionInfo regionInfo;
    InfoarhiveManager infoarhiveManager;
    SubOrderDB subOrderDB;

    private static Logger log = LoggerFactory.getLogger(FileHandler.class);

    public FileHandler(SubOrderDB subOrderDB, HashMap<String, Boolean> hashMap){
        this.subOrderDB = subOrderDB;
        this.regionInfo = RegionInfoUtility.getRecord(subOrderDB.regionID);
        this.treatedRegion = hashMap;
        this.path = AppConfiguration.get("PATH_TO_OUTPUT_FILES_FOR_XPRESSION")  + regionInfo.macroRegionEng + "/" + regionInfo.regionEng;
        beginTimeThread = new Date();
    }

    @Override
    public void run() {
        try {
            subOrderDB.setStPerformed(AppConfiguration.PERFORMED_UPLOAD_FILE, subOrderDB.MODE_FILE_MANAGER);
            log.info("Launched thread. Folder: " + path);
            while (true) {
                    List<File> files = getAllPDFFile();
                    if (files.isEmpty() && isJobCompletedWork()) {
                        break;
                    }
                    Thread.sleep(5000);         // Мониторим папку, каждую секунду (что-бы не выхватить не до конца выложенные файлы)
                    moveProcessedFiles(files);
            }
            File directory = new File(path + "/" + AppConfiguration.get("XML_DIRECTORY_OUTPUT"));
            File[] filesXml = directory.listFiles();
            infoarhiveManager = new InfoarhiveManager();
            infoarhiveManager.UploadXMLFile(filesXml[0]);

            removeProcessedFile(new File(path + "/" + AppConfiguration.get("XML_DIRECTORY_OUTPUT") + " / tele2_base.xml"));
                    log.info("Thread completed. Folder: " + path + ". Job status: " + treatedRegion.get(regionInfo.regionEng));
            treatedRegion.remove(regionInfo.regionEng);
            subOrderDB.setStDone(AppConfiguration.DONE_FILE_UPLOAD, subOrderDB.MODE_FILE_MANAGER);
        } catch (Exception ex){
            subOrderDB.setStError(AtriumError.MOVING_FILES_TO_FOLDER, subOrderDB.MODE_FILE_MANAGER);
            log.error("An error has occurred in the process of relaying files: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private boolean isJobCompletedWork(){
        return treatedRegion.containsKey(regionInfo.regionEng);
    }

    private void moveProcessedFiles(List<File> files) throws IOException, InterruptedException {
        for (File file: files) {
            try {
            String newPath = file.getAbsolutePath().replace("\\","/");
            newPath = newPath.replace(AppConfiguration.get("PATH_TO_OUTPUT_FILES_FOR_XPRESSION"), AppConfiguration.get("PATH_TO_OUTPUT_FILES_FOR_USER"));
            File fileNew = new File(newPath);

            File dir = new File(fileNew.getParent());
            dir.mkdirs();
                Files.move(file.toPath(), fileNew.toPath(), StandardCopyOption.REPLACE_EXISTING);
                log.info("Transferring " + file.toPath() + " in " + fileNew.toPath());
            } catch (Exception ex){
                log.warn("Не удалось перенести файл");
            }
        }
    }

    private void removeProcessedFile(File nameFile){
        nameFile.delete();
    }


    private List<File> getAllPDFFile(){
        List<File> files = new ArrayList<>();
        getFileInFolder(path, files);
        return files;
    }

    /*Рекурсивные поиск файлов в каталоге и его подкаталогах. Игнорируется каталог, прописанный в
    AppConfiguration.XML_DIRECTORY_OUTPUT, т.к. там хранится xml, которая должна будет скопирована после копирования
    всех PDF файлов.
    * @param pathForSearch - путь к каталогу, в котором происходид поиск
    * @param foundFiles - коллекция, содержащая список найденных имен в виде полного пути к файлу и имени*/
    private void getFileInFolder(String pathForSearch,  List<File> foundFiles){
        File directoryForSearch = new File(pathForSearch);
        if (directoryForSearch.getName().equals(AppConfiguration.get("XML_DIRECTORY_OUTPUT"))){
            return;
        }
            String[] allFoundDirectoryOrFile = directoryForSearch.list();
            if (allFoundDirectoryOrFile == null){
                return; // Пустая папка, ничего не возвращаем;
            }
            for (int i = 0; i < allFoundDirectoryOrFile.length; i++){
                File directoryOrFile = new File(pathForSearch + "/" + allFoundDirectoryOrFile[i]);
                if (directoryOrFile.isDirectory()){
                    getFileInFolder(directoryOrFile.getPath(), foundFiles);
                } else {
                    if (directoryOrFile.getAbsolutePath().contains(".pdf"))
                    foundFiles.add(directoryOrFile);
                }
        }
    }
}
