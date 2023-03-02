package ch.epfl.javions.aircraft;

import java.io.*;
import java.net.URLDecoder;
import java.util.Objects;
import java.util.zip.ZipFile;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class AircraftDataBase {
    private String fileName;
    public AircraftDataBase(String fileName){
        this.fileName = Objects.requireNonNull(fileName);
    }

    public AircraftData get(IcaoAddress address) throws IOException {
        String filepath = getClass().getResource("/aircraft.zip").getFile();
        filepath = URLDecoder.decode(filepath, UTF_8);
        for(int i = 1; i <= 256; i++){
            String filename = i + ".csv";
            try (ZipFile zipFile = new ZipFile(filepath);
                 InputStream s = zipFile.getInputStream(zipFile.getEntry(filename));
                 Reader r = new InputStreamReader(s, UTF_8);
                 BufferedReader b = new BufferedReader(r)){
                String l;
                while ((l = b.readLine()) != null) {
                    if (l.startsWith(address.string())) {
                        String[] aircraftString = new String[6];
                        aircraftString = l.split(",", -1);
                        AircraftData aircraftData = new AircraftData(new AircraftRegistration(aircraftString[1]), new AircraftTypeDesignator(aircraftString[2]), aircraftString[3], new AircraftDescription(aircraftString[4]), WakeTurbulenceCategory.of(aircraftString[5]));
                        return aircraftData;
                    }
                }
            }
        }
        return null;
    }
}