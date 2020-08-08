package com.ut.commclient.controller.left;

import com.ut.commclient.componet.TabPaneHasList;
import com.ut.commclient.controller.MainViewController;
import com.ut.commclient.model.TreeViewModel;
import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import org.springframework.beans.factory.annotation.Autowired;

@FXMLController
public class TreeViewMenuController {
    @Autowired
    private MainViewController mainViewController;

    public void addTab(ActionEvent actionEvent) {
        //todo 完善逻辑
        TreeItem<TreeViewModel> selectedItem = mainViewController.getTreeView().getSelectionModel().getSelectedItem();
        TreeViewModel treeViewModel = selectedItem.getValue();
    }
}
