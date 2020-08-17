package com.ut.commclient.scheduletask;

import com.alibaba.fastjson.JSON;
import com.ut.commclient.config.Config;
import com.ut.commclient.contant.PropertyKey;
import com.ut.commclient.controller.MainViewController;
import com.ut.commclient.controller.center.TcpClientTabController;
import com.ut.commclient.controller.center.TcpServerTabController;
import com.ut.commclient.controller.center.UdpDatagramTabController;
import com.ut.commclient.controller.center.UdpMulticastTabController;
import com.ut.commclient.model.TaskModel;
import com.ut.commclient.model.TcpClientModel;
import com.ut.commclient.util.ListUtil;
import javafx.application.Platform;
import javafx.collections.ObservableList;
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

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
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

    private final List<TaskModel> taskModelList = new ArrayList<>();


    @Async
    @Scheduled(initialDelayString = "${task.time-interval}", fixedDelayString = "${task.time-interval}")
    public void task() {
        //检查文件夹内是否存在任务文件
        File taskDir = new File(Config.getTaskDir());
        File[] taskFileArray = taskDir.listFiles();
        if (taskFileArray == null || taskFileArray.length == 0) return;
        //每次都要清除上次任务内容
        taskModelList.clear();
        //遍历文件夹内任务文件
        for (File taskFile : taskFileArray) {
            //读取文件，一行一行的解析任务内容，并存入任务列表
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(taskFile), StandardCharsets.UTF_8))) {
                String taskJson;
                while ((taskJson = br.readLine()) != null) {
                    taskModelList.add(JSON.parseObject(taskJson, TaskModel.class));
                }
            } catch (Exception e) {
                e.printStackTrace();
                //TODO 完善日志
                log.error("任务文件读取错误");
                throw new RuntimeException("任务文件出错，跳过任务");
            }
        }

        if (taskModelList.size() > 0) {
            taskModelList.forEach(task -> {
                switch (task.getTarget()) {
                    //执行tcpClientTask任务
                    case TCP_CLIENT -> tcpClientTask(task);
                    //执行tcpServerTask任务
                    case TCP_SERVER -> tcpServerTask(task);
                    //执行udpDatagramTask任务
                    case UDP_DATAGRAM -> udpDatagramTask(task);
                    //执行udpMulticastTask任务
                    case UDP_MULTICAST -> udpMulticastTask(task);
                }
            });

            new ArrayList<>(Arrays.asList(taskFileArray)).forEach(file -> file.delete());
        }
    }

    private void udpMulticastTask(TaskModel task) {

        TabPane udpMulticastTabPane = mainViewController.getUdpMulticastTabPane();
        List<UdpMulticastTabController> controllerList = (List) udpMulticastTabPane.getProperties().get(PropertyKey.CONTROLLER_LIST);
        URL url = (URL) udpMulticastTabPane.getProperties().get(PropertyKey.TAB_URL);

        if (ListUtil.gtZero(controllerList)) {
            synchronized (controllerList) {
                //遍历tab
                for (UdpMulticastTabController controller : controllerList) {
                    //判断ip、端口是否相等，并且已绑定
                    if (controller.getBindIpGroupTxt().getText().equals(task.getIp()) &&
                            controller.getBindPortTxt().getText().equals(task.getPort()) &&
                            controller.getBindBeginBtn().isDisable()) {
                        //发送消息
                        controller.getSendMsgTxt().setText(task.getData());
                        controller.getSendBtn().fire();
                        return;
                    }
                }
            }
        }

        //不存在tab则创建
        FXMLLoader loader = new FXMLLoader(url);
        Tab tab;
        try {
            tab = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Platform.runLater(() -> udpMulticastTabPane.getTabs().add(tab));
        UdpMulticastTabController controller = loader.getController();
        controllerList.add(controller);
        //执行任务
        controller.getBindIpGroupTxt().setText(task.getIp());
        controller.getBindPortTxt().setText(task.getPort());
        controller.getBindBeginBtn().fire();
        controller.getSendMsgTxt().setText(task.getData());
        controller.getSendBtn().fire();
    }

    private void udpDatagramTask(TaskModel task) {

        TabPane udpDatagramTabPane = mainViewController.getUdpDatagramTabPane();
        List<UdpDatagramTabController> controllerList = (List) udpDatagramTabPane.getProperties().get(PropertyKey.CONTROLLER_LIST);
        URL url = (URL) udpDatagramTabPane.getProperties().get(PropertyKey.TAB_URL);
        //遍历tab
        if (ListUtil.gtZero(controllerList)) {
            synchronized (controllerList) {
                for (UdpDatagramTabController controller : controllerList) {
                    //判断ip、端口是否相等，并且已绑定
                    if (task.getIp().equals(controller.getIpTxt().getText()) &&
                            controller.getSendPortTxt().getText().equals(task.getPort()) &&
                            controller.getBindBtn().isDisable()) {
                        //发送消息
                        controller.getSendMsgTxt().setText(task.getData());
                        controller.getSendBtn().fire();
                        return;
                    }
                }
            }
        }

        //无已连接的任务目录，则创建tab
        FXMLLoader loader = new FXMLLoader(url);
        Tab tab;
        try {
            tab = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Platform.runLater(() -> udpDatagramTabPane.getTabs().add(tab));
        UdpDatagramTabController controller = loader.getController();
        controllerList.add(controller);
        //执行任务
        controller.getIpTxt().setText(task.getIp());
        controller.getSendPortTxt().setText(task.getPort());
        controller.getBindBtn().fire();
        controller.getSendMsgTxt().setText(task.getData());
        controller.getSendBtn().fire();
    }

    private void tcpServerTask(TaskModel task) {
        List<TcpServerTabController> controllerList = (List) mainViewController.getTcpServerTabPane().getProperties().get(PropertyKey.CONTROLLER_LIST);
        //tabServer服务器列表是否为空
        if (controllerList.size() > 0) {
            synchronized (controllerList) {
                //遍历服务器
                controllerList.forEach(controller -> {
                    //遍历与服务器连接中的客户端
                    ObservableList<TcpClientModel> clientList = controller.getClientListView().getItems();
                    if (ListUtil.gtZero(clientList)) {
                        synchronized (clientList) {
                            clientList.forEach(client -> {
                                //判断IP、端口是否相等，判断套接口是否连接
                                if (client.getIp().equals(task.getIp()) &&
                                        client.getPort().equals(task.getPort()) &&
                                        client.getSocket().isConnected()) {
                                    //发送消息
                                    try {
                                        client.getWriter().writeFlush(task.getData());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        //todo 完善日志
                                        log.error("发送失败");
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }
    }

    private void tcpClientTask(TaskModel task) {

        TabPane tcpClientTabPane = mainViewController.getTcpClientTabPane();
        List<TcpClientTabController> controllerList = (List) tcpClientTabPane.getProperties().get(PropertyKey.CONTROLLER_LIST);
        URL url = (URL) tcpClientTabPane.getProperties().get(PropertyKey.TAB_URL);

        //遍历查看是否存在已连接的任务目标host，存在则直接发送消息，不存在则创建客户端执行任务
        if (controllerList.size() > 0) {
            synchronized (controllerList) {
                //遍历tab的controllerList
                for (TcpClientTabController controller : controllerList) {
                    //判断IP、端口是否相同，并且已经连接
                    if (controller.getIpTxt().getText().equals(task.getIp()) &&
                            controller.getPortTxt().getText().equals(task.getPort()) &&
                            controller.getBeginBtn().isDisabled()) {
                        //发送消息
                        controller.getSendMsgTxt().setText(task.getData());
                        controller.getSendBtn().fire();
                        return;
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
            return;
        }

        Platform.runLater(() -> tcpClientTabPane.getTabs().add(tab));
        TcpClientTabController controller = loader.getController();
        controllerList.add(controller);

        //连接服务器
        controller.getIpTxt().setText(task.getIp());
        controller.getPortTxt().setText(task.getPort());
        controller.getBeginBtn().fire();

        boolean isSend = false;
        //等待连接
        for (int i = 0; i < timeRetry / 500; i++) {
            if (controller.getSocket() != null &&
                    controller.getSocket().isConnected() &&
                    controller.getWriter() != null) {
                //连接成功就发送消息
                controller.getSendMsgTxt().setText(task.getData());
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


//    private void udpMulticastTask(TaskModel task) {
//        List<TaskModel.Target> udpMulticastTask = task.getUdpMulticastTask();
//        if (ListUtil.gtZero(udpMulticastTask)) {
//
//            TabPane udpMulticastTabPane = mainViewController.getUdpMulticastTabPane();
//            List<UdpMulticastTabController> controllerList = (List) udpMulticastTabPane.getProperties().get(PropertyKey.CONTROLLER_LIST);
//            URL url = (URL) udpMulticastTabPane.getProperties().get(PropertyKey.TAB_URL);
//            //遍历任务
//            out:
//            for (TaskModel.Target target : udpMulticastTask) {
//
//                if (ListUtil.gtZero(controllerList)) {
//                    synchronized (controllerList) {
//                        //遍历tab
//                        for (UdpMulticastTabController controller : controllerList) {
//                            if (controller.getBindIpGroupTxt().getText().equals(target.getIp()) &&
//                                    controller.getBindPortTxt().getText().equals(target.getPort().toString()) &&
//                                    controller.getBindBeginBtn().isDisable()) {
//                                controller.getSendMsgTxt().setText(target.getData());
//                                controller.getSendBtn().fire();
//                            }
//                        }
//                    }
//                }
//
//                //不存在tab则创建
//                FXMLLoader loader = new FXMLLoader(url);
//                Tab tab;
//                try {
//                    tab = loader.load();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    continue;
//                }
//                Platform.runLater(() -> udpMulticastTabPane.getTabs().add(tab));
//                UdpMulticastTabController controller = loader.getController();
//                controllerList.add(controller);
//                //执行任务
//                controller.getBindIpGroupTxt().setText(target.getIp());
//                controller.getBindPortTxt().setText(target.getPort().toString());
//                controller.getSendMsgTxt().setText(target.getData());
//                controller.getSendBtn().fire();
//            }
//        }
//    }
//
//    private void udpDatagramTask(TaskModel task) {
//        List<TaskModel.Target> udpDatagramTask = task.getUdpDatagramTask();
//        if (ListUtil.gtZero(udpDatagramTask)) {
//
//            TabPane udpDatagramTabPane = mainViewController.getUdpDatagramTabPane();
//            List<UdpDatagramTabController> controllerList = (List) udpDatagramTabPane.getProperties().get(PropertyKey.CONTROLLER_LIST);
//            URL url = (URL) udpDatagramTabPane.getProperties().get(PropertyKey.TAB_URL);
//            //遍历任务
//            out:
//            for (TaskModel.Target target : udpDatagramTask) {
//                //遍历tab
//                if (ListUtil.gtZero(controllerList)) {
//                    synchronized (controllerList) {
//                        for (UdpDatagramTabController controller : controllerList) {
//
//                            if (target.getIp().equals(controller.getIpTxt().getText()) &&
//                                    controller.getSendPortTxt().getText().equals(target.getPort().toString()) &&
//                                    controller.getBindBtn().isDisable()) {
//
//                                controller.getSendMsgTxt().setText(target.getData());
//                                controller.getSendBtn().fire();
//                                continue out;
//                            }
//                        }
//                    }
//                }
//
//                //无已连接的任务目录，则创建tab
//                FXMLLoader loader = new FXMLLoader(url);
//                Tab tab;
//                try {
//                    tab = loader.load();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    continue;
//                }
//                Platform.runLater(() -> udpDatagramTabPane.getTabs().add(tab));
//                UdpDatagramTabController controller = loader.getController();
//                controllerList.add(controller);
//                //执行任务
//                controller.getIpTxt().setText(target.getIp());
//                controller.getSendPortTxt().setText(target.getPort().toString());
//                controller.getSendMsgTxt().setText(target.getData());
//                controller.getSendBtn().fire();
//            }
//        }
//    }
//
//    private void tcpServerTask(TaskModel task) {
//        List<TaskModel.Target> tcpServerTask = task.getTcpServerTask();
//        //任务列表是否为空
//        if (ListUtil.gtZero(tcpServerTask)) {
//            List<TcpServerTabController> controllerList = (List) mainViewController.getTcpServerTabPane().getProperties().get(PropertyKey.CONTROLLER_LIST);
//            //服务器列表是否为空
//            if (ListUtil.gtZero(controllerList)) {
//                synchronized (controllerList) {
//                    //遍历任务
//                    tcpServerTask.forEach(taskModel -> {
//                        //遍历服务器
//                        controllerList.forEach(controller -> {
//                            //遍历与服务器正在连接的客户端
//                            controller.getClientListView().getItems().forEach(client -> {
//                                //判断IP、端口是否相等，判断套接口是否连接
//                                if (client.getIp().equals(taskModel.getIp()) &&
//                                        client.getPort().equals(taskModel.getPort()) &&
//                                        client.getSocket().isConnected()) {
//                                    //发送消息
//                                    try {
//                                        client.getWriter().writeFlush(taskModel.getData());
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                        //todo 完善日志
//                                        log.error("发送失败");
//                                    }
//                                }
//                            });
//                        });
//                    });
//                }
//            }
//        }
//    }
//
//    private void tcpClientTask(TaskModel task) {
//        List<TaskModel.Target> tcpClientTask = task.getTcpClientTask();
//        if (ListUtil.gtZero(tcpClientTask)) {
//
//            TabPane tcpClientTabPane = mainViewController.getTcpClientTabPane();
//            List<TcpClientTabController> controllerList = (List) tcpClientTabPane.getProperties().get(PropertyKey.CONTROLLER_LIST);
//            URL url = (URL) tcpClientTabPane.getProperties().get(PropertyKey.TAB_URL);
//
//            if (controllerList != null) {
//                //遍历tcpClient任务
//                out:
//                for (TaskModel.Target target : tcpClientTask) {
//
//                    //遍历查看是否存在已连接的任务目标host，存在则直接发送消息并且跳出到外层循环，不存在则创建客户端执行任务
//                    if (controllerList.size() > 0) {
//                        synchronized (controllerList) {
//                            for (TcpClientTabController controller : controllerList) {
//                                if (controller.getIpTxt().getText().equals(target.getIp()) &&
//                                        controller.getPortTxt().getText().equals(target.getPort().toString()) &&
//                                        controller.getBeginBtn().isDisabled()) {
//                                    controller.getSendMsgTxt().setText(target.getData());
//                                    controller.getSendBtn().fire();
//                                    continue out;
//                                }
//                            }
//                        }
//                    }
//
//                    //当不存在已连接任务目标时，则创建tab和socket并发送信息
//                    FXMLLoader loader = new FXMLLoader(url);
//                    Tab tab;
//                    try {
//                        tab = loader.load();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        continue;
//                    }
//                    Platform.runLater(() -> tcpClientTabPane.getTabs().add(tab));
//                    TcpClientTabController controller = loader.getController();
//                    controllerList.add(controller);
//
//                    //连接服务器
//                    controller.getIpTxt().setText(target.getIp());
//                    controller.getPortTxt().setText(target.getPort().toString());
//                    controller.getBeginBtn().fire();
//                    //向服务器发送消息
//                    boolean isSend = false;
//                    for (int i = 0; i < timeRetry / 500; i++) {
//                        if (controller.getSocket() != null && controller.getSocket().isConnected() && controller.getWriter() != null) {
//                            controller.getSendMsgTxt().setText(target.getData());
//                            controller.getSendBtn().fire();
//                            isSend = true;
//                            break;
//                        }
//                        try {
//                            Thread.sleep(500);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    if (!isSend) {
//                        //TODO 完善日志
//                        log.error("任务失败");
//                    }
//                }
//            }
//        }
//    }