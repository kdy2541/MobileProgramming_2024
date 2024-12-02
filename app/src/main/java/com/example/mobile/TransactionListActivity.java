package com.example.mobile;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.database.AppDatabase;
import com.example.mobile.database.model.Transaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_list);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_transactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());

        // LiveData 관찰 추가
        db.transactionDAO().getAllTransactions().observe(this, transactions -> {
            if (transactions != null) {
                recyclerView.setAdapter(new TransactionAdapter(transactions));
            } else {
                recyclerView.setAdapter(new TransactionAdapter(new ArrayList<>()));
            }
        });
    }
}
