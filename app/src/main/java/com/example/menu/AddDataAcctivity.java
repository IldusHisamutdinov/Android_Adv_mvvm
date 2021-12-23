package com.example.menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.menu.database.DatabaseHelper;
import com.example.menu.model.DataModel;
import com.example.menu.util.SharedPrefUtil;
import com.example.menu.view.ui.BaseMainActivity;
import com.example.menu.weather.CityRecyclerAdapter;

import static com.example.menu.Constants.NAME_CITY;

public class AddDataAcctivity extends AppCompatActivity implements CityRecyclerAdapter.OnDeleteListener, CityRecyclerAdapter.OnGetListener {

    private RecyclerView recyclerView;
    private DatabaseHelper databaseHelper;
    private Toolbar toolbar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_data_activity);
        toolbar = initToolbar();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        databaseHelper = App.getInstance().getDatabaseInstance();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_button, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add: {
                startActivity(new Intent(this, BaseMainActivity.class));
                break;
            }
        }
        return false;
    }


    protected void onResume() {
        super.onResume();
        CityRecyclerAdapter recyclerAdapter = new CityRecyclerAdapter(this, databaseHelper.getDataDao().getAllData());
        recyclerAdapter.setOnDeleteListener(this);
        recyclerAdapter.onGetListener(this);
        recyclerView.setAdapter(recyclerAdapter);
    }

    @Override
    public void onDelete(DataModel dataModel) {
        databaseHelper.getDataDao().delete(dataModel);
    }

    public void getByTitle(DataModel dataModel) {
        String town = dataModel.city;
        SharedPrefUtil.save(this, NAME_CITY, town);
        Intent intent = new Intent(getApplicationContext(), BaseMainActivity.class);
        intent.putExtra(NAME_CITY, town);
        startActivity(intent);
        finish();
    }

    private Toolbar initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        return toolbar;
    }

}
