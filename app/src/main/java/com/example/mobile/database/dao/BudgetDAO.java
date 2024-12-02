package com.example.mobile.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mobile.database.model.Budget;

@Dao
public interface BudgetDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Budget budget);

    @Query("SELECT * FROM budget_table WHERE month = :month")
    LiveData<Budget> getBudgetForMonth(String month);
}
