package com.ut.commclient.controller.center;

import com.ut.commclient.common.BufferedWriterLock;
import com.ut.commclient.contant.PropertyKey;
import com.ut.commclient.model.TcpClientModel;
import com.ut.commclient.thread.TcpServerThread;
import com.ut.commclient.util.ResUtil;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.StringConverter;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @description:
 * @author: 黄辉鸿
 * @create: 2020-08-04 19:30
 **/
@Log4j2
@Getter
public class TcpServerTabController implements Initializable {
    @FXML
    private Tab tcpServerTab;
    @FXML
    private TextField portTxt;
    @FXML
    private Button beginBtn;
    @FXML
    private Button stopBtn;
    @FXML
    private Button sendBtn;
    @FXML
    private TextArea sendMsgTxt;
    @FXML
    private ListView<TcpClientModel> clientListView;
    @FXML
    private TextArea recTxt;

    private ServerSocket serverSocket;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //clientListView的显示转换
        clientListView.setCellFactory(TextFieldListCell.forListView(new StringConverter<>() {
            @Override
            public String toString(TcpClientModel tcpClientModel) {
                return tcpClientModel.getIp() + ":" + tcpClientModel.getPort();
            }

            @Override
            public TcpClientModel fromString(String s) {
                return null;
            }
        }));
    }

    public void listenBegin(ActionEvent actionEvent) {
        //todo 校验
        int port = Integer.parseInt(portTxt.getText());
        beginBtn.setDisable(true);
        sendBtn.setDisable(false);

        //设置tab标题
        tcpServerTab.setText(portTxt.getText());

        //开启监听线程
        new Thread(() -> {
            try {
                //服务端在xxx端口监听客户端的TCP连接请求
                serverSocket = new ServerSocket(port);

                stopBtn.setDisable(false);

                while (true) {
                    //等待客户端的连接，会阻塞
                    Socket client = serverSocket.accept();

                    //打印成功连接
                    recTxt.appendText("与客户端连接成功！" + client.getInetAddress().getHostAddress() + "\n");

                    //把连接进来的客户端放进队列
                    TcpClientModel tcpClientModel = new TcpClientModel(
                            client.getInetAddress().getHostAddress(),
                            client.getPort(),
                            client,
                            new BufferedWriterLock(new OutputStreamWriter(client.getOutputStream())),
                            System.currentTimeMillis()
                    );

                    Platform.runLater(() -> clientListView.getItems().add(tcpClientModel));

                    //为每个客户端的连接开启一个线程
                    new Thread(new TcpServerThread(serverSocket, client, recTxt, clientListView)).start();
                }

            } catch (Exception e) {
                if (e instanceof BindException) recTxt.appendText("端口已占用" + "\n");

                beginBtn.setDisable(false);
                stopBtn.setDisable(true);
                recTxt.appendText("停止监听" + "\n");

                ResUtil.closeServerSocket(serverSocket);
                e.printStackTrace();
                log.error(e);
            }

        }).start();
    }

    public void listenEnd(ActionEvent actionEvent) {
        //关闭服务端
        ResUtil.closeServerSocket(serverSocket);

        //关闭客户端
        ObservableList<TcpClientModel> clientList = clientListView.getItems();
        if (clientList != null && clientList.size() > 0) {
            clientList.forEach(client -> {
                ResUtil.closeWriterAndSocket(client.getWriter(), client.getSocket());
                Platform.runLater(() -> clientList.remove(client));
            });
        }

        beginBtn.setDisable(false);
        stopBtn.setDisable(true);
        sendBtn.setDisable(true);
    }

    public void sendMsg(ActionEvent actionEvent) {
        //todo 弹出提示未选择客户端
        TcpClientModel client = clientListView.getSelectionModel().getSelectedItem();
        if (client != null) {
            try {
                client.getWriter().writeFlush(sendMsgTxt.getText());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void beforeClose(Event event) {
        //关闭tab之前，释放套接口资源，同时在tabPane移除对应的controller
        ((List) tcpServerTab.getTabPane().getProperties().get(PropertyKey.CONTROLLER_LIST)).remove(this);
        listenEnd(null);
    }
}
