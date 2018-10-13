package com.jhmvin.places;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.jhmvin.places.util.ToastAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StartTracking extends AppCompatActivity {

    //----------------------------------------------------------------------------------------------
    // Widget Variables.
    //----------------------------------------------------------------------------------------------
    @BindView(R.id.edtTrackFrom)
    EditText edtTrackFrom;

    @BindView(R.id.edtTrackTo)
    EditText edtTrackTo;


    //----------------------------------------------------------------------------------------------
    // 1. Create.
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_tracking);
        ButterKnife.bind(this);
    }

    private String getEditText(EditText editText) {
        return editText.getText().toString().trim();
    }


    private boolean checkIfSomethingEmpty() {
        return (this.getEditText(this.edtTrackFrom).isEmpty() ||
                this.getEditText(this.edtTrackTo).isEmpty());
    }


    @OnClick(R.id.btnStartTracking)
    public void onClickButtonStart() {
        if (this.checkIfSomethingEmpty()) {
            ToastAdapter.show(this, "Location Names are required.", ToastAdapter.UNKNOWN);
            return;
        }

        Intent startTracker = new Intent(this,TrackingLocation.class);
        startActivity(startTracker);
        this.finish();

    }


}
