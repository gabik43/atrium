package ru.asteros.atrium.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.asteros.atrium.AppConfiguration;
import ru.asteros.atrium.DB.RegionInfo;

import java.io.File;
import java.nio.file.Files;

/**
 * Created by A.Gabdrakhmanov on 11.08.2015.
 * Класс, предназначеный для сохранения xml файла.
 */
public class SaveXML {
    private static Logger log = LoggerFactory.getLogger(SaveXML.class);
    private static String pathJobInputDirectory = AppConfiguration.get("PATH_TO_XML_FOR_XPRESSION");
    private static String pathInfoarhiveInputDirectory = AppConfiguration.get("PATH_TO_OUTPUT_FILES_FOR_XPRESSION");


    /*Сохранение xml в каталог для xPression Job и сохранение в каталог для инфоархива по конкретному региону;
    Сохранения в два места необходимо, т.к. возможна ситуация, когда в каталоге для xPression будет перезаписан
    файл, еще не выложенный в Infoarhive.
    * @param xml - Строка содержащая xml файл для сохранения*/
    public static void saveToFile(RegionInfo regionInfo) throws Exception {
        File fileForJob = new File(pathJobInputDirectory + "tele2_base.xml");
        try {
            String pathForInfoarhive = pathInfoarhiveInputDirectory + regionInfo.macroRegionEng + "/" + regionInfo.regionEng + "/" + AppConfiguration.get("XML_DIRECTORY_OUTPUT") + "/";
            File fileForInfoarhive = new File(pathForInfoarhive  + GeneratorXML.getNameXML() + ".xml");
            File directoryForInfoarhive = new File(pathForInfoarhive);
            directoryForInfoarhive.mkdirs();
            if (fileForInfoarhive.exists()){
                fileForInfoarhive.delete();
            }
            Files.copy(fileForJob.toPath(), fileForInfoarhive.toPath());

        } catch (Exception ex){
            log.error("Failed to copy and write to a file: " + fileForJob.getAbsolutePath(), ex.getMessage());
        }
        log.info("Generate xml file complete. File: " + fileForJob.getAbsolutePath());
    }
}
