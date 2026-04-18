package com.cashflow.app;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.cashflow.app.DataRepository.Analytics;

public class AnalyticsFragment extends Fragment {
  private CFViewModel viewModel;
  private TextView tvAnalytics;
  public AnalyticsFragment() {}
  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_analytics, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    tvAnalytics = view.findViewById(R.id.tv_analytics);
    viewModel = new ViewModelProvider(requireActivity()).get(CFViewModel.class);
    viewModel.getAnalytics().observe(getViewLifecycleOwner(), new Observer<Analytics>() {
      @Override
      public void onChanged(Analytics analytics) {
        if (analytics != null) {
          StringBuilder sb = new StringBuilder();
          sb.append("Expenses by Category:\n");
          if (analytics.expensesByCategory != null) {
            for (java.util.Map.Entry<String, Double> e : analytics.expensesByCategory.entrySet()) {
              sb.append(e.getKey()).append(": ").append(e.getValue()).append("\n");
            }
          }
          sb.append("\nMonthly Income:\n");
          if (analytics.monthlyIncome != null) {
            for (java.util.Map.Entry<String, Double> e : analytics.monthlyIncome.entrySet()) {
              sb.append(e.getKey()).append(": ").append(e.getValue()).append("\n");
            }
          }
          sb.append("\nMonthly Spending:\n");
          if (analytics.monthlySpending != null) {
            for (java.util.Map.Entry<String, Double> e : analytics.monthlySpending.entrySet()) {
              sb.append(e.getKey()).append(": ").append(e.getValue()).append("\n");
            }
          }
          tvAnalytics.setText(sb.toString());
        }
      }
    });
  }
}
