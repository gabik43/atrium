package atrium.fx.ru.core;

import atrium.fx.ru.AppConfiguration;
import atrium.fx.ru.controller.ThreeHandler;
import atrium.fx.ru.file.FileHandler;
import atrium.fx.ru.file.FileInfoForThree;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.*;

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

    @Override
    public void start(){
        initApplication();
        displayView();
    }


    /*?????? xml ? ????????, ????????? ???????? ?????*/
    private void initApplication(){
        try {
            root = FXMLLoader.load(getClass().getResource("modalWindow.fxml"));
            scene = new Scene(root, 800, 600);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /* ????????? ???????? ?????, ??????????? view ?????????? ? ??????? ? ???????*/
    private void displayView(){
        stage.setTitle(AppConfiguration.TITLE);
        stage.setScene(scene);
        scene.setFill(Color.LIGHTGRAY);

        // Получаем кнопки
        message = (Text)scene.lookup("#message");

        stage.show();

    }

    public void setMessage(String message){
        this.message.setText(message);
    }
}
