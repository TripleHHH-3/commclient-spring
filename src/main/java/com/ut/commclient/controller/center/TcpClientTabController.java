package com.ut.commclient.controller.center;

import com.ut.commclient.common.BufferedWriterLock;
import com.ut.commclient.config.Config;
import com.ut.commclient.config.HeartBeat;
import com.ut.commclient.model.RecModel;
import com.ut.commclient.util.FileUtil;
import com.ut.commclient.util.ResUtil;
import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @description:
 * @author: 黄辉鸿
 * @create: 2020-08-04 18:50
 **/
@Log4j2
@Getter
@FXMLController
public class TcpClientTabController implements Initializable {
    @FXML
    private TextField ipTxt;
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
    private TextArea recMsgTxt;
    @FXML
    private Tab tcpClientTab;

    private Boolean isStop;
    private BufferedWriterLock writer;
    private Socket socket;
    private BufferedReader reader;
    private Long lastEchoTime;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void connectBegin(ActionEvent actionEvent) {
        //获取ip与端口 TODO 验证ip、端口
        String ip = ipTxt.getText();
        int port = Integer.parseInt(portTxt.getText());

        //禁止按钮
        beginBtn.setDisable(true);
        stopBtn.setDisable(false);
        isStop = false;

        //开启线程,连接服务端
        new Thread(this::connectToServer).start();
    }

    public void connectToServer() {
        String ip = ipTxt.getText();
        int port = Integer.parseInt(portTxt.getText());

        //连接服务器，失败则会一直尝试重连
        for (int i = 1; ; i++) {
            try {
                socket = new Socket(ip, port);
                writer = new BufferedWriterLock(new OutputStreamWriter(socket.getOutputStream()));
                break;
            } catch (Exception e) {
                e.printStackTrace();
                if (isStop) return;
                //停止多长时间后再重试
                try {
                    Thread.sleep(HeartBeat.getTimeRetry());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                recMsgTxt.appendText("连接失败：" + e.getMessage() + "重试次数：" + i + "\n");
                //日志
                try {
                    log.error(InetAddress.getLocalHost().getHostAddress() + "===>" + ip + ":" + port, e);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        //连接成功
        stopBtn.setDisable(false);
        sendBtn.setDisable(false);
        recMsgTxt.appendText("连接成功" + "\n");

        //开启接收信息线程
        new Thread(this::recMsgThread).start();

        //初始化心跳包时间
        lastEchoTime = System.currentTimeMillis();

    }

    private void recMsgThread() {
        try {
            //构造输入流
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            char[] chars = new char[1024];

            //循环读取，此处会阻塞
            int len;
            while ((len = reader.read(chars)) != -1) {
                //读出来并打印
                String msg = new String(chars, 0, len);

                //如果收到心跳包则进行相应的处理并忽略此信息
                if (msg.equals(HeartBeat.getEchoServer()) || msg.equals(HeartBeat.getEchoClient())) {
                    System.out.println(msg);
                    heartBeatHandler(msg);
                    continue;
                }

                recMsgTxt.appendText(msg + "\n");
                System.out.println(msg);

                //写入文件
                RecModel recModel = new RecModel("Server",
                        socket.getInetAddress().getHostAddress(),
                        socket.getPort(),
                        "client",
                        socket.getLocalAddress().getHostAddress(),
                        socket.getLocalPort(),
                        msg);
                FileUtil.write(Config.getRecPath(), recModel);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
            ResUtil.closeReader(reader);
        }
    }

    private void heartBeatHandler(String heartBeatStr) {
        //如果是心跳包的回应则刷新时间，否则是心跳包则回复本机IP过去
        if (heartBeatStr.equals(HeartBeat.getEchoServer())) {
            lastEchoTime = System.currentTimeMillis();
        } else {
            writer.writeFlush(HeartBeat.getEchoClient() + ":" + socket.getLocalAddress().getHostAddress() + ":" + socket.getLocalPort());
        }
    }

    public void connectEnd(ActionEvent actionEvent) {
        isStop = true;
        beginBtn.setDisable(false);
        stopBtn.setDisable(true);
        sendBtn.setDisable(true);
        recMsgTxt.appendText("停止连接" + "\n");

        ResUtil.closeWriterAndReaderAndSocket(reader, writer, socket);
    }

    public void sendMsg(ActionEvent actionEvent) {
        String msg = sendMsgTxt.getText();
        writer.writeFlush(msg);
    }

    public void closeBefore(Event event) {
        isStop = true;
        ResUtil.closeWriterAndReaderAndSocket(reader, writer, socket);
    }
}
