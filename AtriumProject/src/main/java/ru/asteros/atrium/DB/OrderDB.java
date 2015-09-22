package ru.asteros.atrium.DB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.asteros.atrium.AppConfiguration;
import ru.asteros.atrium.DataBaseTemplateProvider.DataBaseTemplateProvider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by DOLB on 31-Aug-15.
 */
public class OrderDB {

    //список статусов
    public final static String STATUS_BLOCKED =     "BLOCKED";
    public final static String STATUS_NEW =         "NEW";
    public final static String STATUS_PERFORMED =   "PERFORMED";
    public final static String STATUS_DONE =        "DONE";
    public final static String STATUS_ERROR =       "ERROR";


    private final static String TBL_ORDER =      "[ORDER]";
    private final static String TBL_SUBORDER =   "[SUBORDER]";

    public final static String FIELD_ORDER_STATUS = "stats";
    public final static String FIELD_CREATE_TIME = "createTime";

    public final static String FIELD_SERVER_NAME = "serverName";
    public final static String FIELD_ID = "id";
    public final static String FIELD_START_TIME = "startTime";


    public final static String CONFIG_SERVER_NAME = "SERVER_NAME";


    private static JdbcTemplate dbConnector = DataBaseTemplateProvider.getInternalDataBaseJdbcTemplate();
    private static Logger log = LoggerFactory.getLogger(OrderDB.class);

    long orderId;

    public static boolean addNewOrderBySchedule(){
        try {
            // получаем временной интервал
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1);
            String pastDate = dateFormat.format(cal.getTime());

            cal = Calendar.getInstance();
            cal.add(Calendar.DATE, +1);
            String futureDate = dateFormat.format(cal.getTime());

            cal = Calendar.getInstance();
            String currentDate = dateFormat.format(cal.getTime());

            dbConnector.update("INSERT INTO " + TBL_ORDER + " " +
                    " ( " + FIELD_ORDER_STATUS  + ", " + FIELD_CREATE_TIME + " ) " +
                    "SELECT '" + STATUS_NEW + "' , CONVERT(datetime,'" + currentDate + "')" +
                    " WHERE NOT EXISTS (SELECT 1 " +
                    "                     FROM  " + TBL_ORDER + " " +
                    "                    WHERE " + FIELD_CREATE_TIME + " BETWEEN  " +
                    "                       CONVERT(datetime,'" + pastDate + "') AND " +
                    "                       CONVERT(datetime,'" + futureDate + "'));" );

            // верификация добавления записи
            List<Map<String, Object>> dbObj;
            dbObj = dbConnector.queryForList("SELECT TOP 1 " +FIELD_ID + " " +
                    "              FROM " +TBL_ORDER + " " +
                    "                  WHERE " +FIELD_CREATE_TIME + " BETWEEN " +
                    "                     CONVERT(datetime,'" +pastDate + "') AND " +
                    "                     CONVERT(datetime,'" +futureDate + "');");

            // в случае если dbObj не равен null верификация пройдена
            if (dbObj != null) {
                if(dbObj.size() != 0)
                {
                    return true;
                }
            }
        }catch (Exception e){
            log.error("Exception while adding new order by schedule: " + e);
        }
        return false;
    }

    /**
     * метод добавляет новый заказ
     * в случае если существует заказ в статуса новый или в обработке, добавление будет прервано
     */
    public static void addNewOrderWithoutCondition(){
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Calendar cal = Calendar.getInstance();
            cal = Calendar.getInstance();
            String currentDate = dateFormat.format(cal.getTime());

            String sql = "INSERT INTO " + TBL_ORDER + " " +
                    " ( " + FIELD_ORDER_STATUS + " , " + FIELD_CREATE_TIME + " ) " +
                    "SELECT  '"+STATUS_NEW+"'  , CONVERT(datetime,' "+currentDate+" ')" +
                    " WHERE NOT EXISTS (SELECT 1 " +
                    "                     FROM  "+TBL_ORDER+" " +
                    "                      WHERE ("+FIELD_ORDER_STATUS+" = '" + STATUS_PERFORMED +"'  OR "+FIELD_ORDER_STATUS+" = '"+ STATUS_NEW +"' ));";

            dbConnector.update(sql);

        }catch (Exception e){
            log.error("Exception while adding new order: " + e);
        }
    }

    /**
     * метод получения и блокировки заказа для выполнения
     * @return true в случае если удалось получить и заблокировать заказ
     */
    public static OrderDB getAndBlockNewOrder(){
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Calendar cal = Calendar.getInstance();
            String currentDate = dateFormat.format(cal.getTime());

            String sql = "UPDATE TOP(1) " +TBL_ORDER+  "  " +
                    " SET  " + FIELD_ORDER_STATUS + " = '"+STATUS_BLOCKED+"' , "  +
                    FIELD_SERVER_NAME + " =  '"+AppConfiguration.get("SERVER_NAME")+"' ," +
                    FIELD_START_TIME + "  =     CONVERT(datetime, '"+currentDate+"' )" +
                    " WHERE "+ FIELD_ORDER_STATUS +" = '"+STATUS_NEW+"'  ;";

            dbConnector.update(sql);


            // верификация добавления записи
            List<Map<String, Object>> dbObj;
            sql = "select top(1) "+ FIELD_ID +" "+
                    " FROM "+ TBL_ORDER+ " " +
                    " WHERE "+FIELD_SERVER_NAME+" = '"+AppConfiguration.get("SERVER_NAME")+"'" +
                    " AND "+FIELD_ORDER_STATUS+" = '"+STATUS_BLOCKED+"'";

            dbObj = dbConnector.queryForList(sql);

            // в случае если dbObj не равен null верификация пройдена
            if (dbObj != null) {
                if(dbObj.size() != 0)
                {
                    OrderDB orderDB = new OrderDB();
                    orderDB.orderId = (Long)(dbObj.get(0).get(FIELD_ID));
                    return orderDB;
                }
            }

        }catch (Exception e){
            log.error("Exception while blocking order for working: " + e);
        }
        return null;
    }

    /**
     * метод создания подзаказов
     * в процессе выполнения, метод добавляет новые подзаказы и верифицирует их
     * @return true в случае если структура подзаказов была успещшно создана
     */
    public boolean processOrder(){
        try {
            Object dbObj;

            List<RegionInfo> dbData = RegionInfoUtility.getRecordsWithStatusEnable();

            // добавляем новые подзаказы
            for(int i = 0; i < dbData.size(); i++)
            {
                dbConnector.execute("INSERT INTO " + TBL_SUBORDER +
                        "           (" +
                        SubOrderDB.FIELD_MAIN_THR_STATUS + ", " +       //# 1
                        SubOrderDB.FIELD_MAIN_THR_STATUS_MSG + ", " +    //# 2
                        SubOrderDB.FIELD_FILE_THR_STATUS + ", " +        //# 3
                        SubOrderDB.FIELD_FILE_THR_STATUS_MSG + ", " +    //# 4
                        SubOrderDB.FIELD_SUBORDER_STATUS + ", " +        //# 5
                        SubOrderDB.FIELD_SUBORDER_STATUS_MSG + ", " +    //# 6
                        SubOrderDB.FIELD_INFORMATION + ", " +           //# 7
                        SubOrderDB.FIELD_SERVER_NAME + ", " +            //# 8
                        SubOrderDB.FIELD_ORDER_ID + ", " +              //# 9
                        SubOrderDB.FIELD_ERRORCOUNT + ", " +            //# 10
                        SubOrderDB.FIELD_SKIPCOUNT + ", " +             //# 11
                        SubOrderDB.FIELD_REGION_PRIORITY + ", " +       //# 12
                        SubOrderDB.FIELD_REGION_ID + ", " +             //# 13
                        SubOrderDB.FIELD_REGION_NAME + " " +           //# 14
                        ") " +
                        "     VALUES  (" +

                        "'" + SubOrderDB.DEFAULT_FIELD_MAIN_THR_STATUS + "', " +  //# 1
                        "'" + "'" + ", " +                                      //# 2
                        "'" +  SubOrderDB.DEFAULT_FIELD_FILE_THR_STATUS + "', " +  //# 3
                        "'" + "" + "', " +                                     //# 4
                        "'" +  SubOrderDB.STATUS_BLOCKED + "', " +               //# 5
                        "'" + "" + "', " +                                       //# 6
                        "'" + "" + "', " +                                      //# 7
                        "'" +  AppConfiguration.get(CONFIG_SERVER_NAME) + "', " +  //# 8
                        orderId + ", " +                               //# 9
                        0 + ", " +                                       //# 10
                        0 + ", " +                                       //# 11
                        dbData.get(i).priority + ", " +  //# 12
                        dbData.get(i).id + ", " +  //# 13
                        "N'" + dbData.get(i).regionRus + "' "+  //# 14
                        ") " );
            }
            // проверяем количество подзаказов
            List<Map<String, Object>> dbDatatmp;
            dbDatatmp = dbConnector.queryForList("SELECT COUNT(*) 'count' from "+TBL_SUBORDER+" WHERE "+SubOrderDB.FIELD_ORDER_ID+" = "+orderId+" ");

            if (dbDatatmp != null) {
                if(dbDatatmp.size() != 0)
                {
                    if( ! ((Integer)(dbDatatmp.get(0).get("count")) == dbData.size()))
                        return false;
                } else return false;
            } else return false;

            // устанавливаем подзаказам статус новый
            dbConnector.update("UPDATE  " + TBL_SUBORDER   + " " +
                    " SET  " + SubOrderDB.FIELD_SUBORDER_STATUS + " = '" +SubOrderDB.STATUS_NEW+ "' " +
                    " WHERE " + SubOrderDB.FIELD_ORDER_ID + " = " +orderId+ " ;");


            // устанавливаем ордеру статус в работе
            dbConnector.update("UPDATE  " + TBL_ORDER + " " +
                            " SET  " + FIELD_ORDER_STATUS + "  = '" +STATUS_PERFORMED+ "' " +
                            " WHERE " + FIELD_ID + "  = " +orderId+ "  ;");


        }catch (Exception e){
            log.error("Exception while creating suborders structure " + e);
            return false;
        }
        return true;
    }

    public void restoreDB(){
        try {
            String sql = "UPDATE  " + TBL_ORDER + " " +
                    " SET  " + FIELD_ORDER_STATUS + " = '" + STATUS_NEW + "' " +
                    "WHERE " + FIELD_SERVER_NAME + " = '" + AppConfiguration.get("SERVER_NAME") + "' AND " + FIELD_ORDER_STATUS + " = '" + STATUS_BLOCKED + "';";
            dbConnector.update(sql);

            sql = "DELETE FROM  " + TBL_SUBORDER + " " +
                    "WHERE "+SubOrderDB.FIELD_ORDER_ID+" = " + orderId + " AND "+SubOrderDB.FIELD_SUBORDER_STATUS+" = '"+SubOrderDB.STATUS_BLOCKED+"' ";
            dbConnector.execute(sql);
        }catch (Exception e){
            log.error("Exception while reatoring database " + e);
        }
    }

    /**
     * метод очищает таблицу заказов
     */
    public static void clearDB(){
        try {
            dbConnector.execute("DELETE FROM " + TBL_ORDER);
        }catch (Exception e){
            log.error("Exception while clearing order DB: " + e);
        }
    }

    public static List<Map<String, Object>> GetOrderList() throws Exception{

        String query = "SELECT * FROM " +TBL_ORDER;

        return dbConnector.queryForList(query);
    }

    /**
          * метод предназначен для мониторирга статусов подзаказов.
          * в случае если существует хотябы один подзаказ с ошибочным статусом,
          * заказу проставляется статус ошибка
          */
    public static void updateOrdersStatusError() {
        try {
            String query = " update " + TBL_ORDER + " " +
                            " SET " +
                                " stats =  '" + STATUS_ERROR + "'" +
                            " where id in ( select orderId " +
                                           " from " + TBL_SUBORDER + " " +
                                           " where [errorCount] >= " + AppConfiguration.get("SUB_ORDER_MAX_ERRORS_COUNT") + " )";

            dbConnector.update(query);
        } catch (Exception e) {

        }
    }

    /**
          * метод предназначен для мониторирга статусов подзаказов.
          * в случае если все подзаказы одного заказа выполнены,
          * заказу проставляется статус выполнено
          *
          */
    public static void updateOrdersStatusComplete() {
        try {
            String query = "UPDATE   " + TBL_ORDER + "  " +
                    " SET    " +
                    " stats =  '" + STATUS_DONE + "'" +
                    " WHERE id  in (SELECT id from [atrium].[dbo].[OrderCheckingStartFuncV2] ()) AND stats !=  '" + STATUS_ERROR
                    + "' AND stats !=  '" + STATUS_DONE + "'"
                    + " AND stats !=  '" + STATUS_NEW + "'"
                    + " AND stats !=  '" + STATUS_BLOCKED + "'";


            dbConnector.update(query);
        } catch (Exception e) {

        }
    }


}
