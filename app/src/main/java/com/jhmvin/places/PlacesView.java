package com.jhmvin.places;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jhmvin.places.util.TempTravelStream;
import com.jhmvin.places.util.ToastAdapter;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlacesView extends AppCompatActivity {

    @BindView(R.id.lvFiles)
    ListView lvFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_view);
        ButterKnife.bind(this);

        ArrayList<String> filesToDisplay = new ArrayList<>();
        for (File f : TempTravelStream.getTempTravelStreamDirectory(getApplicationContext()).listFiles()) {
            filesToDisplay.add(f.getName());
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, filesToDisplay);

        lvFiles.setAdapter(adapter);
        lvFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToastAdapter.show(getApplicationContext(), String.valueOf(position));
            }
        });


    }

}
