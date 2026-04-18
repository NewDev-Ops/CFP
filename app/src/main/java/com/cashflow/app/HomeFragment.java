package com.cashflow.app;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.cashflow.app.model.Account;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.cashflow.app.AddTransactionDialog;
import com.cashflow.app.model.Transaction;
import android.content.Context;
import java.util.List;

public class HomeFragment extends Fragment {
  private CFViewModel viewModel;
  private RecyclerView rvAccounts;
  private AccountAdapter adapter;
  public HomeFragment() {}
  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_home, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    rvAccounts = view.findViewById(R.id.rv_accounts);
    rvAccounts.setLayoutManager(new LinearLayoutManager(getContext()));
    adapter = new AccountAdapter();
    rvAccounts.setAdapter(adapter);

    viewModel = new ViewModelProvider(requireActivity()).get(CFViewModel.class);
    viewModel.getAccounts().observe(getViewLifecycleOwner(), new Observer<List<Account>>() {
      @Override
      public void onChanged(List<Account> accounts) {
        adapter.setAccounts(accounts);
      }
    });

    // FAB to add a transaction
    FloatingActionButton fab = view.findViewById(R.id.fab_add_tx);
    if (fab != null) {
      fab.setOnClickListener(v -> {
        final AddTransactionDialog dialog = new AddTransactionDialog();
        dialog.setListener(new AddTransactionDialog.OnTransactionAdded() {
          @Override
          public void onTransactionAdded(Transaction t) {
            if (t != null) {
              viewModel.addTransaction(t);
            }
          }
        });
        dialog.show(getChildFragmentManager(), "add_tx");
      });
    }
  }
}
