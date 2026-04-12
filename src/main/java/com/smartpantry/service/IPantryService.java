package com.smartpantry.service;

import com.smartpantry.model.PantryItem;

import java.util.List;

public interface IPantryService {

  List<PantryItem> getAllItems();

  PantryItem getItemById(Long id);

  PantryItem addItem(PantryItem item);

  void deleteItem(Long id);

  PantryItem consumeItem(Long id, double amount);

  PantryItem restockItem(Long id, double amount);

  List<PantryItem> findByName(String name);

  List<PantryItem> getLowStockItems();

  List<PantryItem> getExpiringSoonItems(int days);
}
