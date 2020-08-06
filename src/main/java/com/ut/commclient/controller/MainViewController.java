package com.ut.commclient.controller;

import com.ut.commclient.model.TreeViewModel;
import de.felixroske.jfxsupport.FXMLController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;
import lombok.Getter;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @description:
 * @author: 黄辉鸿
 * @create: 2020-08-04 11:49
 **/
@FXMLController
@Getter
public class MainViewController implements Initializable {
    @FXML
    private StackPane stackPane;
    @FXML
    private TreeView<TreeViewModel> treeView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //获取stackPane的子节点集合
        ObservableList<Node> stackPaneChildren = stackPane.getChildren();

        //treeView的TCP节点
        TreeItem<TreeViewModel> tcp = new TreeItem<>(new TreeViewModel("TCP", null));
        TreeItem<TreeViewModel> tcpClient = new TreeItem<>(new TreeViewModel("tcpClient", stackPaneChildren.get(0)));
        TreeItem<TreeViewModel> tcpServer = new TreeItem<>(new TreeViewModel("tcpServer", stackPaneChildren.get(1)));
        tcp.getChildren().add(tcpClient);
        tcp.getChildren().add(tcpServer);
        tcp.setExpanded(true);

        //treeView的UDP节点
        TreeItem<TreeViewModel> udp = new TreeItem<>(new TreeViewModel("UDP", null));
        TreeItem<TreeViewModel> udpClient = new TreeItem<>(new TreeViewModel("udpDatagram", stackPaneChildren.get(2)));
        TreeItem<TreeViewModel> udpServer = new TreeItem<>(new TreeViewModel("udpMulticast", stackPaneChildren.get(3)));
        udp.getChildren().add(udpClient);
        udp.getChildren().add(udpServer);
        udp.setExpanded(true);

        //treeView的根节点
        TreeItem<TreeViewModel> root = new TreeItem<>(new TreeViewModel("ROOT", null));
        root.getChildren().add(tcp);
        root.getChildren().add(udp);
        treeView.setRoot(root);
        treeView.setShowRoot(false);

        //treeView的节点显示转换
        treeView.setCellFactory(TextFieldTreeCell.forTreeView(new StringConverter<>() {
            @Override
            public String toString(TreeViewModel treeViewModel) {
                return treeViewModel.getName();
            }

            @Override
            public TreeViewModel fromString(String s) {
                return null;
            }
        }));

        //treeView添加点击监控
        treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<TreeViewModel>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<TreeViewModel>> observableValue, TreeItem<TreeViewModel> oldItem, TreeItem<TreeViewModel> newItem) {
                //拿到item里面保存的stackPane后置为显示
                Node selectedNode = newItem.getValue().getStackNode();
                if (selectedNode == null) return;
                selectedNode.toFront();
                selectedNode.setVisible(true);
                stackPaneChildren.get(stackPaneChildren.size() - 2).setVisible(false);
            }
        });
    }
}
