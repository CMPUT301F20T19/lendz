package cmput301.team19.lendz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ViewRequestCustomAdapter extends ArrayAdapter<BorrowerInfo> {

    private Context mContext;
    int mResource;

    public ViewRequestCustomAdapter(@NonNull Context context, int resource, @NonNull List<BorrowerInfo> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String fullName = getItem(position).getFullName();
       // String photo = getItem(position).getPhoto();
        String timeStamp = getItem(position).getTimeStamp();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource,parent,false);
        TextView full_name = (TextView) convertView.findViewById(R.id.textView8);
        TextView time_stamp = (TextView) convertView.findViewById(R.id.textView9);
        //ImageView owner_image = (ImageView) convertView.findViewById(R.id.requestImage);

        full_name.setText(fullName);
        time_stamp.setText(timeStamp);
        //Picasso.get().load(photo).into(owner_image);

        return convertView;
    };

}
