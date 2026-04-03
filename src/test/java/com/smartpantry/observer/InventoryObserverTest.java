package com.smartpantry.observer;

import static org.junit.jupiter.api.Assertions.*;

import com.smartpantry.model.*;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the Observer Pattern implementation.
 * Covers: observer registration, low stock alerts, expiration alerts,
 * consume/restock triggering, and observer removal.
 */
class InventoryObserverTest {

    private InventoryManager manager;
    private LowStockAlertObserver lowStockObserver;
    private ExpirationAlertObserver expirationObserver;
    private PantryItem chickenItem;
    private PantryItem saltItem;

    @BeforeEach
    void setUp() {
        manager = new InventoryManager(3);  // warn 3 days before expiration
        lowStockObserver = new LowStockAlertObserver();
        expirationObserver = new ExpirationAlertObserver();

        manager.registerObserver(lowStockObserver);
        manager.registerObserver(expirationObserver);

        Ingredient chicken = new Ingredient("chicken thigh", 0, UnitType.GRAM, CategoryType.PROTEIN);
        chickenItem = new PantryItem(chicken, 500, LocalDate.now(),
                LocalDate.now().plusDays(2), 100);  // expires in 2 days (within warning window)

        Ingredient salt = new Ingredient("salt", 0, UnitType.GRAM, CategoryType.SPICE);
        saltItem = new PantryItem(salt, 1000, LocalDate.now(), null, 50);  // never expires

        manager.addItem(chickenItem);
        manager.addItem(saltItem);
    }

    // ---- Low Stock Alerts ----

    @Test
    void consume_belowThreshold_triggersLowStockAlert() {
        manager.consumeItem(chickenItem, 450);  // 500 - 450 = 50, below threshold of 100

        assertFalse(lowStockObserver.getAlertHistory().isEmpty());
        assertTrue(lowStockObserver.getAlertHistory().get(0).contains("chicken thigh"));
    }

    @Test
    void consume_aboveThreshold_noLowStockAlert() {
        lowStockObserver.clearHistory();
        manager.consumeItem(chickenItem, 100);  // 500 - 100 = 400, above threshold

        // Only expiration alerts should fire (chicken expires in 2 days)
        // Filter for low stock specifically
        assertTrue(lowStockObserver.getAlertHistory().isEmpty());
    }

    @Test
    void consume_exactlyAtThreshold_triggersAlert() {
        manager.consumeItem(chickenItem, 400);  // 500 - 400 = 100, exactly at threshold

        assertFalse(lowStockObserver.getAlertHistory().isEmpty());
    }

    // ---- Expiration Alerts ----

    @Test
    void expiringSoon_withinWarningWindow_triggersAlert() {
        // Chicken expires in 2 days, warning window is 3 days — should alert
        manager.consumeItem(chickenItem, 1);  // trigger notify

        assertFalse(expirationObserver.getAlertHistory().isEmpty());
        assertTrue(expirationObserver.getAlertHistory().get(0).contains("chicken thigh"));
    }

    @Test
    void expiringSoon_noExpirationDate_noAlert() {
        expirationObserver.clearHistory();
        manager.consumeItem(saltItem, 1);  // salt has no expiration

        // Salt should not trigger expiration alert
        boolean saltAlert = expirationObserver.getAlertHistory().stream()
                .anyMatch(msg -> msg.contains("salt"));
        assertFalse(saltAlert);
    }

    // ---- Observer Management ----

    @Test
    void removeObserver_stopsReceivingAlerts() {
        manager.removeObserver(lowStockObserver);
        lowStockObserver.clearHistory();

        manager.consumeItem(chickenItem, 450);  // would trigger low stock

        assertTrue(lowStockObserver.getAlertHistory().isEmpty());  // but observer was removed
    }

    @Test
    void registerObserver_duplicateIgnored() {
        manager.registerObserver(lowStockObserver);  // already registered
        lowStockObserver.clearHistory();

        manager.consumeItem(chickenItem, 450);

        // Should only get one alert, not two
        long lowStockAlerts = lowStockObserver.getAlertHistory().stream()
                .filter(msg -> msg.contains("chicken thigh"))
                .count();
        assertEquals(1, lowStockAlerts);
    }

    // ---- Inventory Operations ----

    @Test
    void addItem_increasesCount() {
        int before = manager.getItemCount();
        Ingredient rice = new Ingredient("rice", 0, UnitType.GRAM, CategoryType.GRAIN);
        manager.addItem(new PantryItem(rice, 500, LocalDate.now(), null, 100));

        assertEquals(before + 1, manager.getItemCount());
    }

    @Test
    void removeItem_decreasesCount() {
        int before = manager.getItemCount();
        manager.removeItem(chickenItem);

        assertEquals(before - 1, manager.getItemCount());
    }

    @Test
    void consumeItem_notInInventory_throwsException() {
        Ingredient unknown = new Ingredient("tofu", 0, UnitType.GRAM, CategoryType.PROTEIN);
        PantryItem unknownItem = new PantryItem(unknown, 200, LocalDate.now(), null, 50);

        assertThrows(IllegalArgumentException.class,
                () -> manager.consumeItem(unknownItem, 100));
    }

    @Test
    void restockItem_updatesQuantity() {
        manager.restockItem(saltItem, 500);

        assertEquals(1500, saltItem.getQuantityInStock());
    }

    // ---- Getters ----

    @Test
    void getAllItems_returnsDefensiveCopy() {
        manager.getAllItems().clear();

        assertEquals(2, manager.getItemCount());  // original unaffected
    }

    @Test
    void constructor_negativeWarningDays_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new InventoryManager(-1));
    }
}