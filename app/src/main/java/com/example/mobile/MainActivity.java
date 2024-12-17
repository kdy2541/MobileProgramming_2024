package com.example.mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.database.AppDatabase;
import com.example.mobile.database.model.Transaction;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvIncome, tvExpense, tvTotal, tvBudgetWarning, tvMonthlyBudgetWarning;
    private TransactionAdapter adapter;
    private AppDatabase db;
    private String selectedDate;
    private int dailyBudget = 0;
    private int monthlyBudget = 0;
    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences("BudgetPrefs", MODE_PRIVATE);
        dailyBudget = preferences.getInt("dailyBudget", 0);
        monthlyBudget = preferences.getInt("monthlyBudget", 0);



        // 합계 표시 텍스트뷰 초기화
        tvIncome = findViewById(R.id.tv_income);
        tvExpense = findViewById(R.id.tv_expense);
        tvTotal = findViewById(R.id.tv_total);
        tvBudgetWarning = findViewById(R.id.tv_budget_warning);
        tvMonthlyBudgetWarning = findViewById(R.id.tv_monthly_budget_warning);

        Button btnSetBudget = findViewById(R.id.btn_set_budget);
        Button btnDeleteAll = findViewById(R.id.btn_delete_all);

        // RecyclerView 설정
        RecyclerView recyclerView = findViewById(R.id.recycler_view_transactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        db = AppDatabase.getDatabase(getApplicationContext());

        adapter = new TransactionAdapter(new ArrayList<>(), transaction -> {
            new Thread(() -> {
                db.transactionDAO().deleteTransaction(transaction);
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "거래가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    loadTransactionsForDate(selectedDate);
                });
            }).start();
        });
        recyclerView.setAdapter(adapter);




        btnSetBudget.setOnClickListener(v -> showBudgetDialog());
        btnDeleteAll.setOnClickListener(v -> deleteAllTransactions());


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

        // 초기 날짜 설정 (앱 시작 시 오늘 날짜)
        CalendarView calendar = findViewById(R.id.calendar_view);
        calendar.setDate(System.currentTimeMillis(), false, true);
        selectedDate = getCurrentDate();
        loadTransactionsForDate(selectedDate);


        findViewById(R.id.btn_view_transactions).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TransactionListActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btn_statistics).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
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

    // 현재 날짜를 가져오는 메서드
    private String getCurrentDate() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int year = calendar.get(java.util.Calendar.YEAR);
        int month = calendar.get(java.util.Calendar.MONTH) + 1;
        int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);
        return String.format("%d-%02d-%02d", year, month, day);
    }


    private void showBudgetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("예산 설정");

        // 레이아웃 설정
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        EditText etDailyBudget = new EditText(this);
        etDailyBudget.setHint("일별 예산");
        etDailyBudget.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(etDailyBudget);

        EditText etMonthlyBudget = new EditText(this);
        etMonthlyBudget.setHint("월별 예산");
        etMonthlyBudget.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(etMonthlyBudget);

        builder.setView(layout);

        // 저장 버튼 클릭 시
        builder.setPositiveButton("저장", (dialog, which) -> {
            String dailyBudgetInput = etDailyBudget.getText().toString();
            String monthlyBudgetInput = etMonthlyBudget.getText().toString();

            // 입력값 검증: 비어있으면 기본값 할당
            dailyBudget = dailyBudgetInput.isEmpty() ? 0 : Integer.parseInt(dailyBudgetInput);
            monthlyBudget = monthlyBudgetInput.isEmpty() ? 0 : Integer.parseInt(monthlyBudgetInput);

            // SharedPreferences에 저장
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("dailyBudget", dailyBudget);
            editor.putInt("monthlyBudget", monthlyBudget);
            editor.apply();

            tvBudgetWarning.setVisibility(View.GONE);
            tvMonthlyBudgetWarning.setVisibility(View.GONE);
        });

        builder.setNegativeButton("취소", (dialog, which) -> dialog.dismiss());
        builder.show();
    }



    private void loadTransactionsForDate(String date) {
        Log.d("LOAD_TRANSACTIONS", "Selected Date: " + date);
        new Thread(() -> {
            List<Transaction> transactions = db.transactionDAO().getTransactionsForDate(date);
            int totalMonthlyExpense = db.transactionDAO().getMonthlyExpense();
            Log.d("LOAD_TRANSACTIONS", "Transactions count: " + transactions.size());

            int income = 0, expense = 0;

            for (Transaction t : transactions) {
                if (t.getAmount() > 0) {
                    income += t.getAmount();
                } else {
                    expense += t.getAmount();
                }
            }

            int total = income + expense;

            int finalIncome = income;
            int finalExpense = Math.abs(expense);
            runOnUiThread(() -> {
                tvIncome.setText("수익: " + finalIncome);
                tvExpense.setText("지출: " + Math.abs(finalExpense));
                tvTotal.setText("총 금액: " + total);
                if (finalExpense > dailyBudget) {
                    tvBudgetWarning.setVisibility(View.VISIBLE);
                    tvBudgetWarning.setText("일별 예산 초과!");
                }else {
                    tvBudgetWarning.setVisibility(View.GONE);
                }
                if (Math.abs(totalMonthlyExpense) > monthlyBudget) {
                    tvMonthlyBudgetWarning.setVisibility(View.VISIBLE);
                    tvMonthlyBudgetWarning.setText("월별 예산 초과!");
                } else {
                    tvMonthlyBudgetWarning.setVisibility(View.GONE);
                }
                adapter.updateData(transactions);

            });
        }).start();
    }

    private void deleteTransaction(Transaction transaction) {
        new Thread(() -> {
            db.transactionDAO().deleteTransaction(transaction);
            runOnUiThread(() -> {
                Toast.makeText(MainActivity.this, "거래가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                loadTransactionsForDate(selectedDate); // 삭제 후 새로고침
            });
        }).start();
    }

    private void deleteAllTransactions() {
        new Thread(() -> {
            db.transactionDAO().deleteAllTransactions();
            runOnUiThread(() -> {
                Toast.makeText(MainActivity.this, "전체 거래 내역이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                loadTransactionsForDate(selectedDate); // 삭제 후 새로고침
            });
        }).start();
    }




}
