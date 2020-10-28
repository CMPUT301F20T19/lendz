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

    Context context;
    ArrayList<Book> books;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView book_photo;
        TextView book_title;
        TextView book_author;
        TextView book_owner_username;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            book_photo = itemView.findViewById(R.id.book_photo);
            book_title = itemView.findViewById(R.id.book_title);
            book_author = itemView.findViewById(R.id.book_author);
            book_owner_username = itemView.findViewById(R.id.book_owner_username);
        }
    }

    public ViewBooksSectionAdapter(Context context, ArrayList<Book> books) {
        this.context = context;
        this.books = books;
    }

    @NonNull
    @Override
    public ViewBooksSectionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_view_books_list_item, parent, false);
        ViewBooksSectionAdapter.ViewHolder viewHolder = new ViewBooksSectionAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewBooksSectionAdapter.ViewHolder holder, int position) {
        Book book = books.get(position);
        BookDescription bookDescription = book.getDescription();

        // loading and showing book photo
        final ViewBooksSectionAdapter.ViewHolder holderCopy = holder;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String bookPhotoUrl = book.getPhoto().toString();
        storage.getReferenceFromUrl(bookPhotoUrl)
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>()
                                      {
                                          @Override
                                          public void onSuccess(Uri uri) {
                                              Picasso.with(context).load(uri).into(holderCopy.book_photo);
                                          }
                                      }
                )
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG,"Could not load book photo : ", e);
                    }
                });

        // showing text information
        holder.book_title.setText(bookDescription.getTitle());
        holder.book_author.setText((bookDescription.getAuthor()));
        // holder.book_owner_username.setText(book.getOwnerUsername());
    }

    @Override
    public int getItemCount() {
        return books.size();
    }
}
