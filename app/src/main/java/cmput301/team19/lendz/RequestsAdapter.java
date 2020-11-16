package cmput301.team19.lendz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.RequestViewHolder> {
    private final ArrayList<Request> requests;

    public static class RequestViewHolder extends RecyclerView.ViewHolder {
        private final TextView requesterUsernameTextView;
        private final TextView requesterFullNameTextView;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            requesterUsernameTextView = itemView.findViewById(R.id.requester_username_textview);
            requesterFullNameTextView = itemView.findViewById(R.id.requester_full_name_textview);
        }
    }

    public RequestsAdapter(ArrayList<Request> requests) {
        this.requests = requests;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.request_item, parent, false);
        return new RequestViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Request request = requests.get(position);

        holder.requesterUsernameTextView.setText(request.getRequester().getUsername());
        holder.requesterFullNameTextView.setText(request.getRequester().getFullName());
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }
}
