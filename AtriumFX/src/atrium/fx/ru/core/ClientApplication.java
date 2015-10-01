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
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by A.Gabdrakhmanov on 08.09.2015.
 * Логика инициализации приложения.
 */
public class ClientApplication implements UITemplate{

    private Stage stage, archStage;
    private Parent root;
    private Scene scene, archScene;
    private CheckBoxTreeItem<String> rootNode = new CheckBoxTreeItem<String>(AppConfiguration.get("NAME_ROOT_ELEMENT"));
    List<FileInfoForThree> fileInfoForThrees;
    private static Set<File> noChildFolders = new HashSet();
    Button printButton;
    Button archiveButton;


    public ClientApplication(Stage stage){
        this.stage = stage;
    }

    public void init(){
        readStructFileAndFolder();
        generateThreeView();
    }

    public void start() {
        initApplication();
        displayView();
    }

    @Override
    public void show() {
        initApplication();
        displayView();
    }


    /*Чтение xml с дизайном, установка размеров сцены*/
    private void initApplication(){
        try {
            root = FXMLLoader.load(getClass().getResource("form.fxml"));
            scene = new Scene(root, 1024, 768);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*Чтение всех файлов из папки и преобразование их в структуру FileInfoForThree*/
    private void readStructFileAndFolder(){
        FileHandler fileHandlerManager = new FileHandler();
        List<File> fileList = fileHandlerManager.getFolderList(AppConfiguration.get("PATH_TO_OUT_FOLDER"));
        fileInfoForThrees = new ArrayList<FileInfoForThree>();
        for (File file : fileList){
            FileInfoForThree fileInfoForThree = new FileInfoForThree(file);
            fileInfoForThrees.add(fileInfoForThree);
        }
    }

    /*Генерация дерева для представления файлов в интерфейсе пользователя*/
    private void generateThreeView(){
        for (FileInfoForThree fileInfoForThree : fileInfoForThrees) {
            ThreeHandler.addFolderInThree(fileInfoForThree.getCountFolder(),fileInfoForThree, rootNode);
        }
        findNoChildFolders(rootNode);
    }

    /* Установка настроек сцены, связываение view элекментов с данными с данными*/
    private void displayView(){
        stage.setTitle(AppConfiguration.get("TITLE"));
        stage.setScene(scene);
        scene.setFill(Color.LIGHTGRAY);

        // Получаем View элемент
        SplitPane splitPane = (SplitPane)scene.lookup("#split");
        ObservableList<Node> nodes = splitPane.getItems();
        AnchorPane anchorPane = (AnchorPane)nodes.get(0);
        ObservableList<Node> children = anchorPane.getChildren();
        VBox vBox = (VBox)children.get(0);
        ObservableList<Node> childrenVbox = vBox.getChildren();
        stage.show();


        TreeView treeView = (TreeView)childrenVbox.get(0);
        treeView.setRoot(rootNode);
        treeView.setCellFactory(CheckBoxTreeCell.<String>forTreeView());


        // Получаем кнопки
        printButton = (Button)scene.lookup("#start_print_button");
        archiveButton = (Button)scene.lookup("#start_archive_button");

        printButton.setDisable(noChildFolders.isEmpty()?true : false);
        archiveButton.setDisable(noChildFolders.isEmpty()?true : false);

    }

    public void findNoChildFolders(final CheckBoxTreeItem<String> node) {
        if (node==null) return;
        if (node.getChildren().isEmpty()) {
            node.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (newValue==true) {
                        noChildFolders.add(ThreeHandler.getFolder(node));
                    } else {
                        noChildFolders.remove(ThreeHandler.getFolder(node));
                    }
                    printButton.setDisable(noChildFolders.isEmpty()?true : false);
                    archiveButton.setDisable(noChildFolders.isEmpty()?true : false);
                }
            });
        }

        for (TreeItem<String> child : node.getChildren()) {
            findNoChildFolders((CheckBoxTreeItem<String>) child);
        }
    }

    public static List<File> getSelectedFiles() {
        List<File> files = new LinkedList();
        for (File folder : noChildFolders) {
            for (File file : folder.listFiles()) {
                files.add(file);
            }
        }
        return files;
    }

    public static Set<File> getNoChildFolders() {
        return noChildFolders;
    }

}
