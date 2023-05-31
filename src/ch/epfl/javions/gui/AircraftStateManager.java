package ch.epfl.javions.gui;

import ch.epfl.javions.Units;
import ch.epfl.javions.adsb.AircraftStateAccumulator;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.aircraft.AircraftData;
import ch.epfl.javions.aircraft.AircraftDatabase;
import ch.epfl.javions.aircraft.IcaoAddress;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is responsible for managing the aircraft states. It is responsible for updating the states with messages
 * and purging the states that have not been updated for more than one minute.
 *
 * @author Mateo Tiedra (356525)
 */
public class AircraftStateManager {
    private static final long ONE_MINUTE_IN_NS = (long) (Units.Time.MINUTE * Math.pow(10, 9));
    private final Map<IcaoAddress, AircraftStateAccumulator<ObservableAircraftState>> aircraftStateAccumulatorMap = new HashMap<>();
    private final ObservableSet<ObservableAircraftState> aircraftWithKnownPositionStates = FXCollections.observableSet();
    private final ObservableSet<ObservableAircraftState> unmodifiableAircraftWithKnownPositionStates = FXCollections.unmodifiableObservableSet(aircraftWithKnownPositionStates);

    private final AircraftDatabase aircraftDatabase;
    private long lastMessageTimeStampNs;

    /**
     * @param aircraftDatabase the database to use to get the aircraft data
     */
    public AircraftStateManager(AircraftDatabase aircraftDatabase) {
        this.aircraftDatabase = aircraftDatabase;
    }

    /**
     * @return an unmodifiable set of all the aircraft with known positon states
     */
    public ObservableSet<ObservableAircraftState> states() {
        return unmodifiableAircraftWithKnownPositionStates;
    }

    /**
     * Updates the state of the aircraft with the given message. If the aircraft is not in the map, it is added to the
     * map with the aircraft data from the database.
     *
     * @param message the message to update the state with
     */
    public void updateWithMessage(Message message) {
        IcaoAddress icaoAddress = message.icaoAddress();
        lastMessageTimeStampNs = message.timeStampNs();

        if (aircraftStateAccumulatorMap.containsKey(icaoAddress)) {
            aircraftStateAccumulatorMap.get(icaoAddress).update(message);
        } else {
            try {
                AircraftData aircraftData = aircraftDatabase.get(icaoAddress);
                aircraftStateAccumulatorMap.put(icaoAddress, new AircraftStateAccumulator<>(new ObservableAircraftState(icaoAddress, aircraftData)));
            } catch (IOException e) {
                System.out.println("Database not found\n");
            }
        }

        ObservableAircraftState state = aircraftStateAccumulatorMap.get(icaoAddress) != null
                ? aircraftStateAccumulatorMap.get(icaoAddress).stateSetter()
                : null;

        if (state != null && state.getPosition() != null) {
            aircraftWithKnownPositionStates.add(state);
        }
    }

    /**
     * Purges the states that have not been updated for more than one minute.
     */
    public void purge() {
        List<ObservableAircraftState> aircraftToRemoveList = aircraftWithKnownPositionStates.stream().filter(
                state -> lastMessageTimeStampNs - state.getLastMessageTimeStampNs() > ONE_MINUTE_IN_NS
        ).toList();

        for (ObservableAircraftState aircraftToRemove : aircraftToRemoveList) {
            aircraftWithKnownPositionStates.remove(aircraftToRemove);
            aircraftStateAccumulatorMap.remove(aircraftToRemove.getIcaoAddress());
        }
    }
}
