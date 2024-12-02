package com.example.mobile;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        findViewById(R.id.btn_add_transaction).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddTransactionActivity.class));
        });

        findViewById(R.id.btn_view_transactions).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, TransactionListActivity.class));
        });

        findViewById(R.id.btn_statistics).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, StatisticsActivity.class));
        });

        findViewById(R.id.btn_budget).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, BudgetActivity.class));
        });
    }
}