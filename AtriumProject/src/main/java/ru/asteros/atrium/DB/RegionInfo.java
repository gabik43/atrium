package ru.asteros.atrium.DB;

import org.springframework.jdbc.core.JdbcTemplate;
import ru.asteros.atrium.DataBaseTemplateProvider.DataBaseTemplateProvider;
import ru.asteros.atrium.Utils.Pair;

import java.util.*;

/**
 * Created by A.Gabdrakhmanov on 19.08.2015.
 */
public class RegionInfo {

    public String idInDB = "";
    public String id = "";
    public String regionEng = "";
    public String regionRus = "";
    public String macroRegionEng = "";
    public String macroRegionRus = "";
    public String priority = "";
    public String email = "";
    public String logoId = "";
    public String status = "";
    public String phoneB2C = "";
    public String phoneB2B = "";
    public String deliveryGroupStatus = "";
    public List<Pair<String, String>>  deliveryGroupStatusArray = new ArrayList<Pair<String, String>>();

    private static JdbcTemplate dbConnector = DataBaseTemplateProvider.getInternalDataBaseJdbcTemplate();

    public RegionInfo(String id, String regionEng, String regionRus, String macroRegionEng, String macroRegionRus, String priority,
                      String email, String logoId, String status, String phoneB2C, String phoneB2B, String deliveryGroupStatus,
                      List<Pair<String, String>>  deliveryGroupStatusArray){
        this.id = id;
        this.regionEng = regionEng;
        this.regionRus = regionRus;
        this.macroRegionEng = macroRegionEng;
        this.macroRegionRus = macroRegionRus;
        this.logoId = logoId;
        this.priority = priority;
        this.email = email;
        this.status = status;
        this.phoneB2C = phoneB2C;
        this.phoneB2B = phoneB2B;
        this.deliveryGroupStatus = deliveryGroupStatus;
        this.deliveryGroupStatusArray = deliveryGroupStatusArray;
    }

    public RegionInfo(String idInDB, String id, String regionEng, String regionRus, String macroRegionEng, String macroRegionRus, String priority,
                      String email, String logoId, String status, String phoneB2C, String phoneB2B, String deliveryGroupStatus, List<Pair<String, String>>  deliveryGroupStatusArray){
        this(id, regionEng, regionRus, macroRegionEng, macroRegionRus, priority, email, logoId, status, phoneB2C, phoneB2B, deliveryGroupStatus, deliveryGroupStatusArray);
        this.idInDB = idInDB;
    }

    public RegionInfo(RegionInfo regionInfoExt){
        this(regionInfoExt.id, regionInfoExt.regionEng, regionInfoExt.regionRus, regionInfoExt.macroRegionEng, regionInfoExt.macroRegionRus, regionInfoExt.priority,
                regionInfoExt.email, regionInfoExt.logoId, regionInfoExt.status, regionInfoExt.phoneB2C, regionInfoExt.phoneB2B, regionInfoExt.deliveryGroupStatus,
                regionInfoExt.deliveryGroupStatusArray);
    }

    public RegionInfo(){
    }

}
