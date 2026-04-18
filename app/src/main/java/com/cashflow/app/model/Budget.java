package com.cashflow.app.model;

public class Budget {
  public String id;
  public String categoryId;
  public double limit;
  public double spent;
  public String period; // e.g. "2024-06"

  public Budget() {}

  public Budget(String id, String categoryId, double limit, double spent, String period) {
    this.id = id;
    this.categoryId = categoryId;
    this.limit = limit;
    this.spent = spent;
    this.period = period;
  }
}
