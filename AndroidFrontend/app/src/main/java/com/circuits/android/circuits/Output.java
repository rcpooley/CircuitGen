package com.circuits.android.circuits;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import static android.R.id.input;

public class Output extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_output);

        tableInit();
    }

    public void tableInit() {
        TableLayout truthTable = (TableLayout) findViewById(R.id.truthTable);

        TableRow topHeader = new TableRow(this);

        TextView headerText = new TextView(this);
        headerText.setText("Truth Table Based on Your Pics");
        topHeader.addView(headerText);

        TableRow actualTable = new TableRow(this);

        LinearLayout cellOne = new LinearLayout(this);

        TextView headerinput1 = new TextView(this);
        headerinput1.setText("Input 1");
        TextView inputone = new TextView(this);
        inputone.setText("0");
        TextView inputtwo = new TextView(this);
        inputtwo.setText("0");
        TextView inputthree = new TextView(this);
        inputthree.setText("1");
        TextView inputfour = new TextView(this);
        inputfour.setText("1");

        cellOne.addView(inputone);
        cellOne.addView(inputtwo);
        cellOne.addView(inputthree);
        cellOne.addView(inputfour);


        LinearLayout cellTwo = new LinearLayout(this);

        TextView headerinput2 = new TextView(this);
        headerinput2.setText("Input 1");
        TextView input1 = new TextView(this);
        input1.setText("0");
        TextView input2 = new TextView(this);
        input2.setText("1");
        TextView input3 = new TextView(this);
        input3.setText("0");
        TextView input4 = new TextView(this);
        input4.setText("1");

        cellOne.addView(input1);
        cellOne.addView(input2);
        cellOne.addView(input3);
        cellOne.addView(input4);

        actualTable.addView(cellOne);
        actualTable.addView(cellTwo);

    }
}
