package com.ut.commclient.contant;

/**
 * @description:
 * @author: 黄辉鸿
 * @create: 2020-08-14 16:44
 **/
public enum CommType {
    TCP_CLIENT("tcpClient"), TCP_SERVER("tcpServer"), UDP_DATAGRAM("udpDatagram"), UDP_MULTICAST("udpMulticast");

    CommType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private final String name;
}
