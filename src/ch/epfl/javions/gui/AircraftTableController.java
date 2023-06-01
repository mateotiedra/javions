package ch.epfl.javions.gui;

import ch.epfl.javions.Units;
import ch.epfl.javions.adsb.CallSign;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.function.Consumer;

/**
 * Controller for the aircraft table
 *
 * @author Kevan Lam (356395)
 */
public final class AircraftTableController {
    private final TableView<ObservableAircraftState> tableView;
    private final NumberFormat longAndLatFormater = NumberFormat.getInstance();
    private final NumberFormat altAndVitFormater = NumberFormat.getInstance();
    private final ObjectProperty<ObservableAircraftState> selectedAircraft;

    /**
     * Controller class for the aircraft table view.
     * This class manages the initialization and behavior of the aircraft table view.
     *
     * @param observableAircraftState An ObservableSet of ObservableAircraftState objects representing the aircraft states.
     * @param selectedAircraft        An ObjectProperty representing the currently selected aircraft state.
     */
    public AircraftTableController(
            ObservableSet<ObservableAircraftState> observableAircraftState,
            ObjectProperty<ObservableAircraftState> selectedAircraft
    ) {
        tableView = new TableView<>();
        this.selectedAircraft = selectedAircraft;

        // Listeners
        observableAircraftState.addListener((SetChangeListener<ObservableAircraftState>) change -> {
            if (change.wasAdded()) {
                tableView.getItems().add(change.getElementAdded());
                tableView.sort();
            } else if (change.wasRemoved()) {
                tableView.getItems().remove(change.getElementRemoved());
            }
        });

        selectedAircraft.addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals(oldValue)) {
                tableView.getSelectionModel().select(newValue);
                tableView.scrollTo(newValue);
            }
        });

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
                selectedAircraft.set(newValue);
        });

        // Styles
        tableView.getStylesheets().add("table.css");
        tableView.setTableMenuButtonVisible(true);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);

        createColumns();
    }

    /**
     * @return the pane containing the table
     */
    public Node pane() {
        return tableView;
    }

    /**
     * Sets a double-click event handler for the aircraft table view.
     * The provided consumer function will be called when a double-click event occurs on the table view.
     * The selected aircraft state will be passed as an argument to the consumer function.
     *
     * @param consumer The consumer function to be called on double-click events. It accepts an ObservableAircraftState object as the argument.
     */
    public void setOnDoubleClick(Consumer<ObservableAircraftState> consumer) {
        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && selectedAircraft.get() != null)
                consumer.accept(selectedAircraft.get());
        });
    }

    /**
     * Creates the columns of the table
     */
    private void createColumns() {
        longAndLatFormater.setMaximumFractionDigits(4);
        longAndLatFormater.setMinimumFractionDigits(4);

        altAndVitFormater.setMaximumFractionDigits(0);
        altAndVitFormater.setMinimumFractionDigits(0);

        // Literal columns
        TableColumn<ObservableAircraftState, String> icaoCol = new TableColumn<>("OACI");
        icaoCol.setPrefWidth(60);
        icaoCol.setCellValueFactory(
                f -> new ReadOnlyObjectWrapper<>(f.getValue().getAircraftData() == null
                        ? ""
                        : f.getValue().getIcaoAddress().string())
        );

        TableColumn<ObservableAircraftState, String> callsignCol = new TableColumn<>("Indicatif");
        callsignCol.setPrefWidth(70);
        callsignCol.setCellValueFactory(f -> f.getValue().callSignProperty().map(CallSign::string));

        TableColumn<ObservableAircraftState, String> immatriculationCol = new TableColumn<>("Immatriculation");
        immatriculationCol.setPrefWidth(90);
        immatriculationCol.setCellValueFactory(
                f -> new ReadOnlyObjectWrapper<>(f.getValue().getAircraftData() == null
                        ? ""
                        : f.getValue().getAircraftData().registration().string())
        );

        TableColumn<ObservableAircraftState, String> modelCol = new TableColumn<>("Modèle");
        modelCol.setPrefWidth(230);
        modelCol.setCellValueFactory(
                f -> new ReadOnlyObjectWrapper<>(f.getValue().getAircraftData() == null
                        ? ""
                        : f.getValue().getAircraftData().model())
        );

        TableColumn<ObservableAircraftState, String> typeCol = new TableColumn<>("Type");
        typeCol.setPrefWidth(50);
        typeCol.setCellValueFactory(
                f -> new ReadOnlyObjectWrapper<>(f.getValue().getAircraftData() == null
                        ? ""
                        : f.getValue().getAircraftData().typeDesignator().string())
        );

        TableColumn<ObservableAircraftState, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setPrefWidth(70);
        descriptionCol.setCellValueFactory(
                f -> new ReadOnlyObjectWrapper<>(f.getValue().getAircraftData() == null
                        ? ""
                        : f.getValue().getAircraftData().description().string())
        );

        // Numeric columns

        TableColumn<ObservableAircraftState, String> longitudeCol = new TableColumn<>("Longitude(°)");
        longitudeCol.setPrefWidth(85);
        longitudeCol.getStyleClass().add("numeric");
        columnComparator(longitudeCol);
        longitudeCol.setCellValueFactory(f -> f.getValue().positionProperty().map(
                pos -> longAndLatFormater.format(Units.convert(pos.longitude(), Units.Angle.RADIAN, Units.Angle.DEGREE))
        ));

        TableColumn<ObservableAircraftState, String> latitudeCol = new TableColumn<>("Latitude(°)");
        latitudeCol.setPrefWidth(85);
        latitudeCol.getStyleClass().add("numeric");
        columnComparator(latitudeCol);
        latitudeCol.setCellValueFactory(f -> f.getValue().positionProperty().map(
                pos -> longAndLatFormater.format(Units.convert(pos.latitude(), Units.Angle.RADIAN, Units.Angle.DEGREE))
        ));


        TableColumn<ObservableAircraftState, String> altitudeCol = new TableColumn<>("Altitude (m)");
        altitudeCol.setPrefWidth(85);
        altitudeCol.getStyleClass().add("numeric");
        columnComparator(altitudeCol);
        altitudeCol.setCellValueFactory(f -> f.getValue().altitudeProperty().map(altAndVitFormater::format));


        TableColumn<ObservableAircraftState, String> speedCol = new TableColumn<>("Vitesse (km/h)");
        speedCol.setPrefWidth(85);
        speedCol.getStyleClass().add("numeric");
        columnComparator(speedCol);
        speedCol.setCellValueFactory(f -> f.getValue().velocityProperty().map(
                vel -> altAndVitFormater.format(
                        Units.convert(vel.doubleValue(), Units.Speed.METER_PER_SECOND, Units.Speed.KILOMETER_PER_HOUR)
                )
        ));

        tableView.getColumns().addAll(
                icaoCol,
                callsignCol,
                immatriculationCol,
                modelCol,
                typeCol,
                descriptionCol,
                longitudeCol,
                latitudeCol,
                altitudeCol,
                speedCol
        );
    }

    /**
     * Sets the comparator of the column to compare the values as numbers
     *
     * @param column the column to set the comparator
     */
    private void columnComparator(TableColumn<ObservableAircraftState, String> column) {
        column.setComparator((s1, s2) -> {
            if (s1.isEmpty() || s2.isEmpty())
                return s1.compareTo(s2);
            else {
                try {
                    return Double.compare(
                            longAndLatFormater.parse(s1).doubleValue(),
                            longAndLatFormater.parse(s2).doubleValue()
                    );
                } catch (ParseException e) {
                    throw new Error(e);
                }
            }
        });
    }

}
