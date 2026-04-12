package com.smartpantry.service;

import com.smartpantry.entity.PantryItemEntity;
import com.smartpantry.exception.ResourceNotFoundException;
import com.smartpantry.mapper.PantryItemMapper;
import com.smartpantry.mapper.IngredientMapper;
import com.smartpantry.model.PantryItem;
import com.smartpantry.repository.PantryItemRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PantryServiceImpl implements IPantryService {

  private final PantryItemRepository repository;

  public PantryServiceImpl(PantryItemRepository repository) {
    this.repository = repository;
  }

  @Override
  public List<PantryItem> getAllItems() {
    return repository.findAll().stream().map(PantryItemMapper::toDomain).toList();
  }

  @Override
  public PantryItem getItemById(Long id) {
    PantryItemEntity entity =
        repository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Pantry item not found: " + id));
    return PantryItemMapper.toDomain(entity);
  }

  @Override
  public PantryItem addItem(PantryItem item) {
    PantryItemEntity entity =
        PantryItemMapper.toEntity(item, IngredientMapper.toEntity(item.getIngredient()));
    PantryItemEntity saved = repository.save(entity);
    return PantryItemMapper.toDomain(saved);
  }

  @Override
  public void deleteItem(Long id) {
    if (!repository.existsById(id)) {
      throw new ResourceNotFoundException("Pantry item not found: " + id);
    }
    repository.deleteById(id);
  }

  @Override
  public PantryItem consumeItem(Long id, double amount) {
    PantryItemEntity entity =
        repository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Pantry item not found: " + id));

    double newQuantity = Math.max(0, entity.getQuantityInStock() - amount);
    entity.setQuantityInStock(newQuantity);

    PantryItemEntity saved = repository.save(entity);
    return PantryItemMapper.toDomain(saved);
  }

  @Override
  public PantryItem restockItem(Long id, double amount) {
    PantryItemEntity entity =
        repository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Pantry item not found: " + id));

    entity.setQuantityInStock(entity.getQuantityInStock() + amount);

    PantryItemEntity saved = repository.save(entity);
    return PantryItemMapper.toDomain(saved);
  }

  @Override
  public List<PantryItem> findByName(String name) {
    return repository.findByIngredientNameContainingIgnoreCase(name).stream()
        .map(PantryItemMapper::toDomain)
        .toList();
  }

  @Override
  public List<PantryItem> getLowStockItems() {
    return repository.findAll().stream()
        .map(PantryItemMapper::toDomain)
        .filter(PantryItem::isLowStock)
        .toList();
  }

  @Override
  public List<PantryItem> getExpiringSoonItems(int days) {
    return repository.findAll().stream()
        .map(PantryItemMapper::toDomain)
        .filter(item -> item.isExpiringSoon(days))
        .toList();
  }
}
