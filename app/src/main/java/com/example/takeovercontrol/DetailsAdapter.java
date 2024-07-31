package com.example.takeovercontrol;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.DetailsViewHolder> {
    private Context context;
    private List<Details> detailsList;

    public DetailsAdapter(Context context, List<Details> detailsList) {
        this.context = context;
        this.detailsList = detailsList;
    }

    @NonNull
    @Override
    public DetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_details, parent, false);
        return new DetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailsViewHolder holder, int position) {
        Details details = detailsList.get(position);
        holder.bind(details);

        holder.itemView.setOnClickListener((v) -> {
            Intent intent = new Intent(context, AddActivity.class);
            intent.putExtra("type", details.getType());
            intent.putExtra("size", details.getSize());
            intent.putExtra("alcohol", String.valueOf(details.getAlcohol()));
            intent.putExtra("cost", String.valueOf(details.getCost()));
            intent.putExtra("place", details.getPlace());
            intent.putExtra("unit", String.valueOf(details.getUnit()));
            intent.putExtra("docId", details.getDocId());
            intent.putExtra("date", details.getDate());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return detailsList.size();
    }

    public class DetailsViewHolder extends RecyclerView.ViewHolder {
        private TextView type, size, alcohol, cost, place, unit;

        public DetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.typeTextView);
            size = itemView.findViewById(R.id.sizeTextView);
            alcohol = itemView.findViewById(R.id.alcoholTextView);
            cost = itemView.findViewById(R.id.costTextView);
            place = itemView.findViewById(R.id.placeTextView);
            unit = itemView.findViewById(R.id.unitTextView);
        }

        public void bind(Details details) {
            String formattedCost = String.format(Locale.getDefault(), "%.2f", details.getCost());
            String formattedUnit = String.format(Locale.getDefault(), "%.1f", details.getUnit());

            type.setText("Type: " + details.getType());
            size.setText("Size: " + details.getSize());
            alcohol.setText("Alcohol: " + details.getAlcohol() + "%");
            cost.setText("Cost: " + formattedCost + "€");
            place.setText("Place: " + details.getPlace());
            unit.setText("Unit: " + formattedUnit);
        }
    }
}
