package com.example.myapplication.utils;

import com.example.myapplication.models.Plat;
import java.util.ArrayList;
import java.util.List;

public class OrderManager {
    private static final List<Plat> orderedPlats = new ArrayList<>();

    public static void addPlat(Plat plat) {
        orderedPlats.add(plat);
    }

    public static List<Plat> getOrderedPlats() {
        return new ArrayList<>(orderedPlats);
    }

    public static void clearOrders() {
        orderedPlats.clear();
    }
}
