package com.ut.commclient.scheduletask;

import com.ut.commclient.config.HeartBeat;
import com.ut.commclient.controller.MainViewController;
import com.ut.commclient.controller.center.TcpClientTabController;
import com.ut.commclient.util.ListUtil;
import com.ut.commclient.util.ResUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.Socket;
import java.util.List;

/**
 * @description:
 * @author: 黄辉鸿
 * @create: 2020-08-07 14:32
 **/
@Component
public class TcpClientHeartBeatTask {
    @Autowired
    private MainViewController mainViewController;

    @Scheduled(initialDelayString = "${heartbeat.time-interval}", fixedDelayString = "${heartbeat.time-interval}")
    public void task() {
        List<TcpClientTabController> tabControllerList = mainViewController.getTcpClientTabPane().getTabControllerList();
        //当列表大于0时
        if (ListUtil.gtZero(tabControllerList)) {

            synchronized (tabControllerList) {
                tabControllerList.forEach(controller -> {

                    Socket socket = controller.getSocket();
                    //判断客户端套接字是否连接
                    if (socket != null && !socket.isClosed()) {

                        //如果心跳超时则停止连接并且重新尝试连接
                        if (System.currentTimeMillis() - controller.getLastEchoTime() > HeartBeat.getTimeOut()) {
                            controller.getRecMsgTxt().appendText("对方断线" + "\n");
                            controller.getSendBtn().setDisable(true);
                            ResUtil.closeWriterAndReaderAndSocket(controller.getReader(), controller.getWriter(), socket);
                            //尝试重新连接
                            controller.connectToServer();
                        } else {
                            //没有超时则发送心跳包
                            controller.getWriter().writeFlush(HeartBeat.getEchoServer());
                        }
                    }

                });
            }
        }
    }
}
