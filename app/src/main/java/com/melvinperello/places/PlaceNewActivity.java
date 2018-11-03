package com.melvinperello.places;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlaceNewActivity extends AppCompatActivity {


    @BindView(R.id.edtName)
    EditText edtName;
    @BindView(R.id.edtNotes)
    EditText edtNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_new);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle(R.string.a_new_place);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @OnClick(R.id.btnSave)
    public void onClickBtnSave() {
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
