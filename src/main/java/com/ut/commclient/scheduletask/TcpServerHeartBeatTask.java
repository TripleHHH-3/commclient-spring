package com.ut.commclient.scheduletask;

import com.ut.commclient.componet.TabPaneHasList;
import com.ut.commclient.config.HeartBeat;
import com.ut.commclient.controller.MainViewController;
import com.ut.commclient.controller.center.TcpServerTabController;
import com.ut.commclient.model.TcpClientModel;
import com.ut.commclient.util.ListUtil;
import com.ut.commclient.util.ResUtil;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.ServerSocket;
import java.util.List;

/**
 * @description:
 * @author: 黄辉鸿
 * @create: 2020-08-07 19:29
 **/
@Component
public class TcpServerHeartBeatTask {
    @Autowired
    private MainViewController mainViewController;

    //此任务需两层嵌套遍历，分别是每一个tab和tab里面的listView
    @Scheduled(initialDelayString = "${heartbeat.time-interval}", fixedDelayString = "${heartbeat.time-interval}")
    public void task() {
        List<TcpServerTabController> tabControllerList = mainViewController.getTcpServerTabPane().getTabControllerList();

        if (ListUtil.gtZero(tabControllerList)) {
            synchronized (tabControllerList) {

                //第一层遍历每个tab
                tabControllerList.forEach(controller -> {
                    ServerSocket serverSocket = controller.getServerSocket();
                    if (serverSocket != null && !serverSocket.isClosed()) {

                        ObservableList<TcpClientModel> clientList = controller.getClientListView().getItems();
                        if (ListUtil.gtZero(clientList)) {

                            //第二层遍历listView
                            clientList.forEach(client -> {

                                //心跳时间超时则把客户端从列表移除
                                if (System.currentTimeMillis() - client.getLastRecTime() > HeartBeat.getTimeOut()) {
                                    ResUtil.closeWriterAndSocket(client.getWriter(), client.getSocket());
                                    Platform.runLater(() -> clientList.remove(client));
                                }
                            });
                        }

                        //剩下的未超时的客户端继续发心跳包
                        if (ListUtil.gtZero(clientList)) {
                            synchronized (clientList) {
                                clientList.forEach(client -> client.getWriter().writeFlush(HeartBeat.getEchoClient()));
                            }
                        }
                    }
                });
            }
        }
    }
}
