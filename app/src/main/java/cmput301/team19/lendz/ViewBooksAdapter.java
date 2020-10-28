package cmput301.team19.lendz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ViewBooksAdapter extends RecyclerView.Adapter<ViewBooksAdapter.ViewHolder> {
    Context context;
    ArrayList<ViewBooksSection> sections;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView section_header;
        RecyclerView section_body;

        public ViewHolder(View itemView) {
            super(itemView);

            section_header = itemView.findViewById(R.id.section_header);
            section_body = itemView.findViewById(R.id.section_body);
        }
    }

    public ViewBooksAdapter(Context context, ArrayList<ViewBooksSection> sections) {
        this.context = context;
        this.sections = sections;
    }

    @NonNull
    @Override
    public ViewBooksAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.activity_view_books_section, parent, false);
        ViewBooksAdapter.ViewHolder viewHolder = new ViewBooksAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewBooksAdapter.ViewHolder holder, int position) {
        ViewBooksSection section = sections.get(position);
        holder.section_header.setText(section.getSectionHeader());

        ViewBooksSectionAdapter viewBooksSectionAdapter = new ViewBooksSectionAdapter(this.context, section.getBooks());
        holder.section_body.setAdapter(viewBooksSectionAdapter);
    }

    @Override
    public int getItemCount() {
        return sections.size();
    }
}
