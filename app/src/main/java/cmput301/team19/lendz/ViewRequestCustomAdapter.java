package cmput301.team19.lendz;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ViewRequestCustomAdapter extends ArrayAdapter<BorrowerInfo> {

    private Context mContext;
    int mResource;
    private List<BorrowerInfo> borrowerList= new ArrayList<>();
    FirebaseFirestore firestoreRef;
    CollectionReference requestCollection;

    public ViewRequestCustomAdapter(@NonNull Context context, int resource, @NonNull List<BorrowerInfo> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        borrowerList = objects;
    }

    static class ViewHolder {
        TextView full_name;
        ImageButton accept ;
        ImageButton decline;
        int position;
    }

    /**
     * custom array adapter getview.
     * makes only accept request btn and decline btn clickable
     */
    @NonNull
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View v = convertView;
        ViewHolder viewHolder;

        if (v == null) {
            LayoutInflater layoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v =  layoutInflater.inflate(R.layout.view_book_request,null);
            viewHolder = new ViewHolder();
            viewHolder.full_name = (TextView) v.findViewById(R.id.n1);
            viewHolder.position = position;
            viewHolder.accept = (ImageButton) v.findViewById(R.id.acceptRequest);
            viewHolder.decline = (ImageButton) v.findViewById(R.id.declineRequest);
            v.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) v.getTag();
        }
        String fullName = getItem(position).getFullName();
        viewHolder.full_name.setText(fullName);

        //Handle buttons and add onClickListeners
        viewHolder.decline.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //decline request
                Toast.makeText(getContext(),"decline btn tapped at "+ String.valueOf(position),Toast.LENGTH_SHORT).show();

                //show popup....need to implement

                dialogBox("do you want to decline this request","Decline Book Request",1,position);
                notifyDataSetChanged();
            }
        });

        viewHolder.accept.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //accept request
                Toast.makeText(getContext(),"accept btn tapped at "+ String.valueOf(position),Toast.LENGTH_SHORT).show();
                //show popup....need to implement

                notifyDataSetChanged();
            }
        });
        viewHolder.full_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"name tapped at "+ String.valueOf(position),Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getContext(),"YES",Toast.LENGTH_SHORT).show();
                        if (swap == 1){

                            String requestId= getItem(position).getRequestDocumentId();
                            Toast.makeText(getContext(),String.valueOf(position),Toast.LENGTH_SHORT).show();
                            firestoreRef = FirebaseFirestore.getInstance();
                            requestCollection = firestoreRef.collection("requests");
                            requestCollection
                                    .document(requestId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getContext(),"Request Declined",Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(),"Could not decline request",Toast.LENGTH_SHORT).show();
                                }
                            });

                        }else{
                            //bayo insert here
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Toast.makeText(getContext(),"NO",Toast.LENGTH_SHORT).show();

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();


    }

}
