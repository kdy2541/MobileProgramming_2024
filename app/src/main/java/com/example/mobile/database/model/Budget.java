package com.example.mobile.database.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "budget_table")
public class Budget {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String month;
    private double totalBudget;
    private String category;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public double getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(double totalBudget) {
        this.totalBudget = totalBudget;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
