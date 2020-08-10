package com.ut.commclient.scheduletask;

import com.alibaba.fastjson.JSON;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.ut.commclient.config.Config;
import com.ut.commclient.contant.PropertyKey;
import com.ut.commclient.controller.MainViewController;
import com.ut.commclient.controller.center.TcpClientTabController;
import com.ut.commclient.controller.center.TcpServerTabController;
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

    @Async
    @Scheduled(initialDelayString = "${task.time-interval}", fixedDelayString = "${task.time-interval}")
    public void task() {
        //读取文件
        TaskModel task;
        try {
            YamlReader reader = new YamlReader(new FileReader(Config.getTaskPath()));
            task = JSON.parseObject(JSON.toJSONString(reader.read()), TaskModel.class);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        //执行tcpClientTask任务
        List<TaskModel.target> tcpClientTask = task.getTcpClientTask();
        if (ListUtil.gtZero(tcpClientTask)) {

            TabPane tcpClientTabPane = mainViewController.getTcpClientTabPane();
            List<TcpClientTabController> controllerList = (List) tcpClientTabPane.getProperties().get(PropertyKey.CONTROLLER_LIST);
            URL url = (URL) tcpClientTabPane.getProperties().get(PropertyKey.TAB_URL);
            //遍历tcpClient任务
            out:
            for (TaskModel.target target : tcpClientTask) {

                //遍历查看是否存在已连接的任务目标host，存在则直接发送消息并且跳出到外层循环，不存在则创建客户端执行任务
                for (TcpClientTabController controller : controllerList) {
                    if (controller.getIpTxt().getText().equals(target.getIp()) &&
                            controller.getPortTxt().getText().equals(target.getPort().toString()) &&
                            controller.getBeginBtn().isDisabled()) {
                        controller.getSendMsgTxt().setText(target.getContent());
                        controller.getSendBtn().fire();
                        continue out;
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

        List<TaskModel.target> tcpServerTask = task.getTcpServerTask();
        if (ListUtil.gtZero(tcpServerTask)) {
            List<TcpServerTabController> controllerList = (List) mainViewController.getTcpServerTabPane().getProperties().get(PropertyKey.CONTROLLER_LIST);
            if (ListUtil.gtZero(controllerList)) {
                tcpServerTask.forEach(taskModel -> {

                    controllerList.forEach(controller -> {
                        controller.getClientListView().getItems().forEach(clientModel -> {
                            if (clientModel.getIp().equals(taskModel.getIp()) &&
                                    clientModel.getPort().equals(taskModel.getPort()) &&
                                    clientModel.getSocket().isConnected()) {
                                try {
                                    clientModel.getWriter().writeFlush(taskModel.getContent());
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
}
