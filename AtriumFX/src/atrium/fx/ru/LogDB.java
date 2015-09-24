package atrium.fx.ru;


import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Andrey.A.Koshkin on 08.09.2015.
 */
public class LogDB {

    public final static int LEVEL_ERROR =  4;
    public final static int LEVEL_WARN =   3;
    public final static int LEVEL_INFO =   2;
    public final static int LEVEL_TRACE =  1;
    public final static int LEVEL_DEBUG =  0;


    private final static String LEVEL_TEXT_INFO = "INFORMATION";
    private final static String LEVEL_TEXT_DEBUG = "DEBUG";
    private final static String LEVEL_TEXT_ERROR = "ERROR";
    private final static String LEVEL_TEXT_TRACE = "TRACE";
    private final static String LEVEL_TEXT_WARN = "WARNING";

    private final static String TBL_LOGS =    "[LOGTABLE]";

    public final static String HOST_TYPE_CLIENT = "CLIENT";
    public final static String HOST_TYPE_SERVER = "SERVER";

    private final static String FIELD_ID = "id";
    private final static String FIELD_HOST_TYPE = "hostType";
    private final static String FIELD_HOST_NAME = "hostName";
    private final static String FIELD_MESSAGE_LEVEL = "messageLevel";
    private final static String FIELD_MESSAGE_TEXT = "messageText";
    private final static String FIELD_DATE = "dateValue";

    private static int logLevel = LEVEL_DEBUG;

    private static Connection connection;

    public static void error(String message){

        if(logLevel <= LEVEL_ERROR)
        writeLog(getHostName(),HOST_TYPE_CLIENT,LEVEL_TEXT_ERROR,message, getTime());
    }

    public static void warn(String message){
        if(logLevel <= LEVEL_WARN)
        writeLog(getHostName(),HOST_TYPE_CLIENT,LEVEL_TEXT_WARN,message, getTime());
    }

    public static void info(String message){
        if(logLevel <= LEVEL_INFO)
        writeLog(getHostName(),HOST_TYPE_CLIENT,LEVEL_TEXT_INFO,message, getTime());
    }

    public static void trace(String message){
        if(logLevel <= LEVEL_TRACE)
        writeLog(getHostName(),HOST_TYPE_CLIENT,LEVEL_TEXT_TRACE,message, getTime());
    }

    public static void debug(String message){
        if(logLevel <= LEVEL_DEBUG)
        writeLog(getHostName(),HOST_TYPE_CLIENT,LEVEL_TEXT_DEBUG,message, getTime());
    }

    public static void writeLog(String hostName, String hostType, String level, String message, String time){
        try {
            String sql =  "INSERT INTO " + TBL_LOGS + "  " +
                    " (" + FIELD_HOST_TYPE +
                    "  ," + FIELD_HOST_NAME +
                    "  ," + FIELD_MESSAGE_LEVEL +
                    "  ," + FIELD_MESSAGE_TEXT +
                    "  ," + FIELD_DATE + ")" +
                    " VALUES (cast (N'" + hostType + "' as nvarchar(200) ),cast (N'" + hostName + "' as nvarchar(200) ), cast (N'" + level + "' as nvarchar(200) ), " +
                    " cast (N'" + message + "' as nvarchar(max) ), " + " CONVERT(datetime, '"+ time+"' ))";

            PreparedStatement insertStatement;

            insertStatement = connection.prepareStatement(sql);
            if (insertStatement.executeUpdate() > 0) {
                System.out.println("... record log");
            }


        }catch (Exception e){
            e.printStackTrace();
            System.out.println("... error while DB quering");
        }
    }

    private static String getTime(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    private static String getHostName(){
        return System.getProperty("user.name");
    }


    static void setupConnection() {
        final String MYSQL_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        String  username = "atrium", pw = "atrium";
        try {
            Class.forName(MYSQL_DRIVER);
            System.out.println("... registered " + MYSQL_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            connection = DriverManager.getConnection("jdbc:sqlserver://10.101.145.234:1433;" +
            "databaseName=atrium", username, pw);
            System.out.println("... connected as '" + username );
        } catch (SQLException e) {
            System.out.println("... error while creating DB connection");
            e.printStackTrace();
        }
    }


    public static void closeConnection(){
        try {
            if (!connection.isClosed()) {
                System.out.println("Closing DB connection");
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Error close DB connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void setLogLevel(int level){
        logLevel = level;
    }

    public static int getLogLevel(){
        return logLevel;
    }

}
