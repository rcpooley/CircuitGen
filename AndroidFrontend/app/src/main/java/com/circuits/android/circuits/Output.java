package com.circuits.android.circuits;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;

import static android.R.id.input;
import com.google.gson.Gson;

public class Output extends AppCompatActivity {

    Truth truthInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_output);

        truthInfo = OpeningPage.getObject();

        tableInit();
    }

    public void tableInit() {
        TableLayout truthTable = (TableLayout) findViewById(R.id.truthTable);
        truthTable.setStretchAllColumns(true);

        int numberofInputs = truthInfo.getNumInputs();

        TableRow actualTable = new TableRow(this);

        for(int i = 0; i < truthInfo.getTable().length; i++) { //creates four columns

            LinearLayout currentColumn = new LinearLayout(this);
            currentColumn.setOrientation(LinearLayout.VERTICAL);

            TextView headerInput = new TextView(this);
            if (i < truthInfo.getNumInputs()) {
                headerInput.setText("INPUT " + (i + 1));
            }
            else {
                headerInput.setText("OUTPUT " + (i - truthInfo.getNumInputs() + 1));
            }
            headerInput.setTypeface(null, Typeface.BOLD);

            currentColumn.addView(headerInput);
            currentColumn.setPadding(24,0,24,0);

            for (int j = 0; j < truthInfo.getTable()[0].length; j++) { //each column gets 16 parts
                TextView indCell = new TextView(this); //given an array the column is each one input

                String cellString = "" + truthInfo.getTable()[i][j];
                indCell.setText(cellString.toUpperCase());

                indCell.setPadding(8,8,8,8);

                if(truthInfo.getTable()[i][j]) {
                    indCell.setTextColor(Color.rgb(0,200,0));
                } else {
                    indCell.setTextColor(Color.rgb(200, 0,0));
                }

                currentColumn.addView(indCell, new TableLayout.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT, 1f));
            }

            actualTable.addView(currentColumn);
        }

        //truthTable.addView(topHeader);
        truthTable.addView(actualTable);

    }

    public void goToTogglePage (View view) {
        Intent transitionintent = new Intent(this, TogglePage.class);
        startActivity(transitionintent);
    }
}
