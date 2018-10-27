package com.jhmvin.places;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.jhmvin.places.feature.tempTravel.TempTravelWriter;
import com.jhmvin.places.persistence.text.TextWriter;
import com.jhmvin.places.util.ToastAdapter;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlacesMarkerNew extends AppCompatActivity {

    @BindView(R.id.edtMarkerName)
    EditText edtMarkerName;


    private String mLongitude;
    private String mLatitude;
    private String mStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_marker_new);
        getSupportActionBar().setTitle("New Marker");
        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();

        if (!extras.isEmpty()) {
            mLongitude = extras.getString("long");
            mLatitude = extras.getString("lat");
            mStarted = extras.getString("time");
        }
    }

    @OnClick(R.id.btnMarkerSave)
    public void onClickBtnMarkerSave() {

        String text = edtMarkerName.getText().toString().trim();

        if (text.isEmpty()) {
            ToastAdapter.show(this, "Please enter a marker name", ToastAdapter.WARNING);
            return;
        }

        TextWriter writer = new TempTravelWriter(this, mStarted + "_marker");
        try {
            writer.open();
            String format = String.format("%s,%s,%s", mLongitude, mLatitude, text);
            writer.write(format + "\n");
            ToastAdapter.show(this, "Marker Saved !", ToastAdapter.SUCCESS);
            finish();
        } catch (IOException ioex) {
            ToastAdapter.show(this, "Failed to saved marker", ToastAdapter.ERROR);
        } finally {
            try {
                writer.close();
            } catch (IOException closx) {
                // ignore
            }
        }


    }

}
