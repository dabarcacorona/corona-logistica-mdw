package cl.corona.integration.schuduler;

import cl.corona.integration.service.IntegrationServices;
import cl.corona.integration.service.IntegrationServicesUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ScheduledTasks {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduledTasks.class);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Autowired
    private IntegrationServices intservice;
    private IntegrationServicesUpload intserviceUp;

    @Scheduled(cron = "${cron.expression}")
    public void scheduledZip() throws InterruptedException, IOException {

        LOG.info("{} : Inicio transferencia de archivos",
                dateTimeFormatter.format(LocalDateTime.now()));

        // Date exceptions
        LocalDate today = LocalDate.now();
        int count = 0;

        intservice.DownloadFile();

        LOG.info("{} : Descarga Ok",
                dateTimeFormatter.format(LocalDateTime.now()));

        intserviceUp.UploadFile();

        LOG.info("{} : Carga a PWC Ok",
                dateTimeFormatter.format(LocalDateTime.now()));

        LOG.info("{} : Fin transferencia de archivos",
                dateTimeFormatter.format(LocalDateTime.now()));

    }
}
