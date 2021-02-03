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

    /**
     * Called by RecyclerView to display the data at the specified position. This method should update the contents of the RecyclerView.ViewHolder.itemView to reflect the item at the given position.
     * @param holder The ViewHolder which should be updated to represent the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     * @param model Travel object
     */
    @Override
    protected void onBindViewHolder(@NonNull TravelHolder holder, int position, @NonNull Travel model) {
        holder.fromTextView.setText(String.valueOf(model.getFrom()));
        holder.toTextView.setText(String.valueOf(model.getTo()));
        holder.dateTextView.setText(String.valueOf(model.getDate()));
        holder.timeTextView.setText(String.valueOf(model.getTime()));
    }

    /**
     * Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an item.
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public TravelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.travel_item,
                parent,false);
        return new TravelHolder(v);
    }

    /**
     * Function called to delete recycler view item
     * @param position Item position
     */
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
