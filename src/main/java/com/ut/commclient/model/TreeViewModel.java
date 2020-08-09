package com.ut.commclient.model;

import javafx.scene.control.TabPane;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author: 黄辉鸿
 * @create: 2020-08-09 19:29
 **/
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TreeViewModel {
    private String name;
    private TabPane stackNode;
}
