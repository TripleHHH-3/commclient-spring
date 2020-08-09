package com.ut.commclient.controller.center;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @description:
 * @author: 黄辉鸿
 * @create: 2020-08-04 19:48
 **/
@Log4j2
@Getter
public class UdpDatagramTabController implements Initializable {
    @FXML
    private TextArea recMsgTxt;
    @FXML
    private Tab udpDatagramTab;
    @FXML
    private TextField ipTxt;
    @FXML
    private TextField sendPortTxt;
    @FXML
    private Button bindBtn;
    @FXML
    private Button stopBindBtn;
    @FXML
    private Button sendBtn;
    @FXML
    private TextArea sendMsgTxt;
    @FXML
    private TextField recPortTxt;
    @FXML
    private Button listenBtn;
    @FXML
    private Button stopListenBtn;

    private DatagramSocket sendSocket;
    private DatagramSocket recSocket;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void bindBegin(ActionEvent actionEvent) {
        bindBtn.setDisable(true);
        try {
            sendSocket = new DatagramSocket();
            stopBindBtn.setDisable(false);
            sendBtn.setDisable(false);
            sendPortTxt.setDisable(true);
        } catch (Exception e) {
            e.printStackTrace();
            if (sendSocket != null) sendSocket.close();
            bindBtn.setDisable(false);
            log.error(e);
        }
    }

    public void bindEnd(ActionEvent actionEvent) {
        sendSocket.close();
        bindBtn.setDisable(false);
        stopBindBtn.setDisable(true);
        sendBtn.setDisable(true);
        sendPortTxt.setDisable(false);
    }

    public void sendMsg(ActionEvent actionEvent) {
        String ip = ipTxt.getText();
        byte[] bytes = sendMsgTxt.getText().getBytes();
        int port = Integer.parseInt(sendPortTxt.getText());
        //2.创建数据包对象

        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getByName(ip);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, inetAddress, port);

        //3.发送数据包
        try {
            sendSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e);
        }
    }

    public void listenBegin(ActionEvent actionEvent) {
        int port = Integer.parseInt(recPortTxt.getText());

        listenBtn.setDisable(true);
        //开启绑定线程
        new Thread(() -> {

            //1.创建DatagramSocket数据报包套接字对象
            try {
                recSocket = new DatagramSocket(port);

                stopListenBtn.setDisable(false);

                while (true) {
                    //创建一个数据包用于接收服务端返回的信息
                    byte[] bytes = new byte[1024];
                    DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);

                    //接收消息，会阻塞
                    recSocket.receive(datagramPacket);
                    //解析消息并打印数据
                    String recStr = new String(datagramPacket.getData());
                    System.out.println(recStr);
                    System.out.println(datagramPacket.getAddress());
                    System.out.println(datagramPacket.getPort());
                    recMsgTxt.appendText(recStr + "\n");

                }

            } catch (Exception e) {
                e.printStackTrace();
                if (recSocket != null) {
                    recSocket.close();
                }
                listenBtn.setDisable(false);
                log.error(e);
            }

        }).start();
    }

    public void listenEnd(ActionEvent actionEvent) {
        listenBtn.setDisable(false);
        stopListenBtn.setDisable(true);
        recSocket.close();
    }
}
