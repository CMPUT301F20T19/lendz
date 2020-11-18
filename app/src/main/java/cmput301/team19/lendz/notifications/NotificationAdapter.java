package cmput301.team19.lendz.notifications;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import cmput301.team19.lendz.R;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationViewHolder> {
    private final Context context;
    private final ArrayList<Notification> notifications;

    public NotificationAdapter(Context context, ArrayList<Notification> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @Override
    public int getItemViewType(int position) {
        Notification notification = notifications.get(position);
        return notification.type.ordinal();
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        NotificationType notificationType = NotificationType.values()[viewType];
        switch (notificationType) {
            case BookRequested:
                view = inflater.inflate(
                        R.layout.book_requested_notification, parent, false);
                return new BookRequestedNotification.ViewHolder(view);
            case RequestAcknowledged:
                view = inflater.inflate(
                        R.layout.request_acknowledged_notification, parent, false);
                return new RequestAcknowledgedNotification.ViewHolder(view);
            default:
                throw new IllegalArgumentException("unknown notificationType " + notificationType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        holder.bind(context, notifications.get(position));
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }
}
