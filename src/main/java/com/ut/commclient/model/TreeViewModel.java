package com.ut.commclient.model;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.net.URL;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TreeViewModel {
    private String name;
    private TabPane stackNode;
    private URL url;

    public Tab getView() {
        try {
            return (Tab) FXMLLoader.load(url);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("生成tab异常");
        }
    }
}
