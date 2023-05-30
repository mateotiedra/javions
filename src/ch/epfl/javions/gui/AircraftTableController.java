package ch.epfl.javions.gui;

import ch.epfl.javions.Units;
import ch.epfl.javions.adsb.CallSign;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.function.Consumer;

/**
 * Controller for the aircraft table
 *
 * @author Kevan Lam (356395)
 */
public final class AircraftTableController {
    private ObservableSet<ObservableAircraftState> observableState;
    private TableView<ObservableAircraftState> tableView;
    private NumberFormat longlatFormater = NumberFormat.getInstance();
    private NumberFormat altvitFormater = NumberFormat.getInstance();
    private Pane pane;
    private ObjectProperty<ObservableAircraftState> selectedAircraft;

    /**
     * Controller class for the aircraft table view.
     * This class manages the initialization and behavior of the aircraft table view.
     *
     * @param observableAircraftState An ObservableSet of ObservableAircraftState objects representing the aircraft states.
     * @param selectedAircraft        An ObjectProperty representing the currently selected aircraft state.
     */
    public AircraftTableController(ObservableSet<ObservableAircraftState> observableAircraftState, ObjectProperty<ObservableAircraftState> selectedAircraft) {
        this.tableView = new TableView<>();
        pane = new Pane(tableView);
        this.observableState = observableAircraftState;
        this.selectedAircraft = selectedAircraft;
        observableState.addListener((SetChangeListener<ObservableAircraftState>) change -> {
            if (change.wasAdded()) {
                tableView.getItems().add(change.getElementAdded());
                tableView.sort();
            } else if (change.wasRemoved()) {
                tableView.getItems().remove(change.getElementRemoved());
            }
        });
        tableView.getStylesheets().add("table.css");
        tableView.setTableMenuButtonVisible(true);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_SUBSEQUENT_COLUMNS);


        createColumns();
    }

    /**
     * @return the pane containing the table
     */
    public TableView<ObservableAircraftState> pane() {
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
            if (event.getClickCount() == 2) {
                if (selectedAircraft != null) {
                    consumer.accept(selectedAircraft.get());
                }
            }
        });
    }

    /**
     * Creates the columns of the table
     */
    private void createColumns() {
        longlatFormater.setMaximumFractionDigits(4);
        longlatFormater.setMinimumFractionDigits(4);

        altvitFormater.setMaximumFractionDigits(0);
        altvitFormater.setMinimumFractionDigits(0);

        // Litteral columns
        TableColumn<ObservableAircraftState, String> icaoCol = new TableColumn<>("OACI");
        icaoCol.setPrefWidth(60);
        icaoCol.setCellValueFactory(f -> {
            if (f.getValue().getAircraftData() == null)
                return new ReadOnlyObjectWrapper<>("");
            else
                return new ReadOnlyObjectWrapper<>(f.getValue().getIcaoAddress().string());
        });

        TableColumn<ObservableAircraftState, String> callsignCol = new TableColumn<>("Indicatif");
        callsignCol.setPrefWidth(70);
        callsignCol.setCellValueFactory(f -> f.getValue().callSignProperty().map(CallSign::string));

        TableColumn<ObservableAircraftState, String> immatriculationCol = new TableColumn<>("Immatriculation");
        immatriculationCol.setPrefWidth(90);
        immatriculationCol.setCellValueFactory(f -> {
            if (f.getValue().getAircraftData() == null)
                return new ReadOnlyObjectWrapper<>("");
            else
                return new ReadOnlyObjectWrapper<>(f.getValue().getAircraftData().
                        registration().string());
        });

        TableColumn<ObservableAircraftState, String> modelCol = new TableColumn<>("Modèle");
        modelCol.setPrefWidth(230);
        modelCol.setCellValueFactory(f -> {
            if (f.getValue().getAircraftData() == null)
                return new ReadOnlyObjectWrapper<>("");
            else
                return new ReadOnlyObjectWrapper<>(f.getValue().getAircraftData().model());
        });

        TableColumn<ObservableAircraftState, String> typeCol = new TableColumn<>("Type");
        typeCol.setPrefWidth(50);
        typeCol.setCellValueFactory(
                f -> {
                    if (f.getValue().getAircraftData() == null)
                        return new ReadOnlyObjectWrapper<>("");
                    else
                        return new ReadOnlyObjectWrapper<>(f.getValue().getAircraftData().
                                typeDesignator().string());
                });

        TableColumn<ObservableAircraftState, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setPrefWidth(70);
        descriptionCol.setCellValueFactory(f -> {
            if (f.getValue().getAircraftData() == null)
                return new ReadOnlyObjectWrapper<>("");
            else
                return new ReadOnlyObjectWrapper<>(f.getValue().getAircraftData().
                        description().string());
        });

        // Numeric columns

        TableColumn<ObservableAircraftState, String> longitudeCol = new TableColumn<>("Longitude(°)");
        longitudeCol.setPrefWidth(85);
        longitudeCol.getStyleClass().add("numeric");
        columnComparator(longitudeCol);
        longitudeCol.setCellValueFactory(f -> {
            double lon = f.getValue().positionProperty().get().longitude();
            lon = Units.convert(lon, Units.Angle.RADIAN, Units.Angle.DEGREE);
            return new ReadOnlyObjectWrapper<>(longlatFormater.format(lon));
        });

        TableColumn<ObservableAircraftState, String> latitudeCol = new TableColumn<>("Latitude(°)");
        latitudeCol.setPrefWidth(85);
        latitudeCol.getStyleClass().add("numeric");
        columnComparator(latitudeCol);
        latitudeCol.setCellValueFactory(f -> {
            double lat = f.getValue().positionProperty().get().latitude();
            lat = Units.convert(lat, Units.Angle.RADIAN, Units.Angle.DEGREE);
            return new ReadOnlyObjectWrapper<>(longlatFormater.format(lat));
        });

        TableColumn<ObservableAircraftState, String> altitudeCol = new TableColumn<>("Altitude (m)");
        altitudeCol.setPrefWidth(85);
        altitudeCol.getStyleClass().add("numeric");
        columnComparator(altitudeCol);
        altitudeCol.setCellValueFactory(f -> {
            double alt = f.getValue().altitudeProperty().get();
            return new ReadOnlyObjectWrapper<>(altvitFormater.format(alt));
        });


        TableColumn<ObservableAircraftState, String> speedCol = new TableColumn<>("Vitesse (km/h)");
        speedCol.setPrefWidth(85);
        speedCol.getStyleClass().add("numeric");
        columnComparator(speedCol);
        speedCol.setCellValueFactory(f -> {
            double speed = f.getValue().velocityProperty().get();
            return new ReadOnlyObjectWrapper<>(altvitFormater.format(speed));
        });

        tableView.getColumns().addAll(icaoCol, callsignCol, immatriculationCol, modelCol, typeCol, descriptionCol,
                longitudeCol, latitudeCol, altitudeCol, speedCol);
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
                    return Double.compare(longlatFormater.parse(s1).doubleValue(), longlatFormater.parse(s2).doubleValue());
                } catch (ParseException e) {
                    throw new Error(e);
                }
            }
        });
    }

}
