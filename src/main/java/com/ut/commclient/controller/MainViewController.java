package com.ut.commclient.controller;

import com.ut.commclient.model.TreeViewModel;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeView;
import lombok.Getter;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @description:
 * @author: 黄辉鸿
 * @create: 2020-08-04 11:49
 **/
@Getter
@FXMLController
public class MainViewController implements Initializable {
    @FXML
    private TabPane tcpClientTabPane;
    @FXML
    private TabPane tcpServerTabPane;
    @FXML
    private TabPane udpDatagramTabPane;
    @FXML
    private TabPane udpMulticastTabPane;
    @FXML
    private TreeView<TreeViewModel> treeView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}
