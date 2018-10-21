package com.jhmvin.places;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.jhmvin.places.domain.TravelStreamListData;
import com.jhmvin.places.domain.TravelStreamListDataAdapter;
import com.jhmvin.places.persistence.text.TempTravelLocationWriter;
import com.jhmvin.places.util.ToastAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlacesView extends AppCompatActivity implements TravelStreamListDataAdapter.ItemClicked {


    @BindView(R.id.rvFiles)
    RecyclerView rvFiles;


    private final ArrayList<TravelStreamListData> dataList = new ArrayList<>();
    RecyclerView.Adapter adapter;


    @OnClick(R.id.btnAdd)
    public void onClickBtnAdd() {
        TravelStreamListData data = new TravelStreamListData();
        data.setOrigin("New");
        data.setDestination("Object");
        data.setStartTime("Added");
        this.dataList.add(data);
        this.adapter.notifyDataSetChanged();
        ToastAdapter.show(getApplicationContext(), "Mock object added !.", ToastAdapter.SUCCESS);
    }

    private File[] files;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_view);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle("Location Record");

        rvFiles.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvFiles.setLayoutManager(manager);

        this.adapter = new TravelStreamListDataAdapter(this, dataList);
        this.rvFiles.setAdapter(adapter);
        this.files = TempTravelLocationWriter.getWorkingDirectory(getApplicationContext()).listFiles();
        for (File f : files) {
            TravelStreamListData data = new TravelStreamListData();
            data.setOrigin("Place Started");
            data.setDestination("Place Ended");
            data.setStartTime(String.valueOf(f.getName()));
            this.dataList.add(data);
            this.adapter.notifyDataSetChanged();
        }


    }

    @Override
    public void onItemClicked(int index) {
        try {
            if (this.files != null) {
                File selectedFile = this.files[index];
                String content = readFileToString(selectedFile);
                Intent intent = new Intent(this, PlacesViewStream.class);
                intent.putExtra("file_content", content);
                startActivity(intent);
            }
        } catch (Exception ex) {
            ToastAdapter.show(getApplicationContext(), "Cannot Display", ToastAdapter.ERROR);
        }
    }


    private String readFileToString(File file) {
        String content = "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null) {
                content += (line + "\n");
                line = reader.readLine();
            }
            return content;
        } catch (Exception ex) {
            return "read error !";
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ioex) {
                    // ignore
                }
                reader = null;
            }
        }
    }
}
