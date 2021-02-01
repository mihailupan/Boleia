package com.example.boleia;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class TravelAdapter extends FirestoreRecyclerAdapter<Travel,TravelAdapter.TravelHolder>{

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public TravelAdapter(@NonNull FirestoreRecyclerOptions<Travel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull TravelHolder holder, int position, @NonNull Travel model) {
        holder.fromTextView.setText(String.valueOf(model.getFrom()));
        holder.toTextView.setText(String.valueOf(model.getTo()));
        holder.dateTextView.setText(String.valueOf(model.getDate()));
        holder.timeTextView.setText(String.valueOf(model.getTime()));
    }

    @NonNull
    @Override
    public TravelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.travel_item,
                parent,false);
        return new TravelHolder(v);
    }

    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class TravelHolder extends RecyclerView.ViewHolder{

        TextView fromTextView, dateTextView, toTextView, timeTextView;

        public TravelHolder(@NonNull View itemView) {
            super(itemView);
            fromTextView = itemView.findViewById(R.id.recycle_view_from);
            toTextView = itemView.findViewById(R.id.recycle_view_to);
            dateTextView = itemView.findViewById(R.id.recycle_view_date);
            timeTextView = itemView.findViewById(R.id.recycle_view_time);
        }
    }
}
