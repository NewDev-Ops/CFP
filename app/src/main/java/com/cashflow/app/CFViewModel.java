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
    // Load data from SharedPreferences (or initialize)
    DataRepository.loadFromPrefs(application);
    // Initialize with repository data
    refreshData();
  }

  private void refreshData() {
    accounts.setValue(new ArrayList<>(DataRepository.accounts));
    transactions.setValue(new ArrayList<>(DataRepository.transactions));
    budgets.setValue(new ArrayList<>(DataRepository.budgets));
    analytics.setValue(DataRepository.calculateAnalytics(transactions.getValue()));
  }

  public LiveData<List<Account>> getAccounts() { return accounts; }
  public LiveData<List<Transaction>> getTransactions() { return transactions; }
  public LiveData<List<Budget>> getBudgets() { return budgets; }
  public LiveData<Analytics> getAnalytics() { return analytics; }

  public void addTransaction(Transaction t) {
    DataRepository.applyTransaction(t);
    // Persist changes
    DataRepository.saveToPrefs(getApplication());
    // Refresh UI
    refreshData();
  }

  public void deleteTransaction(String id) {
    Transaction toDel = null;
    List<Transaction> list = transactions.getValue();
    if (list != null) {
      for (Transaction tr : list) if (tr.id.equals(id)) { toDel = tr; break; }
    }
    if (toDel == null) return;
    // Reverse the effects
    DataRepository.reverseTransaction(toDel);
    // Persist changes
    DataRepository.saveToPrefs(getApplication());
    // Refresh UI
    refreshData();
  }
}
