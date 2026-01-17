package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * RecyclerView Adapter for displaying currency rates.
 */
public class RatesAdapter extends RecyclerView.Adapter<RatesAdapter.ViewHolder> {

    private List<CurrencyItem> currencyList;

    public RatesAdapter(List<CurrencyItem> currencyList) {
        this.currencyList = currencyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_currency, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CurrencyItem item = currencyList.get(position);
        holder.tvCurrencyCode.setText(item.getCode());
        holder.tvCurrencyRate.setText(item.getRate());
    }

    @Override
    public int getItemCount() {
        return currencyList.size();
    }

    public void updateData(List<CurrencyItem> newList) {
        this.currencyList = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCurrencyCode;
        TextView tvCurrencyRate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCurrencyCode = itemView.findViewById(R.id.tvCurrencyCode);
            tvCurrencyRate = itemView.findViewById(R.id.tvCurrencyRate);
        }
    }
}
