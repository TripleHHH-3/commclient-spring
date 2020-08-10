package com.ut.commclient.model;

import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: 黄辉鸿
 * @create: 2020-08-09 19:29
 **/
@Data
public class StartModel {
    private List<TcpHost> tcpClient;
    private List<TcpHost> tcpServer;
    private List<UdpHost> udpDatagram;
    private List<UdpHost> udpMulticast;

    @Data
    public static class TcpHost {
        private String ip;
        private Integer port;
    }

    @Data
    public static class UdpHost {
        private String sendIp;
        private Integer sendPort;
        private String recIp;
        private Integer recPort;
    }
}
