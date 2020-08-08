package com.ut.commclient.model;

import javafx.scene.control.TabPane;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TreeViewModel {
    private String name;
    private TabPane stackNode;
}
