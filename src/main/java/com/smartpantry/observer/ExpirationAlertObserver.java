package com.smartpantry.observer;

import com.smartpantry.model.PantryItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Logs a warning when a pantry item is approaching its expiration date.
 * Keeps a history of all alerts for later display or testing.
 */
public class ExpirationAlertObserver implements IInventoryObserver {

    private final List<String> alertHistory = new ArrayList<>();

    @Override
    public void onLowStock(PantryItem item) {
        // Not this observer's responsibility — does nothing
    }

    @Override
    public void onExpiringSoon(PantryItem item) {
        String message = "EXPIRING SOON: " + item.getName()
                + " — expires " + item.getExpirationDate() + "!";
        alertHistory.add(message);
        System.out.println(message);
    }

    /**
     * Returns a copy of all alerts that have been fired.
     */
    public List<String> getAlertHistory() {
        return new ArrayList<>(alertHistory);
    }

    /** Clears all stored alerts. */
    public void clearHistory() {
        alertHistory.clear();
    }
}