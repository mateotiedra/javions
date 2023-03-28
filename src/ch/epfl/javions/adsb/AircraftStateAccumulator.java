package ch.epfl.javions.adsb;

import java.util.Objects;

/**
 * Accumulate the state of an aircraft from ADS-B messages.
 *
 * @param <T> the type of the state setter.
 */
public class AircraftStateAccumulator<T extends AircraftStateSetter> {
    private final T t;

    private Message previousMessage;
    private int parity;

    public AircraftStateAccumulator(T t) {
        this.t = Objects.requireNonNull(t);
    }

    public T stateSetter() {
        return t;
    }

    public void update(Message message) {
        updateParity();

        switch (message) {
            case AircraftIdentificationMessage aim -> {
                t.setCategory(aim.category());
                t.setCallSign(aim.callSign());
            }
            case AirbornePositionMessage apm -> {
                t.setAltitude(apm.altitude());
                // TODO: set position
            }
            case AirborneVelocityMessage avm -> {
                t.setVelocity(avm.speed());
                t.setTrackOrHeading(avm.trackOrHeading());
            }
            default -> System.out.println("Autre type de message.");
        }

        updateLastMessage(message);
    }


    // PAS SUR QUE CE SOIT UTILE à PARTIR D'ICI

    /**
     * Update the parity, 0 becoes 1 and 1 becomes 0.
     */
    private void updateParity() {
        parity = (parity + 1) % 2;
    }

    /**
     * Save the actual message which will become the previous message.
     *
     * @param message the next previous message.
     */
    private void updateLastMessage(Message message) {
        t.setLastMessageTimeStampNs(message.timeStampNs());
        previousMessage = message;
    }
}
