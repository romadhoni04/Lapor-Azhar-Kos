package com.azhar.reportapps.ui.report;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.azhar.reportapps.R;
import com.azhar.reportapps.model.ModelDatabase;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    List<ModelDatabase> modelDatabase;
    Context mContext;
    HistoryAdapterCallback mAdapterCallback;

    public HistoryAdapter(Context context, List<ModelDatabase> modelDatabaseList,
                          HistoryAdapterCallback adapterCallback) {
        this.mContext = context;
        this.modelDatabase = modelDatabaseList;
        this.mAdapterCallback = adapterCallback;
    }

    // Definisi antarmuka HistoryAdapterCallback
    public interface HistoryAdapterCallback {
        void onDelete(ModelDatabase modelDatabase);
    }

    public void setDataAdapter(List<ModelDatabase> items) {
        modelDatabase.clear();
        modelDatabase.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ModelDatabase data = modelDatabase.get(position);

        // Cek jika data.getKategori() null
        String kategori = data.getKategori() != null ? data.getKategori() : "Unknown";

        holder.tvKategori.setText(kategori);
        holder.tvNama.setText(kategori); // Periksa apakah ini yang benar, seharusnya mungkin `data.getNama()`
        holder.tvDate.setText(data.getTanggal());
        holder.tvLokasi.setText(data.getLokasi());

        // Set warna latar belakang berdasarkan kategori
        switch (kategori) {
            case "Laporan Laundry":
                holder.layoutHeader.setBackgroundResource(R.color.red);
                break;
            case "Laporan Medis":
                holder.layoutHeader.setBackgroundResource(R.color.blue);
                break;
            case "Laporan Galon":
                holder.layoutHeader.setBackgroundResource(R.color.green);
                break;
            default:
                holder.layoutHeader.setBackgroundResource(R.color.white); // Tambahkan warna default jika diperlukan
                break;
        }
    }

    @Override
    public int getItemCount() {
        return modelDatabase.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvKategori, tvNama, tvDate, tvLokasi;
        public CardView cvHistory;
        public LinearLayout layoutHeader;

        public ViewHolder(View itemView) {
            super(itemView);
            tvKategori = itemView.findViewById(R.id.tvKategori);
            tvNama = itemView.findViewById(R.id.tvNama);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvLokasi = itemView.findViewById(R.id.tvLokasi);
            cvHistory = itemView.findViewById(R.id.cvHistory);
            layoutHeader = itemView.findViewById(R.id.layoutHeader);

            cvHistory.setOnClickListener(view -> {
                ModelDatabase modelLaundry = modelDatabase.get(getAdapterPosition());
                mAdapterCallback.onDelete(modelLaundry);
            });
        }
    }
}
