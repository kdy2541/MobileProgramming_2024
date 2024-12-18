package com.example.mobile;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile.database.AppDatabase;
import com.example.mobile.database.dao.TransactionDAO;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StatisticsActivity extends AppCompatActivity {
    private BarChart barChart;
    private PieChart pieChart;
    private TextView tvBudgetStatus, tvSelectedMonth;
    private AppDatabase db;
    private String selectedMonth; // "YYYY-MM" 형식

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        barChart = findViewById(R.id.bar_chart_income_expense);
        pieChart = findViewById(R.id.pie_chart_category);
        tvBudgetStatus = findViewById(R.id.tv_budget_status);
        tvSelectedMonth = findViewById(R.id.tv_selected_month);
        Button btnSelectMonth = findViewById(R.id.btn_select_month);

        db = AppDatabase.getDatabase(getApplicationContext());

        // 현재 월 설정
        selectedMonth = getCurrentMonth();
        tvSelectedMonth.setText(selectedMonth);

        // 데이터 로드
        loadStatistics(selectedMonth);

        // 월 선택 버튼 클릭 이벤트
        btnSelectMonth.setOnClickListener(v -> showMonthPicker());
    }

    private void loadStatistics(String month) {
        new Thread(() -> {
            // 월별 수익/지출 데이터
            int income = db.transactionDAO().getMonthlyIncome(month);
            int expense = db.transactionDAO().getMonthlyExpense(month);

            // 카테고리별 지출 데이터
            List<TransactionDAO.CategoryExpense> categoryExpenses = db.transactionDAO().getCategoryExpenses(month);

            // SharedPreferences에서 월별 예산 가져오기
            int monthlyBudget = getSharedPreferences("BudgetPrefs", MODE_PRIVATE).getInt("monthlyBudget", 0);
            boolean isOverBudget = Math.abs(expense) > monthlyBudget;

            runOnUiThread(() -> {
                // 바 차트 설정
                List<BarEntry> barEntries = new ArrayList<>();
                barEntries.add(new BarEntry(0, income));
                barEntries.add(new BarEntry(1, Math.abs(expense)));

                BarDataSet barDataSet = new BarDataSet(barEntries, "수익/지출");
                barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                BarData barData = new BarData(barDataSet);
                barChart.setData(barData);
                barChart.invalidate();

                // 원형 차트 설정
                List<PieEntry> pieEntries = new ArrayList<>();
                for (TransactionDAO.CategoryExpense ce : categoryExpenses) {
                    pieEntries.add(new PieEntry(Math.abs(ce.amount), ce.category));
                }

                PieDataSet pieDataSet = new PieDataSet(pieEntries, "카테고리별 지출");
                pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                PieData pieData = new PieData(pieDataSet);
                pieChart.setData(pieData);
                pieChart.invalidate();

                // 예산 초과 상태 표시
                if (isOverBudget) {
                    tvBudgetStatus.setText("예산 초과! 잔액: " + (monthlyBudget + expense) + "원");
                } else {
                    tvBudgetStatus.setText("예산 내 관리 중. 잔액: " + (monthlyBudget + expense) + "원");
                }
            });
        }).start();
    }

    private void showMonthPicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedMonth = String.format(Locale.getDefault(), "%d-%02d", year, month + 1);
                    tvSelectedMonth.setText(selectedMonth);
                    loadStatistics(selectedMonth);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // DayPicker 숨기기 (안전하게 처리)
        try {
            int dayPickerId = getResources().getIdentifier("android:id/day", null, null);
            View dayPicker = datePickerDialog.getDatePicker().findViewById(dayPickerId);
            if (dayPicker != null) {
                dayPicker.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        datePickerDialog.show();
    }

    private String getCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        return new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(calendar.getTime());
    }
}
