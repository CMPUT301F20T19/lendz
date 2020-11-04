package cmput301.team19.lendz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SearchViewAdapter extends RecyclerView.Adapter<SearchViewAdapter.ViewHolder> {

    private ArrayList<Book> searchResults;
    private Context context;
    private OnBookClickListener onBookClickListener;


    public SearchViewAdapter(ArrayList<Book> results, Context context, OnBookClickListener onBookClickListener) {
        this.searchResults = results;
        this.context = context;
        this.onBookClickListener = onBookClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_content,parent,false);
        return new ViewHolder(view,onBookClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //have to use stuff from a arraylist
        Book book = searchResults.get(position);
        holder.usernameTV.setText("Owned by: " + book.getOwner().getUsername());
        holder.descriptionTV.setText(book.getDescription().getDescription());
        String status = book.getStatus() == BookStatus.AVAILABLE ? "Available": "Currently Requested";
        holder.statusTV.setText( "Status: " + status);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

        public TextView usernameTV;
        public TextView statusTV;
        public TextView descriptionTV;
        private OnBookClickListener onBookClickListener;

        public ViewHolder (View itemView, OnBookClickListener onBookClickListener ) {
            super(itemView);

            usernameTV = itemView.findViewById(R.id.username);
            statusTV = itemView.findViewById(R.id.book_status);
            descriptionTV = itemView.findViewById(R.id.book_description);
            this.onBookClickListener = onBookClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View itemView) {
            onBookClickListener.onBookClick(getAdapterPosition());
        }
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }
}
