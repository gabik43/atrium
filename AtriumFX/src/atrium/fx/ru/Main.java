package atrium.fx.ru;

import atrium.fx.ru.core.ClientApplication;
import atrium.fx.ru.core.ModalWindow;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
        //LogDB.closeConnection();
    }

    @Override
    public void start(Stage stage) throws IOException {
        LogDB.setupConnection();
        LogDB.info("Start client application");
        //AppConfiguration.readConfigFile();

        InternalInterface.modalWindow = new ModalWindow(stage);
        InternalInterface.modalWindow.start();
        InternalInterface.modalWindow.setMessage("Подождите, идет загрузка");

        InternalInterface.clientApplication = new ClientApplication(stage);
        InternalInterface.prepareDelayedSHow(InternalInterface.clientApplication);

        new Thread(new Runnable() {
            @Override
            public void run() {
                InternalInterface.clientApplication.init();
                InternalInterface.setDelayedWindow(InternalInterface.clientApplication);
                InternalInterface.showDelayedWindow();
            }
        }).start();







    }
}
