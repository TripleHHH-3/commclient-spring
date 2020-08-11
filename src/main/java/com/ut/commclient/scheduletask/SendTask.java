package com.ut.commclient.scheduletask;

import com.alibaba.fastjson.JSON;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.ut.commclient.config.Config;
import com.ut.commclient.contant.PropertyKey;
import com.ut.commclient.controller.MainViewController;
import com.ut.commclient.controller.center.TcpClientTabController;
import com.ut.commclient.controller.center.TcpServerTabController;
import com.ut.commclient.controller.center.UdpDatagramTabController;
import com.ut.commclient.model.TaskModel;
import com.ut.commclient.util.ListUtil;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * @description: 定时读取任务配置并执行任务
 * @author: 黄辉鸿
 * @create: 2020-08-10 08:56
 **/
@Log4j2
@Component
@EnableAsync
public class SendTask {
    @Autowired
    private MainViewController mainViewController;
    @Value("${task.time-interval}")
    private long timeInterval;
    @Value("${task.time-retry}")
    private long timeRetry;

    private TaskModel task;


    @Async
    @Scheduled(initialDelayString = "${task.time-interval}", fixedDelayString = "${task.time-interval}")
    public void task() {
        //读取文件
        try {
            YamlReader reader = new YamlReader(new FileReader(Config.getTaskPath()));
            task = JSON.parseObject(JSON.toJSONString(reader.read()), TaskModel.class);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (task == null) return;

        //执行tcpClientTask任务
        tcpClientTask();

        //执行tcpServerTask任务
        tcpServerTask();

        //执行udpDatagramTask任务
//        List<TaskModel.Target> udpDatagramTask = task.getUdpDatagramTask();
//        if (ListUtil.gtZero(udpDatagramTask)) {
//            List<UdpDatagramTabController> controllerList = (List) mainViewController.getUdpDatagramTabPane().getProperties().get(PropertyKey.CONTROLLER_LIST);
//            if (ListUtil.gtZero(controllerList)) {
//
//            }
//        }
//
//
//        List<TaskModel> udpDatagramTask2 = task.getUdpDatagramTask();
//        if (ListUtil.GtZero(udpDatagramTask)) {
//            UdpDatagramTab udpDatagramTab = new UdpDatagramTab();
//            udpDatagramTab.setText("New Tab");
//            Platform.runLater(() -> udpDatagramTabPane.getTabs().add(udpDatagramTab));
//            udpDatagramTask.forEach(taskModel -> {
//                udpDatagramTab.getIpTxt().setText(taskModel.getIp());
//                udpDatagramTab.getSendPortTxt().setText(taskModel.getPort().toString());
//                udpDatagramTab.getSendMsgTxt().setText(taskModel.getContent());
//                udpDatagramTab.getBindBtn().fire();
//                udpDatagramTab.getSendBtn().fire();
//            });
//        }
    }

    private void tcpServerTask() {
        List<TaskModel.Target> tcpServerTask = task.getTcpServerTask();
        //任务列表是否为空
        if (ListUtil.gtZero(tcpServerTask)) {
            List<TcpServerTabController> controllerList = (List) mainViewController.getTcpServerTabPane().getProperties().get(PropertyKey.CONTROLLER_LIST);
            //服务器列表是否为空
            if (ListUtil.gtZero(controllerList)) {
                //遍历任务
                tcpServerTask.forEach(taskModel -> {
                    //遍历服务器
                    controllerList.forEach(controller -> {
                        //遍历与服务器正在连接的客户端
                        controller.getClientListView().getItems().forEach(client -> {
                            //判断IP、端口是否相等，判断套接口是否连接
                            if (client.getIp().equals(taskModel.getIp()) &&
                                    client.getPort().equals(taskModel.getPort()) &&
                                    client.getSocket().isConnected()) {
                                //发送消息
                                try {
                                    client.getWriter().writeFlush(taskModel.getContent());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    });
                });
            }
        }
    }

    private void tcpClientTask() {
        List<TaskModel.Target> tcpClientTask = task.getTcpClientTask();
        if (ListUtil.gtZero(tcpClientTask)) {

            TabPane tcpClientTabPane = mainViewController.getTcpClientTabPane();
            List<TcpClientTabController> controllerList = (List) tcpClientTabPane.getProperties().get(PropertyKey.CONTROLLER_LIST);

            if (controllerList != null) {
                URL url = (URL) tcpClientTabPane.getProperties().get(PropertyKey.TAB_URL);
                //遍历tcpClient任务
                out:
                for (TaskModel.Target target : tcpClientTask) {

                    //遍历查看是否存在已连接的任务目标host，存在则直接发送消息并且跳出到外层循环，不存在则创建客户端执行任务
                    if (controllerList.size() > 0) {
                        synchronized (controllerList) {
                            for (TcpClientTabController controller : controllerList) {
                                if (controller.getIpTxt().getText().equals(target.getIp()) &&
                                        controller.getPortTxt().getText().equals(target.getPort().toString()) &&
                                        controller.getBeginBtn().isDisabled()) {
                                    controller.getSendMsgTxt().setText(target.getContent());
                                    controller.getSendBtn().fire();
                                    continue out;
                                }
                            }
                        }
                    }

                    //当不存在已连接任务目标时，则创建tab和socket并发送信息
                    FXMLLoader loader = new FXMLLoader(url);
                    Tab tab;
                    try {
                        tab = loader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                        continue;
                    }
                    TcpClientTabController controller = loader.getController();
                    Platform.runLater(() -> tcpClientTabPane.getTabs().add(tab));
                    controllerList.add(controller);

                    //连接服务器
                    controller.getIpTxt().setText(target.getIp());
                    controller.getPortTxt().setText(target.getPort().toString());
                    controller.getBeginBtn().fire();
                    //向服务器发送消息
                    boolean isSend = false;
                    for (int i = 0; i < timeRetry / 500; i++) {
                        if (controller.getSocket() != null && controller.getSocket().isConnected() && controller.getWriter() != null) {
                            controller.getSendMsgTxt().setText(target.getContent());
                            controller.getSendBtn().fire();
                            isSend = true;
                            break;
                        }
                        try {
                            Thread.sleep(500);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (!isSend) {
                        //TODO 完善日志
                        log.error("任务失败");
                    }
                }
            }
        }
    }
}
