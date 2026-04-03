package com.smartpantry.observer;

import com.smartpantry.model.PantryItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Logs a warning when a pantry item drops to or below its low-stock threshold.
 * Keeps a history of all alerts for later display or testing.
 */
public class LowStockAlertObserver implements IInventoryObserver {

    private final List<String> alertHistory = new ArrayList<>();

    @Override
    public void onLowStock(PantryItem item) {
        String message = "LOW STOCK ALERT: " + item.getName()
                + " — only " + item.getQuantityInStock()
                + item.getIngredient().getUnitType().getAbbreviation() + " remaining!";
        alertHistory.add(message);
        System.out.println(message);
    }

    @Override
    public void onExpiringSoon(PantryItem item) {
        // Not this observer's responsibility — does nothing
    }

    /**
     * Returns a copy of all alerts that have been fired.
     * Useful for testing and for the View to display alert history.
     */
    public List<String> getAlertHistory() {
        return new ArrayList<>(alertHistory);
    }

    /** Clears all stored alerts. */
    public void clearHistory() {
        alertHistory.clear();
    }
}