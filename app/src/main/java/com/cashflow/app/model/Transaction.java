package com.cashflow.app.model;

import com.cashflow.app.model.TransactionType;
import com.cashflow.app.model.Frequency;

public class Transaction {
  public String id;
  public double amount;
  public TransactionType type;
  public String categoryId;
  public String fromAccountId;
  public String toAccountId;
  public String description;
  public String date;
  public boolean isRecurring;
  public Frequency frequency;

  public Transaction() {}

  public Transaction(String id, double amount, TransactionType type, String categoryId,
                     String fromAccountId, String toAccountId, String description, String date,
                     boolean isRecurring, Frequency frequency) {
    this.id = id;
    this.amount = amount;
    this.type = type;
    this.categoryId = categoryId;
    this.fromAccountId = fromAccountId;
    this.toAccountId = toAccountId;
    this.description = description;
    this.date = date;
    this.isRecurring = isRecurring;
    this.frequency = frequency;
  }
}
