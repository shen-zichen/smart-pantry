package com.smartpantry.entity;

import com.smartpantry.model.CategoryType;
import com.smartpantry.model.UnitType;
import jakarta.persistence.*;

@Entity
@Table(name = "ingredients")
public class IngredientEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private double quantity;

  @Enumerated(EnumType.STRING)
  private UnitType unitType;

  @Enumerated(EnumType.STRING)
  private CategoryType categoryType;

  // JPA requires this — protected signals "don't call this yourself"
  protected IngredientEntity() {}

  public IngredientEntity(
      String name, double quantity, UnitType unitType, CategoryType categoryType) {
    this.name = name;
    this.quantity = quantity;
    this.unitType = unitType;
    this.categoryType = categoryType;
  }

  // Getters and setters — JPA needs setters to hydrate objects from DB
  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public double getQuantity() {
    return quantity;
  }

  public void setQuantity(double quantity) {
    this.quantity = quantity;
  }

  public UnitType getUnitType() {
    return unitType;
  }

  public void setUnitType(UnitType unitType) {
    this.unitType = unitType;
  }

  public CategoryType getCategoryType() {
    return categoryType;
  }

  public void setCategoryType(CategoryType categoryType) {
    this.categoryType = categoryType;
  }
}
