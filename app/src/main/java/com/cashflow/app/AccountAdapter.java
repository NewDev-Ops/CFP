package com.cashflow.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cashflow.app.model.Account;
import java.util.ArrayList;
import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder> {
  private List<Account> accounts = new ArrayList<>();
  public void setAccounts(List<Account> accounts) {
    this.accounts = accounts != null ? accounts : new ArrayList<>();
    notifyDataSetChanged();
  }
  @NonNull
  @Override
  public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
    return new AccountViewHolder(v);
  }
  @Override
  public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
    Account a = accounts.get(position);
    holder.title.setText(a.name);
    holder.sub.setText(String.format(java.util.Locale.US, "$%.2f", a.balance));
  }
  @Override
  public int getItemCount() { return accounts.size(); }
  static class AccountViewHolder extends RecyclerView.ViewHolder {
    TextView title;
    TextView sub;
    AccountViewHolder(@NonNull View itemView) {
      super(itemView);
      title = itemView.findViewById(android.R.id.text1);
      sub = itemView.findViewById(android.R.id.text2);
    }
  }
}
