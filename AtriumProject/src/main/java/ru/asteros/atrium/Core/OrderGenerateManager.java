package ru.asteros.atrium.Core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.asteros.atrium.AppConfiguration;
//import ru.asteros.atrium.DB.Order;
import ru.asteros.atrium.DB.OrderDB;
import ru.asteros.atrium.file.FileTools;

import java.io.File;

/**
 * Created by DOLB on 14-Aug-15.
 */
@Component("orderGenerateManager")
@Scope("singleton")
@EnableScheduling
public class OrderGenerateManager {
    private static Logger log = LoggerFactory.getLogger(OrderGenerateManager.class);


    /*Метод обработки ORDER - заказов*/
    @Scheduled(fixedDelay = 30000)
    private static void OrderHandler()
    {
        //OrderDB.addNewOrderBySchedule();

        // Проверяем считаны ли данные из конфигурационного файла
        // если не считаны, пытаемся читать и выходим
        if( ! AppConfiguration.isConfigLoad()) {
            AppConfiguration.readConfigFile();
            return;
        }

// проставление статуса выполнено и статуса ошибка
        OrderDB.updateOrdersStatusComplete();
        OrderDB.updateOrdersStatusError();


        // основной этап обработк
        OrderDB orderDB;
        if ((orderDB = OrderDB.getAndBlockNewOrder()) == null) return;

        initFileStructure();

        if (orderDB.processOrder()) {
            log.info("subOrder list Created");
        } else {
            orderDB.restoreDB();
            log.info("subOrder list Creating error");
        }


    }

    public void CreateNewOrder()
    {
        OrderDB.addNewOrderWithoutCondition();
    }

    /*Инициализация нового заказа. Включает в себя очистку папок xPression и папки для пользователя*/
    private static void initFileStructure(){
        File fileForXpression = new File(AppConfiguration.get("PATH_TO_OUTPUT_FILES_FOR_XPRESSION"));
        FileTools.clearFolder(fileForXpression);

        File fileForUser = new File(AppConfiguration.get("PATH_TO_OUTPUT_FILES_FOR_USER"));
        FileTools.clearFolder(fileForUser);
        log.info("Folder: " + AppConfiguration.get("PATH_TO_OUTPUT_FILES_FOR_XPRESSION") + ", " + AppConfiguration.get("PATH_TO_OUTPUT_FILES_FOR_USER") +
                " cleared");
    }
}
