package atrium.fx.ru.controller;

import atrium.fx.ru.InternalInterface;
import atrium.fx.ru.LogDB;
import atrium.fx.ru.PrinterTask;
import atrium.fx.ru.arhive.ZIPArchive;
import atrium.fx.ru.core.ClientApplication;
import atrium.fx.ru.file.FileHandler;
import atrium.fx.ru.viewPDF.PDFViewer;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by A.Gabdrakhmanov on 08.09.2015.
 */
public class Controller{
    @FXML
    public  TreeView three;

    @FXML
    public ListView list;

    @FXML
    public SplitPane split;

    @FXML
    public Button start_print_button;


    @FXML
    private void start_print(ActionEvent actionEvent) {

//        List<File> files = new LinkedList();
//        try {
//            files.add(new File("C:/ForPrint/50kb.pdf"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        new PrinterTask(files).printWithQueue();

        new PrinterTask(ClientApplication.getSelectedFiles()).printWithQueue();
    }

    /* Клик по папке в дереве (открытие содержимого папки)*/
    @FXML
    public void tree_mouse_clicked(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 1) {
            clickTree();
        }
    }

    /*Обработка одинарного клика на элементе TreeView*/
    private void clickTree(){
        if (isItemSelectedAndNotChildren()){
            setNewFileList();
        } else {
            clearFileList();
        }
    }

    /* Проверка: Есть выбранный элемент, и этот элемент не имеет вложенных (последняя ветка в дереве)?*/
    private boolean isItemSelectedAndNotChildren(){
        TreeItem<String> selectionItem = getSelectedTreeItem();
        if (selectionItem == null) return false;  // Нет выбранного элемента, выходим.
        if (ThreeHandler.isItemNotChildren(selectionItem)) {
            return true;
        }
        return false;
    }

    /* Получение выбранного элемента в дереве TreeView*/
    private TreeItem<String> getSelectedTreeItem(){
        return (TreeItem<String>) three.getSelectionModel().getSelectedItem();
    }


    /* Установка нового списка файлов в ListView*/
    private void setNewFileList(){
        ObservableList<String> observableList = getFileListFromThreeView();
        list.setItems(observableList);
    }

    /*Получение списка файлов из выбранного элемента TreeView*/
    private ObservableList<String> getFileListFromThreeView(){
        TreeItem<String> selectionItem = getSelectedTreeItem();
        File folder = ThreeHandler.getFolder(selectionItem);
        List<File> fileList = FileHandler.getFileList(folder.getAbsolutePath());
        List<String> nameFiles = new ArrayList<String>();
        for (File fileCurrent : fileList){
            nameFiles.add(fileCurrent.getName());
        }
        return FXCollections.observableList(nameFiles);
    }

    /* Очистка списка файлов в ListView*/
    private void clearFileList(){
        list.getItems().clear();
    }
    @FXML
    public void click_list(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2){
            doubleClickList();
        }
    }

    /*Двойной клик мыши на элементе ListView*/
    private void doubleClickList(){
        // Получаем путь к файлу из TreeView:
        TreeItem<String> selectionThreeItem = getSelectedTreeItem();
        File folder = ThreeHandler.getFolder(selectionThreeItem);

        // Получаем имя файла из ListView:
        String nameSelectedFile = getSelectedListItem();

        // Формируем файл и выполняем просмотр документа:
        File file = new File(folder.getAbsolutePath() + "/" + nameSelectedFile);
        PDFViewer.View(file);
    }
    /* Получение выбранного элемента в списке ListView*/
    private String getSelectedListItem(){
      return (String)list.getSelectionModel().getSelectedItem();
    }


    private String zipFilePath = "";
    private boolean waitArchFlag = true;
    public void start_arhive(ActionEvent actionEvent) {

        JFileChooser fc = new JFileChooser();
        FileNameExtensionFilter xmlFilter = new FileNameExtensionFilter("Zip files (*.zip)", "zip");
        fc.resetChoosableFileFilters();
        fc.addChoosableFileFilter(xmlFilter);
        fc.setFileFilter(xmlFilter);
        fc.setAcceptAllFileFilterUsed(false);

        if ( fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION ) {
            ClientApplication.getSelectedFiles();
            zipFilePath = fc.getSelectedFile().getPath();
            if (!zipFilePath.endsWith(".zip") && !zipFilePath.endsWith(".ZIP"))
                zipFilePath += ".zip";

            InternalInterface.showWindow(InternalInterface.modalWindow);
            InternalInterface.modalWindow.setMessage("Не закрывайте окно. Идет архивирование");
            InternalInterface.prepareDelayedSHow(InternalInterface.clientApplication);
            
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ZIPArchive.start(ClientApplication.getSelectedFiles(), new File(zipFilePath));

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            InternalInterface.showDelayedWindow();
                        }
                    });

                }
            }).start();

            Set<File> foldersSet = ClientApplication.getNoChildFolders();
            for (File file : foldersSet) {
                //String filePath = file.getAbsolutePath().substring(file.getAbsolutePath().indexOf(AppConfiguration.FOLDER_FILE) + AppConfiguration.FOLDER_FILE.length() + 1);
                LogDB.info("Архивирование документов из папки " + file.getAbsolutePath());
            }
        }

    }

}
