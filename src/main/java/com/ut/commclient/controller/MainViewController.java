package com.ut.commclient.controller;

import com.ut.commclient.model.TreeViewModel;
import com.ut.commclient.view.TcpClientTabView;
import com.ut.commclient.view.TcpServerTabView;
import com.ut.commclient.view.UdpDatagramTabView;
import com.ut.commclient.view.UdpMulticastTabView;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @description:
 * @author: 黄辉鸿
 * @create: 2020-08-04 11:49
 **/
@FXMLController
public class MainViewController implements Initializable {
    public TreeView<TreeViewModel> treeView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        TreeItem<TreeViewModel> root = new TreeItem<>(new TreeViewModel("ROOT", null));

        TreeItem<TreeViewModel> tcp = new TreeItem<>(new TreeViewModel("TCP", null));
        TreeItem<TreeViewModel> tcpClient = new TreeItem<>(new TreeViewModel("tcpClient", TcpClientTabView.class));
        TreeItem<TreeViewModel> tcpServer = new TreeItem<>(new TreeViewModel("tcpServer", TcpServerTabView.class));
        tcp.getChildren().add(tcpClient);
        tcp.getChildren().add(tcpServer);
        tcp.setExpanded(true);

        TreeItem<TreeViewModel> udp = new TreeItem<>(new TreeViewModel("UDP", null));
        TreeItem<TreeViewModel> udpClient = new TreeItem<>(new TreeViewModel("udpDatagramTab", UdpDatagramTabView.class));
        TreeItem<TreeViewModel> udpServer = new TreeItem<>(new TreeViewModel("udpMulticastTab", UdpMulticastTabView.class));
        udp.getChildren().add(udpClient);
        udp.getChildren().add(udpServer);
        udp.setExpanded(true);

        root.getChildren().add(tcp);
        root.getChildren().add(udp);
        treeView.setRoot(root);
        treeView.setShowRoot(false);
    }
}
