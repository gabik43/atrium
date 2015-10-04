package atrium.fx.ru.core;

import atrium.fx.ru.AppConfiguration;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by Andrey.A.Koshkin on 24.09.2015.
 */
public class ModalWindow  implements UITemplate{
    private Stage stage, archStage;
    private Parent root;
    private Scene scene;

    private Text message;


    public ModalWindow(Stage stage){
        this.stage = stage;
    }


    public void start(){
        initApplication();
        displayView();
    }


    @Override
    public void show(){
        displayView();
    }

    /*?????? xml ? ????????, ????????? ???????? ?????*/
    private void initApplication(){
        try {
            root = FXMLLoader.load(getClass().getResource("modalWindow.fxml"));
            scene = new Scene(root, 1024, 768);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /* ????????? ???????? ?????, ??????????? view ?????????? ? ??????? ? ???????*/
    private void displayView(){
        stage.setTitle(AppConfiguration.get("TITLE"));
        stage.setScene(scene);
        scene.setFill(Color.LIGHTGRAY);

        stage.show();

        // Получаем кнопки
        message = (Text)scene.lookup("#message");

    }

    public void setMessage(String message){
        this.message.setText(message);
    }
}
