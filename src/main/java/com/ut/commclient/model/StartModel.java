package com.ut.commclient.model;

import com.ut.commclient.contant.CommType;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: 黄辉鸿
 * @create: 2020-08-09 19:29
 **/
@Data
public class StartModel {
    private CommType commType;
    private String connectIp;
    private String connectPort;
    private String listenIp;
    private String listenPort;
}
