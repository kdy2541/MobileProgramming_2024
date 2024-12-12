package com.example.mobile;

import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        BarChart barChart = findViewById(R.id.bar_chart_income_expense);
        PieChart pieChart = findViewById(R.id.pie_chart_category);
        TextView tvBudgetStatus = findViewById(R.id.tv_budget_status);

        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        String currentMonth = "2024-12"; // 테스트용. 실제로는 동적으로 설정 가능.

        new Thread(() -> {
            // 월별 수익/지출 데이터
            int income = db.transactionDAO().getMonthlyIncome(currentMonth);
            int expense = db.transactionDAO().getMonthlyExpense(currentMonth);

            // 카테고리별 지출 데이터
            List<TransactionDAO.CategoryExpense> categoryExpenses = db.transactionDAO().getCategoryExpenses(currentMonth);

            // 예산 상태 (예: 월별 예산이 100000인 경우)
            int monthlyBudget = 100000;
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
}
