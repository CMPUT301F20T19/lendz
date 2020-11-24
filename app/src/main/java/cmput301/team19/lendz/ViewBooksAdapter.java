package cmput301.team19.lendz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ViewBooksAdapter extends RecyclerView.Adapter<ViewBooksAdapter.ViewHolder> {
    Context context;
    ArrayList<ViewBooksSection> sections;
    private OnBookClickListener onBookClickListener;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView sectionHeader;
        RecyclerView sectionBody;

        public ViewHolder(View itemView) {
            super(itemView);

            sectionHeader = itemView.findViewById(R.id.section_header);
            sectionBody = itemView.findViewById(R.id.section_body);

            sectionBody.addItemDecoration(new DividerItemDecoration(
                    ViewBooksAdapter.this.context, DividerItemDecoration.VERTICAL));
        }
    }

    public ViewBooksAdapter(Context context, ArrayList<ViewBooksSection> sections, OnBookClickListener onBookClickListener) {
        this.context = context;
        this.sections = sections;
        this.onBookClickListener = onBookClickListener;
    }

    @NonNull
    @Override
    public ViewBooksAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.book_list_section_item, parent, false);
        ViewBooksAdapter.ViewHolder viewHolder = new ViewBooksAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewBooksAdapter.ViewHolder holder, int position) {
        ViewBooksSection section = sections.get(position);
        holder.sectionHeader.setText(section.getSectionHeader());
        ViewBooksSectionAdapter viewBooksSectionAdapter = new ViewBooksSectionAdapter(this.context, section.getBooks(),onBookClickListener);
        holder.sectionBody.setAdapter(viewBooksSectionAdapter);
    }

    @Override
    public int getItemCount() {
        return sections.size();
    }
}
