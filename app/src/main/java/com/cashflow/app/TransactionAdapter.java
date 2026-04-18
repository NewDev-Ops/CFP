package com.cashflow.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cashflow.app.model.Transaction;
import com.cashflow.app.model.TransactionType;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TxViewHolder> {
  private List<Transaction> txs = new ArrayList<>();
  public void setTransactions(List<Transaction> txs) {
    this.txs = txs != null ? txs : new ArrayList<>();
    notifyDataSetChanged();
  }
  @NonNull
  @Override
  public TxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
    return new TxViewHolder(v);
  }
  @Override
  public void onBindViewHolder(@NonNull TxViewHolder holder, int position) {
    Transaction t = txs.get(position);
    holder.title.setText(t.description);
    String sign = (t.type == TransactionType.INCOME) ? "+" : (t.type == TransactionType.TRANSFER ? "⇅" : "-");
    holder.sub.setText(sign + String.format(java.util.Locale.US, "$%.2f", t.amount));
  }
  @Override
  public int getItemCount() { return txs.size(); }
  static class TxViewHolder extends RecyclerView.ViewHolder {
    TextView title;
    TextView sub;
    TxViewHolder(@NonNull View itemView) {
      super(itemView);
      title = itemView.findViewById(android.R.id.text1);
      sub = itemView.findViewById(android.R.id.text2);
    }
  }
}
