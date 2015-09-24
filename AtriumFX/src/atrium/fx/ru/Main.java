package atrium.fx.ru;

import atrium.fx.ru.core.ClientApplication;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
        LogDB.closeConnection();
    }

    @Override
    public void start(Stage stage) throws IOException {
        LogDB.setupConnection();
        LogDB.info("Start client application");
        AppConfiguration.readConfigFile();
        ClientApplication clientApplication = new ClientApplication(stage);
        clientApplication.start();
    }
}
