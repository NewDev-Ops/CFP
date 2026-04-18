package com.cashflow.app;

import com.cashflow.app.model.Account;
import com.cashflow.app.model.Budget;
import com.cashflow.app.model.Category;
import com.cashflow.app.model.Transaction;
import com.cashflow.app.model.TransactionType;
import com.cashflow.app.model.Frequency;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DataRepository {
  public static List<Account> accounts = new ArrayList<>();
  public static List<Category> categories = new ArrayList<>();
  public static List<Budget> budgets = new ArrayList<>();
  public static List<Transaction> transactions = new ArrayList<>();

  static {
    initData();
  }

  public static void initData() {
    // Categories
    categories.clear();
    categories.add(new Category("1", "Food", "Utensils", "#FF6B6B", "expense"));
    categories.add(new Category("2", "Transport", "Car", "#4DABF7", "expense"));
    categories.add(new Category("3", "Entertainment", "Film", "#FCC419", "expense"));
    categories.add(new Category("4", "Shopping", "ShoppingBag", "#94D82D", "expense"));
    categories.add(new Category("5", "Housing", "Home", "#FF922B", "expense"));
    categories.add(new Category("6", "Salary", "ArrowUpCircle", "#51CF66", "income"));

    // Accounts
    accounts.clear();
    accounts.add(new Account("acc1", "Daily Checking", 2450.0, "checking", "#386B3F"));
    accounts.add(new Account("acc2", "Growth Savings", 12000.0, "savings", "#52634F"));
    accounts.add(new Account("acc3", "Reserve Fund", 4500.0, "investment", "#747970"));

    // Budgets
    budgets.clear();
    budgets.add(new Budget("b1", "1", 600.0, 450.0, "2024-06"));
    budgets.add(new Budget("b2", "4", 300.0, 120.0, "2024-06"));

    // Transactions
    transactions.clear();
  }

  public static class Result {
    public List<Account> nextAccounts;
    public List<Budget> nextBudgets;
    public Transaction newTransaction;
  }

  public static Result applyTransaction(Transaction tx) {
    Result res = new Result();
    // Create a new transaction with id
    String id = java.util.UUID.randomUUID().toString();
    Transaction newTx = new Transaction(id, tx.amount, tx.type, tx.categoryId, tx.fromAccountId,
      tx.toAccountId, tx.description, tx.date != null ? tx.date : new Date().toString(), tx.isRecurring, tx.frequency);
    // Update accounts
    List<Account> nextAccounts = new ArrayList<>();
    for (Account acc : accounts) {
      Account copy = new Account(acc.id, acc.name, acc.balance, acc.type, acc.color);
      if (acc.id.equals(tx.fromAccountId)) {
        int multiplier = (tx.type == TransactionType.INCOME) ? 1 : -1;
        copy.balance = acc.balance + (tx.amount * multiplier);
      }
      if (tx.type == TransactionType.TRANSFER && acc.id.equals(tx.toAccountId)) {
        copy.balance = acc.balance + tx.amount;
      }
      nextAccounts.add(copy);
    }

    // Update budgets
    List<Budget> nextBudgets = new ArrayList<>();
    for (Budget bud : budgets) {
      Budget copy = new Budget(bud.id, bud.categoryId, bud.limit, bud.spent, bud.period);
      if (tx.type == TransactionType.EXPENSE && bud.categoryId.equals(tx.categoryId)) {
        copy.spent = bud.spent + tx.amount;
      }
      nextBudgets.add(copy);
    }

    // Persist
    accounts = nextAccounts;
    budgets = nextBudgets;
    transactions.add(0, newTx);
    res.nextAccounts = nextAccounts;
    res.nextBudgets = nextBudgets;
    res.newTransaction = newTx;
    return res;
  }

  public static class Analytics {
    public java.util.Map<String, Double> expensesByCategory = new java.util.HashMap<>();
    public java.util.Map<String, Double> monthlySpending = new java.util.HashMap<>();
    public java.util.Map<String, Double> monthlyIncome = new java.util.HashMap<>();
  }

  public static Analytics calculateAnalytics(java.util.List<Transaction> txs) {
    Analytics a = new Analytics();
    for (Transaction t : txs) {
      String month = t.date.substring(0, 7);
      if (t.type == TransactionType.EXPENSE && t.categoryId != null) {
        a.expensesByCategory.put(t.categoryId, (a.expensesByCategory.getOrDefault(t.categoryId, 0.0)) + t.amount);
        a.monthlySpending.put(month, (a.monthlySpending.getOrDefault(month, 0.0)) + t.amount);
      } else if (t.type == TransactionType.INCOME) {
        a.monthlyIncome.put(month, (a.monthlyIncome.getOrDefault(month, 0.0)) + t.amount);
      }
    }
    return a;
  }
}
