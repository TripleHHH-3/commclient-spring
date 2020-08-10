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
    private List<target> tcpClientTask;
    private List<target> tcpServerTask;
    private List<target> udpDatagramTask;
    private List<target> udpMulticastTask;

    @Data
    public static class target {
        private String ip;
        private Integer port;
        private String content;
    }
}