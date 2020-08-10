package com.ut.commclient.controller.left;

import com.ut.commclient.contant.PropertyKey;
import com.ut.commclient.controller.MainViewController;
import com.ut.commclient.model.TreeViewModel;
import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.List;

/**
 * @description:
 * @author: 黄辉鸿
 * @create: 2020-08-09 19:29
 **/
@FXMLController
public class TreeViewMenuController {
    @Autowired
    private MainViewController mainViewController;

    @SneakyThrows
    public void addTab(ActionEvent actionEvent) {
        TreeItem<TreeViewModel> selectedItem = mainViewController.getTreeView().getSelectionModel().getSelectedItem();
        TabPane tabPane = selectedItem.getValue().getStackNode();
        URL url = (URL) tabPane.getProperties().get(PropertyKey.TAB_URL);
        FXMLLoader tabLoader = new FXMLLoader(url);
        //给tabPane添加tab
        tabPane.getTabs().add(tabLoader.load());
        //同时给controllerList添加
        List controllerList = (List) tabPane.getProperties().get(PropertyKey.CONTROLLER_LIST);
        controllerList.add(tabLoader.getController());
    }
}
