package ch.epfl.javions.gui;

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

public class AircraftStateManager {
    private final Map<IcaoAddress, AircraftStateAccumulator<ObservableAircraftState>> aircraftStateAccumulatorMap = new HashMap<>();
    private final ObservableSet<ObservableAircraftState> aircraftWithKnownPositionStates = FXCollections.observableSet();
    private final ObservableSet<ObservableAircraftState> unmodifiableAircraftWithKnownPositionStates = FXCollections.unmodifiableObservableSet(aircraftWithKnownPositionStates);

    private final AircraftDatabase aircraftDatabase;
    private long lastMessageTimeStampNs;
    private static final long ONE_MINUTE_IN_NS = 60000000000L;

    public AircraftStateManager(AircraftDatabase aircraftDatabase) {
        this.aircraftDatabase = aircraftDatabase;
    }

    /**
     * @return an unmodifiable set of all the aircraft with known positon states
     */
    public ObservableSet<ObservableAircraftState> states() {
        return unmodifiableAircraftWithKnownPositionStates;
    }

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

        ObservableAircraftState state = aircraftStateAccumulatorMap.get(icaoAddress).stateSetter();

        if (state.getPosition() != null) {
            aircraftWithKnownPositionStates.add(state);
        }
    }

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
