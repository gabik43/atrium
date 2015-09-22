/*
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.asteros.atrium.DB.RegionInfo;
import ru.asteros.atrium.DataBaseTemplateProvider.DataBaseTemplateProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

*/
/**
 * Created by DOLB on 13-Aug-15.
 *//*

public class test {
    private static Logger log = LoggerFactory.getLogger(test.class);
    private static JdbcTemplate dbConnector = DataBaseTemplateProvider.getInternalDataBaseJdbcTemplate();
    private static String regionsTableName = "REGIONS_DIRECTORY";

    public static void main(String[] args) {

//        log.info(getRecord("45").toString());
//        addRecord(new RegionInfo("3500", "TestRegion", "TestMacroRegion", "ТестРегион", "33", "12345@m.ru", "3", "15"));

//        removeRecord("3531");
//        updateRecord("3535", new RegionInfo("3536", "TestRegion2", "TestMacroRegion2", "ТестРегион2", "33", "12345@mail.ru2", "30", "150"));


    }

    public static List<RegionInfo> getAllRecords() {

        List<RegionInfo> data = new ArrayList<>();

        String query = "SELECT * FROM " + regionsTableName;
        List<Map<String, Object>> dbData = dbConnector.queryForList(query);

        try {
            for (Map<String, Object> record : dbData) {
               // data.add(new RegionInfo(record.get("BRANCH_ID").toString(), record.get("BRANCH_NAME").toString(), record.get("MACRO_REGION").toString(), record.get("MACRO_REG_RUS").toString(), record.get("PRIORITY").toString(), record.get("E-MAIL").toString(), record.get("LOGO_ID").toString(), record.get("STATUS").toString()));
            }
        } catch (Exception e) {
            log.error("Transfer from data to RegionInfo list was failed: " + e.getMessage());
            e.printStackTrace();
        }

        return data;
    }

    public static RegionInfo getRecord (String branchId) {

*/
/*        String query = "SELECT * FROM " + regionsTableName + " WHERE [BRANCH_ID] = " + branchId;
        List<Map<String, Object>> dbData = dbConnector.queryForList(query);

        try {
            Map<String, Object> record = dbData.get(0);
            return new RegionInfo(
                    record.get("BRANCH_ID").toString(),
                    record.get("BRANCH_NAME").toString(),
                    record.get("MACRO_REGION").toString(),
                    record.get("MACRO_REG_RUS").toString(),
                    record.get("PRIORITY").toString(),
                    record.get("E-MAIL").toString(),
                    record.get("LOGO_ID").toString(),
                    record.get("STATUS").toString());
        } catch (Exception e) {
            log.error("Transfer from data to RegionInfo object was failed" + e.getMessage());
            e.printStackTrace();
        }

        return null;*//*

    }

    public static void addRecord(RegionInfo newRecord) {

        try {

            if (newRecord.id.equals("")) {
                throw new Exception("New record wasn't writed in DB. Branch id is empty");
            }

            String query = "INSERT INTO " + regionsTableName +
                    " ([BRANCH_ID]" +
                    " ,[BRANCH_NAME]" +
                    " ,[MACRO_REGION]" +
                    " ,[MACRO_REG_RUS]" +
                    " ,[PRIORITY]" +
                    " ,[E-MAIL]" +
                    " ,[LOGO_ID]" +
                    " ,[STATUS])" +
                    " VALUES" +
                    " (  " + newRecord.id +
                    " , '" + newRecord.regionRus + "' " +
                    " , '" + newRecord.macroRegionEng + "' " +
                    " , N'" + newRecord.macroRegionRus + "' " +
                    " ,  " + newRecord.priority +
                    " , '" + newRecord.email + "' " +
                    " , '" + newRecord.logoId + "' " +
                    " ,  " + newRecord.status + " )";

            dbConnector.update(query);

        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }


    }

    public static void removeRecord(String branchId) {

        try {
            String query = "DELETE FROM " + regionsTableName + " WHERE BRANCH_ID = " + branchId;
            dbConnector.update(query);
        } catch (Exception e) {
            log.error("Remove was failed" + e.getMessage());
            e.printStackTrace();
        }

    }

    public static void updateRecord(String branchId, RegionInfo newRecord) {

        try {
            if (newRecord==null) {
                throw new Exception("RegionInfo object is null");
            }

            if(newRecord.id.equals("")) {
                throw new Exception("New branch id is empty");
            }

            String query = "UPDATE " + regionsTableName +
                    " SET " +
                    " [BRANCH_ID] =  " + newRecord.id + " ," +
                    " [BRANCH_NAME] =  '" + newRecord.regionRus + "' ," +
                    " [MACRO_REGION] =  '" + newRecord.macroRegionEng + "' ," +
                    " [MACRO_REG_RUS] =  N'" + newRecord.macroRegionRus + "' ," +
                    " [PRIORITY] =  " + newRecord.priority + " ," +
                    " [E-MAIL] =  '" + newRecord.email + "' ," +
                    " [LOGO_ID] =  '" + newRecord.logoId + "' ," +
                    " [STATUS] =  " + newRecord.status +
                    " WHERE [BRANCH_ID] = " + branchId;

            dbConnector.update(query);

        } catch (Exception e) {
            log.error("Update was failed. " + e.getMessage());
            e.printStackTrace();
        }

    }

}
*/
