package com.ut.commclient.view;

import com.ut.commclient.componet.TabPaneHasList;
import com.ut.commclient.controller.center.TcpClientTabController;
import com.ut.commclient.controller.center.TcpServerTabController;
import com.ut.commclient.controller.center.UdpDatagramTabController;
import com.ut.commclient.controller.center.UdpMulticastTabController;
import com.ut.commclient.model.TreeViewModel;
import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;
import lombok.SneakyThrows;


/**
 * @description:
 * @author: 黄辉鸿
 * @create: 2020-08-04 11:49
 **/
@FXMLView(value = "/view/MainView.fxml")
public class MainView extends AbstractFxmlView {

    @Override
    @SneakyThrows
    public Parent getView() {
        Parent parent = super.getView();
        FXMLLoader fxmlLoader;

        //找到每个TabPane，添加相应的Tab和controller
        TabPaneHasList<TcpClientTabController> tcpClientTabPane = (TabPaneHasList) parent.lookup("#tcpClientTabPane");
        fxmlLoader = new FXMLLoader(getClass().getResource("/view/center/TcpClientTabView.fxml"));
        tcpClientTabPane.addTab(fxmlLoader.load(), fxmlLoader.getController());

        TabPaneHasList<TcpServerTabController> tcpServerTabPane = (TabPaneHasList) parent.lookup("#tcpServerTabPane");
        fxmlLoader = new FXMLLoader(getClass().getResource("/view/center/TcpServerTabView.fxml"));
        tcpServerTabPane.addTab(fxmlLoader.load(), fxmlLoader.getController());

        TabPaneHasList<UdpDatagramTabController> udpDatagramTabPane = (TabPaneHasList) parent.lookup("#udpDatagramTabPane");
        fxmlLoader = new FXMLLoader(getClass().getResource("/view/center/UdpDatagramTabView.fxml"));
        udpDatagramTabPane.addTab(fxmlLoader.load(), fxmlLoader.getController());

        TabPaneHasList<UdpMulticastTabController> udpMulticastTabPane = (TabPaneHasList) parent.lookup("#udpMulticastTabPane");
        fxmlLoader = new FXMLLoader(getClass().getResource("/view/center/UdpMulticastTabView.fxml"));
        udpMulticastTabPane.addTab(fxmlLoader.load(), fxmlLoader.getController());

        TreeView<TreeViewModel> treeView = (TreeView) parent.lookup("#treeView");

        //treeView的TCP节点
        TreeItem<TreeViewModel> tcp = new TreeItem<>(new TreeViewModel("TCP", null));
        TreeItem<TreeViewModel> tcpClient = new TreeItem<>(new TreeViewModel("tcpClient", tcpClientTabPane));
        TreeItem<TreeViewModel> tcpServer = new TreeItem<>(new TreeViewModel("tcpServer", tcpServerTabPane));
        tcp.getChildren().add(tcpClient);
        tcp.getChildren().add(tcpServer);
        tcp.setExpanded(true);

        //treeView的UDP节点
        TreeItem<TreeViewModel> udp = new TreeItem<>(new TreeViewModel("UDP", null));
        TreeItem<TreeViewModel> udpClient = new TreeItem<>(new TreeViewModel("udpDatagram", udpDatagramTabPane));
        TreeItem<TreeViewModel> udpServer = new TreeItem<>(new TreeViewModel("udpMulticast", udpMulticastTabPane));
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
                if (!newItem.isLeaf()) return;

                //拿到选中的item里面保存的stackPane后置为显示并置前
                Node stackNode = newItem.getValue().getStackNode();
                stackNode.toFront();
                stackNode.setVisible(true);

                //拿到后一位的stackPane置为隐藏
                ObservableList<Node> children = ((StackPane) stackNode.getParent()).getChildren();
                children.get(children.size() - 2).setVisible(false);
            }
        });

        return parent;
    }
}
