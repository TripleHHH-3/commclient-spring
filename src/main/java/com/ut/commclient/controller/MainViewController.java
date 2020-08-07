package com.ut.commclient.controller;

import com.ut.commclient.componet.TabPaneHasList;
import com.ut.commclient.controller.center.TcpClientTabController;
import com.ut.commclient.controller.center.TcpServerTabController;
import com.ut.commclient.controller.center.UdpDatagramTabController;
import com.ut.commclient.controller.center.UdpMulticastTabController;
import com.ut.commclient.model.TreeViewModel;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
    private TabPaneHasList<TcpClientTabController> tcpClientTabPane;
    @FXML
    private TabPaneHasList<TcpServerTabController> tcpServerTabPane;
    @FXML
    private TabPaneHasList<UdpDatagramTabController> udpDatagramTabPane;
    @FXML
    private TabPaneHasList<UdpMulticastTabController> udpMulticastTabPane;
    @FXML
    private TreeView<TreeViewModel> treeView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}
