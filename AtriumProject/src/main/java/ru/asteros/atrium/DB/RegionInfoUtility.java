package ru.asteros.atrium.DB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.asteros.atrium.DataBaseTemplateProvider.DataBaseTemplateProvider;
import ru.asteros.atrium.Utils.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Created by Timofey on 02.09.2015.
 */
public class RegionInfoUtility {

    private static JdbcTemplate dbConnector = DataBaseTemplateProvider.getInternalDataBaseJdbcTemplate();
    private static Logger log = LoggerFactory.getLogger(RegionInfoUtility.class);
    private final static String REGIONS_TABLE_NAME = "REGIONS_DIRECTORY_NEW";
    private final static String ID_IN_DB = "id";
    private final static String ID = "BRANCH_ID";
    private final static String REGION_ENG = "REGION_ENG";
    private final static String REGION_RUS = "REGION_RUS";
    private final static String MACRO_REGION_ENG = "MACRO_REGION_ENG";
    private final static String MACRO_REGION_RUS = "MACRO_REGION_RUS";
    private final static String PRIORITY = "PRIORITY";
    private final static String E_MAIL = "E-MAIL";
    private final static String LOGO_ID = "LOGO_ID";
    private final static String STATUS = "STATUS";
    private final static String PHONE_B2C = "B2C_PHONE_SHORT";
    private final static String PHONE_B2B = "B2B_PHONE_SHORT";
    private final static String DELIVERY_GROUP_STATUS = "DELIVERY_GROUP_STATUS";


    public final static String PAIR_DIVIDER =        ";";
    public final static String ELEMENT_DIVIDER =     "-";

    public static List<RegionInfo> getAllRecords() {

        List<RegionInfo> data = new ArrayList<>();

        String query = "SELECT * FROM " + REGIONS_TABLE_NAME + " ORDER BY " + ID;
        List<Map<String, Object>> dbData = dbConnector.queryForList(query);

        try {
            for (Map<String, Object> record : dbData) {
                data.add(new RegionInfo(record.get(ID_IN_DB).toString(),
                        record.get(ID).toString(),
                        record.get(REGION_ENG).toString(),
                        record.get(REGION_RUS).toString(),
                        record.get(MACRO_REGION_ENG).toString(),
                        record.get(MACRO_REGION_RUS).toString(),
                        record.get(PRIORITY).toString(),
                        record.get(E_MAIL).toString(),
                        record.get(LOGO_ID).toString(),
                        record.get(STATUS).toString(),
                        record.get(PHONE_B2C).toString(),
                        record.get(PHONE_B2B).toString(),
                        record.get(DELIVERY_GROUP_STATUS)!=null? record.get(DELIVERY_GROUP_STATUS).toString() : "",
                        formatDeliveryGroupStatus(record.get(DELIVERY_GROUP_STATUS)!=null? record.get(DELIVERY_GROUP_STATUS).toString() : "")));
            }
        } catch (Exception e) {
            log.error("Transfer from data to RegionInfo list was failed: " + e.getMessage());
            e.printStackTrace();
        }

        return data;
    }

    public static List<RegionInfo> getRecordsWithStatusEnable() {

        String query = "SELECT * FROM " + REGIONS_TABLE_NAME + " WHERE "+ STATUS +"  = 1";
        List<Map<String, Object>> dbData = dbConnector.queryForList(query);

        List<RegionInfo> regionInfoList = new ArrayList<>();

        try {
            for (Map<String, Object> record : dbData) {
                regionInfoList.add(new RegionInfo(record.get(ID_IN_DB).toString(),
                        record.get(ID).toString(),
                        record.get(REGION_ENG).toString(),
                        record.get(REGION_RUS).toString(),
                        record.get(MACRO_REGION_ENG).toString(),
                        record.get(MACRO_REGION_RUS).toString(),
                        record.get(PRIORITY).toString(),
                        record.get(E_MAIL).toString(),
                        record.get(LOGO_ID).toString(),
                        record.get(STATUS).toString(),
                        record.get(PHONE_B2C).toString(),
                        record.get(PHONE_B2B).toString(),
                        record.get(DELIVERY_GROUP_STATUS)!=null? record.get(DELIVERY_GROUP_STATUS).toString() : "",
                        formatDeliveryGroupStatus(record.get(DELIVERY_GROUP_STATUS)!=null? record.get(DELIVERY_GROUP_STATUS).toString() : "")));
            }
        } catch (Exception e) {
            log.error("Transfer from data to RegionInfo list was failed: " + e.getMessage());
            e.printStackTrace();
        }

        return regionInfoList;
    }

    public static RegionInfo getRecord (int id) {

        String query = "SELECT * FROM " + REGIONS_TABLE_NAME + " WHERE " + ID + " = " + id;
        List<Map<String, Object>> dbData = dbConnector.queryForList(query);

        try {
            if (dbData!=null&&dbData.size()!=0) {
                Map<String, Object> record = dbData.get(0);
                return new RegionInfo(record.get(ID_IN_DB).toString(),
                        record.get(ID).toString(),
                        record.get(REGION_ENG).toString(),
                        record.get(REGION_RUS).toString(),
                        record.get(MACRO_REGION_ENG).toString(),
                        record.get(MACRO_REGION_RUS).toString(),
                        record.get(PRIORITY).toString(),
                        record.get(E_MAIL).toString(),
                        record.get(LOGO_ID).toString(),
                        record.get(STATUS).toString(),
                        record.get(PHONE_B2C).toString(),
                        record.get(PHONE_B2B).toString(),
                        record.get(DELIVERY_GROUP_STATUS) != null ? record.get(DELIVERY_GROUP_STATUS).toString() : "",
                        formatDeliveryGroupStatus(record.get(DELIVERY_GROUP_STATUS)!=null? record.get(DELIVERY_GROUP_STATUS).toString() : ""));
            }
        } catch (Exception e) {
            log.error("Transfer from data to RegionInfo object was failed: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public static void addRecord(RegionInfo newRecord) {

        try {

            if (newRecord.id.equals("")) {
                throw new Exception("New record wasn't writed in DB. Branch id is empty");
            }

            String query = "INSERT INTO " + REGIONS_TABLE_NAME +
                    " ([" + ID + "] " +
                    " ,[" + REGION_ENG + "] " +
                    " ,[" + REGION_RUS + "] " +
                    " ,[" + MACRO_REGION_ENG + "] " +
                    " ,[" + MACRO_REGION_RUS + "] " +
                    " ,[" + PRIORITY + "] " +
                    " ,[" + E_MAIL + "] " +
                    " ,[" + LOGO_ID + "] " +
                    " ,[" + STATUS + "] " +
                    " ,[" + PHONE_B2C + "] " +
                    " ,[" + PHONE_B2B + "] " +
                    " ,[" + DELIVERY_GROUP_STATUS + "]) " +
                    " VALUES " +
                    " (  " + newRecord.id +
                    " , '" + newRecord.regionEng + "' " +
                    " , N'" + newRecord.regionRus + "' " +
                    " , '" + newRecord.macroRegionEng + "' " +
                    " , N'" + newRecord.macroRegionRus + "' " +
                    " ,  " + newRecord.priority +
                    " , '" + newRecord.email + "' " +
                    " , '" + newRecord.logoId + "' " +
                    " ,  " + newRecord.status +
                    " , '" + newRecord.phoneB2C + "' " +
                    " , '" + newRecord.phoneB2B + "' " +
                    " , '" + newRecord.deliveryGroupStatus + "' )";

            dbConnector.update(query);

        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }


    }

    public static void removeRecord(String idInDB) {

        try {
            String query = "DELETE FROM " + REGIONS_TABLE_NAME + " WHERE " + ID_IN_DB + " = " + idInDB;
            dbConnector.update(query);
        } catch (Exception e) {
            log.error("Remove was failed" + e.getMessage());
            e.printStackTrace();
        }

    }

    public static void updateRecord(String idInDB, RegionInfo newRecord) {

        try {
            if (newRecord==null) {
                throw new Exception("RegionInfo object is null");
            }

            if(newRecord.id.equals("")) {
                throw new Exception("New branch id is empty");
            }

            String query = "UPDATE " + REGIONS_TABLE_NAME +
                    " SET " +
                    " [" + ID + "] =  " + newRecord.id + " ," +
                    " [" + REGION_ENG + "] =  '" + newRecord.regionEng + "' ," +
                    " [" + REGION_RUS + "] =  N'" + newRecord.regionRus + "' ," +
                    " [" + MACRO_REGION_ENG + "] =  '" + newRecord.macroRegionEng + "' ," +
                    " [" + MACRO_REGION_RUS + "] =  N'" + newRecord.macroRegionRus + "' ," +
                    " [" + PRIORITY + "] =  " + newRecord.priority + " ," +
                    " [" + E_MAIL + "] =  '" + newRecord.email + "' ," +
                    " [" + LOGO_ID + "] =  '" + newRecord.logoId + "' ," +
                    " [" + STATUS + "] =  " + newRecord.status + " ," +
                    " [" + PHONE_B2C + "] =  '" + newRecord.phoneB2C + "' ," +
                    " [" + PHONE_B2B + "] =  '" + newRecord.phoneB2B + "' ," +
                    " [" + DELIVERY_GROUP_STATUS + "] =  '" + newRecord.deliveryGroupStatus + "' " +
                    " WHERE [" + ID_IN_DB + "] = " + idInDB;

            dbConnector.update(query);

        } catch (Exception e) {
            log.error("Update was failed. " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static List<Pair<String, String>> formatDeliveryGroupStatus(String delStatus){

        List<Pair<String, String>> returnObj = new ArrayList<Pair<String, String>>();
        String[] parts = delStatus.split(PAIR_DIVIDER);
        for(String pairStr:parts){
            String[] pairArr = pairStr.split(ELEMENT_DIVIDER);
            if (pairArr.length != 2) continue;

            for(int i = 0; i < pairArr.length; i++){
                pairArr[i] = pairArr[i].trim();
                pairArr[i] = StringEscapeUtils.escapeJava(pairArr[i]);;

                if(pairArr[i].contains("null") && pairArr[i].length() < 6)
                    pairArr[i] = "null";
            }

            if(pairArr[0].equals("") || pairArr[1].equals(""))
                continue;

            Pair<String,String> pair = new Pair(pairArr[0], pairArr[1]);
            returnObj.add(pair);
        }

        return returnObj;
    }

}