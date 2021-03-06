package ru.asteros.atrium.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.asteros.atrium.AppConfiguration;
import ru.asteros.atrium.DB.RegionInfo;
import ru.asteros.atrium.DB.TemplateDB;
import ru.asteros.atrium.DB.TemplateInfo;
import ru.asteros.atrium.DataBaseTemplateProvider.DataBaseTemplateProvider;
import ru.asteros.atrium.ProjectException.AtriumError;
import ru.asteros.atrium.ProjectException.AtriumException;
import ru.asteros.atrium.Report.ReportDWH;
import ru.asteros.atrium.Utils.Pair;
import ru.asteros.atrium.stringVerification.ClearedStringAndString;
import ru.asteros.atrium.stringVerification.StringVerification;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by A.Gabdrakhmanov on 10.08.2015.
 * Класс, генерирующий xml. Входными данными являются поля из DWH таблицы.
 */
public class GeneratorXML {
    private static Logger log = LoggerFactory.getLogger(GeneratorXML.class);


    private static String pathJobInputDirectory = AppConfiguration.get("PATH_TO_XML_FOR_XPRESSION");

    private static JdbcTemplate wayDatabase = DataBaseTemplateProvider.getDwhJdbcTemplate();



    private static FileWriter fw;
    private static OutputStreamWriter char_output;


    private static ServerOption serverOption = new ServerOption();

    private static TemplateInfo templateInfo = new TemplateInfo();

    /*Информация, уникальная для региона*/
    private static RegionInfo regionInfo = new RegionInfo();

    private static String orderId;

    /*Номер столбца отведенного для имени таблицы в описании полей*/
    private final static int TABLE_NAME = 0;
    /*Номер столбца отведенного для имени поля в базе данных в описании полей*/
    private final static int FIELD_NAME_IN_DWH = 1;
    /*Номер столбца отведенного для имени поля в xml в описании полей*/
    private final static int FIELD_NAME_IN_XML = 2;

    private final static String REPORT_TABLE_NAME = "DOC_OUT_CR1700_CLNT_REPORT";
    private final static String ACT_TABLE_NAME = "DOC_OUT_CR1700_CLNT_ACT_PS";
    private final static String DETAIL_TABLE_NAME = "DOC_OUT_CR1700_CLNT_DETAIL";
    private final static String FACTURA_TABLE_NAME = "DOC_OUT_CR1700_CLNT_FACTURA";
    private final static String PAYMENT_TABLE_NAME = "DOC_OUT_CR1700_CLNT_PAYMENT";

    /* Префикс таблицы. Предназначен для уникальности запроса: В разных таблицах есть поля с одинаковыми именами.*/
    private final static String PREFIX_REPORT = "REPORT__";
    /*Массив, формирующий строку запроса.
    Формат: {"Имя таблицы источника", "Имя поля в базе данных (источник)", "Имя поля в xml (назначение)"}*/
    private final static String[][] ALL_FIELD_REPORT = {
            {REPORT_TABLE_NAME, "P_ITG_CLNT_ID", "P_ITG_CLNT_ID"},
            {REPORT_TABLE_NAME, "ACCOUNT", "ACCOUNT"},
            {REPORT_TABLE_NAME, "CLNT_ID", "CLNT_ID"},
            {REPORT_TABLE_NAME, "BRANCH_ID", "BRANCH_ID"},
            {REPORT_TABLE_NAME, "CITY_NAME", "CITY_NAME"},
            {REPORT_TABLE_NAME, "NAME", "NAME"},
            {REPORT_TABLE_NAME, "ADDR_INDEX", "ADDR_INDEX"},
            {REPORT_TABLE_NAME, "ADDR_CITY", "ADDR_CITY"},
            {REPORT_TABLE_NAME, "ADDR_STREET", "ADDR_STREET"},
            {REPORT_TABLE_NAME, "ADDR_HOUSE", "ADDR_HOUSE"},
            {REPORT_TABLE_NAME, "CONTACT_FACE", "CONTACT_FACE"},
            {REPORT_TABLE_NAME, "LOC_INDEX", "LOC_INDEX"},
            {REPORT_TABLE_NAME, "LOC_CITY", "LOC_CITY"},
            {REPORT_TABLE_NAME, "LOC_STREET", "LOC_STREET"},
            {REPORT_TABLE_NAME, "LOC_HOUSE", "LOC_HOUSE"},
            {REPORT_TABLE_NAME, "CONTRACT_INN", "CONTRACT_INN"},
            {REPORT_TABLE_NAME, "CONTRACT_KPP", "CONTRACT_KPP"},
            {REPORT_TABLE_NAME, "CONTRACT_NUM", "CONTRACT_NUM"},
            {REPORT_TABLE_NAME, "CL_CONTACT_PHONE", "CL_CONTACT_PHONE"},
            {REPORT_TABLE_NAME, "SHORT", "SHORT"},
            {REPORT_TABLE_NAME, "DEF", "DEF"},
            {REPORT_TABLE_NAME, "COMPANY_NAME", "COMPANY_NAME"},
            {REPORT_TABLE_NAME, "JUR_ADDRESS", "JUR_ADDRESS"},
            {REPORT_TABLE_NAME, "DELIVERY_ADDRESS", "DELIVERY_ADDRESS"},
            {REPORT_TABLE_NAME, "DIRECTOR", "DIRECTOR"},
            {REPORT_TABLE_NAME, "BUHGALTER", "BUHGALTER"},
            {REPORT_TABLE_NAME, "COMPANY_PHONE", "COMPANY_PHONE"},
            {REPORT_TABLE_NAME, "COMPANY_INN", "COMPANY_INN"},
            {REPORT_TABLE_NAME, "COMPANY_KPP", "COMPANY_KPP"},
            {REPORT_TABLE_NAME, "BANK_NAME", "BANK_NAME"},
            {REPORT_TABLE_NAME, "BIK", "BIK"},
            {REPORT_TABLE_NAME, "KS", "KS"},
            {REPORT_TABLE_NAME, "RS", "RS"},
            {REPORT_TABLE_NAME, "CJT_ID", "CJT_ID"},
            {REPORT_TABLE_NAME, "TAX_RATE", "TAX_RATE"},
            {REPORT_TABLE_NAME, "TAX_RATE_PERCENT", "TAX_RATE_PERCENT"},
            {REPORT_TABLE_NAME, "OB_SDATE", "OB_SDATE"},
            {REPORT_TABLE_NAME, "OB_EDATE", "OB_EDATE"},
            {REPORT_TABLE_NAME, "DATE_NEXT_MONTH", "DATE_NEXT_MONTH"},
            {REPORT_TABLE_NAME, "ITGC_ID", "ITGC_ID"},
            {REPORT_TABLE_NAME, "PDOC_STR", "PDOC_STR"},
            {REPORT_TABLE_NAME, "BDT_ID", "BDT_ID"},
            {REPORT_TABLE_NAME, "BDT_NAME", "BDT_NAME"},
            {REPORT_TABLE_NAME, "DG_ID", "DG_ID"},
            {REPORT_TABLE_NAME, "DELIVERY_GROUP", "DELIVERY_GROUP"},
            {ACT_TABLE_NAME, "V_TEMP_MONTH_NAME", "V_TEMP_MONTH_NAME"}};

    /* Префикс таблицы. Предназначен для уникальности запроса: В разных таблицах есть поля с одинаковыми именами.*/
    private final static String PREFIX_PAY = "PAY__";
    /*Массив, формирующий строку запроса.
    Формат: {"Имя таблицы источника", "Имя поля в базе данных (источник)", "Имя поля в xml (назначение)"}*/
    private final static String[][] ALL_FIELD_PAY = {
            {REPORT_TABLE_NAME, "P_ITG_CLNT_ID", "P_ITG_CLNT_ID"},
            {REPORT_TABLE_NAME, "START_BAL_$", "START_BAL"},
            {REPORT_TABLE_NAME, "END_BAL_$", "END_BAL"},
            {REPORT_TABLE_NAME, "ALL_CHARGE", "ALL_CHARGE"},
            {REPORT_TABLE_NAME, "DISCOUNT_$", "DISCOUNT"},
            {REPORT_TABLE_NAME, "FIX_DISCOUNT_$", "FIX_DISCOUNT"},
            {REPORT_TABLE_NAME, "ADM_SUM_$", "ADM_SUM"},
            {REPORT_TABLE_NAME, "COM_CORRN_$", "COM_CORRN"},
            {REPORT_TABLE_NAME, "CL_CORRN_$", "CL_CORRN"},
            {REPORT_TABLE_NAME, "SUM_CALL_$", "SUM_CALL"},
            {REPORT_TABLE_NAME, "SUM_PERIOD_$", "SUM_PERIOD"},
            {REPORT_TABLE_NAME, "SUM_RAZ_$", "SUM_RAZ"},
            {REPORT_TABLE_NAME, "SUM_CALL_OTHER_$", "SUM_CALL_OTHER"},
            {REPORT_TABLE_NAME, "SUM_ROAM_CALL_$", "SUM_ROAM_CALL"},
            {PAYMENT_TABLE_NAME, "ALL_SUMM", "ALL_SUMM"}};

    /* Префикс таблицы. Предназначен для уникальности запроса: В разных таблицах есть поля с одинаковыми именами.*/
    private final static String PREFIX_ACT = "ACT__";
    /*Массив, формирующий строку запроса.
    Формат: {"Имя таблицы источника", "Имя поля в базе данных (источник)", "Имя поля в xml (назначение)"}*/
    private final static String[][] ALL_FIELD_ACT = {
            {ACT_TABLE_NAME, "P_ITG_CLNT_ID", "P_ITG_CLNT_ID"},
            {ACT_TABLE_NAME, "ALL_PRICE_IN_WORDS", "ALL_PRICE_IN_WORDS"},
            {ACT_TABLE_NAME, "NDS", "NDS"},
            {ACT_TABLE_NAME, "NDS_IN_WORDS", "NDS_IN_WORDS"},
            {ACT_TABLE_NAME, "ALL_PRICE", "ALL_PRICE"},
            {ACT_TABLE_NAME, "ALL_VAT", "ALL_VAT"},
            {ACT_TABLE_NAME, "FACT_DATE", "FACT_DATE"},
            {ACT_TABLE_NAME, "FACT_NUM", "FACT_NUM"},
            {ACT_TABLE_NAME, "DET_NAME", "DET_NAME"},
            {ACT_TABLE_NAME, "KOP", "KOP"},
            {ACT_TABLE_NAME, "KOP_NDS", "KOP_NDS"},
            {ACT_TABLE_NAME, "V_MONTH_NAME2", "V_MONTH_NAME2"}};

    /*Поля исключения. При нахождении в базе значени NULL или "" вместо данных полей вставляется "0"*/
    private static final HashSet<String> exclusionNullFields = new HashSet<String>() {{
        add("START_BAL"); add("END_BAL"); add("ALL_CHARGE");
        add("DISCOUNT"); add("CL_CORRN"); add("SUM_CALL");
        add("SUM_PERIOD"); add("SUM_RAZ"); add("SUM_CALL_OTHER");
        add("SUM_ROAM_CALL"); add("ALL_SUMM"); add("WOVAT");
        add("VAT"); add("SUMM"); add("CHARGE_SUM");
        add("ADM_SUB_SUM"); add("NDS"); add("ALL_PRICE");
        add("ALL_VAT"); add("KOP"); add("KOP_NDS");
    }};

    /*Формат полей для даты*/
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat dateFormat20d = new SimpleDateFormat("yyyy-MM-20");
    private static final SimpleDateFormat dateFormatTextMonth = new SimpleDateFormat("MM", new Locale("ru"));
    private static final SimpleDateFormat dateFormatYear = new SimpleDateFormat("yyyy");

    /*Используется для печати даты за который был сформирован отчет ( предыдущий месяц текущей даты)*/
    private static Calendar calendar = new GregorianCalendar();

    private static String B2C = "B2C";
    private static String B2C_CDMA = "B2C CDMA";

    private static String B2B = "B2B";
    private static String B2B_CDMA = "B2B CDMA";

    /*Генерация SELECT запроса для таблиц, у которых нет повторяющихся элементов.*/
    private static StringBuilder GenerateSelectNotRepeatedTable(RegionInfo regionInfoInput){
        StringBuilder queryString = new StringBuilder();
        queryString.append("SELECT DISTINCT ");
        addSelectFromTable(queryString, ALL_FIELD_REPORT, PREFIX_REPORT);
        addSelectFromTable(queryString, ALL_FIELD_PAY, PREFIX_PAY);
        addSelectFromTable(queryString, ALL_FIELD_ACT, PREFIX_ACT);

        // Удаляем последнюю ненужную запятую. Иначе будет ошибочное SQL выражение.
        queryString.delete(queryString.length() - 2, queryString.length() - 1);

        queryString.append(" FROM " + REPORT_TABLE_NAME + " LEFT JOIN " + ACT_TABLE_NAME + " ON " + REPORT_TABLE_NAME + ".P_ITG_CLNT_ID = " + ACT_TABLE_NAME + ".P_ITG_CLNT_ID LEFT JOIN "
                + PAYMENT_TABLE_NAME + " ON " + REPORT_TABLE_NAME + ".P_ITG_CLNT_ID = " + PAYMENT_TABLE_NAME + ".P_ITG_CLNT_ID WHERE " + REPORT_TABLE_NAME +
                ".BRANCH_ID = '" + regionInfo.id + "'");
        queryString.append(FormatDeliveryQualification(REPORT_TABLE_NAME+ ".BDT_NAME", REPORT_TABLE_NAME+ ".DELIVERY_GROUP", regionInfoInput ));

        return queryString;
    }

    private static String FormatDeliveryQualification(String firstColumn, String secondColumn, RegionInfo regionInfoInput){
        if (regionInfoInput.deliveryGroupStatusArray.size() == 0) return "";
        String qualification =" AND ";
        String delimiter = " AND ";

        for(Pair<String,String> pair:regionInfoInput.deliveryGroupStatusArray){
            if(pair.getL().equals("null")){
                qualification += " NOT(" + firstColumn + " is null AND ";
            } else {
                qualification += " NOT((" + firstColumn + " = '" + pair.getL() + "' AND " + firstColumn + " is not null) AND ";
            }

            if(pair.getR().equals("null")){
                qualification += secondColumn + " is null  ) ";
            } else {
                qualification += "(" + secondColumn + " = '" + pair.getR() + "' AND " + secondColumn + " is not null))";
            }

            qualification += delimiter;
        }

        // delete last delimiter
        if(qualification.length() >= delimiter.length())
            qualification = qualification.substring(0, qualification.length() - delimiter.length());

        return qualification;
    }


    /*Добавление SELECT запроса для полей fields таблицы.
    * @param outExpressionSelect Ds*/
    private static void addSelectFromTable(StringBuilder outExpressionSelect, String[][] fields, String prefixTable) {
        for (int i = 0; i < fields.length; i++){
            outExpressionSelect.append(fields[i][TABLE_NAME]).append(".").append(fields[i][FIELD_NAME_IN_DWH]).append(" AS ").append(prefixTable).append(fields[i][FIELD_NAME_IN_XML]);
            outExpressionSelect.append(", ");
        }
    }

    private static String currentPrimaryKey;

    /*Формирование xml из базы данных DWH
    * @param regionEng - имя региона считываемого из базы.*/
    public static void getXMLStringFromDWH(RegionInfo regionInfoInput, String idOrder) throws Exception {
        try {
            File fileForJob = new File(pathJobInputDirectory + "tele2_base.xml");
            if (fileForJob.exists()){
                fileForJob.delete();
            }


            char_output = new OutputStreamWriter(new FileOutputStream(fileForJob), Charset.forName("UTF-8").newEncoder());
            fileForJob.createNewFile();
            GeneratorXML.orderId = idOrder;
            GeneratorXML.regionInfo = regionInfoInput;
            GeneratorXML.templateInfo = TemplateDB.getCurrentTemplateInfo();
            log.info("Receiving StringBuilder - xml. Region id: " + regionInfo.id);
            log.info("Started process generation XML. Expected...: ");
            writeText("<ROOT>");
            addElementDirect("TEMPLATE_SET", templateInfo.TEMPLATE_SET);
            addElementDirect("TEMPLATE_BILL", templateInfo.TEMPLATE_BILL);
            addElementDirect("TEMPLATE_DETAIL", templateInfo.TEMPLATE_DETAIL);
            addElementDirect("TEMPLATE_ACT", templateInfo.TEMPLATE_ACT);
            addElementDirect("TEMPLATE_INVOICE", templateInfo.TEMPLATE_INVOICE);
            addElementDirect("TEMPLATE_INVOICE_AVANS", templateInfo.TEMPLATE_INVOICE_AVANS);
            addElementDirect("TEMPLATE_NOTICE", templateInfo.TEMPLATE_NOTICE);
            StringBuilder clientSetQuery = GenerateSelectNotRepeatedTable(regionInfoInput);

            // Проход по таблице клиентов
            long lBegin = System.currentTimeMillis();
            List<Map<String, Object>> clientList = wayDatabase.queryForList(clientSetQuery.toString());
            if (clientList.size() == 0) throw new AtriumException(AtriumError.ZERO_CUSTOMER);
            log.info("Region id: " + regionInfo.id + ". Number of record: " + clientList.size());
            for (Map clientElement : clientList) {
                initServerOptionForClient(clientElement);
                currentPrimaryKey = clientElement.get(PREFIX_REPORT + "P_ITG_CLNT_ID").toString();
                writeText("<CLIENTREPORT>");
                addPrimaryKey();
                addClientSection(clientElement, "CLIENTROW", ALL_FIELD_REPORT, PREFIX_REPORT);
                addSection(clientElement, "PAYROW", ALL_FIELD_PAY, PREFIX_PAY);

                addDetailSection();

                if (serverOption.clientType.equals(B2B) || serverOption.clientType.equals(B2B_CDMA)) {
                    addFacturaSection();
                    addSection(clientElement, "ACTPSROW", ALL_FIELD_ACT, PREFIX_ACT);
                }

                addServOption(clientElement);
                writeText("</CLIENTREPORT>");
            }

            writeText("</ROOT>");
            long lEnd = System.currentTimeMillis();
            long lDelta = lEnd - lBegin;
            log.info("Data sampling rate: " + lDelta);
        } catch (AtriumException ex){
            throw ex;
        } finally {
            char_output.close();
        }
    }

    private static void writeText(Object text){
        try {
            char_output.write(text.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addPrimaryKey(){
        writeText("<CLIENTREPORT_ID>");
        writeText(currentPrimaryKey);
        writeText("</CLIENTREPORT_ID>");
    }

    private static void addDetailSection(){
        writeText("<DETAIL>");
        addPrimaryKey();
        addPaymentSection();
        addMsisDnSection();
        writeText("</DETAIL>");
    }

    private static void addPaymentSection(){
        String selectTable = "SELECT " + PAYMENT_TABLE_NAME + ".P_ITG_CLNT_ID, " + PAYMENT_TABLE_NAME + ".PDOC_NUM, " + PAYMENT_TABLE_NAME + ".PAY_DATE, " +
                PAYMENT_TABLE_NAME + ".MSISDN, " + PAYMENT_TABLE_NAME + ".KASSA_BANK, " + PAYMENT_TABLE_NAME + ".SUMM_PAY_$ AS SUMM_PAY FROM " + PAYMENT_TABLE_NAME +" WHERE " + PAYMENT_TABLE_NAME + ".P_ITG_CLNT_ID =" + currentPrimaryKey;
        List<Map<String, Object>> tableList = wayDatabase.queryForList(selectTable);
        for (Map<String, Object> tableElement : tableList){
            writeText("<PAYMENT>");
            for (Map.Entry<String, Object> elementMat: tableElement.entrySet()){
                addElement("", elementMat.getKey().toString(),tableElement);
            }
            writeText("</PAYMENT>");
        }
    }

    /*Множество, содержащее набор всех уникальных значений поля "GRP_NB" из таблицы MSISDNSET
    * Испольуется для вычисления значения поля CHARGE_ID*/
    private static HashSet<Object> grpNb = new HashSet<Object>(4);

    /*Добавление секции с таблицами MSISDNSET*/
    private static void addMsisDnSection(){
        String selectClient = "SELECT DISTINCT " +  DETAIL_TABLE_NAME + ".P_SUBS_ID, " + DETAIL_TABLE_NAME +  ".MSISDN FROM " + DETAIL_TABLE_NAME + " WHERE " + DETAIL_TABLE_NAME +".P_ITG_CLNT_ID = " + currentPrimaryKey + " ORDER BY "
                + DETAIL_TABLE_NAME + ".MSISDN";
        List<Map<String, Object>> clientList = wayDatabase.queryForList(selectClient);
        int indexWithGBRID_1000000000 = 0;
        for (Map<String, Object>  clientElement : clientList){
            writeText("<MSISDNSET>");
            addPrimaryKey();
            String selectTable = "SELECT " + DETAIL_TABLE_NAME + ".P_ITG_CLNT_ID, " + DETAIL_TABLE_NAME + ".P_SUBS_ID, " + DETAIL_TABLE_NAME + ".CLNT_ID, " +
                    DETAIL_TABLE_NAME + ".GRP_NB, " + DETAIL_TABLE_NAME + ".CHARGE_GROUP, " + DETAIL_TABLE_NAME + ".CHARGE_TYPE, " +
                    DETAIL_TABLE_NAME + ".CHARGE_SUM, " + DETAIL_TABLE_NAME + ".KOLVO, " + DETAIL_TABLE_NAME + ".ED_IZM, " +
                    DETAIL_TABLE_NAME + ".V_MONTH_NAME, "  + DETAIL_TABLE_NAME + ".ADM_SUB_SUM_$ AS ADM_SUB_SUM, " +
                    DETAIL_TABLE_NAME + ".MSISDN, " + DETAIL_TABLE_NAME + ".TRPL_NAME " + " FROM " + DETAIL_TABLE_NAME + " WHERE " + DETAIL_TABLE_NAME + ".P_ITG_CLNT_ID = " + currentPrimaryKey +
                    " AND " + DETAIL_TABLE_NAME + ".P_SUBS_ID = " + clientElement.get("P_SUBS_ID");
            List<Map<String, Object>> transList = wayDatabase.queryForList(selectTable);
            grpNb.clear();
            indexWithGBRID_1000000000 = 0;
            boolean chargeTypeIsGPRS = false;
            for (Map<String, Object>  transElement : transList){
                Object type = transElement.get("CHARGE_TYPE");
                if (type == null){
                    chargeTypeIsGPRS = false;
                } else {
                    chargeTypeIsGPRS = (type.toString().equals("GPRS") ? true: false);
                }
                double kolvoFromGPRS = 0;
                if (chargeTypeIsGPRS){
                    try {
                        double kolvoFromDWH = Double.parseDouble(transElement.get("KOLVO").toString());
                        kolvoFromGPRS = kolvoFromDWH * 50 / 1024;
                    } catch (Exception ex){
                        // В случае, если не удалось получить значение параметра KOLVO делаем вид что поле
                        // отвечающее за тип не GPRS.
                        chargeTypeIsGPRS = false;
                        log.warn("Not read field KOLVO. P_ITG_CLNT_ID = " + transElement.get("P_ITG_CLNT_ID"));
                    }
                }
                writeText("<MSISDNROW>");
                for (Map.Entry<String, Object> elementMat: transElement.entrySet()){
                    if(!elementMat.getKey().toString().equals("ADM_SUB_SUM") &&
                            !elementMat.getKey().toString().equals("MSISDN") &&
                            !elementMat.getKey().toString().equals("TRPL_NAME") &&
                            !elementMat.getKey().toString().equals("KOLVO") &&
                            !elementMat.getKey().toString().equals("ED_IZM")) {
                        addElement("", elementMat.getKey().toString(), transElement);
                    }
                    if (elementMat.getKey().toString().equals("KOLVO")){
                        if (chargeTypeIsGPRS){
                            addElementDirect("KOLVO", new BigDecimal(kolvoFromGPRS).setScale(2, RoundingMode.UP).doubleValue());
                        } else {
                            addElement("", elementMat.getKey().toString(), transElement);
                        }
                    }
                    if (elementMat.getKey().toString().equals("ED_IZM")) {
                        if (chargeTypeIsGPRS) {
                            addElementDirect("ED_IZM", "Мб");
                        } else {
                            addElement("", elementMat.getKey().toString(), transElement);
                        }
                    }
                    if (elementMat.getKey().toString().equals("GRP_NB")) {
                        grpNb.add(elementMat.getValue().toString());
                        if (elementMat.getValue().toString().equals("1000000000")){
                            indexWithGBRID_1000000000 = transList.indexOf(transElement);
                        }
                    }
                }
                writeText("</MSISDNROW>");
            }
            if (transList.size() > 0) {
                    addElement("", "CHARGE_SUM", transList.get(indexWithGBRID_1000000000));
                    addElement("", "ADM_SUB_SUM", transList.get(0));
                    addElement("", "MSISDN", transList.get(0));
                    addElement("", "TRPL_NAME", transList.get(0));

                writeText("<CHARGE_ID>");
                writeText(GetChargeId(grpNb));
                writeText("</CHARGE_ID>");
            }

            writeText("</MSISDNSET>");
        }
    }

    /*Вычисление поля CHARGE_ID по полям GRP_ID
    * @param grpNbs - множество, содержащее значение полей GRP_NB для раздела
    * @return   1 - если GRP_NB содержит только 1 и 1000000000;
                2 - если GRP_NB содержит только 2 и 1000000000;
                3 - если GRP_NB содержит только 3 и 1000000000;
                4 - если GRP_NB содержит только 1,2 и 1000000000;
                5 - если GRP_NB содержит только 1,3 и 1000000000;
                6 - если GRP_NB содержит только 2,3 и 1000000000;
                7 - если GRP_NB содержит 1,2,3 и 1000000000*/
    private static int GetChargeId(HashSet<Object> grpNbs){
        if (grpNbs.size() == 2){                //Два элемента и...
            if (grpNbs.contains("1")){          return 1; // содержит 1 (второй элемент 1000000000);
            } else if (grpNbs.contains("2")){   return 2; // ...
            } else if (grpNbs.contains("3")) {  return 3;}// ...
        } else {                                // Иначе (три элемента) и ...
            if (!grpNbs.contains("3")){         return 4; // не содержит 3 => в наличии 1, 2, 1000000000;
            } else if (!grpNbs.contains("2")){  return 5; // ...
            } else if (!grpNbs.contains("1")){  return 6;}// ...
        }
        return 7; // не два, не три => содержит 1,2,3 и 1000000000;
    }


    /*Добавление секции с фактурой
    * @param key - внешний ключ таблицы (P_ITG_CLNT_ID) */
    private static void addFacturaSection(){
        writeText("<FACTURASET>");
        String selectTable = "SELECT " + FACTURA_TABLE_NAME + ".P_ITG_CLNT_ID, " + FACTURA_TABLE_NAME + ".FACT_ID, " + FACTURA_TABLE_NAME + ".FACT_DATE, " +
                FACTURA_TABLE_NAME + ".FACT_NUM, " + FACTURA_TABLE_NAME + ".DET_NAME, " + FACTURA_TABLE_NAME + ".WOVAT_$ AS WOVAT, " + FACTURA_TABLE_NAME + ".VAT_$ AS VAT, " +
                FACTURA_TABLE_NAME + ".SUMM_$ AS SUMM, " + FACTURA_TABLE_NAME + ".FT_ID FROM " + FACTURA_TABLE_NAME +" WHERE " + FACTURA_TABLE_NAME + ".P_ITG_CLNT_ID =" + currentPrimaryKey;
        List<Map<String, Object>> tableList = wayDatabase.queryForList(selectTable);
        for (Map<String, Object> tableElement : tableList){
            // В зависимости от поля FT_ID записываем в разные теги таблицы: FACTURAROW_1 или FACTURAROW_2
            Boolean typeFacturaOne = tableElement.get("FT_ID").toString().equals("1") ? true : false;
            writeText(typeFacturaOne ? "<FACTURAROW_1>" : "<FACTURAROW_2>");
            for (Map.Entry<String, Object> elementMat: tableElement.entrySet()){
                addElement("", elementMat.getKey().toString(),tableElement);
            }
            writeText(typeFacturaOne ? "</FACTURAROW_1>" : "</FACTURAROW_2>");
        }
        writeText("</FACTURASET>");
    }


    /*Добавление секции без повторяющихся элементов и исключений.
    * @param clientElement - строка БД из которой берутся данные;
    * @param teg - имя тега, обременяющего секцию;
    * @param ALL_FIELD - поля, необходимые для добавления в xml;
    * @prefix - префикс таблицы, для создания уникальности имен полей.*/
    private static void addSection(Map clientElement, String teg, String[][] ALL_FIELD, String prefix){
        writeText("<" + teg + ">");
        for (int i = 0; i < ALL_FIELD.length; i++){
            addElement(prefix, ALL_FIELD[i][FIELD_NAME_IN_XML], clientElement);
        }
        writeText("</" + teg + ">");
    }

    /*Добавление секции клиентов
* @param clientElement - строка БД из которой берутся данные;
* @param teg - имя тега, обременяющего секцию;
* @param ALL_FIELD - поля, необходимые для добавления в xml;
* @prefix - префикс таблицы, для создания уникальности имен полей.*/
    private static void addClientSection(Map clientElement, String teg, String[][] ALL_FIELD, String prefix){
        writeText("<" + teg + ">");
        for (int i = 0; i < ALL_FIELD.length; i++){
            if (ALL_FIELD[i][FIELD_NAME_IN_XML].equals("DELIVERY_GROUP")){
                if (clientElement.get(prefix + ALL_FIELD[i][FIELD_NAME_IN_XML]) == null || clientElement.get(prefix + ALL_FIELD[i][FIELD_NAME_IN_XML]) == ""){
                    addElementDirect("DELIVERY_GROUP", "Без группы доставки");
                } else if (clientElement.get(prefix + ALL_FIELD[i][FIELD_NAME_IN_XML]) == " - ") {
                    ReportDWH.addWarning(orderId, currentPrimaryKey, "DELIVERY_GROUP", "у поля недопустимое значение: -");
                    addElementDirect("DELIVERY_GROUP", "Без группы доставки");
                } else {
                    String clientElementString = (clientElement.get(prefix + ALL_FIELD[i][FIELD_NAME_IN_XML])).toString();
                    addElementWithReport(clientElementString, "DELIVERY_GROUP", "DELIVERY_GROUP");
                }
            } else {
                addElement(prefix, ALL_FIELD[i][FIELD_NAME_IN_XML], clientElement);
            }
        }
        addElementDirect("BRANCH_NAME", regionInfo.regionRus);
        addElementDirect("MACRO_REGION_RUS", regionInfo.macroRegionRus);
        addElementDirect("BRANCH_NAME_ENG", regionInfo.regionEng);
        addElementDirect("MACRO_REGION_ENG", regionInfo.macroRegionEng);
        addElementDirect("E-MAIL", regionInfo.email);

        if (serverOption.clientType.equals(B2B) || serverOption.clientType.equals(B2B_CDMA)) {
            addElementDirect("PHONE_SHORT", regionInfo.phoneB2B);
        } else {
            addElementDirect("PHONE_SHORT", regionInfo.phoneB2C);
        }



        addElementDirect("CLIENT_TYPE", serverOption.clientType);

        writeText("</" + teg + ">");

    }

    private static void addElementWithReport(String clientElementString,String nameFieldForAddInXml, String nameFieldForWarningMessage) {
        ClearedStringAndString clearedStringAndString = StringVerification.getClearedStringAndStringWitchErrorSymbols(clientElementString);
        addElementDirect(nameFieldForAddInXml, clearedStringAndString.clearedString);
        if (clearedStringAndString.isFindPermSymbols){
            ReportDWH.addWarning(orderId, currentPrimaryKey, nameFieldForWarningMessage, "Нaйдены недопустимые символы, они отмечены в квадратных скобках []: " + clearedStringAndString.stringWithPermissionSymbols);
        }
    }

    /*Добавление секции со служебными полями
    * @param clientElement - строка БД из которой берутся данные;*/
    private static void addServOption(Map clientElement){
        writeText("<SERVOPTION>");
        addElementWithReport(serverOption.fileName, "FILE_NAME", "ACCOUNT + CONTRACT_INN + NAME + OB_EDATE");
        addElementDirect("YEAR_QUERY", serverOption.yearQuery);
        addElementDirect("MONTH_REQUEST", serverOption.monthRequest);
        addElementDirect("LOGO_ID", regionInfo.logoId);

        writeText("</SERVOPTION>");
    }

    public static String getNameXML(){
        return serverOption.yearQuery + "__" + serverOption.monthRequest + "__" +regionInfo.id;
    }
    private static void initServerOptionForClient(Map clientElement){

        String allName = getCorrectString(getStringElementDirectForServerOption("NAME", clientElement.get(PREFIX_REPORT + "NAME")));
        // Возможна ситуация, когда после образания строки до 20 символов, обрежится запрещенный в xml символ и вместо
        // "&gt;" останется "&". В этом случае необходимо еще заменить "&" на " "
        String shortName = (allName.length() <= 20 ? allName : allName.substring(0,20).replace("&"," "));
        serverOption.fileName = getStringElementDirectForServerOption("ACCOUNT", clientElement.get(PREFIX_REPORT + "ACCOUNT")) + "_" + getStringElementDirectForServerOption("CONTRACT_INN",clientElement.get(PREFIX_REPORT + "CONTRACT_INN"))
                + "_" + shortName + "_" + dateFormat.format(getDateElementDirectForServerOption("OB_EDATE", clientElement.get(PREFIX_REPORT + "OB_EDATE")));
        serverOption.clientType = setClientType(getStringElementDirectForServerOption("CJT_ID", clientElement.get(PREFIX_REPORT + "CJT_ID")));
        serverOption.monthRequest = dateFormatTextMonth.format(getDateElementDirectForServerOption("OB_EDATE", clientElement.get(PREFIX_REPORT + "OB_EDATE")));
        serverOption.yearQuery = dateFormatYear.format(getDateElementDirectForServerOption("OB_EDATE", clientElement.get(PREFIX_REPORT + "OB_EDATE")));
    }

    /*Безопасное добавление элемента String,
    * при наличии ошибочных данных записывается сообщение в отчет*/
    public static String getStringElementDirectForServerOption(String nameElement, Object element){
        if (element == null) {
            ReportDWH.addWarning(orderId, currentPrimaryKey, nameElement, "Поле имеет недопустимое значение: null");
            return "";
        }
        return element.toString();
    }

    /*Безопасное добавление элемента Date,
    * при наличии ошибочны данных записывается сообщение в отчет*/
    public static Date getDateElementDirectForServerOption(String nameElement, Object element){
        if (element == null) {
            ReportDWH.addWarning(orderId, currentPrimaryKey, nameElement, "Поле имеет недопустимое значение: null");
            return new Date();}
        if (element instanceof Date) {
            return (Date)element;
        }
        ReportDWH.addWarning(orderId, currentPrimaryKey, nameElement, "Поле имеет недопустимое значение: ожидается дата");
        return new Date();
    }

    /*Установка типа клиента в зависимости от значения поля*/
    public static String setClientType(String typeInField){
        if (typeInField.equals("1")){
            return B2C;
        } else if (typeInField.equals("5")){
            return B2C_CDMA;
        } else if (typeInField.equals("2") || typeInField.equals("3")){
            return B2B;
        } else if (typeInField.equals("6") || typeInField.equals("7")){
            return B2B_CDMA;
        }
        return "Неизвестный юр.тип";
    }

    /*Добавление элемента в xml файл
    * @param prefix Префикс поля. Используется для уникальности названий полей.
    * @param field Имя поля. Имя поля, которое добавиться в xml.
    * @param element Набор для получения значения элемента. Значения хранятся в виде префикс + имя.*/
    private static void addElement(String prefix, String field, Map element){
        writeText("<" + field + ">");
        if (element.get(prefix + field) instanceof String)          // Строка
        {
            addTextElement(prefix, field, element);
        } else  if (element.get(prefix + field) instanceof Date){   // Дата
            addDateElement(prefix, field, element);
        } else {                                                    // Чило или пустое поле
            if (element.get(prefix + field) != null){               // Число
                addNumberElement(prefix, field, element);
            } else {                                                // Пустое поле
                addNullElement(field);
            }
        }
        writeText("</" + field + ">");
    }

    /*Добавление текстового элемента в xml. Текстовый элемент считается любым
    * элементом, в котором содержутся не только цифры и который не равен нулю
    * @param prefix - префикс таблицы, необходим для поиска элемента в массиве;
    * @param fieldName - имя поля из массива element для добавления;
    * @element - коллекция хранения строки, в которой необходимо искать поле.*/
    private static void addTextElement(String prefix, String fieldName, Map element){
        writeText(getCorrectString((String) element.get(prefix + fieldName)));
    }

    /*Замена запрещенных символов в строке*/
    private static String getCorrectString(String inputString){
        return inputString.replace("&", "&amp;").replace(">", "&gt;").replace("<", "&lt;");
    }


    /*Добавление числа в xml. Числом считается любой не нулевой элемент содержащий только числа и/или разделители
    * дробных частей
    * @param prefix - префикс таблицы, необходим для поиска элемента в массиве;
    * @param fieldName - имя поля из массива element для добавления;
    * @element - коллекция хранения строки, в которой необходимо искать поле.*/
    private static void addNumberElement(String prefix, String field, Map element){
        writeText(element.get(prefix + field));
    }

    /*Добавление Даты в xml в формате, описанном полем dateFormat*
    * @param prefix - префикс таблицы, необходим для поиска элемента в массиве;
    * @param fieldName - имя поля из массива element для добавления;
    * @element - коллекция хранения строки, в которой необходимо искать поле.
     */
    private static void addDateElement(String prefix, String field, Map element){
        if (!field.equals("DATE_NEXT_MONTH")) {
            writeText(dateFormat.format(element.get(prefix + field)));
        } else {
            writeText(dateFormat20d.format(element.get(prefix + field)));
        }
    }

    /*Добавление пустого поля в xml.
    * @param fieldName - имя поля*/
    private static void addNullElement(String fieldName){
        if (exclusionNullFields.contains(fieldName)){
            writeText("0");
        } else {
            writeText("");
        }
    }

    // Создание тега на прямую по имени fieldName
    private static void addElementDirect(String fieldName, Object value) {
        writeText("<" + fieldName + ">");
        writeText(value);
        writeText("</" + fieldName + ">");
    }

    /*Хранение структуры информации о полях, высчитываемых для каждого клиента*/
    private static class ServerOption
    {
        public String fileName;
        public String yearQuery;
        public String monthRequest;
        public String clientType;
    }
}
