package com.ut.commclient.common;

import lombok.Data;

/**
 * @description:
 * @author: 黄辉鸿
 * @create: 2020-08-09 19:29
 **/
@Data
public class UdpHost {
    private String sendIp;
    private Integer sendPort;
    private String recIp;
    private Integer recPort;
}
