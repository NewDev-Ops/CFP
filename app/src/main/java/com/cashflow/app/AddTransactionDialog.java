package com.cashflow.app;

import android.app.Dialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import com.cashflow.app.model.Account;
import com.cashflow.app.model.Category;
import com.cashflow.app.model.Transaction;
import com.cashflow.app.model.TransactionType;
import com.cashflow.app.model.Frequency;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddTransactionDialog extends DialogFragment {
  interface OnTransactionAdded {
    void onTransactionAdded(Transaction t);
  }
  private OnTransactionAdded listener;
  public void setListener(OnTransactionAdded listener) { this.listener = listener; }

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_transaction, null);
    Spinner spType = v.findViewById(R.id.spinner_type);
    Spinner spFrom = v.findViewById(R.id.spinner_from);
    Spinner spTo = v.findViewById(R.id.spinner_to);
    Spinner spCat = v.findViewById(R.id.spinner_category);
    final EditText etAmount = v.findViewById(R.id.edit_amount);
    EditText etDesc = v.findViewById(R.id.edit_description);

    // Type choices
    TransactionType[] types = TransactionType.values();
    ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, new String[]{"INCOME","EXPENSE","TRANSFER"});
    typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spType.setAdapter(typeAdapter);

    // Accounts
    List<Account> accounts = new ArrayList<>(com.cashflow.app.DataRepository.accounts);
    ArrayAdapter<String> fromAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, toStringNames(accounts));
    fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spFrom.setAdapter(fromAdapter);
    spTo.setAdapter(fromAdapter);

    // Categories
    List<String> catNames = new ArrayList<>();
    for (Category c : com.cashflow.app.DataRepository.categories) {
      if (c.type.equals("expense")) catNames.add(c.name);
    }
    ArrayAdapter<String> catAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, catNames);
    catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spCat.setAdapter(catAdapter);

    spType.setOnItemSelectedListener(new android.view.AdapterView.OnItemSelectedListener() {
      @Override public void onItemSelected(androidx.adapterview.AdapterView<?> parent, View view, int position, long id) {
        String t = (String) spType.getSelectedItem();
        boolean isTransfer = "TRANSFER".equals(t);
        spTo.setEnabled(isTransfer);
      }
      @Override public void onNothingSelected(android.view.AdapterView<?> parent) {}
    });

    return new AlertDialog.Builder(getActivity())
      .setTitle("Add Transaction")
      .setView(v)
      .setPositiveButton("Add", (dialog, which) -> {
        String amtStr = etAmount.getText().toString();
        if (amtStr.isEmpty()) return;
        double amount = Double.parseDouble(amtStr);
        String description = etDesc.getText().toString();
        int typeIndex = spType.getSelectedItemPosition();
        TransactionType type = types[Math.max(0, Math.min(typeIndex, types.length-1))];
        String fromId = accounts.get(spFrom.getSelectedItemPosition()).id;
        String toId = null;
        if ("TRANSFER".equals(spType.getSelectedItem())) {
          toId = accounts.get(spTo.getSelectedItemPosition()).id;
        }
        String categoryId = null;
        if (type != TransactionType.TRANSFER) {
          String catName = (String) spCat.getSelectedItem();
          for (Category c : com.cashflow.app.DataRepository.categories) {
            if (c.name.equals(catName)) { categoryId = c.id; break; }
          }
        }
        Transaction t = new Transaction();
        t.amount = amount;
        t.description = description;
        t.type = type;
        t.categoryId = categoryId;
        t.fromAccountId = fromId;
        t.toAccountId = toId;
        t.date = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(new java.util.Date());
        if (listener != null) listener.onTransactionAdded(t);
      })
      .setNegativeButton("Cancel", null)
      .create();
  }
  private List<String> toStringNames(List<Account> accounts) {
    List<String> names = new ArrayList<>();
    for (Account a : accounts) names.add(a.name);
    return names;
  }
}
