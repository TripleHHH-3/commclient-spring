package com.ut.commclient.model;

import de.felixroske.jfxsupport.AbstractFxmlView;
import javafx.scene.control.Tab;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreeViewModel {
    private String name;
    private Class<? extends AbstractFxmlView> tabClass;
}
