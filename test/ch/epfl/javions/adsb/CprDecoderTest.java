package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Units;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CprDecoderTest {
    @Test
    void worksForCransMontana() {
        GeoPos cransMontana = CprDecoder.decodePosition(Math.scalb(111600, -17), Math.scalb(94445, -17),
                Math.scalb(108865, -17), Math.scalb(77558, -17), 0);
        assertEquals(Units.convertFrom(7.476062, Units.Angle.DEGREE), cransMontana.longitude(), .5);
        assertEquals(Units.convertFrom(46.323349, Units.Angle.DEGREE), cransMontana.latitude(), .5);
        GeoPos test = CprDecoder.decodePosition(0.6867904663085938, 0.7254638671875, 0.6865463256835983, 0.725311279296875, 1);
    }
}
