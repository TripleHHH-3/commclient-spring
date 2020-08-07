package com.ut.commclient.componet;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @description:
 * @author: 黄辉鸿
 * @create: 2020-08-07 14:47
 **/
@Getter
public class TabPaneHasList<T> extends TabPane {
    private final List<T> tabControllerList = Collections.synchronizedList(new ArrayList<>());

    public void addTab(Tab tab, T controller) {
        getTabs().add(tab);
        tabControllerList.add(controller);
    }

    //todo
    public void removeTab(Tab tab) {

    }
}
