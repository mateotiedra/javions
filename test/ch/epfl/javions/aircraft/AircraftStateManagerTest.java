package ch.epfl.javions.aircraft;

import ch.epfl.javions.ByteString;
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
            AircraftStateManager aircraftStateManager = new AircraftStateManager(new AircraftDatabase("resources/messages_20230318_0915.bin"));
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
        final char[] directions = new char[]{'↑', '↗', '→', '↘', '↓', '↙', '←', '↖'};
        for (ObservableAircraftState state : aircraft) {
            String icao = state.getIcaoAddress().string();
            String callsign = state.getCallsign().string();
            String registration = state.getAircraftData().registration().string();
            String model = state.getAircraftData().model();
            double longitude = state.getPosition().longitude();
            double latitude = state.getPosition().latitude();
            double altitude = state.getAltitude();
            double speed = state.getVelocity();
            String direction = getDirectionArrow(state.getTrackOrHeading());
            System.out.printf("%-6s  %-9s %-6s  %-18s  %9s  %9s  %5s  %3s\n",
                    icao, callsign, registration, model, longitude, latitude, altitude, speed, direction);
        }
        System.out.println();
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
