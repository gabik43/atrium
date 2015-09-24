package atrium.fx.ru;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.QueuedJobCount;
import javax.print.attribute.standard.Sides;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;
import javax.print.event.PrintJobListener;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Timofey on 10.09.2015.
 */
public class PrinterTask {

    public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
    public static final Charset UTF_8 = Charset.forName("UTF-8");

    private List<File> files;
    private List<File> failedFiles = new ArrayList();
    private List<File> successFiles = new ArrayList();
    private int printingDoc = -1; // для служателей объектов PrintJob, чтобы знать информацию о каком именно документе писать в лог
    private int maxQueueSize = 1;  // more or equals 1 //TODO вынести в клиентский конфиг
    private int sleepTime = 100;

    public PrinterTask(List<File> files) {
        this.files = files;
    }

    public List<File> getList() {
        return files;
    }

    public PrinterTask setList(List<File> files) {
        this.files = files;
        return this;
    }

    public void print() {
        try {
            if (files == null) {
                throw new NullPointerException("List is empty");
            }

            DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PAGEABLE;
            PrintRequestAttributeSet patts = new HashPrintRequestAttributeSet();
            patts.add(Sides.DUPLEX);

            PrintService[] ps = PrintServiceLookup.lookupPrintServices(flavor, patts);
            if (ps.length == 0) {
                throw new IllegalStateException("No Printer found");
            }
            PrintService myService = ServiceUI.printDialog(null, 50, 50, ps, PrintServiceLookup.lookupDefaultPrintService(), DocFlavor.INPUT_STREAM.AUTOSENSE, patts);
            if (myService == null) {
                throw new IllegalStateException("Printer not found");
            }

            for (final File file : files) {
                DocPrintJob printJob = myService.createPrintJob();
                printJob.addPrintJobListener(new PrintJobAdapter() {
                    @Override
                    public void printDataTransferCompleted(PrintJobEvent pje) {
                        System.out.println(file + ": Data has been successfully transferred to the print service");
                        successFiles.add(file);
                    }
                    @Override
                    public void printJobCompleted(PrintJobEvent pje) {
                        System.out.println(file + ": The job completed successfully.");
                        successFiles.add(file);
                    }
                    @Override
                    public void printJobFailed(PrintJobEvent pje) {
                        System.out.println(file + ": The job failed to complete successfully");
                        failedFiles.add(file);
                    }

                    /*Called to notify the client that the job was canceled by a user or a program.*/
                    @Override
                    public void printJobCanceled(PrintJobEvent pje) {
                        System.out.println(file + ": The job was canceled by a user or a program");
                        failedFiles.add(file);
                    }
                    @Override
                    public void printJobNoMoreEvents(PrintJobEvent pje) {
                        System.out.println(file + ": Print service has finished the job");
                    }
                    @Override
                    public void printJobRequiresAttention(PrintJobEvent pje) {
                        System.out.println(file + ": An error has occurred that the user might be able to fix");
                        failedFiles.add(file);
                    }
                });
                printJob.print(new SimpleDoc(new FileInputStream(file), DocFlavor.INPUT_STREAM.AUTOSENSE, null), new HashPrintRequestAttributeSet());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            runWhenFinish();
        }
    }

    public void printWithQueue() {
        try {
            LogDB.trace("Нажата кнопка печати");
            if (files == null || files.isEmpty()) {
                throw new NullPointerException("List is empty");
            }

            DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PAGEABLE;
            PrintRequestAttributeSet patts = new HashPrintRequestAttributeSet();
            patts.add(Sides.DUPLEX);

            PrintService[] ps = PrintServiceLookup.lookupPrintServices(flavor, patts);
            if (ps.length == 0) {
                throw new IllegalStateException("No Printer found");
            }
            final PrintService myService = ServiceUI.printDialog(null, 50, 50, ps, PrintServiceLookup.lookupDefaultPrintService(), DocFlavor.INPUT_STREAM.AUTOSENSE, patts);
            if (myService == null) {
                LogDB.warn("Печать отменена пользователем или принтер не найден");
                return;
            }
            LogDB.info("Принтер выбран, печать начата");

            LogDB.info("Принтер выбран, печать начата");

            final PrintJobListener printJobListener = new PrintJobListener(){

                /*Called to notify the client that data has been successfully transferred to the print service,
                and the client may free local resources allocated for that data.
                The client should not assume that the data has been completely printed after receiving this event.
                If this event is not received the client should wait for a terminal event
                (completed/canceled/failed) before freeing the resources.*/
                @Override
                public void printDataTransferCompleted(PrintJobEvent pje) {
                    LogDB.trace("Данные успешно отправлены на print service: " + files.get(printingDoc));
                    successFiles.add(files.get(printingDoc));
                }

                /*Called to notify the client that the job completed successfully.*/
                @Override
                public void printJobCompleted(PrintJobEvent pje) {
                    LogDB.trace("Работа завершена удачно: " + files.get(printingDoc));
                    successFiles.add(files.get(printingDoc));
                }

                /*Called to notify the client that the job failed to complete successfully and will have to be resubmitted.*/
                @Override
                public void printJobFailed(PrintJobEvent pje) {
                    LogDB.error("Работу не удалось завершить удачно: " + files.get(printingDoc));
                    failedFiles.add(files.get(printingDoc));
                }

                /*Called to notify the client that the job was canceled by a user or a program.*/
                @Override
                public void printJobCanceled(PrintJobEvent pje) {
                    LogDB.warn("Печать отменена пользователем или программой: " + files.get(printingDoc));
                    failedFiles.add(files.get(printingDoc));
                }

                /*Called to notify the client that no more events will be delivered.
                One cause of this event being generated is if the job has successfully completed,
                but the printing system is limited in capability and cannot verify this.
                This event is required to be delivered if none of the other terminal events (completed/failed/canceled) are delivered.*/
                @Override
                public void printJobNoMoreEvents(PrintJobEvent pje) {
                    LogDB.trace("Print service закончил работу с файлом: " + files.get(printingDoc));
                }

                /*Called to notify the client that an error has occurred that the user might be able to fix.
                One example of an error that can generate this event is when the printer runs out of paper.*/
                @Override
                public void printJobRequiresAttention(PrintJobEvent pje) {
                    LogDB.warn("Произошла ошибка, которую пользователь в силах исправить: " + files.get(printingDoc));
                    failedFiles.add(files.get(printingDoc));
                }
            };

            new Thread(new Runnable() {
                @Override
                public void run() {
                    startPrint(myService, printJobListener);
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
            runWhenFinish();
        }
    }

    private void startPrint (PrintService myService, PrintJobListener printJobListener) {
//        while (!files.isEmpty()) {
//            try {
//                DocPrintJob printJob = myService.createPrintJob();
//                printJob.addPrintJobListener(printJobListener);
//                printJob.print(new SimpleDoc(new FileInputStream(files.get(0)), DocFlavor.INPUT_STREAM.AUTOSENSE, null), new HashPrintRequestAttributeSet());
//                while (new Integer(myService.getAttribute(QueuedJobCount.class).toString()).intValue() > maxQueueSize - 1) {
//                    Thread.sleep(sleepTime);
//                }
//                if (maxQueueSize==1) {
//                    System.out.println("File has been sent to the printer: " + files.get(0));
//                }
//                files.remove(0);
//            } catch (Exception e) {
//                e.printStackTrace();
//                failedFiles.add(files.get(0));
//                files.remove(0);
//            }
//        }
//        runWhenFinish();
        DocPrintJob printJob;
        for (File fileToPrint : files) {
            try {
                printingDoc++;
                printJob = myService.createPrintJob();
                printJob.addPrintJobListener(printJobListener);
                printJob.print(new SimpleDoc(new FileInputStream(fileToPrint), DocFlavor.INPUT_STREAM.AUTOSENSE, null), new HashPrintRequestAttributeSet());
                while (new Integer(myService.getAttribute(QueuedJobCount.class).toString()).intValue() > maxQueueSize - 1) {
                    Thread.sleep(sleepTime);
                }
                if (maxQueueSize==1) {
                    LogDB.trace("Файл отправлен на принтер: " + fileToPrint);
                }
            } catch (Exception e) {
                e.printStackTrace();
                failedFiles.add(fileToPrint);
            }
        }
        runWhenFinish();
    }

    private void runWhenFinish() {
        LogDB.info("Печать закончена");
//        System.out.println("Отправлены на принтер: " + successFiles.toString());
//        System.out.println("Завершено с ошибкой: " + failedFiles.toString());
    }

}