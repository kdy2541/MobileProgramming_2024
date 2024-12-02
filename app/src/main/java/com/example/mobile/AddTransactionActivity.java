package com.example.mobile;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.database.AppDatabase;
import com.example.mobile.database.model.Transaction;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddTransactionActivity extends AppCompatActivity {
    private Date selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        EditText etAmount = findViewById(R.id.et_amount);
        Spinner spinnerCategory = findViewById(R.id.spinner_category);
        EditText etNote = findViewById(R.id.et_note);
        Button btnSelectDate = findViewById(R.id.btn_select_date);
        Button btnSaveTransaction = findViewById(R.id.btn_save_transaction);

        btnSelectDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                selectedDate = calendar.getTime();
                Toast.makeText(this, "선택한 날짜: " + selectedDate.toString(), Toast.LENGTH_SHORT).show();
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnSaveTransaction.setOnClickListener(v -> {
            String amountStr = etAmount.getText().toString();
            if (amountStr.isEmpty()) {
                Toast.makeText(this, "금액을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            int amount = Integer.parseInt(amountStr);
            String category = spinnerCategory.getSelectedItem().toString();
            String note = etNote.getText().toString();

            if (selectedDate == null) {
                Toast.makeText(this, "날짜를 선택해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            Transaction transaction = new Transaction();
            transaction.setAmount(amount);
            transaction.setCategory(category);
            transaction.setNote(note);
            transaction.setDate(selectedDate);

            new Thread(() -> {
                AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
                db.transactionDAO().insert(transaction);

                Log.d("DB_DEBUG", "Transaction Saved: " + transaction.getCategory() + ", " + transaction.getAmount());

                runOnUiThread(() -> {
                    Toast.makeText(this, "수입/지출이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }).start();
        });
    }
}
