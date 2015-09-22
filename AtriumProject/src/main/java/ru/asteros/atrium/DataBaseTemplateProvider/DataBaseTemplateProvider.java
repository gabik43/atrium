package ru.asteros.atrium.DataBaseTemplateProvider;


import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by A.Gabdrakhmanov on 04.08.2015.
 */
@Component
@Scope("singleton")
public class DataBaseTemplateProvider {

    private static Logger log = LoggerFactory.getLogger(DataBaseTemplateProvider.class);

    //Имя ресурса DataSource обменных таблиц
    private static final String DATA_SOURCE_DWH = "java:/OracleDS";
    private static final String DATABASE_PROVIDER_SOURCE = "java:/MSSQLXADS";

    private static volatile JdbcTemplate dwhJdbcTemplate = getJdbcTemplateDWHxchange();
    private static volatile JdbcTemplate internalDataBaseJdbcTemplate = getinternalDataBaseJdbcTemplate();

    private static Connection connectionDWH;
    private static Connection connectionMySOL;



    private static JdbcTemplate getJdbcTemplateDWHxchange(){
        DataSource dataSource = null;
        try {
            InitialContext ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup(DATA_SOURCE_DWH);
        } catch (Exception e) {
            log.error("Error getting DataSource " + DATA_SOURCE_DWH, e);
        }

        if (dataSource == null){
            log.warn("Use local driver for Oracle");
            dataSource = new BasicDataSource();
            ((BasicDataSource)dataSource).setDriverClassName("oracle.jdbc.driver.OracleDriver");
            ((BasicDataSource)dataSource).setUsername("TELE");
            ((BasicDataSource)dataSource).setPassword("TELE135");
            ((BasicDataSource)dataSource).setUrl("jdbc:oracle:thin:@10.101.145.83:1521:orcl");
            ((BasicDataSource)dataSource).setInitialSize(1);
            ((BasicDataSource)dataSource).setMaxActive(25);
        } else {
            log.info("Use server driver for Oracle");
        }

        try {
            connectionDWH = dataSource.getConnection(); //ускоряет первое обращение к БД в каждом потоке (в 5-10 раз)

        } catch (SQLException e) {
            log.info("DataSource to receive the following resource name SINGLE_EXCHANGE_DATA_SOURCE " + DATA_SOURCE_DWH);
            e.printStackTrace();
        }
        return new JdbcTemplate(dataSource);
    }

    private static JdbcTemplate getinternalDataBaseJdbcTemplate(){
        DataSource dataSource = null;
        try {
            InitialContext ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup(DATABASE_PROVIDER_SOURCE);
        } catch (Exception e) {
            log.error("Error getting DataSource " + DATABASE_PROVIDER_SOURCE, e);
        }

        if (dataSource == null){
            log.warn("Use local driver for MySQL");
            dataSource = new BasicDataSource();
            ((BasicDataSource)dataSource).setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            ((BasicDataSource)dataSource).setUsername("atrium");
            ((BasicDataSource)dataSource).setPassword("atrium");
            ((BasicDataSource)dataSource).setUrl("jdbc:sqlserver://10.101.145.234:1433;" +
                    "databaseName=atrium");
            ((BasicDataSource)dataSource).setInitialSize(1);
            ((BasicDataSource)dataSource).setMaxActive(25);
        } else {
            log.info("Use server driver for MySQL");
        }

        try {
            connectionMySOL = dataSource.getConnection(); //ускоряет первое обращение к БД в каждом потоке (в 5-10 раз)
        } catch (SQLException e) {
            log.info("DataSource to receive the following resource name SINGLE_EXCHANGE_DATA_SOURCE(required action) " + DATABASE_PROVIDER_SOURCE);
            e.printStackTrace();
        }
        return new JdbcTemplate(dataSource);

    }

    /**
     * Получение JdbcTemplate для работы регулярной выписки с обменными таблицами
     */
    public static JdbcTemplate getDwhJdbcTemplate(){
        return dwhJdbcTemplate;
    }

    /**
     * Получение JdbcTemplate для работы c базой данных требуемых действий
     */
    public static JdbcTemplate getInternalDataBaseJdbcTemplate(){
        return internalDataBaseJdbcTemplate;
    }
    @PreDestroy
    public static void closeConnect(){
        try {
            dwhJdbcTemplate = null;
            internalDataBaseJdbcTemplate = null;
            if (!connectionDWH.isClosed()) {
                log.info("Close DB connectionDWH");
                connectionDWH.close();
            }
            if (!connectionMySOL.isClosed()) {
                log.info("Close DB connectionMySOL");
                connectionMySOL.close();
            }
        } catch (SQLException e) {
            log.error("Error close DB connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
