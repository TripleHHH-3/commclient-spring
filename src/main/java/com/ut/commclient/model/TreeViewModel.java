package com.ut.commclient.model;

import javafx.scene.Node;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreeViewModel {
    private String name;
    private Node stackNode;
}
