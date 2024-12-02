package com.example.mobile.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;


import com.example.mobile.database.dao.BudgetDAO;
import com.example.mobile.database.dao.TransactionDAO;
import com.example.mobile.database.model.Budget;
import com.example.mobile.database.model.Transaction;
import com.example.mobile.util.DateConverter;

@Database(entities = {Transaction.class, Budget.class}, version = 1)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract TransactionDAO transactionDAO();
    public abstract BudgetDAO budgetDAO();

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "budget_manager_db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}