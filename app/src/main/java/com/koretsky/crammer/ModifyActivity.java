package com.koretsky.crammer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.koretsky.crammer.sm5.Package;

import java.io.File;
import java.util.HashMap;


public class ModifyActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemLongClickListener {

    ListView itemsLV = null;
    Package p = null;
    String handleItemName = null;
    Button btnAddItem;
    Context that = this;
    Menu m;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_modify, menu);
        m = menu;
        m.findItem(R.id.itemsAdded).setVisible(false);
        m.findItem(R.id.addItem).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addItem:
                m.findItem(R.id.addItem).setVisible(false);
                setContentView(R.layout.add_item);
                btnAddItem = (Button) findViewById(R.id.btnAddItem);
                btnAddItem.setOnClickListener((View.OnClickListener) that);
                break;
            case R.id.itemsAdded:
                m.findItem(R.id.itemsAdded).setVisible(false);
                onPause();
                setContentView(R.layout.activity_modify);
                m.findItem(R.id.addItem).setVisible(true);
                onStart();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);
        setTitle("Modifying package");
        itemsLV = (ListView) findViewById(R.id.itemsLV);
        itemsLV.setLongClickable(true);

        itemsLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                HashMap hashMapPackName = (HashMap) itemsLV.getItemAtPosition(i);
                handleItemName = hashMapPackName.get("value").toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(that);
                builder.setMessage("Delete item?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Log.d("NAME", handleItemName);
                                p.removeItem(handleItemName);
                                onStart();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                builder.create().show();
                return false;
            }
        });
        Intent intent = getIntent();
        try {
            p = Package.deserializePackage(getFilesDir(), intent.getStringExtra("packagePath"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        itemsLV = (ListView) findViewById(R.id.itemsLV);
        SimpleAdapter adapter = new SimpleAdapter(this, p.getItemsArrayList(),
                android.R.layout.simple_list_item_2,
                new String[]{"value", "info"},
                new int[]{android.R.id.text1, android.R.id.text2});
        itemsLV.setAdapter(adapter);
        super.onStart();
    }

    @Override
    protected void onPause() {
        try {
            new File(getFilesDir(), getIntent().getStringExtra("packagePath")).delete();
            p.serializePackage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPause();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.btnAddItem):
                EditText et = (EditText) findViewById(R.id.itemValue);
                EditText eta = (EditText) findViewById(R.id.itemAnswer);
                if (et.getText().toString().isEmpty()) {
                    Toast.makeText(this, "The value is empty!", Toast.LENGTH_SHORT).show();
                } else {
                    m.findItem(R.id.itemsAdded).setVisible(true);//Надо бы выполнять единожды
                    p.addItem(et.getText().toString(), eta.getText().toString());
                    et.setText("");
                    et = (EditText) findViewById(R.id.itemAnswer);
                    et.setText("");
                    Toast.makeText(this, "The item added!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        return false;
    }
}