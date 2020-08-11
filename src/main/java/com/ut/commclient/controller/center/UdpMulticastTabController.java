package com.ut.commclient.controller.center;

import com.ut.commclient.contant.PropertyKey;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @description:
 * @author: 黄辉鸿
 * @create: 2020-08-04 19:50
 **/
@Log4j2
@Getter
public class UdpMulticastTabController implements Initializable {
    @FXML
    private TextArea recTxt;
    @FXML
    private Tab udpMulticastTab;
    @FXML
    private TextField bindIpGroupTxt;
    @FXML
    private TextField bindPortTxt;
    @FXML
    private Button bindBeginBtn;
    @FXML
    private Button bindStopBtn;
    @FXML
    private Button sendBtn;
    @FXML
    private TextArea sendMsgTxt;
    @FXML
    private TextField listenIpGroupTxt;
    @FXML
    private TextField listenPortTxt;
    @FXML
    private Button listenBeginBtn;
    @FXML
    private Button listenStopBtn;

    private MulticastSocket recSocket;
    private MulticastSocket sendSocket;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void bindBegin(ActionEvent actionEvent) {
        // todo 绑定失败的处理
        try {
            sendSocket = new MulticastSocket();
            bindBeginBtn.setDisable(true);
            bindStopBtn.setDisable(false);
            sendBtn.setDisable(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void bindEnd(ActionEvent actionEvent) {
        if (sendSocket != null) sendSocket.close();
        bindBeginBtn.setDisable(false);
        bindStopBtn.setDisable(true);
        sendBtn.setDisable(true);
    }

    public void sendMsg(ActionEvent actionEvent) {
        //todo ip 端口 格式判断
        String ip = bindIpGroupTxt.getText();
        int port = Integer.parseInt(bindPortTxt.getText());

        InetAddress ipGroup;
        try {
            ipGroup = InetAddress.getByName(ip);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        byte[] data = sendMsgTxt.getText().getBytes(); // 声明字节数组
        // 将数据打包
        DatagramPacket packet = new DatagramPacket(data, data.length, ipGroup, port);
        System.out.println(new String(data)); // 将广播信息输出
        try {
            sendSocket.send(packet); // 发送数据
        } catch (Exception e) {
            e.printStackTrace(); // 输出异常信息
        }
    }

    public void listenBegin(ActionEvent actionEvent) {
        //TODO ip 端口 格式判断
        int port = Integer.parseInt(listenPortTxt.getText());
        String ip = listenIpGroupTxt.getText();

        //设置标题
        udpMulticastTab.setText(ip + ":" + port);

        new Thread(() -> {
            try {
                InetAddress ipGroup = InetAddress.getByName(ip); // 指定接收地址
                recSocket = new MulticastSocket(port); // 绑定多点广播套接字
                recSocket.setTimeToLive(1);
                recSocket.joinGroup(ipGroup); // 加入广播组

                listenBeginBtn.setDisable(true);
                listenStopBtn.setDisable(false);

                while (true) {
                    byte[] data = new byte[1024]; // 创建byte数组
                    // 待接收的数据包
                    DatagramPacket packet = new DatagramPacket(data, data.length, ipGroup, port);
                    recSocket.receive(packet); // 接收数据包
                    String message = new String(packet.getData(), 0, packet.getLength()); // 获取数据包中内容
                    // 将接收内容显示在文本域中
                    recTxt.appendText(message + "\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (recSocket != null) recSocket.close();
            }
        }).start();
    }

    public void listenEnd(ActionEvent actionEvent) {
        listenBeginBtn.setDisable(false);
        listenStopBtn.setDisable(true);
        if (recSocket != null) recSocket.close();
    }

    public void closeBefore(Event event) {
        ((List) udpMulticastTab.getProperties().get(PropertyKey.CONTROLLER_LIST)).remove(this);
        if (sendSocket != null) {
            sendSocket.close();
        }
        if (recSocket != null) {
            recSocket.close();
        }
    }
}
