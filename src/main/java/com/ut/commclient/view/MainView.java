package com.ut.commclient.view;

import com.ut.commclient.contant.CommType;
import com.ut.commclient.contant.PropertyKey;
import com.ut.commclient.model.TreeViewModel;
import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import de.felixroske.jfxsupport.GUIState;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;
import lombok.SneakyThrows;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


/**
 * @description:
 * @author: 黄辉鸿
 * @create: 2020-08-04 11:49
 **/
@FXMLView(value = "/view/MainView.fxml")
public class MainView extends AbstractFxmlView {
    @SneakyThrows
    @PostConstruct
    private void initView() {
        //初始化四个tabPane
        TabPane tcpClientTabPane = initTabPane("#tcpClientTabPane", "/view/center/TcpClientTabView.fxml");
        TabPane tcpServerTabPane = initTabPane("#tcpServerTabPane", "/view/center/TcpServerTabView.fxml");
        TabPane udpDatagramTabPane = initTabPane("#udpDatagramTabPane", "/view/center/UdpDatagramTabView.fxml");
        TabPane udpMulticastTabPane = initTabPane("#udpMulticastTabPane", "/view/center/UdpMulticastTabView.fxml");

        TreeView<TreeViewModel> treeView = (TreeView) super.getView().lookup("#treeView");

        //treeView的TCP节点
        TreeItem<TreeViewModel> tcp = new TreeItem<>(new TreeViewModel("TCP", null));
        TreeItem<TreeViewModel> tcpClient = new TreeItem<>(new TreeViewModel(CommType.TCP_CLIENT.getName(), tcpClientTabPane));
        TreeItem<TreeViewModel> tcpServer = new TreeItem<>(new TreeViewModel(CommType.TCP_SERVER.getName(), tcpServerTabPane));
        tcp.getChildren().add(tcpClient);
        tcp.getChildren().add(tcpServer);
        tcp.setExpanded(true);

        //treeView的UDP节点
        TreeItem<TreeViewModel> udp = new TreeItem<>(new TreeViewModel("UDP", null));
        TreeItem<TreeViewModel> udpClient = new TreeItem<>(new TreeViewModel(CommType.UDP_DATAGRAM.getName(), udpDatagramTabPane));
        TreeItem<TreeViewModel> udpServer = new TreeItem<>(new TreeViewModel(CommType.UDP_MULTICAST.getName(), udpMulticastTabPane));
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

        //关闭程序时结束所有后台线程
        GUIState.getStage().setOnCloseRequest(event -> System.exit(0));
    }

    private TabPane initTabPane(String id, String path) throws IOException {
        //拿到根视图
        Parent parent = super.getView();
        //根据ID拿到tabPane
        TabPane tabPane = (TabPane) parent.lookup(id);
        //获得tab加载器
        FXMLLoader tabLoader = new FXMLLoader(getClass().getResource(path));
        //加载tab并添加到tabPane
        tabPane.getTabs().add(tabLoader.load());
        //把tab对应的controller放到tabPane的map
        Object controller = tabLoader.getController();
        tabPane.getProperties().put(PropertyKey.CONTROLLER_LIST, Collections.synchronizedList(new ArrayList<>(Arrays.asList(controller))));
        //把tab的URL存到tabPane的map，方便listView的菜单使用
        tabPane.getProperties().put(PropertyKey.TAB_URL, tabLoader.getLocation());

        return tabPane;
    }
}
