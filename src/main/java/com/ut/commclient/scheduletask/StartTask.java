package com.ut.commclient.scheduletask;

import com.alibaba.fastjson.JSON;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.ut.commclient.common.Host;
import com.ut.commclient.common.UdpHost;
import com.ut.commclient.config.Config;
import com.ut.commclient.contant.KeyName;
import com.ut.commclient.controller.MainViewController;
import com.ut.commclient.controller.center.TcpClientTabController;
import com.ut.commclient.controller.center.TcpServerTabController;
import com.ut.commclient.controller.center.UdpDatagramTabController;
import com.ut.commclient.controller.center.UdpMulticastTabController;
import com.ut.commclient.model.StartModel;
import com.ut.commclient.util.ListUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.net.URL;
import java.util.List;

/**
 * @description:
 * @author: 黄辉鸿
 * @create: 2020-08-09 19:29
 **/
@Component
public class StartTask implements ApplicationRunner {
    @Autowired
    private MainViewController mainViewController;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //读取配置文件
        YamlReader reader = new YamlReader(new FileReader(Config.getStartPath()));
        StartModel startModel = JSON.parseObject(JSON.toJSONString(reader.read()), StartModel.class);

        //执行tcpClient初始化
        List<Host> tcpClient = startModel.getTcpClient();
        if (ListUtil.gtZero(tcpClient)) {
            TabPane tcpClientTabPane = mainViewController.getTcpClientTabPane();
            URL url = (URL) tcpClientTabPane.getProperties().get(KeyName.TAB_URL);
            for (Host host : tcpClient) {
                FXMLLoader loader = new FXMLLoader(url);
                //添加到面板
                Tab tab = loader.load();
                tcpClientTabPane.getTabs().add(tab);
                //添加到controllerList,为了执行心跳任务
                TcpClientTabController controller = loader.getController();
                List list = (List) tcpClientTabPane.getProperties().get(KeyName.CONTROLLER_LIST);
                list.add(controller);
                //设置ip
                controller.getIpTxt().setText(host.getIp());
                //设置端口
                controller.getPortTxt().setText(host.getPort().toString());
                //开始连接
                controller.getBeginBtn().fire();
            }
        }

        //执行tcpServer初始化
        List<Host> tcpServer = startModel.getTcpServer();
        if (ListUtil.gtZero(tcpServer)) {
            TabPane tcpServerTabPane = mainViewController.getTcpServerTabPane();
            URL url = (URL) tcpServerTabPane.getProperties().get(KeyName.TAB_URL);
            for (Host host : tcpServer) {
                FXMLLoader loader = new FXMLLoader(url);
                //添加到面板
                Tab tab = loader.load();
                tcpServerTabPane.getTabs().add(tab);
                //添加到controllerList,为了执行心跳任务
                TcpServerTabController controller = loader.getController();
                List list = (List) tcpServerTabPane.getProperties().get(KeyName.CONTROLLER_LIST);
                list.add(controller);
                //设置端口
                controller.getPortTxt().setText(host.getPort().toString());
                //开始连接
                controller.getBeginBtn().fire();
            }
        }

        //执行udpDatagram初始化
        List<UdpHost> udpDatagram = startModel.getUdpDatagram();
        if (ListUtil.gtZero(udpDatagram)) {
            TabPane udpDatagramTabPane = mainViewController.getUdpDatagramTabPane();
            URL url = (URL) udpDatagramTabPane.getProperties().get(KeyName.TAB_URL);
            for (UdpHost udpHost : udpDatagram) {
                FXMLLoader loader = new FXMLLoader(url);
                //添加到面板
                Tab tab = loader.load();
                udpDatagramTabPane.getTabs().add(tab);
                //添加到controllerList
                UdpDatagramTabController controller = loader.getController();
                List list = (List) udpDatagramTabPane.getProperties().get(KeyName.CONTROLLER_LIST);
                list.add(controller);
                //绑定客户端
                controller.getIpTxt().setText(udpHost.getSendIp());
                controller.getSendPortTxt().setText(udpHost.getSendPort().toString());
                controller.getBindBtn().fire();
                //监听端口
                controller.getRecPortTxt().setText(udpHost.getRecPort().toString());
                controller.getListenBtn().fire();
            }
        }

        //执行udpMulticast初始化
        List<UdpHost> udpMulticast = startModel.getUdpMulticast();
        if (ListUtil.gtZero(udpMulticast)) {
            TabPane udpMulticastTabPane = mainViewController.getUdpMulticastTabPane();
            URL url = (URL) udpMulticastTabPane.getProperties().get(KeyName.TAB_URL);
            for (UdpHost udpHost : udpMulticast) {
                FXMLLoader loader = new FXMLLoader(url);
                //添加到面板
                Tab tab = loader.load();
                udpMulticastTabPane.getTabs().add(tab);
                //添加到controllerList
                UdpMulticastTabController controller = loader.getController();
                List list = (List) udpMulticastTabPane.getProperties().get(KeyName.CONTROLLER_LIST);
                list.add(controller);
                //绑定广播组
                controller.getBindIpGroupTxt().setText(udpHost.getSendIp());
                controller.getBindPortTxt().setText(udpHost.getSendPort().toString());
                controller.getBindBeginBtn().fire();
                //监听广播组
                controller.getListenIpGroupTxt().setText(udpHost.getRecIp());
                controller.getListenPortTxt().setText(udpHost.getRecPort().toString());
                controller.getListenBeginBtn().fire();
            }
        }
    }
}
