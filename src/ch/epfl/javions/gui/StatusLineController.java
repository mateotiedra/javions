package ch.epfl.javions.gui;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

/**
 * A controller for the status line.
 *
 * @author Kevan Lam (356395)
 */
public final class StatusLineController {
    private final BorderPane pane;
    private final IntegerProperty aircraftCount = new SimpleIntegerProperty();
    private final LongProperty messageCount = new SimpleLongProperty();

    /**
     * Constructs a new status line controller.
     */
    public StatusLineController() {
        pane = new BorderPane();
        pane.getStyleClass().add("status-pane");

        Text aircraftCountText = new Text();
        aircraftCountText.textProperty().bind(aircraftCount.map(count -> "Aéronefs visibles : " + count));
        Text messageCountText = new Text();
        messageCountText.textProperty().bind(messageCount.map(count -> "Messages reçus : " + count));

        pane.setLeft(aircraftCountText);
        pane.setRight(messageCountText);
    }

    /**
     * Returns the pane of the status line.
     *
     * @return the pane of the status line
     */
    public Pane pane() {
        return pane;
    }

    /**
     * Returns the aircraft count property.
     *
     * @return the aircraft count property
     */
    public IntegerProperty aircraftCountProperty() {
        return aircraftCount;
    }

    /**
     * Returns the message count property.
     *
     * @return the message count property
     */
    public LongProperty messageCountProperty() {
        return messageCount;
    }
}
