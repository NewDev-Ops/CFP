package com.cashflow.app;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.cashflow.app.model.Account;
import com.cashflow.app.model.Budget;
import com.cashflow.app.model.Transaction;
import com.cashflow.app.model.TransactionType;
import com.cashflow.app.model.Frequency;
import com.cashflow.app.DataRepository;
import com.cashflow.app.DataRepository.Analytics;

import java.util.ArrayList;
import java.util.List;

public class CFViewModel extends AndroidViewModel {
  private final MutableLiveData<List<Account>> accounts = new MutableLiveData<>();
  private final MutableLiveData<List<Transaction>> transactions = new MutableLiveData<>();
  private final MutableLiveData<List<Budget>> budgets = new MutableLiveData<>();
  private final MutableLiveData<Analytics> analytics = new MutableLiveData<>();

  public CFViewModel(@NonNull Application application) {
    super(application);
    // Initialize with repository data
    accounts.setValue(new ArrayList<>(DataRepository.accounts));
    transactions.setValue(new ArrayList<>(DataRepository.transactions));
    budgets.setValue(new ArrayList<>(DataRepository.budgets));
    analytics.setValue(DataRepository.calculateAnalytics(transactions.getValue() != null ? transactions.getValue() : new ArrayList<>()));
  }

  public LiveData<List<Account>> getAccounts() { return accounts; }
  public LiveData<List<Transaction>> getTransactions() { return transactions; }
  public LiveData<List<Budget>> getBudgets() { return budgets; }
  public LiveData<Analytics> getAnalytics() { return analytics; }

  public void addTransaction(Transaction t) {
    DataRepository.applyTransaction(t);
    // Refresh all exposed data
    accounts.setValue(new ArrayList<>(DataRepository.accounts));
    budgets.setValue(new ArrayList<>(DataRepository.budgets));
    List<Transaction> txs = new ArrayList<>(DataRepository.transactions);
    // Prepend to list for recency
    txs.add(0, t);
    transactions.setValue(txs);
    analytics.setValue(DataRepository.calculateAnalytics(txs));
  }

  public void deleteTransaction(String id) {
    Transaction toDel = null;
    List<Transaction> list = transactions.getValue();
    if (list != null) {
      for (Transaction tr : list) if (tr.id.equals(id)) { toDel = tr; break; }
    }
    if (toDel == null) return;
    // Reverse the effects
    DataRepositoryReverse.reverseTransaction(toDel);
    // Refresh
    accounts.setValue(new ArrayList<>(DataRepository.accounts));
    budgets.setValue(new ArrayList<>(DataRepository.budgets));
    List<Transaction> txs = new ArrayList<>(DataRepository.transactions);
    txs.remove(toDel);
    transactions.setValue(txs);
    analytics.setValue(DataRepository.calculateAnalytics(txs));
  }
}

class DataRepositoryReverse {
  static void reverseTransaction(Transaction tx) {
    // Reverse accounts
    for (com.cashflow.app.model.Account acc : DataRepository.accounts) {
      if (acc.id.equals(tx.fromAccountId)) {
        int multiplier = (tx.type == TransactionType.INCOME) ? -1 : 1;
        acc.balance += tx.amount * multiplier;
      }
      if (tx.type == TransactionType.TRANSFER && acc.id.equals(tx.toAccountId)) {
        acc.balance -= tx.amount;
      }
    }
    // Reverse budgets
    for (Budget bud : DataRepository.budgets) {
      if (tx.type == TransactionType.EXPENSE && bud.categoryId.equals(tx.categoryId)) {
        bud.spent -= tx.amount;
        if (bud.spent < 0) bud.spent = 0;
      }
    }
    // Remove from repository transactions if exists
    for (int i = 0; i < DataRepository.transactions.size(); i++) {
      if (DataRepository.transactions.get(i).id.equals(tx.id)) {
        DataRepository.transactions.remove(i);
        break;
      }
    }
  }
}
