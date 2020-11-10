package cmput301.team19.lendz;

import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ViewRequestActivity extends AppCompatActivity {

    private ListView requestBookListView;
    private ViewRequestCustomAdapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestBookListView = findViewById(R.id.requestListView);

        BorrowerInfo newOne = new BorrowerInfo("Andrews", "https://firebasestorage.googleapis.com/v0/b/lendz-7eb71.appspot.com/o/BookImages%2FQQc5i9NnqjGSeT4Wxq41?alt=media&token=6b795d0d-d809-45e6-87e2-8a2502ceb664",
                "23:21");
        BorrowerInfo newTwo = new BorrowerInfo("Isaac", "https://firebasestorage.googleapis.com/v0/b/lendz-7eb71.appspot.com/o/BookImages%2FQQc5i9NnqjGSeT4Wxq41?alt=media&token=6b795d0d-d809-45e6-87e2-8a2502ceb664",
                "24:21");
        BorrowerInfo newThree = new BorrowerInfo("Ziggy", "https://firebasestorage.googleapis.com/v0/b/lendz-7eb71.appspot.com/o/BookImages%2FQQc5i9NnqjGSeT4Wxq41?alt=media&token=6b795d0d-d809-45e6-87e2-8a2502ceb664",
                "25:21");
        BorrowerInfoArray.getInstance().add_BorrowerInfo(newOne);
        BorrowerInfoArray.getInstance().add_BorrowerInfo(newTwo);
        BorrowerInfoArray.getInstance().add_BorrowerInfo(newThree);

        //connect ListView to its array content using a custom adapter
        adapter = new ViewRequestCustomAdapter(this, R.layout.view_book_request, BorrowerInfoArray.getInstance().getArray());
        requestBookListView.setAdapter(adapter);

    }
}
