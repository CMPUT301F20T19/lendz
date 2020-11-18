package cmput301.team19.lendz.notifications;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class NotificationViewHolder extends RecyclerView.ViewHolder {
    public NotificationViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void bind(Context context, Notification n);
}
