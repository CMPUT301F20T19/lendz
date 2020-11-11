package cmput301.team19.lendz;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ViewRequestCustomAdapter extends ArrayAdapter<BorrowerInfo> {

    private Context mContext;
    int mResource;
    private List<BorrowerInfo> borrowerList= new ArrayList<>();

    public ViewRequestCustomAdapter(@NonNull Context context, int resource, @NonNull List<BorrowerInfo> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        borrowerList = objects;
    }

    static class ViewHolder {
        TextView full_name;
        TextView time_stamp;
        ImageView owner_image;
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
            viewHolder.time_stamp = (TextView) v.findViewById(R.id.n2);
            viewHolder.owner_image = (ImageView) v.findViewById(R.id.requestImage);
            viewHolder.position = position;
            viewHolder.accept = (ImageButton) v.findViewById(R.id.acceptRequest);
            viewHolder.decline = (ImageButton) v.findViewById(R.id.declineRequest);
            v.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) v.getTag();
        }
        String fullName = getItem(position).getFullName();
        String photo = getItem(position).getPhoto();
        String timeStamp = getItem(position).getTimeStamp();

        viewHolder.full_name.setText(fullName);
        viewHolder.time_stamp.setText(timeStamp);
        Picasso.get().load(photo).into(viewHolder.owner_image);

        //Handle buttons and add onClickListeners
        viewHolder.decline.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //decline request
                Toast.makeText(getContext(),"decline btn tapped at "+ String.valueOf(position),Toast.LENGTH_SHORT).show();

                //show popup....need to implement

                borrowerList.remove(position);
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

        return v;
    };

}
