package com.koretsky.crammer.Forms;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.koretsky.crammer.R;
import com.koretsky.crammer.sm5.Package;


public class CreateActivity extends AppCompatActivity implements View.OnClickListener {

    Package p;
    Button btnCreatePackage;
    Button btnAddItem;
    Menu m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        setTitle("Creating package");
        btnCreatePackage = (Button) findViewById(R.id.btnCreatePackage);
        btnCreatePackage.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        m = menu;
        getMenuInflater().inflate(R.menu.menu_activity_create, menu);
        m.findItem(R.id.finishCreatingPackage).setVisible(false);
        m.findItem(R.id.finishCreatingPackage).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent = new Intent();
                try {
                    p.serializePackage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                setResult(RESULT_OK, intent);
                finish();
                return false;
            }
        });
        return true;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.btnCreatePackage):
                EditText et = (EditText) findViewById(R.id.packageName);
                if (et.getText().toString().isEmpty()) {
                    Toast.makeText(this, "The name is empty!", Toast.LENGTH_LONG).show();
                } else {
                    if (et.getText().toString().contains("/") || et.getText().toString().contains("."))
                        Toast.makeText(this, "The name shall not contain characters: \"/\" or \".\" !", Toast.LENGTH_LONG).show();
                    else {
                        try {
                            p = new Package(et.getText().toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        setContentView(R.layout.add_item);
                        btnAddItem = (Button) findViewById(R.id.btnAddItem);
                        btnAddItem.setOnClickListener(this);
                    }
                }

                break;
            case (R.id.btnAddItem):
                et = (EditText) findViewById(R.id.itemValue);
                EditText eta = (EditText) findViewById(R.id.itemAnswer);
                if (et.getText().toString().isEmpty()) {
                    Toast.makeText(this, "The value is empty!", Toast.LENGTH_SHORT).show();
                } else {
                    m.findItem(R.id.finishCreatingPackage).setVisible(true);//Надо бы выполнять единожды
                    Log.d("testor", et.getText().toString());
                    Log.d("testor", eta.getText().toString());
                    p.addItem(et.getText().toString(), eta.getText().toString());
                    et.setText("");
                    et = (EditText) findViewById(R.id.itemAnswer);
                    et.setText("");
                    Toast.makeText(this, "The item added!", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }
}
