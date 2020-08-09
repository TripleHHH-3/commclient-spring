package com.ut.commclient.util;

import java.util.List;

/**
 * @description:
 * @author: 黄辉鸿
 * @create: 2020-08-09 19:29
 **/
public abstract class ListUtil {
    public static Boolean gtZero(List list) {
        return list != null && list.size() > 0;
    }
}
