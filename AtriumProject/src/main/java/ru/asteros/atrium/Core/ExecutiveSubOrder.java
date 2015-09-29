package ru.asteros.atrium.Core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.asteros.atrium.AppConfiguration;
import ru.asteros.atrium.DB.OrderDB;
import ru.asteros.atrium.DB.RegionInfo;
import ru.asteros.atrium.DB.RegionInfoUtility;
import ru.asteros.atrium.DB.SubOrderDB;
import ru.asteros.atrium.ProjectException.AtriumError;
import ru.asteros.atrium.ProjectException.AtriumException;
import ru.asteros.atrium.file.FileHandler;
import ru.asteros.atrium.rest.JobManager;
import ru.asteros.atrium.xml.GeneratorXML;
import ru.asteros.atrium.xml.SaveXML;

import java.util.HashMap;

/**
 * Created by A.Gabdrakhmanov on 13.08.2015.
 * Процесс обработки SubOrder (регионально - привязанного заказа).
 */
@Service("managerGenerateDocument")
@Scope("singleton")
@EnableScheduling
public class ExecutiveSubOrder {
    private static Logger log = LoggerFactory.getLogger(ExecutiveSubOrder.class);

    /*Статус выполнения xPression Job для региона. Используется в потоке обработки
    * файлов по региону для остановки ожидания файла. При исполнении Job в данную структуру
    * записывается имя региона и статус Job - true, в случае успешного выполнения и false -
    * - если Job завершился с ошибкой.*/
    private static volatile HashMap<String, Boolean> statusJob = new HashMap<>();

    // Информация о текущем обрабатываемом регионе.
    private RegionInfo regionInfoCurrent;

    // Данные, взятые из DWH и сформированные в виде xml.
    private StringBuilder xml = new StringBuilder();

    // установка статусов производится через этот объект
    private SubOrderDB subOrderDB;

    /*Мониторинг подзаказа. При нахождении свободного подзаказа выполняется запуск
    * процесса обработки региона.*/
    @Scheduled(fixedDelay = 1000)
    public void suborderMonitoring() {

        // Проверяем считаны ли данные из конфигурационного файла
        // если не считаны, пытаемся читать и выходим
        if( ! AppConfiguration.isConfigLoad()) {
            AppConfiguration.readConfigFile();
            return;
        }

        subOrderDB = subOrderDB.GetFreeSuborder();
        if(subOrderDB != null) {
            performSubOrder();
        }
        SubOrderDB.ValidateSuborders();
    }

    /*Бизнесс-логика выполнения подзаказа.*/
    private void performSubOrder(){
        try {
            setActiveRegion();
            processingDWHData();
            XMLStorage();
            creationThreadProcessingFiles();
            LaunchAndWaitingExecutionJob();
        } catch(AtriumException e){
            if (e.getMessage().equals(AtriumError.ZERO_CUSTOMER)) {
                subOrderDB.setStDone(AppConfiguration.DONE_NOT_CUSTOMER, subOrderDB.MODE_DOCUMENT_GENERATION);
            } else {
                subOrderDB.setStError(e.getMessage(), subOrderDB.MODE_DOCUMENT_GENERATION);
            }
        } catch(Exception e){
            log.error("Во время работы заказа N возникла ошибка: " + e.getMessage());
            e.getStackTrace();
        }finally {
            SubOrderDB.ValidateSuborders();
        }
    }

    private void LaunchAndWaitingExecutionJob() throws AtriumException {
        try {
            subOrderDB.setStPerformed(AppConfiguration.PERFORMED_GENERATE_DOCUMENT, subOrderDB.MODE_DOCUMENT_GENERATION);
            if (JobManager.startJobAndWaitingCompletion() == true) {
                statusJob.put(regionInfoCurrent.regionEng, true);
                subOrderDB.setStDone(AppConfiguration.DONE_GENERATE_DOCUMENT_COMPLETE, subOrderDB.MODE_DOCUMENT_GENERATION);
            } else {
                setErrorGenerateDocument();
            }
        } catch (Exception ex){
            setErrorGenerateDocument();
            throw new AtriumException(AtriumError.DOCUMENT_GENERATION);
        }
    }

    private void setErrorGenerateDocument() {
        statusJob.put(regionInfoCurrent.regionEng, false);
        subOrderDB.setStError(AtriumError.DOCUMENT_GENERATION, subOrderDB.MODE_DOCUMENT_GENERATION);
    }

    private void creationThreadProcessingFiles() throws AtriumException {
        try {
            subOrderDB.setStPerformed(AppConfiguration.PERFORMED_START_FILE_THREAD, subOrderDB.MODE_DOCUMENT_GENERATION);
            subOrderDB.setStPerformed(AppConfiguration.PERFORMED_START_FILE_THREAD, subOrderDB.MODE_FILE_MANAGER);
            FileHandler fileHandler = new FileHandler(subOrderDB, statusJob);
            fileHandler.setDaemon(true);
            fileHandler.start();
        } catch (Exception ex){
            ex.printStackTrace();
            throw new AtriumException(AtriumError.CREATION_THREAD_PROCESSING_FILES);
        }
    }

    private void XMLStorage() throws AtriumException {
        try {
            subOrderDB.setStPerformed(AppConfiguration.PERFORMED_SAVE_XML, subOrderDB.MODE_DOCUMENT_GENERATION);
            SaveXML.saveToFile(xml, regionInfoCurrent);
        } catch (Exception ex){
            ex.printStackTrace();
            throw new AtriumException(AtriumError.SAVING_XML);
        }
    }

    private void processingDWHData() throws AtriumException {
        try {
            subOrderDB.setStPerformed(AppConfiguration.PERFORMED_GET_CUSTOMER_DATA, subOrderDB.MODE_DOCUMENT_GENERATION);
            xml.delete(0, xml.length());
            xml = GeneratorXML.getXMLStringFromDWH(regionInfoCurrent, OrderDB.getActiveIdOrder());
        } catch (AtriumException ex){   // Поймали внутренее исключение приложение, высылаем дальше.
            throw ex;
        } catch (Exception ex){
            ex.printStackTrace();
            throw new AtriumException(AtriumError.DATA_DWH_PROCESSING);
        }
    }

    private void setActiveRegion() throws AtriumException{
        try {
            subOrderDB.setStPerformed(AppConfiguration.PERFORMED_GET_REGION_INFO, subOrderDB.MODE_DOCUMENT_GENERATION);
            regionInfoCurrent = RegionInfoUtility.getRecord(subOrderDB.regionID);
        } catch (Exception ex){
            ex.printStackTrace();
            throw new AtriumException(AtriumError.GET_REGION_INFO);
        }
    }
}


