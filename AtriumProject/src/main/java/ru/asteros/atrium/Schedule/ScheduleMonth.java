

package ru.asteros.atrium.Schedule;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.asteros.atrium.AppConfiguration;
import ru.asteros.atrium.DataBaseTemplateProvider.DataBaseTemplateProvider;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;


@Component("scheduleMonth")
@Scope("singleton")
public class ScheduleMonth {

    private static JobDetail jobDetail;
    private static Trigger trigger;
    private static Scheduler scheduler;
    private static Logger log = LoggerFactory.getLogger(ScheduleMonth.class);
    ScheduleMonth(){}


    @PostConstruct
    public void init() {
        try {
            jobDetail = JobBuilder.newJob(ScheduleMonthWorker.class)
                    .withIdentity("OrderScheduledGenerator", "OrderScheduledGroup").build();


            trigger = TriggerBuilder.newTrigger()
                    .withIdentity("OrderScheduleGeneratorTrigger", "OrderScheduledGroup")
                    .withSchedule(CronScheduleBuilder.cronSchedule(AppConfiguration.get("ORDER_SCHEDULED_INTERVAL")))
                    .build();

            // schedule it
            scheduler = new StdSchedulerFactory().getScheduler();
            //scheduler.start();
            scheduler.scheduleJob(jobDetail, trigger);

        } catch (Exception e) {
            log.error("Exception while starting scheduler: " + e);
        }
    }

    @PreDestroy
    private void stopScheduler(){
        try {
            scheduler.shutdown();
            ScheduleMonthWorker.stopThread();
            DataBaseTemplateProvider.closeConnect();
        } catch (SchedulerException e) {
            log.error("Exception while stoping scheduler: " + e);
            e.printStackTrace();
        }
    }
}



