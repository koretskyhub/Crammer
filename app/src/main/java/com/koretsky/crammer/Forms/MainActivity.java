package com.koretsky.crammer.Forms;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.koretsky.crammer.R;
import com.koretsky.crammer.sm5.Package;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ListView mainLV;
    String handlePackName = null;
    Context that = this;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.newPackage == item.getItemId()) {
            Intent intent = new Intent(this, CreateActivity.class);
            startActivityForResult(intent, 1);
        }
        return super.onOptionsItemSelected(item);
    }

/*    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }*/

    @Override
    protected void onStart() {
        mainLV = (ListView) findViewById(R.id.mainLV);
        ArrayList<String> packageList = new ArrayList<String>(Arrays.asList(getFilesDir().list()));
        Comparator<String> comparator = new Comparator<String>() {
            @Override
            public int compare(String left, String right) {
                DateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
                Date leftDate = null, rightDate = null;
                try {
                    leftDate = format.parse(left.substring(0, left.indexOf('_')));
                    rightDate = format.parse(right.substring(0, right.indexOf('_')));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return rightDate.compareTo(leftDate);
            }
        };
        if (!packageList.isEmpty()) {
            Collections.sort(packageList, comparator);
        }
        DateFormat fullFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        DateFormat shortFormat = new SimpleDateFormat("EEE, d MMM");
        ArrayList<HashMap<String, String>> listForAdapter = new ArrayList<>();
        HashMap<String, String> map;
        for (int i = 0; i < packageList.size(); i++) {
            map = new HashMap<>();
            map.put("name", packageList.get(i).substring(packageList.get(i).indexOf("_") + 1, packageList.get(i).indexOf(".")));
            try {
                Date d = fullFormat.parse(packageList.get(i).substring(0, packageList.get(i).indexOf('_')));
                String sd = new String(shortFormat.format(d).toString());
                map.put("repDate", "Should be repeated at: " + sd);
                listForAdapter.add(map);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        SimpleAdapter adapter = new SimpleAdapter(this, listForAdapter,
                android.R.layout.simple_list_item_2,
                new String[]{"name", "repDate"},
                new int[]{android.R.id.text1, android.R.id.text2});
        mainLV.setAdapter(adapter);
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainLV = (ListView) findViewById(R.id.mainLV);
        mainLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HashMap hashMapPackName = (HashMap) mainLV.getItemAtPosition(i);
                handlePackName = hashMapPackName.get("name").toString();
                Intent intent = new Intent(that, ReviewActivity.class);
                intent.putExtra("packagePath", handlePackName);
                startActivityForResult(intent, 1);
            }
        });
        registerForContextMenu(mainLV);
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.mainLV) {
            menu.add(0, 0, 0, "Modify items");
            menu.add(0, 1, 2, "Remove");
            menu.add(0, 2, 1, "Rename package");

        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        HashMap hashMapPackName = (HashMap) mainLV.getItemAtPosition(info.position);
        handlePackName = hashMapPackName.get("name").toString();
        switch (item.getItemId()) {
            case 0:
                Intent intent = new Intent(this, ModifyActivity.class);
                for (String s : new ArrayList<String>(Arrays.asList(getFilesDir().list()))
                        ) {
                    if (s.substring(s.indexOf("_") + 1, s.indexOf(".")).equals(handlePackName)) {
                        handlePackName = s;
                        break;
                    }
                }
                Log.d("FORLOG", handlePackName);
                intent.putExtra("packagePath", handlePackName);
                startActivityForResult(intent, 1);
                return true;
            case 1:
                AlertDialog.Builder builderDelete = new AlertDialog.Builder(this);
                builderDelete.setTitle("Delete package?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                for (String s : new ArrayList<String>(Arrays.asList(getFilesDir().list()))
                                        ) {
                                    if (s.substring(s.indexOf("_") + 1, s.indexOf(".")).equals(handlePackName)) {
                                        handlePackName = s;
                                        break;
                                    }
                                }
                                Log.d("LOGD", handlePackName);
                                new File(getFilesDir(), handlePackName).delete();
                                onStart();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                builderDelete.create().show();
                return true;
            case 2:
                final AlertDialog.Builder builderRename = new AlertDialog.Builder(this);
                LayoutInflater inflater = this.getLayoutInflater();
                final View renameV = inflater.inflate(R.layout.dialog_rename, null);
                builderRename.setView(renameV)
                        .setPositiveButton("Rename", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                EditText et = (EditText) renameV.findViewById(R.id.etRenamePackage);
                                if (!et.getText().toString().isEmpty()) {
                                    for (String s : new ArrayList<String>(Arrays.asList(getFilesDir().list()))) {
                                        if (s.substring(s.indexOf("_") + 1, s.indexOf(".")).equals(handlePackName))
                                            handlePackName = s;
                                    }
                                    Package p = null;
                                    try {
                                        p = Package.deserializePackage(getFilesDir(), handlePackName);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    p.setPackageName(et.getText().toString());
                                    new File(getFilesDir(), handlePackName).delete();
                                    try {
                                        p.serializePackage();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    onStart();
                                } else
                                    Toast.makeText(that, "The name is empty!", Toast.LENGTH_SHORT);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        })
                        .setTitle(R.string.renamingPackage);
                builderRename.create().show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}