package ru.alfabattle.borisov.atms.services;

import java.awt.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public class CoordinateUtils {

    public static Integer getClosestDeviceId(Point point, Map<Integer, Point> pointsOfDevices) {

        Point closest = Collections.min(pointsOfDevices.values(), new Comparator<Point>() {
            public int compare(final Point p1, final Point p2) {
                return (int) p1.distanceSq(p2);
            }
        });

        return pointsOfDevices.entrySet().stream()
                .filter(e -> e.getValue().equals(closest))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

}
