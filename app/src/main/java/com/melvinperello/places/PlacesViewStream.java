package com.melvinperello.places;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlacesViewStream extends AppCompatActivity {

    @BindView(R.id.txtView)
    TextView txtView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_view_stream);
        ButterKnife.bind(this);

        txtView.setMovementMethod(new ScrollingMovementMethod());

        String display = getIntent().getExtras().getString("file_content");
        if (display != null) {
            txtView.setText(display);
        }
    }
}
