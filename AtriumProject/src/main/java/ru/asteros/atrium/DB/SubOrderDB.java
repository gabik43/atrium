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
 * Created by DOLB on 30.08.2015.
 */
public class SubOrderDB {

    //список статусов
    public final static String STATUS_BLOCKED =     "BLOCKED";
    public final static String STATUS_RESERVED =     "RESERVED";
    public final static String STATUS_NEW =         "NEW";
    public final static String STATUS_READY =       "READY";
    public final static String STATUS_PERFORMED =   "PERFORMED";
    public final static String STATUS_DONE =        "DONE";
    public final static String STATUS_ERROR =       "ERROR";


    // установки имен полей в таблице
    public final static String FIELD_SUBORDER_ID = "id";
    public final static String FIELD_ORDER_ID = "orderId";
    public final static String FIELD_ERRORCOUNT = "errorCount";
    public final static String FIELD_SKIPCOUNT = "skipCount";
    public final static String FIELD_REGION_PRIORITY = "priority";
    public final static String FIELD_SERVER_NAME = "serverName";
    public final static String FIELD_SUBORDER_STATUS = "stats";
    public final static String FIELD_SUBORDER_STATUS_MSG = "statsMsg";
    public final static String FIELD_MAIN_THR_STATUS = "documentGenerationStats";
    public final static String FIELD_MAIN_THR_STATUS_MSG = "documentGenerationStatsMsg";
    public final static String FIELD_FILE_THR_STATUS = "fileManagerStats";
    public final static String FIELD_FILE_THR_STATUS_MSG = "fileManagerStatsMsg";
    public final static String FIELD_REGION_ID = "regionId";
    public final static String FIELD_REGION_NAME = "region";
    public final static String FIELD_START_TIME = "startTime";
    public final static String FIELD_END_TIME = "endTime";
    public final static String FIELD_INFORMATION = "information";

    public final static String DEFAULT_FIELD_MAIN_THR_STATUS = "NOT_STARTED";
    public final static String DEFAULT_FIELD_FILE_THR_STATUS = "NOT_STARTED";
    public final static String DEFAULT_FIELD_SUBORDER_STATUS = "SUBORDER_STATUS";

    // имена используемых таблиц
    private final static String TBL_ORDER =      "ORDER";
    private final static String TBL_SUBORDER =   "SUBORDER";
    private final static String TBL_REGIONS =    "REGIONS_DIRECTORY";

    public final static String CONFIG_SERVER_NAME = "SERVER_NAME";
    public final static String CONFIG_MAX_ERROR_COUNT = "SUB_ORDER_MAX_ERRORS_COUNT";
    public final static String CONFIG_SKIP_COUNT = "SUB_ORDER_SKIP_COUNT";

    public final static int MODE_DOCUMENT_GENERATION = 1;
    public final static int MODE_FILE_MANAGER = 2;
    private static JdbcTemplate dbConnector = DataBaseTemplateProvider.getInternalDataBaseJdbcTemplate();
    private static Logger log = LoggerFactory.getLogger(SubOrderDB.class);

    public long subOrderId;
    public int regionID;

    public static SubOrderDB GetFreeSuborder(){
        try {
            List<Map<String, Object>> dbObj;
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Calendar cal = Calendar.getInstance();
            String currentDate = dateFormat.format(cal.getTime());


            String information = "";

            String sql =  " with T1 as (select TOP (1) * from " + TBL_SUBORDER + " " +// set tbl name
                    " where ( " + FIELD_SUBORDER_STATUS + " = '" + STATUS_NEW + "' " +  // set condition new
                    " OR " + FIELD_SUBORDER_STATUS + " = '" + STATUS_ERROR + "'  )" + // set condition error
                    " AND " + FIELD_ERRORCOUNT + " < "+AppConfiguration.get(CONFIG_MAX_ERROR_COUNT) + // set condition error count less than max error count
                    " AND " + FIELD_SKIPCOUNT + " <= 0" + // set condition skip is zero
            " order by " + FIELD_REGION_PRIORITY + " DESC)" +
                    " update T1 set  " +
                    " T1."+FIELD_SUBORDER_STATUS+" = '"+STATUS_RESERVED+"', " + // set status
                    " T1."+FIELD_SERVER_NAME+" = '"+AppConfiguration.get(CONFIG_SERVER_NAME)+"', " + // set server name
                    //" T1."+FIELD_INFORMATION+" = cast ( T1."+FIELD_INFORMATION+" as nvarchar(100000))  + cast (N'" + information + "' as nvarchar(499) ), " + // set information
                    " T1."+FIELD_START_TIME+"  =     CONVERT(datetime,'" + currentDate + "'), " +
                    " T1."+FIELD_SUBORDER_STATUS_MSG+"  =  cast (N'' as nvarchar(5) ), " +
                    " T1."+FIELD_MAIN_THR_STATUS_MSG+"  =  cast (N'' as nvarchar(5) ), " +
                    " T1."+FIELD_FILE_THR_STATUS_MSG+"  =  cast (N'' as nvarchar(5) )" ;

            dbConnector.update(sql);  // sets up start time)

            sql = "select top(1) * from "+TBL_SUBORDER+" " +  // sets up field name and form name
                    " where "+FIELD_SERVER_NAME+" = '" + AppConfiguration.get(CONFIG_SERVER_NAME) + "' " + // condition server name
                    " AND "+FIELD_SUBORDER_STATUS+" = '"+STATUS_RESERVED+"' ";
            dbObj = dbConnector.queryForList(sql); // set condition preformed

            if (dbObj != null) {
                if(dbObj.size() != 0)
                {
                    SubOrderDB subOrderDB = new SubOrderDB();
                    subOrderDB.subOrderId = (Long) dbObj.get(0).get(FIELD_SUBORDER_ID);
                    subOrderDB.regionID = (int) dbObj.get(0).get(FIELD_REGION_ID);

                    sql =  "update "+ TBL_SUBORDER +" set  " + // set tbl name
                            " "+FIELD_SUBORDER_STATUS+" = '"+STATUS_PERFORMED+"' " +
                            " where "+FIELD_SUBORDER_ID + "=   " + (Long) dbObj.get(0).get(FIELD_SUBORDER_ID);
                    dbConnector.update(sql); // set condition

                    return subOrderDB;
                }
            }
        } catch (Exception e) {
            log.error("Exception while getting free suborder : " + e);
        }
        return null;
    }


    public void setStPerformed(String stMsg, int mode){
        setStatus(STATUS_PERFORMED, stMsg, mode, subOrderId);
    }

    public static void setStPerformed(String stMsg, int mode, long suborderID){
        setStatus(STATUS_PERFORMED, stMsg, mode, suborderID);
    }

    public void setStDone(String stMsg, int mode){
        setStatus(STATUS_DONE, stMsg, mode, subOrderId);
    }

    public static void setStDone(String stMsg, int mode, long suborderID){
        setStatus(STATUS_DONE, stMsg, mode, suborderID);
    }

    public void setStError(String stMsg, int mode){
        setStatus(STATUS_ERROR, stMsg, mode, subOrderId);
    }

    public static void setStError(String stMsg, int mode, long suborderID){
        setStatus(STATUS_ERROR, stMsg, mode, suborderID);
    }


    public static void ValidateSuborders(){
        String information;
        try {
            String sql;

            // сбрасываем статус зарезервированно на статус новый
            sql =  "update "+ TBL_SUBORDER +" set  " + // set tbl name
                    " "+FIELD_SUBORDER_STATUS+" = '"+STATUS_NEW+"'  " + // decrement skip count
                    " where "+FIELD_SUBORDER_STATUS + "=  '" + STATUS_RESERVED + "'";
            dbConnector.update(sql); // set condition

            // уменьшаем значение skip count для всех записей
            sql =  "update "+ TBL_SUBORDER +" set  " + // set tbl name
                    " "+FIELD_SKIPCOUNT+" = "+FIELD_SKIPCOUNT+"  - 1" + // decrement skip count
                    " where "+FIELD_SKIPCOUNT + " > 0 ";
            dbConnector.update(sql); // set condition


            information = "Превышено количество ошибок";
            sql = "update "+TBL_SUBORDER+" set  " + // set tbl name
                    " "+FIELD_SUBORDER_STATUS+" = '"+STATUS_ERROR+"', " + // set status
                    " "+FIELD_SUBORDER_STATUS_MSG+" = cast ( "+FIELD_MAIN_THR_STATUS_MSG+" as nvarchar(max))  + cast ( "+FIELD_FILE_THR_STATUS_MSG+"  as nvarchar(max) ), " + // set status msg
                    " "+FIELD_INFORMATION+" = cast ( ["+FIELD_INFORMATION+"] as nvarchar(max))  + cast (N'"+information+"' as nvarchar(max) ) " + // set information
                    " where "+ FIELD_ERRORCOUNT +" >= "+AppConfiguration.get(CONFIG_MAX_ERROR_COUNT)+" AND " +  // set condition
                    " "+FIELD_SUBORDER_STATUS+" != '"+STATUS_ERROR+"' ";
            dbConnector.update(sql); // set condition

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Calendar cal = Calendar.getInstance();
            String currentDate = dateFormat.format(cal.getTime());

            information = "Обработка завершена";

             sql =  "update "+TBL_SUBORDER+" set  " + // set tbl name
                    " "+FIELD_SUBORDER_STATUS+" = '"+STATUS_DONE+"', " + // set status
                    " "+FIELD_SUBORDER_STATUS_MSG+" = cast ( "+FIELD_MAIN_THR_STATUS_MSG+" as nvarchar(max))  + cast ( "+FIELD_FILE_THR_STATUS_MSG+"  as nvarchar(max) ), " + // set status msg
                    " "+FIELD_INFORMATION+" = cast ( ["+FIELD_INFORMATION+"] as nvarchar(max))  + cast (N'"+information+"' as nvarchar(max) ) ," + // set information
                    " "+FIELD_END_TIME+" = CONVERT(datetime,'" + currentDate + "')" + // sets up end time
                    " where (("+FIELD_MAIN_THR_STATUS+" = '"+STATUS_DONE+"' AND " +  // set condition
                    " "+FIELD_FILE_THR_STATUS+" = '"+DEFAULT_FIELD_FILE_THR_STATUS+"' ) OR " +
                    " ( "+FIELD_MAIN_THR_STATUS+" = '"+STATUS_DONE+"'  AND " +
                    " "+FIELD_FILE_THR_STATUS+" = '"+STATUS_DONE+"')) AND " +
                    " "+FIELD_SUBORDER_STATUS+" != '"+STATUS_DONE+"' AND " +
                     " "+FIELD_SUBORDER_STATUS+" != '"+STATUS_ERROR+"' AND " +
                     " "+FIELD_MAIN_THR_STATUS+" != '"+STATUS_ERROR+"' AND " +
                     " "+FIELD_FILE_THR_STATUS+" != '"+STATUS_ERROR+"' ";
            dbConnector.update(sql); // #1


            information = "Ошибка обработки";
            sql = "update "+TBL_SUBORDER+" set  " + // set tbl name
                    " "+FIELD_SUBORDER_STATUS+" = '"+STATUS_ERROR+"', " + // set status
                    " "+FIELD_ERRORCOUNT+" = "+FIELD_ERRORCOUNT+"  + 1," + // set error count
                    " "+FIELD_SKIPCOUNT+" = "+AppConfiguration.get(CONFIG_SKIP_COUNT)+"," + // set skip count
                    " "+FIELD_SUBORDER_STATUS_MSG+" =  "+FIELD_MAIN_THR_STATUS_MSG+"  +  "+FIELD_FILE_THR_STATUS_MSG+"  " + // set status msg
                    //" "+FIELD_INFORMATION+" = cast ("+FIELD_INFORMATION+"  + cast (N'"+information+"' as nvarchar(max) )as nvarchar(max)) " + // set information
                    " where ( "+FIELD_MAIN_THR_STATUS+" = '"+STATUS_ERROR+"' OR " +  // set condition
                    " "+FIELD_FILE_THR_STATUS+" = '"+STATUS_ERROR+"' ) AND " +
                    " "+FIELD_SUBORDER_STATUS+" != '"+STATUS_ERROR+"' AND " +
                    " "+FIELD_SUBORDER_STATUS+" != '"+STATUS_DONE+"' ";
            dbConnector.update(sql);


        } catch (Exception e) {
            log.error("Exception while validating : " + e);
        }
    }

    public static void setStatus(String status, String stMsg, int mode, long suborderID){
        try {
            String information = "";

            String sql =  "update "+TBL_SUBORDER+" set  " + // set tbl name
                    " "+getStatusFieldNameFromMode(mode)+" = '"+status+"', " + // set status
                    " "+getStatusMSGFieldNameFromMode(mode)+" = N'"+stMsg+".    ', " + // set status message
                    //" "+FIELD_INFORMATION+" = "+FIELD_INFORMATION+"  + cast (N'"+information+"' as nvarchar(max) ), " + // set information
                    " "+FIELD_SERVER_NAME+" = '"+AppConfiguration.get(CONFIG_SERVER_NAME)+"' " + // set server name

                    " where "+FIELD_SUBORDER_ID+" = "+suborderID;
            dbConnector.update(sql); // set condition

            // update suborder ststus msg
            sql = "update "+TBL_SUBORDER+" set  " + // set tbl name
                    " "+FIELD_SUBORDER_STATUS_MSG+" = cast ( "+FIELD_MAIN_THR_STATUS_MSG+" as nvarchar(2000))  + cast ( "+FIELD_FILE_THR_STATUS_MSG+"  as nvarchar(2000) ) " + // set status
                    " where "+FIELD_SUBORDER_ID+" = "+suborderID+" ";
            dbConnector.update(sql); // set condition

        } catch (Exception e) {
            log.error("Exception while setStatus : " + e);
        }
    }

    private static String getStatusFieldNameFromMode(int mode){
        String tmpFieldName = FIELD_MAIN_THR_STATUS; // default value
        switch (mode){
            case MODE_DOCUMENT_GENERATION:
                tmpFieldName = FIELD_MAIN_THR_STATUS;
                break;
            case MODE_FILE_MANAGER:
                tmpFieldName = FIELD_FILE_THR_STATUS;
                break;
        }
        return tmpFieldName;
    }

    private static String getStatusMSGFieldNameFromMode(int mode){
        String tmpFieldName = FIELD_MAIN_THR_STATUS_MSG; // default value
        switch (mode){
            case MODE_DOCUMENT_GENERATION:
                tmpFieldName = FIELD_MAIN_THR_STATUS_MSG;
                break;
            case MODE_FILE_MANAGER:
                tmpFieldName = FIELD_FILE_THR_STATUS_MSG;
                break;
        }
        return tmpFieldName;
    }


    /**
     * метод очищает таблицу
     */
    public static void clearDB() {
        try {
            dbConnector.execute("DELETE FROM " + TBL_SUBORDER);
        }catch (Exception e){
            log.error("Exception while clearing order DB: " + e);
        }
    }

    public static List<Map<String, Object>> GetSuborderList(long orderId) throws Exception {
        String query = "SELECT * FROM  " + TBL_SUBORDER + "  WHERE orderId = " + orderId;
        return dbConnector.queryForList(query);
    }
}