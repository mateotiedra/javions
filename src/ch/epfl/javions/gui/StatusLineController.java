package ch.epfl.javions.gui;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public final class StatusLineController {
    private final Pane pane;
    private SimpleIntegerProperty aircraftCount;
    private SimpleLongProperty messageCount;

    public StatusLineController() {
        pane = new Pane();
        aircraftCount = new SimpleIntegerProperty();
        messageCount = new SimpleLongProperty();
    }

    public Pane pane() {
        return pane;
    }

    public SimpleIntegerProperty aircraftCountProperty() {
        return aircraftCount;
    }

    public SimpleLongProperty messageCountProperty() {
        return messageCount;
    }

    private BorderPane createStatusPane() {
        BorderPane borderPane = new BorderPane();
        borderPane.getStyleClass().add("status-pane");

        Text aircraftCountText = new Text();
        aircraftCountText.textProperty().bind(aircraftCount.map(count -> "Aéronefs visibles : " + count));
        Text messageCountText = new Text();
        messageCountText.textProperty().bind(messageCount.map(count -> "Messages reçus : " + count));

        borderPane.setLeft(aircraftCountText);
        borderPane.setRight(messageCountText);

        return borderPane;
    }
}
