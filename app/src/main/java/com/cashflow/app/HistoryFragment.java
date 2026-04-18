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
import java.util.List;

import com.cashflow.app.model.Transaction;

public class HistoryFragment extends Fragment {
  private CFViewModel viewModel;
  private RecyclerView rvTx;
  private TransactionAdapter txAdapter;
  public HistoryFragment() {}
  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_history, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    rvTx = view.findViewById(R.id.rv_transactions);
    rvTx.setLayoutManager(new LinearLayoutManager(getContext()));
    txAdapter = new TransactionAdapter();
    rvTx.setAdapter(txAdapter);
    viewModel = new ViewModelProvider(requireActivity()).get(CFViewModel.class);
    viewModel.getTransactions().observe(getViewLifecycleOwner(), new Observer<List<Transaction>>() {
      @Override
      public void onChanged(List<Transaction> transactions) {
        txAdapter.setTransactions(transactions);
      }
    });
  }
}
