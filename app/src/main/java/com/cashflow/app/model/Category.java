package com.cashflow.app.model;

public class Category {
  public String id;
  public String name;
  public String icon;
  public String color;
  public String type; // "income" | "expense"

  public Category() {}

  public Category(String id, String name, String icon, String color, String type) {
    this.id = id;
    this.name = name;
    this.icon = icon;
    this.color = color;
    this.type = type;
  }
}
