package com.example.mobile.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mobile.database.model.Transaction;

import java.util.List;
import java.util.Map;

@Dao
public interface TransactionDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Transaction transaction);

    @Query("SELECT * FROM transaction_table ORDER BY date DESC")
    LiveData<List<Transaction>> getAllTransactions();

    @Query("SELECT * FROM transaction_table WHERE DATE(date / 1000, 'unixepoch') = :date ORDER BY date DESC")
    List<Transaction> getTransactionsForDate(String date);


    // 월별 수익 합계
    @Query("SELECT SUM(amount) FROM transaction_table WHERE amount > 0 AND strftime('%Y-%m', date / 1000, 'unixepoch') = :month")
    int getMonthlyIncome(String month);

    // 월별 지출 합계
    @Query("SELECT SUM(amount) FROM transaction_table WHERE amount < 0 AND strftime('%Y-%m', date / 1000, 'unixepoch') = :month")
    int getMonthlyExpense(String month);

    // 카테고리별 지출 합계
    @Query("SELECT category, SUM(amount) AS amount FROM transaction_table WHERE amount < 0 AND strftime('%Y-%m', date / 1000, 'unixepoch') = :month GROUP BY category")
    List<CategoryExpense> getCategoryExpenses(String month);

    public class CategoryExpense {
        public String category;
        public int amount;
    }



    @Delete
    void delete(Transaction transaction);
}


