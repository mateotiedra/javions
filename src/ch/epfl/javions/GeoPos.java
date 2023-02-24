package ch.epfl.javions;

public record GeoPos(int longitudeT32, int latitudeT32) {
    public GeoPos {
        if(!isValidLatitudeT32(latitudeT32))
            throw new IllegalArgumentException();
    }
    public static boolean isValidLatitudeT32(int latitudeT32){
        if(latitudeT32>=-Math.pow(2,30) || latitudeT32<=Math.pow(2,30)) return true;
        else return false;
    }
    public double longitude(){return Units.convert(longitudeT32, Units.Angle.T32,Units.Angle.RADIAN);}
    public double latitude(){return Units.convert(latitudeT32,Units.Angle.T32,Units.Angle.RADIAN);}
    @Override
    public String toString() {
        double longitudeDegree = Units.convert(longitudeT32,Units.Angle.T32,Units.Angle.DEGREE);
        double latitudeDegree = Units.convert(latitudeT32,Units.Angle.T32,Units.Angle.DEGREE);
        return ("(" + longitudeDegree + "°, "+ latitudeDegree + "°)");
    }
}
