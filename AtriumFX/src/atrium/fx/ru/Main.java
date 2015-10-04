package atrium.fx.ru;

import atrium.fx.ru.core.ClientApplication;
import atrium.fx.ru.core.ModalWindow;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
        //LogDB.closeConnection();
    }

    @Override
    public void start(Stage stage) throws IOException {
        if (!AppConfiguration.LOCAL) {
            AppConfiguration.setAppUrl(getHostServices().getDocumentBase());
        }
        LogDB.setupConnection();
        LogDB.info("Start client application");

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
//                Thread.currentThread().interrupt();
//                System.out.println(Thread.getAllStackTraces().keySet().size());
//                for (Thread t : Thread.getAllStackTraces().keySet()) {
//                    System.out.println(t);
//                }
//                PlatformImpl.tkExit();
                Platform.exit();
                System.exit(0);
            }
        });


        InternalInterface.clientApplication = new ClientApplication(stage);
        InternalInterface.modalWindow = new ModalWindow(stage);

        InternalInterface.clientApplication.start();
        InternalInterface.modalWindow.start();

        InternalInterface.clientApplication.show();
    }
}
