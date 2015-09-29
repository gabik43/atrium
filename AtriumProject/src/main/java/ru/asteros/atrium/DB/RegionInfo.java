package ru.asteros.atrium.DB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.asteros.atrium.Utils.Pair;
import ru.asteros.atrium.stringVerification.ClearedStringAndString;
import ru.asteros.atrium.stringVerification.StringVerification;

import java.util.ArrayList;
import java.util.List;

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

    private static Logger log = LoggerFactory.getLogger(RegionInfo.class);

    public RegionInfo(String id, String regionEng, String regionRus, String macroRegionEng, String macroRegionRus, String priority,
                      String email, String logoId, String status, String phoneB2C, String phoneB2B, String deliveryGroupStatus,
                      List<Pair<String, String>>  deliveryGroupStatusArray){
        this.id = getStringWitchoutControllSymbols(id);
        this.regionEng = getStringWitchoutControllSymbols(regionEng);
        this.regionRus = getStringWitchoutControllSymbols(regionRus);
        this.macroRegionEng = getStringWitchoutControllSymbols(macroRegionEng);
        this.macroRegionRus = getStringWitchoutControllSymbols(macroRegionRus);
        this.logoId = getStringWitchoutControllSymbols(logoId);
        this.priority = getStringWitchoutControllSymbols(priority);
        this.email = getStringWitchoutControllSymbols(email);
        this.status = getStringWitchoutControllSymbols(status);
        this.phoneB2C = getStringWitchoutControllSymbols(phoneB2C);
        this.phoneB2B = getStringWitchoutControllSymbols(phoneB2B);
        this.deliveryGroupStatus = getStringWitchoutControllSymbols(deliveryGroupStatus);
        this.deliveryGroupStatusArray = deliveryGroupStatusArray;
    }

    private String getStringWitchoutControllSymbols(String inputString){
        ClearedStringAndString clearedStringAndString = StringVerification.getClearedStringAndStringWitchErrorSymbols(inputString);
        return clearedStringAndString.clearedString;
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
