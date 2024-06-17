package org.example;

import lombok.Data;
import lombok.NonNull;

@Data
public class Point {
    @NonNull
    private double x;
    @NonNull
    private double y;

    double distance(Point other) {
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }
}
