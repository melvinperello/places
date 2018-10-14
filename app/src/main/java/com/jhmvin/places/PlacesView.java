package com.jhmvin.places;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.jhmvin.places.domain.TravelStreamListData;
import com.jhmvin.places.domain.TravelStreamListDataAdapter;
import com.jhmvin.places.util.TempTravelStream;
import com.jhmvin.places.util.ToastAdapter;

import java.io.File;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_view);
        ButterKnife.bind(this);

        rvFiles.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvFiles.setLayoutManager(manager);

        this.adapter = new TravelStreamListDataAdapter(this, dataList);
        this.rvFiles.setAdapter(adapter);

        for (File f : TempTravelStream.getTempTravelStreamDirectory(getApplicationContext()).listFiles()) {
            TravelStreamListData data = new TravelStreamListData();
            data.setOrigin("Home");
            data.setDestination("Clark");
            data.setStartTime(String.valueOf(f.getName()));
            this.dataList.add(data);
            this.adapter.notifyDataSetChanged();
        }


    }

    @Override
    public void onItemClicked(int index) {
        ToastAdapter.show(getApplicationContext(), String.valueOf(index));
    }
}
