package cmput301.team19.lendz;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class ViewRequestCustomAdapter extends ArrayAdapter<BorrowerInfo>{

    public ViewRequestCustomAdapter(@NonNull Context context, int resource, @NonNull List<BorrowerInfo> objects) {
        super(context, resource, objects);
    }

    static class ViewHolder {
        TextView username;
        ImageButton accept;
        ImageButton decline;
        int position;
    }

    /**
     * custom array adapter getView.
     * makes only accept request btn and decline btn clickable
     */
    @NonNull
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View v = convertView;
        ViewHolder viewHolder;

        if (v == null) {
            LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v =  layoutInflater.inflate(R.layout.view_book_request,null);
            viewHolder = new ViewHolder();
            viewHolder.username = (TextView) v.findViewById(R.id.n1);
            viewHolder.position = position;
            viewHolder.accept = (ImageButton) v.findViewById(R.id.acceptRequest);
            viewHolder.decline = (ImageButton) v.findViewById(R.id.declineRequest);
            v.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) v.getTag();
        }
        String fullName = getItem(position).getFullName();
        viewHolder.username.setText(fullName);

        //Handle buttons and add onClickListeners
        viewHolder.decline.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //decline request
                dialogBox("do you want to decline this request","Decline Book Request",1,position);

            }
        });

        viewHolder.accept.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //accept request
                dialogBox("Do you want to accept this request","Accept Book Request",0,position);

            }
        });

        viewHolder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String requester_Id = getItem(position).getId();
                Fragment fragment =  ViewUserProfileFragment.newInstance(requester_Id);
                ((AppCompatActivity)getContext()).
                        getSupportFragmentManager().
                        beginTransaction().replace(R.id.bookrequestframe, fragment)
                        .commit();

            }
        });
        return v;

    };
    public void dialogBox(String message, final String title, final int swap,final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (swap == 1){

                            declineRequest(position);

                        }else{
                            acceptRequest(position);
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    public void declineRequest(int position){
        String requestId = getItem(position).getRequestDocumentId();
        Request request = Request.getOrCreate(requestId);
        request.setStatus(RequestStatus.DECLINED);
        request.store()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        notifyDataSetChanged();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(
                                getContext(),
                                "Could not decline request",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * this handles accepting requests
     * @param position
     * this is the position of the book that you click to accept
     */
    public void acceptRequest(int position)
    {
        String requestId= getItem(position).getRequestDocumentId();
        String bookID = getItem(position).getBookID();
        String requesterId = getItem(position).getId();

        Request request = Request.getOrCreate(requestId);
        Book book  = Book.getOrCreate(bookID);
        User user = User.getOrCreate(requesterId);
        request.setRequester(user);
        request.setBook(book);

        //do setting location
        openMapFragment(requestId,bookID,requesterId);
        notifyDataSetChanged();
    }

    public void openMapFragment(String requestId,String bookID,String requesterId)
    {
       // Initialize fragment
        Intent intent = new Intent(getContext(), MapsActivity.class);
        intent.putExtra("requestID",requestId);
        intent.putExtra("bookID",bookID);
        intent.putExtra("requesterID",requesterId);
        getContext().startActivity(intent);
    }
}
