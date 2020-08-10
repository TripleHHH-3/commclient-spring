package com.ut.commclient.scheduletask;

import com.ut.commclient.config.HeartBeat;
import com.ut.commclient.contant.PropertyKey;
import com.ut.commclient.controller.MainViewController;
import com.ut.commclient.controller.center.TcpServerTabController;
import com.ut.commclient.model.TcpClientModel;
import com.ut.commclient.util.ListUtil;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: tcp服务端的心跳任务
 * @author: 黄辉鸿
 * @create: 2020-08-07 19:29
 **/
@Component
@EnableAsync
public class TcpServerHeartBeatTask {
    @Autowired
    private MainViewController mainViewController;


    @Async
    //此任务需两层嵌套遍历，分别是每一个tab和tab里面的listView
    @Scheduled(initialDelayString = "${heartbeat.time-interval}", fixedDelayString = "${heartbeat.time-interval}")
    public void task() {
        List<TcpServerTabController> tabControllerList = (List<TcpServerTabController>) mainViewController.getTcpServerTabPane().getProperties().get(PropertyKey.CONTROLLER_LIST);

        if (ListUtil.gtZero(tabControllerList)) {
            synchronized (tabControllerList) {

                //第一层遍历每个tab
                tabControllerList.forEach(controller -> {
                    ServerSocket serverSocket = controller.getServerSocket();
                    if (serverSocket != null && !serverSocket.isClosed()) {

                        ObservableList<TcpClientModel> clientList = controller.getClientListView().getItems();
                        //第二层遍历listView
                        if (ListUtil.gtZero(clientList)) {
                            List<TcpClientModel> downClients = new ArrayList<>();
                            clientList.forEach(client -> {
                                //心跳超时加入移除列表
                                if ((System.currentTimeMillis() - client.getLastRecTime() > HeartBeat.getTimeOut())) {
                                    downClients.add(client);
                                } else {
                                    //给未超时的客户端发送心跳
                                    try {
                                        client.getWriter().writeFlush(HeartBeat.getEchoClient());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        downClients.add(client);
                                    }
                                }
                            });
                            //把心跳时间超时的客户端从列表移除
                            if (downClients.size() > 0) {
                                downClients.forEach(client -> Platform.runLater(() -> clientList.remove(client)));
                            }
                        }
                    }
                });
            }
        }
    }
}
//                        在非UI线程中执行移除操作报异常
//                        clientList.removeIf(client -> {
//                            if ((System.currentTimeMillis() - client.getLastRecTime() > HeartBeat.getTimeOut())) {
//                                return true;
//                            } else {
//                                try {
//                                    client.getWriter().writeFlush(HeartBeat.getEchoClient());
//                                    return false;
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                    return true;
//                                }
//                            }
//                        });
