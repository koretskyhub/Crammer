package com.koretsky.crammer;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.koretsky.crammer.sm5.CrammerItem;
import com.koretsky.crammer.sm5.Package;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class ReviewActivity extends AppCompatActivity implements View.OnClickListener {

    private Package p = null;
    private Iterator iter = null;
    private Iterator iterForBadMarked = null;
    private TextView tvItemValue = null;
    private TextView tvItemAnswer = null;
    private Button btnNextItem = null;
    private RatingBar ratingBar = null;
    private String answer = null;
    private CrammerItem item = null;
    private ArrayList<CrammerItem> badMarkedItems = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_help_marking, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.showHelp == item.getItemId()) {
            Intent intent = new Intent(this, Help.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        Intent intent = getIntent();
        String pName = intent.getStringExtra("packagePath");
        setTitle("Review " + pName);
        for (String s : new ArrayList<String>(Arrays.asList(getFilesDir().list()))
                ) {
            if (s.substring(s.indexOf("_") + 1, s.indexOf(".")).equals(pName)) {
                try {
                    p = Package.deserializePackage(getFilesDir(), s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        badMarkedItems = new ArrayList<CrammerItem>();
        iter = p.getCrammerItemsArrayList().iterator();
        iterForBadMarked = badMarkedItems.iterator();
        tvItemValue = (TextView) findViewById(R.id.tvItemValue);
        tvItemValue.setMovementMethod(new ScrollingMovementMethod());
        tvItemAnswer = (TextView) findViewById(R.id.tvItemAnswer);
        tvItemAnswer.setMovementMethod(new ScrollingMovementMethod());
        btnNextItem = (Button) findViewById(R.id.btnNextItem);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        tvItemAnswer.setOnClickListener(this);
        btnNextItem.setOnClickListener(this);
        prepareItem();
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

    protected void prepareItem() {
        if (iter.hasNext()) {
            item = (CrammerItem) iter.next();
            tvItemAnswer.setText("");
            tvItemValue.setText(item.getValue());
            ratingBar.setRating(0);
            answer = item.getAnswer();
            if (!answer.isEmpty()) {
                tvItemAnswer.setText(R.string.tapToView);
                ratingBar.setEnabled(false);
                btnNextItem.setEnabled(false);
            }
        } else {
            if (!badMarkedItems.isEmpty()) {
                item = badMarkedItems.get(badMarkedItems.size() - 1);
                badMarkedItems.remove(badMarkedItems.size() - 1);
                tvItemAnswer.setText("");
                tvItemValue.setText(item.getValue());
                ratingBar.setRating(0);
                answer = item.getAnswer();
                if (!answer.isEmpty()) {
                    tvItemAnswer.setText(R.string.tapToView);
                    ratingBar.setEnabled(false);
                    btnNextItem.setEnabled(false);
                }
            } else {
                AlertDialog.Builder builderDelete = new AlertDialog.Builder(this);
                builderDelete.setTitle("Well Done!")
                        .setIcon(R.mipmap.ic_well_done)
                        .setNeutralButton("Go to main menu", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                onPause();
                                finish();
                            }
                        });
                builderDelete.create().show();
            }
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.tvItemAnswer):
                ratingBar.setEnabled(true);
                tvItemAnswer.setText(answer);
                btnNextItem.setEnabled(true);
                break;
            case (R.id.btnNextItem):
                if (ratingBar.getRating() == 0.0) {
                    Toast.makeText(this, "Select your mark", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (ratingBar.getRating() < 3.0) badMarkedItems.add(item);
                Log.d("Rating", String.valueOf(ratingBar.getRating()));
                p.OnItemReviewed(item, (int) ratingBar.getRating());
                prepareItem();
                break;
        }
    }
}