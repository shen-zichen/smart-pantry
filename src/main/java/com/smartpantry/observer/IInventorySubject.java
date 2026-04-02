package com.smartpantry.observer;

/**
 * Subject interface for the Observer Pattern.
 * The class that implements this (InventoryManager) maintains a list of observers
 * and notifies them when inventory events occur.
 *
 * <p>Observers register/unregister themselves. The subject never knows
 * the concrete type of its observers — it only talks through the
 * {@link IInventoryObserver} interface. This is programming to an interface,
 * not an implementation.
 */
public interface IInventorySubject {

  /**
   * Registers an observer to receive inventory event notifications.
   *
   * @param observer the observer to add
   */
  void registerObserver(IInventoryObserver observer);

  /**
   * Removes an observer so it no longer receives notifications.
   *
   * @param observer the observer to remove
   */
  void removeObserver(IInventoryObserver observer);

  /**
   * Checks all pantry items and notifies observers of any low-stock
   * or expiring-soon conditions. Typically called after a consume or
   * restock operation changes inventory state.
   */
  void notifyObservers();
}