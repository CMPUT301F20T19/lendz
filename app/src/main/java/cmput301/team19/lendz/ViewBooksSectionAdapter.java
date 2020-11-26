package cmput301.team19.lendz;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ViewBooksSectionAdapter extends RecyclerView.Adapter<ViewBooksSectionAdapter.ViewHolder> {
    private static final String TAG = "ViewBooksSectionAdapter";
    private Context context;
    private ArrayList<Book> books;
    private OnBookClickListener onBookClickListener;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView book_photo;
        TextView book_title;
        TextView book_author;
        TextView book_owner_username;
        private OnBookClickListener onBookClickListener;

        public ViewHolder(@NonNull View itemView, OnBookClickListener onBookClickListener) {
            super(itemView);

            book_photo = itemView.findViewById(R.id.book_photo);
            book_title = itemView.findViewById(R.id.book_title);
            book_author = itemView.findViewById(R.id.book_author);
            book_owner_username = itemView.findViewById(R.id.book_owner_username);
            this.onBookClickListener = onBookClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onBookClickListener.onBookClick(ViewBooksSectionAdapter.this.books.get(getAdapterPosition()));
        }
    }

    public ViewBooksSectionAdapter(Context context, ArrayList<Book> books, OnBookClickListener onBookClickListener) {
        this.context = context;
        this.books = books;
        this.onBookClickListener = onBookClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.book_list_item, parent, false);
        ViewBooksSectionAdapter.ViewHolder viewHolder = new ViewHolder(view,onBookClickListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book book = books.get(position);

        // loading and showing book photo
        String bookPhotoUrl = book.getPhoto();

        if (bookPhotoUrl != null && bookPhotoUrl != "") {
            final ViewBooksSectionAdapter.ViewHolder holderCopy = holder;
            FirebaseStorage storage = FirebaseStorage.getInstance();

            storage.getReferenceFromUrl(bookPhotoUrl)
                    .getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>()
                                          {
                                              @Override
                                              public void onSuccess(Uri uri) {
                                                  Picasso.get().load(uri).into(holderCopy.book_photo);
                                              }
                                          }
                    )
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG,"Could not load book photo : ", e);
                            holderCopy.book_photo.setImageResource(R.drawable.ic_baseline_image_24);
                        }
                    });
        } else {
            holder.book_photo.setImageResource(R.drawable.ic_baseline_image_24);
        }

        // showing text information
        BookDescription bookDescription = book.getDescription();

        if (bookDescription != null) {
            holder.book_title.setText(bookDescription.getTitle());
            holder.book_author.setText((bookDescription.getAuthor()));
            if(User.getCurrentUser() != book.getOwner()) {
                 holder.book_owner_username.setText(book.getOwnerUsername());
            }
        }
    }

    @Override
    public int getItemCount() {
        return books.size();
    }
}
