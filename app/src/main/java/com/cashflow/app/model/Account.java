package com.cashflow.app.model;

public class Account {
  public String id;
  public String name;
  public double balance;
  public String type; // e.g. "checking", "savings", etc.
  public String color;

  public Account() {}

  public Account(String id, String name, double balance, String type, String color) {
    this.id = id;
    this.name = name;
    this.balance = balance;
    this.type = type;
    this.color = color;
  }
}
