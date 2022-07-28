package com.doanhung.client2;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baseproject.Item;

import java.util.List;


public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private List<Item> mItemList;

    public ItemAdapter(List<Item> mItemList) {
        this.mItemList = mItemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ItemViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = mItemList.get(position);
        if (item != null) {
            holder.tvName.setText("Name: " + item.mName);
            holder.tvPrice.setText("Price: " + item.mPrice);
        }
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    public void setData(List<Item> itemList) {
        mItemList = itemList;
        notifyItemInserted(mItemList.size() - 1);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final TextView tvPrice;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}
