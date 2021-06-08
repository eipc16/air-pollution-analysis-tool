package org.ppietrzak.grounddatacore.domain.stations.logic;

public class CoordinatesBox {

    private final double bottomLatitude;
    private final double upperLatitude;
    private final double bottomLongitude;
    private final double upperLongitude;

    public CoordinatesBox(double bottomLatitude, double upperLatitude,
                           double bottomLongitude, double upperLongitude) {
        this.bottomLatitude = bottomLatitude;
        this.upperLatitude = upperLatitude;
        this.bottomLongitude = bottomLongitude;
        this.upperLongitude = upperLongitude;
    }

    public double getBottomLatitude() {
        return bottomLatitude;
    }

    public double getUpperLatitude() {
        return upperLatitude;
    }

    public double getBottomLongitude() {
        return bottomLongitude;
    }

    public double getUpperLongitude() {
        return upperLongitude;
    }
}
