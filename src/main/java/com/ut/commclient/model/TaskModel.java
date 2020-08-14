package com.ut.commclient.model;

import com.ut.commclient.contant.CommType;
import lombok.Data;

/**
 * @description:
 * @author: 黄辉鸿
 * @create: 2020-08-10 08:49
 **/
@Data
public class TaskModel {
    private CommType target;
    private String ip;
    private String port;
    private String data;
}