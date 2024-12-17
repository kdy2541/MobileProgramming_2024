package com.example.mobile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile.database.model.Transaction;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private List<Transaction> transactions;
    private OnDeleteTransactionListener deleteListener;

    public interface OnDeleteTransactionListener {
        void onDelete(Transaction transaction);
    }

    // 생성자에서 삭제 리스너를 받도록 수정
    public TransactionAdapter(List<Transaction> transactions, OnDeleteTransactionListener deleteListener) {
        this.transactions = transactions;
        this.deleteListener = deleteListener;
    }

    public TransactionAdapter(List<Transaction> transactions) {
        this(transactions, null); // 삭제 리스너를 제공하지 않으면 null로 초기화
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);

        // 데이터 바인딩
        holder.tvCategory.setText("카테고리: " + transaction.getCategory());
        holder.tvAmount.setText("금액: " + transaction.getAmount() + "원");

        // 삭제 버튼 클릭 이벤트
        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(transaction);
            }
        });
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    // ViewHolder 클래스 수정
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvAmount, btnDelete;

        ViewHolder(View view) {
            super(view);
            tvCategory = view.findViewById(R.id.tv_category);
            tvAmount = view.findViewById(R.id.tv_amount);
            btnDelete = view.findViewById(R.id.btn_delete);
        }
    }

    // 데이터 업데이트
    public void updateData(List<Transaction> newTransactions) {
        this.transactions = newTransactions;
        notifyDataSetChanged();
    }
}

