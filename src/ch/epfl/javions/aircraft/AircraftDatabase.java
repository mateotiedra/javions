package ch.epfl.javions.aircraft;

import java.io.*;
import java.util.Objects;
import java.util.zip.ZipFile;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Represents a database of aircraft data, which can be quiered by using the ICAO address.
 *
 * @author Kevan Lam (356395)
 **/
public final class AircraftDatabase {
    private final String fileName;

    public AircraftDatabase(String fileName) {
        this.fileName = Objects.requireNonNull(fileName);
    }

    /**
     * Returns the aircraft data corresponding to the given ICAO address.
     *
     * @param address the ICAO address of the aircraft
     * @return the aircraft data corresponding to the given ICAO address
     * @throws IOException if an I/O error occurs
     */
    public AircraftData get(IcaoAddress address) throws IOException {
        String filename = address.string().substring(4, 6) + ".csv";
        try (ZipFile zipFile = new ZipFile(fileName);
             InputStream s = zipFile.getInputStream(zipFile.getEntry(filename));
             Reader r = new InputStreamReader(s, UTF_8);
             BufferedReader b = new BufferedReader(r)) {
            String l;
            while ((l = b.readLine()) != null) {
                if (l.startsWith(address.string())) {
                    String[] aircraftString = l.split(",", -1);
                    return new AircraftData(new AircraftRegistration(aircraftString[1]),
                            new AircraftTypeDesignator(aircraftString[2]), aircraftString[3],
                            new AircraftDescription(aircraftString[4]), WakeTurbulenceCategory.of(aircraftString[5]));
                }
            }
        }
        return null;
    }
}