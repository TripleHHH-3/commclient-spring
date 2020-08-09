package com.ut.commclient.model;

import com.ut.commclient.common.Host;
import com.ut.commclient.common.UdpHost;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: 黄辉鸿
 * @create: 2020-08-09 19:29
 **/
@Data
public class StartModel {
    private List<Host> tcpClient;
    private List<Host> tcpServer;
    private List<UdpHost> udpDatagram;
    private List<UdpHost> udpMulticast;
}
