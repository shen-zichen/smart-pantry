package com.smartpantry.observer;

import com.smartpantry.model.PantryItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Manages the pantry inventory and acts as the Subject in the Observer Pattern.
 * After any consume or restock operation, it checks for alert conditions
 * and notifies all registered observers.
 *
 * <p>This is the central hub for inventory operations. Controllers interact
 * with the pantry through this class, never directly with PantryItem lists.
 */
public class InventoryManager implements IInventorySubject {

    private final List<PantryItem> pantryItems;
    private final List<IInventoryObserver> observers;
    private int expirationWarningDays;  // how many days ahead to warn about expiration

    /**
     * Constructs an InventoryManager.
     *
     * @param expirationWarningDays how many days before expiration to trigger alerts
     */
    public InventoryManager(int expirationWarningDays) {
        if (expirationWarningDays < 0) {
            throw new IllegalArgumentException(
                    "Expiration warning days cannot be negative: " + expirationWarningDays);
        }
        this.pantryItems = new ArrayList<>();
        this.observers = new ArrayList<>();
        this.expirationWarningDays = expirationWarningDays;
    }

    /** Default constructor — warns 3 days before expiration. */
    public InventoryManager() {
        this(3);
    }

    // ======== Observer Pattern: Subject Methods ========

    @Override
    public void registerObserver(IInventoryObserver observer) {
        Objects.requireNonNull(observer, "Observer cannot be null");
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(IInventoryObserver observer) {
        observers.remove(observer);
    }

    /**
     * Checks all pantry items for alert conditions and notifies observers.
     * Called automatically after consume/restock, but can also be called
     * manually (e.g., daily expiration check).
     */
    @Override
    public void notifyObservers() {
        for (PantryItem item : pantryItems) {
            if (item.isLowStock()) {
                for (IInventoryObserver observer : observers) {
                    observer.onLowStock(item);
                }
            }
            if (item.isExpiringSoon(expirationWarningDays)) {
                for (IInventoryObserver observer : observers) {
                    observer.onExpiringSoon(item);
                }
            }
        }
    }

    // ======== Inventory Operations ========

    /**
     * Adds a new pantry item to the inventory.
     *
     * @param item the pantry item to add
     */
    public void addItem(PantryItem item) {
        Objects.requireNonNull(item, "PantryItem cannot be null");
        pantryItems.add(item);
    }

    /**
     * Removes a pantry item from the inventory.
     *
     * @param item the pantry item to remove
     * @return true if the item was found and removed
     */
    public boolean removeItem(PantryItem item) {
        return pantryItems.remove(item);
    }

    /**
     * Consumes a quantity of a pantry item and triggers observer notifications.
     * This is the key method — it's where the Observer Pattern fires.
     *
     * @param item   the pantry item to consume from
     * @param amount how much to consume
     */
    public void consumeItem(PantryItem item, double amount) {
        Objects.requireNonNull(item, "PantryItem cannot be null");
        if (!pantryItems.contains(item)) {
            throw new IllegalArgumentException("Item not in inventory: " + item.getName());
        }
        item.consume(amount);
        notifyObservers();  // <-- THIS is where Observer Pattern triggers
    }

    /**
     * Restocks a pantry item and triggers observer notifications.
     *
     * @param item   the pantry item to restock
     * @param amount how much to add
     */
    public void restockItem(PantryItem item, double amount) {
        Objects.requireNonNull(item, "PantryItem cannot be null");
        if (!pantryItems.contains(item)) {
            throw new IllegalArgumentException("Item not in inventory: " + item.getName());
        }
        item.restock(amount);
        notifyObservers();
    }

    // ======== Getters ========

    /** Returns a defensive copy of all pantry items. */
    public List<PantryItem> getAllItems() {
        return new ArrayList<>(pantryItems);
    }

    /** Returns the number of items in the pantry. */
    public int getItemCount() {
        return pantryItems.size();
    }

    public int getExpirationWarningDays() {
        return expirationWarningDays;
    }

    public void setExpirationWarningDays(int days) {
        if (days < 0) {
            throw new IllegalArgumentException(
                    "Expiration warning days cannot be negative: " + days);
        }
        this.expirationWarningDays = days;
    }
}