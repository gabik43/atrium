package atrium.fx.ru.controller;


import atrium.fx.ru.AppConfiguration;
import atrium.fx.ru.file.FileInfoForThree;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;

import java.io.File;

/**
 * Created by A.Gabdrakhmanov on 08.09.2015.
 * Класс для работы с объектом ThreeView.
 */
public class ThreeHandler {

    /*Если у объекта treeItem нет вложенных элементов - это говорит о том, что
    * это краевой элемент, содержащий файлы.*/
    public static boolean isItemNotChildren(TreeItem<String> treeItem){
        if (treeItem.getChildren().isEmpty()){
            return true;
        }
        return false;
    }


    /*Получение полного пути папки
    * @param treeItem - элемент, из которого необходимо составить имя папки.*/
    public static File getFolder(TreeItem<String> treeItem){
        StringBuilder pathFile = new StringBuilder();

        pathFile.append("//").append(AppConfiguration.SERVER_IP).append("/").append(AppConfiguration.FOLDER_FILE).append("/");
        int countFolderAndFileName = getCountParent(treeItem);

        TreeItem<String> currentTreeItem = treeItem;
        String[] stringName = new String[countFolderAndFileName];

        // Получаем путь файла в обратном порядке file, folderOne, folderTwo ....
        for (int i = 0; i < countFolderAndFileName; i++){
            stringName[i] = currentTreeItem.getValue();
            currentTreeItem = currentTreeItem.getParent();
        }

        // Добавляем путь файла в нужном порядке: ..., folderTwo, folderOne, file
        for (int i = countFolderAndFileName - 1; i >=0; i-- ){
            pathFile.append(stringName[i]);
            pathFile.append("/");
        }
        return new File(pathFile.toString());
    }

    private static int getCountParent(TreeItem<String> rootNode){
        TreeItem<String> rootNodeCurrent = rootNode.getParent();
        int countParrent = 1;
        while (true){
            if (rootNodeCurrent.getValue().equals(AppConfiguration.NAME_ROOT_ELEMENT)){
                return countParrent;
            }
            countParrent++;
            rootNodeCurrent = rootNodeCurrent.getParent();
        }

    }

    /*Добавление папки в структуру treeView в виде:
    * folderRoot
    *   folder
    *     folder
    *       ...
    *     ...
    *   folder
    *   ...
    *   folder
    *   Функция рекурсивная*/
    public static void addFolderInThree(int countParrentFolder, FileInfoForThree fileInfoForThree, TreeItem<String> rootNode){
        if (countParrentFolder == 0) return;
        if (rootNode.getChildren().isEmpty()){
            tryToAddElement(fileInfoForThree.absolutePath[countParrentFolder - 1], rootNode.getChildren());
        }
        if (rootNode.getChildren().get(0).getValue().indexOf(".pdf") != -1){
            return;
        }
        tryToAddElement(fileInfoForThree.absolutePath[countParrentFolder - 1], rootNode.getChildren());
        for (TreeItem<String> depNode : rootNode.getChildren()) {
            if (depNode.getValue().equals(fileInfoForThree.absolutePath[countParrentFolder - 1]))
                addFolderInThree(--countParrentFolder, fileInfoForThree, (CheckBoxTreeItem) depNode);
        }
    }


    /*Попытка добавить элемент в дерево. Элемент добавляется только в случае,
    * если на его уровне нет элементов с таким именем*/
    private static void tryToAddElement(String nameElement, ObservableList<TreeItem<String>> nodeList){

        if (nodeList.isEmpty()){
            addElement(nameElement, nodeList);
        }

        boolean foundElement = false;
        for (TreeItem<String> depNode : nodeList) {
            if (depNode.getValue().contentEquals(nameElement)){
                foundElement = true;
            }
        }
        if (!foundElement){
            addElement(nameElement, nodeList);
        }
    }

    private static void addElement(String nameElement, ObservableList<TreeItem<String>> nodeList){

        CheckBoxTreeItem<String> newThreeElement = new CheckBoxTreeItem<String>(nameElement);
/*        newThreeElement.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                int i = 0;
            }
        });*/
        nodeList.add(newThreeElement);

    }
}
