package cmput301.team19.lendz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * RecyclerAdapter for presenting the searchResults
 */
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_book_list_item,parent,false);
        return new ViewHolder(view,onBookClickListener);
    }

    /**
     * Sets the text value of the various textViews present on the item
     * @param holder contains the various descriptive view for presenting a search result
     * @param position the position of the item
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book book = searchResults.get(position);
        holder.ownerUsernameTextView.setText(context.getResources().getString(R.string.owned_by, book.getOwnerUsername()));
        holder.bookTitleTextView.setText(book.getDescription().getTitle());
        holder.bookAuthorTextView.setText(book.getDescription().getAuthor());
        holder.bookStatusTextView.setText(book.getStatus().toString(context.getResources()));
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        public TextView ownerUsernameTextView;
        public TextView bookStatusTextView;
        public TextView bookTitleTextView;
        public TextView bookAuthorTextView;
        private OnBookClickListener onBookClickListener;

        public ViewHolder (View itemView, OnBookClickListener onBookClickListener ) {
            super(itemView);

            ownerUsernameTextView = itemView.findViewById(R.id.search_item_owner_username);
            bookStatusTextView = itemView.findViewById(R.id.search_item_book_status);
            bookTitleTextView = itemView.findViewById(R.id.search_item_book_title);
            bookAuthorTextView = itemView.findViewById(R.id.search_item_book_author);
            this.onBookClickListener = onBookClickListener;
            itemView.setOnClickListener(this);
        }

        /**
         * sets a clickListener on each recyclerView item
         * @param itemView contains details of the card
         */
        @Override
        public void onClick(View itemView) {
            onBookClickListener.onBookClick(getAdapterPosition());
        }
    }

    /**
     * @return the size of the arrayList for searchResults
     */
    @Override
    public int getItemCount() {
        return searchResults.size();
    }
}
