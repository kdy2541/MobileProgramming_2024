package com.example.mobile;

import android.os.Bundle;
import android.widget.Toast;

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
        String selectedDate = getIntent().getStringExtra("selectedDate");

        // LiveData 관찰 추가
        new Thread(() -> {
            List<Transaction> transactions;
            if (selectedDate != null) {
                transactions = db.transactionDAO().getTransactionsForDate(selectedDate);
            } else {
                transactions = db.transactionDAO().getAllTransactions().getValue();
            }

            runOnUiThread(() -> {
                recyclerView.setAdapter(new TransactionAdapter(transactions, transaction -> {
                    // 삭제 기능 구현
                    new Thread(() -> {
                        db.transactionDAO().deleteTransaction(transaction);
                        runOnUiThread(() -> {
                            Toast.makeText(this, "거래가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            recreate(); // 새로고침
                        });
                    }).start();
                }));
            });
        }).start();
    }
}
