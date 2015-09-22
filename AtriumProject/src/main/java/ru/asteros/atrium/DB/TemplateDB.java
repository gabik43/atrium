package ru.asteros.atrium.DB;

import org.springframework.jdbc.core.JdbcTemplate;
import ru.asteros.atrium.DataBaseTemplateProvider.DataBaseTemplateProvider;

import java.util.List;
import java.util.Map;

/**
 * Created by A.Gabdrakhmanov on 18.09.2015.
 */
public class TemplateDB {
    private static JdbcTemplate dbConnector = DataBaseTemplateProvider.getInternalDataBaseJdbcTemplate();
    public static TemplateInfo getCurrentTemplateInfo(){
        TemplateInfo templateInfo = new TemplateInfo();
        String query = "SELECT * FROM  TEMPLATE";
        List<Map<String, Object>> fieldList =  dbConnector.queryForList(query);

        for (Map<String, Object> field : fieldList)
        {
            String name = field.get("NAME").toString();
            String version = field.get("VERSION").toString();
            if (name.equals("TEMPLATE_SET")){
                templateInfo.TEMPLATE_SET = version;
            } else if (name.equals("TEMPLATE_BILL")){
                templateInfo.TEMPLATE_BILL = version;
            } else if (name.equals("TEMPLATE_DETAIL")){
                templateInfo.TEMPLATE_DETAIL = version;
            } else if (name.equals("TEMPLATE_ACT")){
                templateInfo.TEMPLATE_ACT = version;
            } else if (name.equals("TEMPLATE_INVOICE")){
                templateInfo.TEMPLATE_INVOICE = version;
            } else if (name.equals("TEMPLATE_INVOICE_AVANS")){
                templateInfo.TEMPLATE_INVOICE_AVANS = version;
            } else if (name.equals("TEMPLATE_NOTICE")){
                templateInfo.TEMPLATE_NOTICE = version;
            }
        }
        return templateInfo;
    }
}
