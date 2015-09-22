package ru.asteros.atrium.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.asteros.atrium.AppConfiguration;

/**
 * Created by A.Gabdrakhmanov on 12.08.2015.
 */
public class JobManager {
    private static Logger log = LoggerFactory.getLogger(JobManager.class);


    private static String serverEndpoint = AppConfiguration.get("XPRESSION_URL") +
            "/xFramework/restful/services/jobs/all/list/names";

    private static String serverEndpointStart = AppConfiguration.get("XPRESSION_URL") +
            "/xFramework/restful/services/job/start";

    private static String serverEndpointStatus = AppConfiguration.get("XPRESSION_URL") +
            "/xFramework/restful/services/job/status";


    public static String getJobID() {
        return jobID;
    }

    /*Идентификатор xPression Job на сервере*/
    private static String jobID = "0";


    /*Запуск xPression Job и ожидание его выполнения.
    * @return - true, если Job завершился успешно, иначе false*/
    public static boolean startJobAndWaitingCompletion(){
            startJob();
            JobStatus jobStatus;
            while (true) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                jobStatus = getJobStatus();
                // Выходим из цикла, как только Job выходит из статусов начала запуска и работы
                if (jobStatus != JobStatus.RUNNING && jobStatus != JobStatus.STARTING) {
                    break;
                }
            }
            return (jobStatus == JobStatus.SUCCESS ? true : false);
    }

    private static void startJob(){
        final String reqBodyStart = "<XServicesRequest>\n" +
                "   <RequestContext function=\"General.security\">\n" +
                "      <Credentials method=\"UserID and Password\">\n" +
                "         <UserID>" + AppConfiguration.get("XPRESSION_USER") + "</UserID>\n" +
                "         <Password>" + AppConfiguration.get("XPRESSION_PASSWORD") + "</Password>\n" +
                "      </Credentials>\n" +
                "      <ApplicationName>xPression Batch</ApplicationName>\n" +
                "   </RequestContext>\n" +
                "   <RequestContext function=\"Job.start\">\n" +
                "       <Job version=\"" + 2.0 + "\" name=\"" + AppConfiguration.get("JOB_NAME") +"\">\n"+
                "           <JobOption></JobOption>\n"+
                "       </Job>\n"+
                "   </RequestContext>\n" +
                "</XServicesRequest>";

        String response = null;
        RestTemplate restTemplate = new RestTemplate();
        try {
            log.info("Sending POST request Job.start the server URL[" + AppConfiguration.get("XPRESSION_URL") + "]");
            response = restTemplate.postForObject(serverEndpointStart, reqBodyStart, String.class);
            jobID = response;
            log.info("Obtained prompted Job.start from the server URL[" + AppConfiguration.get("XPRESSION_URL") + "]. Response text: "
                    + response);
        } catch (RestClientException e){
            if (e.getMessage().contains("ConnectException")) {
                log.error("Server xPression [" + AppConfiguration.get("XPRESSION_URL") + "] obtained prompted.");
                e.printStackTrace();
            } else {
                log.error(e.getClass().getName() + " " + e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
    }


    /**
     * Запрос статуса job'а xPression
     * @return - статус job'а
     */
    private static JobStatus getJobStatus() {
        final String reqBodyStatus = "<XServicesRequest>\n" +
                "   <RequestContext function=\"General.security\">\n" +
                "      <Credentials method=\"UserID and Password\">\n" +
                "         <UserID>" + AppConfiguration.get("XPRESSION_USER") + "</UserID>\n" +
                "         <Password>" + AppConfiguration.get("XPRESSION_PASSWORD") + "</Password>\n" +
                "      </Credentials>\n" +
                "      <ApplicationName>xPression Batch</ApplicationName>\n" +
                "   </RequestContext>\n" +
                "   <RequestContext function=\"Job.status\">\n" +
                "       <Item ref=\"queueID\">" + jobID + "</Item>\n" +
                "       <ReturnInfo>statistics with details</ReturnInfo>\n" +
                "   </RequestContext>\n" +
                "</XServicesRequest>";

        RestTemplate restTemplate = new RestTemplate();

        try {
            log.debug("Отправка POST запроса Job.status[RunID = " + jobID + "] на сервер URL[" + AppConfiguration.get("XPRESSION_URL") + "]");
            String response = restTemplate.postForObject(serverEndpointStatus, reqBodyStatus, String.class);
            log.debug("Получен ответ на запрос Job.status[RunID = \" + jobQueueId + \"] от сервера URL[" + AppConfiguration.get("XPRESSION_URL")
                    + "]. Текст ответа: " + response);

            if (response == null) return JobStatus.NOT_FOUND;

            String[] pars = response.split("<ReturnCode>");
            if (pars.length < 2 || pars[1] == null) return JobStatus.NOT_FOUND;
            pars = pars[1].split("</ReturnCode>");
            if (pars.length < 2 || pars[0] == null) return JobStatus.NOT_FOUND;
            String statusId = pars[0];
            if(statusId.equals(BatchJobStatus.NOT_STARTED))
                return JobStatus.STARTING;
            else if(statusId.equals(BatchJobStatus.CHECKED_OUT))
                return JobStatus.RUNNING;
            else if(statusId.equals(BatchJobStatus.COMPLETE))
                return JobStatus.SUCCESS;
            else return JobStatus.ERROR;
        } catch (RestClientException e) {
            if (e.getMessage().contains("ConnectException")) {
                log.warn("Сервер xPression [" + AppConfiguration.get("XPRESSION_URL") + "] отказал в доступе для POST запросов по REST.");
                e.printStackTrace();
            } else {
                log.error(e.getClass().getName() + " " + e.getLocalizedMessage());
                e.printStackTrace();
            }
        }

        return JobStatus.NO_DEFINITION;
    }

    private enum  JobStatus{
        NOT_FOUND,
        STARTING,
        RUNNING,
        SUCCESS,
        ERROR,
        NO_DEFINITION
    }

    public static final class BatchJobStatus {
        public final static String NOT_STARTED 		=  "-1";
        public final static String CHECKED_OUT		=  "0";
        public final static String COMPLETE 		=  "1";
        public final static String NOT_FOUND 		=  "2";
        public final static String ERROR 		    =  "3";
    }


}
