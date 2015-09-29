package ru.asteros.atrium.infoarhive;

import com.xhive.XhiveDriverFactory;
import com.xhive.core.interfaces.XhiveDatabaseIf;
import com.xhive.core.interfaces.XhiveDriverIf;
import com.xhive.core.interfaces.XhivePageCacheIf;
import com.xhive.core.interfaces.XhiveSessionIf;
import com.xhive.dom.interfaces.XhiveDocumentIf;
import com.xhive.dom.interfaces.XhiveLibraryIf;
import com.xhive.federationset.interfaces.XhiveFederationSetFactory;
import com.xhive.federationset.interfaces.XhiveFederationSetIf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.ls.LSParser;
import ru.asteros.atrium.AppConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Created by A.Gabdrakhmanov on 16.08.2015.
 */
public class InfoarhiveManager {
    private static Logger log = LoggerFactory.getLogger(InfoarhiveManager.class);
    XhiveSessionIf session;
    LSParser builderXml;
    XhiveLibraryIf charterXMLFiles;

    public InfoarhiveManager() throws IOException {
    }


    public synchronized void UploadXMLFile(File file) throws IOException {
        try {
            XhivePageCacheIf pageCache =
                    XhiveDriverFactory.getFederationFactory().createPageCache(4096);
            XhiveFederationSetIf fs =
                    XhiveFederationSetFactory.getFederationSet(AppConfiguration.get("INFOARHIVE_URL"),
                            pageCache);
            XhiveDriverIf driver = fs.getFederation(AppConfiguration.get("INFOARHIVE_FEDERATION_NAME"));

            session = driver.createSession();
            session.connect(AppConfiguration.get("INFOARHIVE_USER"), AppConfiguration.get("INFOARHIVE_PASSWORD"), AppConfiguration.get("INFOARHIVE_FEDERATION_NAME"));
            session.begin();
            String absolutePath = file.getAbsolutePath();
            String[] threeFolderName = getFoldersName(file.getName());
            charterXMLFiles = getLibrary(threeFolderName);
            builderXml = charterXMLFiles.createLSParser();
            builderXml.getDomConfig().setParameter("error-handler", new SimpleDOMErrorPrinter());
            File fileXml = new File(absolutePath);
            Document firstDocument = builderXml.parseURI(fileXml.toURI().toString());
            if (!(charterXMLFiles.nameExists(fileXml.getName()))) {
                charterXMLFiles.appendChild(firstDocument);
                ((XhiveDocumentIf) firstDocument).setName(fileXml.getName());
                session.commit();
                log.info("Загружаем файл xml: " + file);
            }
        }catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        } finally {
            if (session.isOpen())
                session.commit();
            if (session.isConnected()) {
                session.disconnect();
            }
        }
    }

    /* Создание иерархии папок в infoahgive*/
    private XhiveLibraryIf getLibrary(String[] foldersNames){
        XhiveDatabaseIf unitedNationsDb = session.getDatabase();
        XhiveLibraryIf rootLibrary = unitedNationsDb.getRoot();
        XhiveLibraryIf charterData = DataLoader.createLibrary(rootLibrary, "DATA");
        XhiveLibraryIf charterInfoarhive = DataLoader.createLibrary(charterData, "infoarchive_ad");
        XhiveLibraryIf charterCollection = DataLoader.createLibrary(charterInfoarhive, "Collection");
        XhiveLibraryIf tele2 = DataLoader.createLibrary(charterCollection, "TELE2_DEMO");
        XhiveLibraryIf year = DataLoader.createLibrary(tele2, foldersNames[0]);
        XhiveLibraryIf mount = DataLoader.createLibrary(year, foldersNames[1]);
        XhiveLibraryIf idRegion = DataLoader.createLibrary(mount, foldersNames[2]);

        return idRegion;
    }

    /* Получение имени папок, формирующегося из имени файла в инфоархиве.
    * Папки необходимы для ускорения работы xQery запросов.*/
    private String[] getFoldersName(String fileName){
        String[] fileAndExtensions = fileName.split("\\."); // Делим имя файла на имя и расширение
        String[] foldersNames = fileAndExtensions[0].split("__"); // Делим имя на три части, которые будут каталогами.
        if (foldersNames.length != 3){
            log.info("Ошибка разбора имени файла. Ожидалось три параметра: год, месяц, id региона. " +
                    "Имя папок будет: error_parsing, error_parsing, error_parsing. В инфоархиве " +
                    "данный регион не будет доступен.");
            return new String[] {"error_parsing", "error_parsing", "error_parsing"};
        }
        return foldersNames;
    }
}
