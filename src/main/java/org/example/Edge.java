package org.example;

import lombok.Data;
import lombok.NonNull;

@Data
public class Edge {
    @NonNull
    private int u;
    @NonNull
    private int v;
    @NonNull
    private double weight;
}
