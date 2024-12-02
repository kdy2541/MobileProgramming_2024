package com.example.mobile.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mobile.database.model.Transaction;

import java.util.List;

@Dao
public interface TransactionDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Transaction transaction);

    @Query("SELECT * FROM transaction_table ORDER BY date DESC")
    LiveData<List<Transaction>> getAllTransactions();

    @Delete
    void delete(Transaction transaction);
}
