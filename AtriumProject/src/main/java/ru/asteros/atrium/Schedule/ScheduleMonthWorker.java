

package ru.asteros.atrium.Schedule;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.asteros.atrium.DB.OrderDB;
//import ru.asteros.atrium.DB.Order;

import javax.annotation.PreDestroy;


@Component("scheduleMonthWorker")
public class ScheduleMonthWorker implements Job{
    private static Logger log = LoggerFactory.getLogger(ScheduleMonthWorker.class);
    private static int latency = 30000;
    private static boolean runThread = true;
    public static void stopThread(){
        runThread = false;
    }

    SubOrderScheduledCreator subOrderScheduledCreator;
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        // starting new process for create suborders
/*        subOrderScheduledCreator = new SubOrderScheduledCreator();
        subOrderScheduledCreator.setDaemon(true);
        subOrderScheduledCreator.start();*/
    }

    class SubOrderScheduledCreator extends Thread {

        public void run() {
            while(runThread) {
                try {
                    Thread.sleep(latency);
                    if (!OrderDB.addNewOrderBySchedule()) return;

                } catch(Exception e){
                    log.info("Exception while adding new order by scheduled: " + e ) ;
                }
            }
        }
    }

    @PreDestroy
    public void destroy(){
        log.info("Compliteng thread subOrderScheduledCreator!");
    }
}


