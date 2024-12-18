package com.example.mobile;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.database.AppDatabase;
import com.example.mobile.database.model.Transaction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddTransactionActivity extends AppCompatActivity {
    private Date selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        EditText etAmount = findViewById(R.id.et_amount);
        Spinner spinnerCategory = findViewById(R.id.spinner_category);
        EditText etNote = findViewById(R.id.et_note);
        Button btnSaveTransaction = findViewById(R.id.btn_save_transaction);
        RadioGroup rgTransactionType = findViewById(R.id.rg_transaction_type);

        // 캘린더에서 전달된 날짜를 받아 기본값으로 설정
        String dateString = getIntent().getStringExtra("selectedDate");
        if (dateString != null) {
            try {
                selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // 저장 버튼 클릭 이벤트
        btnSaveTransaction.setOnClickListener(v -> {
            String amountStr = etAmount.getText().toString();
            if (amountStr.isEmpty()) {
                Toast.makeText(this, "금액을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            int amount = Integer.parseInt(amountStr);
            if (rgTransactionType.getCheckedRadioButtonId() == R.id.rb_expense) {
                amount = -amount; // 지출일 경우 음수 처리
            }


            String category = spinnerCategory.getSelectedItem() != null ? spinnerCategory.getSelectedItem().toString() : "기타";
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

                runOnUiThread(() -> {
                    Toast.makeText(this, "거래 내역이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }).start();
        });
    }
}
