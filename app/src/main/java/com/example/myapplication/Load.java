package com.example.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Load extends AppCompatActivity {

    int i;
    int counter = 0;
    TableRow tableRow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        String[] fileNames = getFilesDir().list();
        TableLayout tableLayout = findViewById(R.id.table_layout);
        for (i = 0; i < fileNames.length; i++){
            System.out.println(counter);
            if (fileNames[i].contains(".xml")) {

                if (counter == 0) {
                    System.out.println("FIRST XML");
                    tableRow = new TableRow(this);
                    tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    TextView textView = new TextView(this);
                    textView.setText(fileNames[i]);
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16); // Change 16 to the desired size in SP
                    tableRow.setMinimumHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics())); // Change 48 to the desired height in DP

                    textView.setId(1000 + i);
                    textView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                    textView.setOnClickListener(selectFile);
                    tableRow.addView(textView);
                    counter += 1;
                } else if (counter == 1) {
                    System.out.println("SECOND XML");
                    TextView textView = new TextView(this);
                    textView.setText(fileNames[i]);
                    textView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                    textView.setId(1000 + i);
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16); // Change 16 to the desired size in SP
                    tableRow.setMinimumHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics())); // Change 48 to the desired height in DP

                    textView.setOnClickListener(selectFile);
                    tableRow.addView(textView);
                    tableLayout.addView(tableRow);
                    counter = 0;
                }
            }
            if (i+1==fileNames.length) {
                if (counter == 1) {
                    System.out.println("COUNTER"); //check if odd and last one, if so add blank text to create tablerow
                    TextView textView = new TextView(this);
                    textView.setText(" ");
                    textView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));

                    tableRow.addView(textView);
                    tableLayout.addView(tableRow);
                    counter = 0;

                }
            }
        }

    }
    View.OnClickListener selectFile=new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            int id=v.getId();
            TextView textView = findViewById(id);
            Intent intent = new Intent(Load.this, MainActivity.class);
            intent.putExtra("file", textView.getText().toString());
            startActivity(intent);

        }
    };
    }
