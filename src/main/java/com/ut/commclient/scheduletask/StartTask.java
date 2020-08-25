package com.ut.commclient.scheduletask;

import com.alibaba.fastjson.JSON;
import com.ut.commclient.config.Config;
import com.ut.commclient.contant.PropertyKey;
import com.ut.commclient.controller.MainViewController;
import com.ut.commclient.controller.center.TcpClientTabController;
import com.ut.commclient.controller.center.TcpServerTabController;
import com.ut.commclient.controller.center.UdpDatagramTabController;
import com.ut.commclient.controller.center.UdpMulticastTabController;
import com.ut.commclient.model.StartModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: 程序启动时打开各个连接任务，只会执行一次
 * @author: 黄辉鸿
 * @create: 2020-08-09 19:29
 **/
@Log4j2
@Component
@DependsOn("config")
public class StartTask implements ApplicationRunner {
    @Autowired
    private MainViewController mainViewController;

    @Override
    public void run(ApplicationArguments args) {
        File startFile = new File(Config.getStartPath());
        List<StartModel> startList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(startFile), StandardCharsets.UTF_8))) {
            String startJson;
            while ((startJson = br.readLine()) != null) {
                StartModel startModel = JSON.parseObject(startJson, StartModel.class);
                if (startModel != null) {
                    startList.add(startModel);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            //TODO 完善日志
            log.error("任务文件读取错误");
            throw new RuntimeException("启动文件出错，跳过任务");
        }

        //判断配置信息
        if (startList.size() > 0) {
            startList.forEach(start -> {

                switch (start.getCommType()) {
                    //执行tcpClient初始化
                    case TCP_CLIENT -> tcpClientInit(start);
                    //执行tcpServer初始化
                    case TCP_SERVER -> tcpServerInit(start);
                    //执行udpDatagram初始化
                    case UDP_DATAGRAM -> udpDatagramInit(start);
                    //执行udpMulticast初始化
                    case UDP_MULTICAST -> udpMulticastInit(start);
                }

            });
        }
    }

    private void tcpClientInit(StartModel start) {
        TabPane tcpClientTabPane = mainViewController.getTcpClientTabPane();
        URL url = (URL) tcpClientTabPane.getProperties().get(PropertyKey.TAB_URL);
        List list = (List) tcpClientTabPane.getProperties().get(PropertyKey.CONTROLLER_LIST);
        //添加到面板
        try {
            FXMLLoader loader = new FXMLLoader(url);
            Tab tab = loader.load();
            tcpClientTabPane.getTabs().add(tab);
            //添加到controllerList,方便后面执行心跳任务
            TcpClientTabController controller = loader.getController();
            list.add(controller);
            //设置ip、端口、开始连接
            controller.getIpTxt().setText(start.getConnectIp());
            controller.getPortTxt().setText(start.getConnectPort());
            controller.getBeginBtn().fire();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void tcpServerInit(StartModel start) {
        TabPane tcpServerTabPane = mainViewController.getTcpServerTabPane();
        URL url = (URL) tcpServerTabPane.getProperties().get(PropertyKey.TAB_URL);
        List list = (List) tcpServerTabPane.getProperties().get(PropertyKey.CONTROLLER_LIST);
        //添加到面板
        try {
            FXMLLoader loader = new FXMLLoader(url);
            Tab tab = loader.load();
            tcpServerTabPane.getTabs().add(tab);
            //添加到controllerList,方便后面执行心跳任务
            TcpServerTabController controller = loader.getController();
            list.add(controller);
            //设置端口,开始监听
            controller.getPortTxt().setText(start.getListenPort());
            controller.getBeginBtn().fire();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void udpDatagramInit(StartModel start) {
        TabPane udpDatagramTabPane = mainViewController.getUdpDatagramTabPane();
        URL url = (URL) udpDatagramTabPane.getProperties().get(PropertyKey.TAB_URL);
        List list = (List) udpDatagramTabPane.getProperties().get(PropertyKey.CONTROLLER_LIST);
        //添加到面板
        try {
            FXMLLoader loader = new FXMLLoader(url);
            Tab tab = loader.load();
            udpDatagramTabPane.getTabs().add(tab);
            //添加到controllerList
            UdpDatagramTabController controller = loader.getController();
            list.add(controller);
            //绑定客户端
            controller.getIpTxt().setText(start.getConnectIp());
            controller.getSendPortTxt().setText(start.getConnectPort());
            controller.getBindBtn().fire();
            //监听端口
            controller.getRecPortTxt().setText(start.getListenPort());
            controller.getListenBtn().fire();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void udpMulticastInit(StartModel start) {
        TabPane udpMulticastTabPane = mainViewController.getUdpMulticastTabPane();
        URL url = (URL) udpMulticastTabPane.getProperties().get(PropertyKey.TAB_URL);
        List list = (List) udpMulticastTabPane.getProperties().get(PropertyKey.CONTROLLER_LIST);
        //添加到面板
        try {
            FXMLLoader loader = new FXMLLoader(url);
            Tab tab = loader.load();
            udpMulticastTabPane.getTabs().add(tab);
            //添加到controllerList
            UdpMulticastTabController controller = loader.getController();
            list.add(controller);
            //绑定广播组
            controller.getBindIpGroupTxt().setText(start.getConnectIp());
            controller.getBindPortTxt().setText(start.getConnectPort());
            controller.getBindBeginBtn().fire();
            //监听广播组
            controller.getListenIpGroupTxt().setText(start.getListenIp());
            controller.getListenPortTxt().setText(start.getListenPort());
            controller.getListenBeginBtn().fire();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
