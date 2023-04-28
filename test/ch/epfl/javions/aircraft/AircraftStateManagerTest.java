package ch.epfl.javions.aircraft;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.Units;
import ch.epfl.javions.adsb.Message;
import ch.epfl.javions.adsb.MessageParser;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.gui.AircraftStateManager;
import ch.epfl.javions.gui.ObservableAircraftState;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.util.Set;

public class AircraftStateManagerTest {
    final static double PI = Math.PI;

    @Test
    public void testAircraftManager() throws Exception {
        try (DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream("resources/messages_20230318_0915.bin")))) {
            byte[] bytes = new byte[RawMessage.LENGTH];
            System.out.println("OACI    Indicatif Immat.  Modèle             Longitude   Latitude   Alt.  Vit.");
            System.out.println("――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――――");
            AircraftStateManager aircraftStateManager = new AircraftStateManager(new AircraftDatabase("resources/aircraft.zip"));
            Set<ObservableAircraftState> aircraft;
            while (true) {
                long timeStampNs = dataInputStream.readLong();
                int bytesRead = dataInputStream.readNBytes(bytes, 0, bytes.length);
                assert bytesRead == RawMessage.LENGTH;
                ByteString messageByte = new ByteString(bytes);
                RawMessage rawMessage = new RawMessage(timeStampNs, messageByte);
                Message message = MessageParser.parse(rawMessage);
                if (message != null) aircraftStateManager.updateWithMessage(message);
                aircraft = aircraftStateManager.states();
                printAircraftTable(aircraft);
                aircraftStateManager.purge();
            }
        } catch (EOFException e) {
        }
    }

    @Test
    public void givenTest() throws Exception {
        try (DataInputStream s = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream("resources/messages_20230318_0915.bin")))) {
            byte[] bytes = new byte[RawMessage.LENGTH];
            while (true) {
                long timeStampNs = s.readLong();
                int bytesRead = s.readNBytes(bytes, 0, bytes.length);
                assert bytesRead == RawMessage.LENGTH;
                ByteString message = new ByteString(bytes);
                System.out.printf("%13d: %s\n", timeStampNs, message);
            }
        } catch (EOFException e) { /* nothing to do */ }
    }

    private static void printAircraftTable(Set<ObservableAircraftState> aircraft) {
        for (ObservableAircraftState state : aircraft) {
            String callsign = "";
            String registration = "";
            String model = "";
            String icao = state.getIcaoAddress().string();
            if (state.getCallsign() != null)
                callsign = state.getCallsign().string();
            if (state.getAircraftData() != null) {
                registration = state.getAircraftData().registration().string();
                model = state.getAircraftData().model();
            }
            double longitude = state.getPosition().longitude();
            double latitude = state.getPosition().latitude();
            double altitude = state.getAltitude();
            longitude = Units.convert(longitude, Units.Angle.RADIAN, Units.Angle.DEGREE);
            latitude = Units.convert(latitude, Units.Angle.RADIAN, Units.Angle.DEGREE);
            double speed = state.getVelocity();
            String direction = getDirectionArrow(state.getTrackOrHeading());
            System.out.printf("%-6s  %-9s %-6s  %-6s  %.7s  %.5s  %.3s  %.1s\n",
                    icao, callsign, registration, model, longitude, latitude, altitude, speed, direction);

        }
    }

    private static String getDirectionArrow(double degrees) {
        if (degrees < 3 * PI / 8 && degrees >= PI / 8) {
            return "↗";
        } else if (degrees <= 5 * PI / 8 && degrees >= 3 * PI / 8) {
            return "→";
        } else if (degrees < 7 * PI / 8 && degrees > 5 * PI / 8) {
            return "↘";
        } else if (degrees <= 9 * PI / 8 && degrees >= 7 * PI / 8) {
            return "↓";
        } else if (degrees < 11 * PI / 8 && degrees > 9 * PI / 8) {
            return "↙";
        } else if (degrees <= 13 * PI / 8 && degrees >= 11 * PI / 8) {
            return "←";
        } else if (degrees < 15 * PI / 8 && degrees > 13 * PI / 8) {
            return "↖";
        } else {
            return "↑";
        }
    }
}
