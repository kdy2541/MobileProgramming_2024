package com.example.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.database.AppDatabase;
import com.example.mobile.database.model.Transaction;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvIncome, tvExpense, tvTotal;
    private TransactionAdapter adapter;
    private AppDatabase db;
    private String selectedDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // RecyclerView 설정
        RecyclerView recyclerView = findViewById(R.id.recycler_view_transactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransactionAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // 합계 표시 텍스트뷰 초기화
        tvIncome = findViewById(R.id.tv_income);
        tvExpense = findViewById(R.id.tv_expense);
        tvTotal = findViewById(R.id.tv_total);

        // Database 연결
        db = AppDatabase.getDatabase(getApplicationContext());

        // CalendarView 설정
        CalendarView calendarView = findViewById(R.id.calendar_view);
        if (calendarView != null) {
            calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
                selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth; // 멤버 변수에 할당
                Log.d("CalendarView", "Date Selected: " + selectedDate);

                // 선택된 날짜의 거래 내역 로드
                loadTransactionsForDate(selectedDate);
            });
        }

        findViewById(R.id.btn_view_transactions).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TransactionListActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btn_statistics).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btn_budget).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BudgetActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btn_add_transaction).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTransactionActivity.class);
            if (selectedDate != null) {
                intent.putExtra("selectedDate", selectedDate); // 선택된 날짜 전달
            }
            startActivity(intent);
        });
    }

    private void loadTransactionsForDate(String date) {
        new Thread(() -> {
            List<Transaction> transactions = db.transactionDAO().getTransactionsForDate(date);

            int income = 0, expense = 0;



            if (transactions == null || transactions.isEmpty()) {
                Log.d("DB_DEBUG", "No transactions found for date: " + date);
            } else {
                for (Transaction t : transactions) {
                    Log.d("DB_DEBUG", "Transaction: " + t.getCategory() + ", Amount: " + t.getAmount());
                }
            }


            for (Transaction t : transactions) {
                if (t.getAmount() > 0) {
                    income += t.getAmount();
                } else {
                    expense += t.getAmount();
                }
            }

            int total = income + expense;

            int finalIncome = income;
            int finalExpense = expense;
            runOnUiThread(() -> {
                tvIncome.setText("수익: " + finalIncome);
                tvExpense.setText("지출: " + Math.abs(finalExpense));
                tvTotal.setText("총 금액: " + total);
                adapter.updateData(transactions);
            });
        }).start();
    }
}
