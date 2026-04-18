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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.content.SharedPreferences;

public class DataRepository {
  public static List<Account> accounts = new ArrayList<>();
  public static List<Category> categories = new ArrayList<>();
  public static List<Budget> budgets = new ArrayList<>();
  public static List<Transaction> transactions = new ArrayList<>();
  private static final String PREFS_NAME = "cashflow_prefs";

  public static void loadFromPrefs(Context ctx) {
    try {
      SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
      String accJson = prefs.getString("accounts", null);
      String budJson = prefs.getString("budgets", null);
      String txJson = prefs.getString("transactions", null);
      if (accJson == null && budJson == null && txJson == null) {
        initData();
        saveToPrefs(ctx);
        return;
      }
      // accounts
      if (accJson != null) {
        accounts.clear();
        JSONArray arr = new JSONArray(accJson);
        for (int i = 0; i < arr.length(); i++) {
          JSONObject o = arr.getJSONObject(i);
          Account a = new Account(o.getString("id"), o.getString("name"), o.getDouble("balance"), o.getString("type"), o.getString("color"));
          accounts.add(a);
        }
      }
      // budgets
      if (budJson != null) {
        budgets.clear();
        JSONArray arr = new JSONArray(budJson);
        for (int i = 0; i < arr.length(); i++) {
          JSONObject o = arr.getJSONObject(i);
          Budget b = new Budget(o.getString("id"), o.getString("categoryId"), o.getDouble("limit"), o.getDouble("spent"), o.getString("period"));
          budgets.add(b);
        }
      }
      // transactions
      if (txJson != null) {
        transactions.clear();
        JSONArray arr = new JSONArray(txJson);
        for (int i = 0; i < arr.length(); i++) {
          JSONObject o = arr.getJSONObject(i);
          Transaction t = new Transaction(
            o.getString("id"),
            o.getDouble("amount"),
            parseType(o.optString("type", "expense")),
            o.has("categoryId") ? o.optString("categoryId") : null,
            o.optString("fromAccountId"),
            o.has("toAccountId") ? o.optString("toAccountId") : null,
            o.optString("description"),
            o.optString("date"),
            o.optBoolean("isRecurring", false),
            o.has("frequency") && !o.isNull("frequency") ? Frequency.valueOf(o.getString("frequency").toUpperCase()) : null
          );
          transactions.add(t);
        }
      }
    } catch (JSONException e) {
      initData();
    }
  }

  public static void saveToPrefs(Context ctx) {
    SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    SharedPreferences.Editor ed = prefs.edit();
    // accounts
    JSONArray accArr = new JSONArray();
    for (Account a : accounts) {
      JSONObject o = new JSONObject();
      try {
        o.put("id", a.id);
        o.put("name", a.name);
        o.put("balance", a.balance);
        o.put("type", a.type);
        o.put("color", a.color);
      } catch (JSONException ignored) {}
      accArr.put(o);
    }
    ed.putString("accounts", accArr.toString());
    // budgets
    JSONArray budArr = new JSONArray();
    for (Budget b : budgets) {
      JSONObject o = new JSONObject();
      try {
        o.put("id", b.id);
        o.put("categoryId", b.categoryId);
        o.put("limit", b.limit);
        o.put("spent", b.spent);
        o.put("period", b.period);
      } catch (JSONException ignored) {}
      budArr.put(o);
    }
    ed.putString("budgets", budArr.toString());
    // transactions
    JSONArray txArr = new JSONArray();
    for (Transaction t : transactions) {
      JSONObject o = new JSONObject();
      try {
        o.put("id", t.id);
        o.put("amount", t.amount);
        o.put("type", t.type == null ? "expense" : t.type.name().toLowerCase());
        if (t.categoryId != null) o.put("categoryId", t.categoryId);
        o.put("fromAccountId", t.fromAccountId);
        if (t.toAccountId != null) o.put("toAccountId", t.toAccountId);
        o.put("description", t.description);
        o.put("date", t.date);
        o.put("isRecurring", t.isRecurring);
        if (t.frequency != null) o.put("frequency", t.frequency.name().toLowerCase());
      } catch (JSONException ignored) {}
      txArr.put(o);
    }
    ed.putString("transactions", txArr.toString());
    ed.apply();
  }

  public static void reverseTransaction(Transaction tx) {
    // Reverse accounts
    for (Account acc : accounts) {
      if (acc.id.equals(tx.fromAccountId)) {
        int multiplier = (tx.type == TransactionType.INCOME) ? -1 : 1;
        acc.balance += tx.amount * multiplier;
      }
      if (tx.type == TransactionType.TRANSFER && acc.id.equals(tx.toAccountId)) {
        acc.balance -= tx.amount;
      }
    }
    // Reverse budgets
    for (Budget bud : budgets) {
      if (tx.type == TransactionType.EXPENSE && bud.categoryId != null && bud.categoryId.equals(tx.categoryId)) {
        bud.spent = Math.max(0, bud.spent - tx.amount);
      }
    }
    // Remove from transactions
    for (int i = 0; i < transactions.size(); i++) {
      if (transactions.get(i).id.equals(tx.id)) {
        transactions.remove(i);
        break;
      }
    }
  }

  private static TransactionType parseType(String s) {
    if (s == null) return TransactionType.EXPENSE;
    switch (s.toLowerCase()) {
      case "income": return TransactionType.INCOME;
      case "transfer": return TransactionType.TRANSFER;
      default: return TransactionType.EXPENSE;
    }
  }

  static {
    // data loaded via loadFromPrefs() at runtime
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
