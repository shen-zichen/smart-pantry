package com.smartpantry.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "pantry_items")
public class PantryItemEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "ingredient_id")
  private IngredientEntity ingredient;

  private double quantityInStock;
  private LocalDate boughtDate;
  private LocalDate expirationDate;
  private double lowStockThreshold;

  protected PantryItemEntity() {}

  public PantryItemEntity(
      IngredientEntity ingredient,
      double quantityInStock,
      LocalDate boughtDate,
      LocalDate expirationDate,
      double lowStockThreshold) {
    this.ingredient = ingredient;
    this.quantityInStock = quantityInStock;
    this.boughtDate = boughtDate;
    this.expirationDate = expirationDate;
    this.lowStockThreshold = lowStockThreshold;
  }

  public Long getId() {
    return id;
  }

  public IngredientEntity getIngredient() {
    return ingredient;
  }

  public void setIngredient(IngredientEntity ingredient) {
    this.ingredient = ingredient;
  }

  public double getQuantityInStock() {
    return quantityInStock;
  }

  public void setQuantityInStock(double quantityInStock) {
    this.quantityInStock = quantityInStock;
  }

  public LocalDate getBoughtDate() {
    return boughtDate;
  }

  public void setBoughtDate(LocalDate boughtDate) {
    this.boughtDate = boughtDate;
  }

  public LocalDate getExpirationDate() {
    return expirationDate;
  }

  public void setExpirationDate(LocalDate expirationDate) {
    this.expirationDate = expirationDate;
  }

  public double getLowStockThreshold() {
    return lowStockThreshold;
  }

  public void setLowStockThreshold(double lowStockThreshold) {
    this.lowStockThreshold = lowStockThreshold;
  }
}
