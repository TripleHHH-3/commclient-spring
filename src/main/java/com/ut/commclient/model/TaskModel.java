package com.ut.commclient.model;

import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: 黄辉鸿
 * @create: 2020-08-10 08:49
 **/
@Data
public class TaskModel {
    private List<Target> tcpClientTask;
    private List<Target> tcpServerTask;
    private List<Target> udpDatagramTask;
    private List<Target> udpMulticastTask;

    @Data
    public static class Target {
        private String ip;
        private Integer port;
        private String content;
    }
}