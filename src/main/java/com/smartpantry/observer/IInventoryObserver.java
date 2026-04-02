package com.smartpantry.observer;

import com.smartpantry.model.PantryItem;

/**
 * Observer interface for inventory events. Implementing classes react to pantry changes — logging,
 * alerting, UI updates, etc.
 *
 * <p>Part of the Observer Pattern: IInventorySubject fires events, IInventoryObserver
 * implementations handle them.
 */
public interface IInventoryObserver {

  /**
   * Called when a pantry item's quantity drops at or below its low-stock threshold.
   *
   * @param item the pantry item that is running low
   */
  void onLowStock(PantryItem item);

  /**
   * Called when a pantry item is approaching its expiration date.
   *
   * @param item the pantry item that is expiring soon
   */
  void onExpiringSoon(PantryItem item);
}
