package com.circuits.android.circuits;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import static android.R.attr.bottom;
import static android.R.attr.button;
import static android.R.attr.left;
import static android.R.attr.right;
import static android.R.attr.top;
import static android.R.id.input;
import static android.app.ProgressDialog.STYLE_SPINNER;

public class TogglePage extends AppCompatActivity {

    Truth truthInfo;
    int numberOfInputs; //5
    private boolean[] inputValues; //default is all false.
    private boolean[][] actualTable;  //len = 8 col = 32

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toggle_page);
        truthInfo = OpeningPage.getObject();

        Handler handler = new Handler(this.getMainLooper());

        handler.post(new Runnable() {
            @Override
            public void run() {
                truthInfo = OpeningPage.getObject();
                numberOfInputs = truthInfo.getNumInputs();
                actualTable = truthInfo.getTable();

                inputValues = new boolean[numberOfInputs];

                inputSideInit();
                outputSideInit();
            }
        });

    }

    public void inputSideInit() {
        LinearLayout inputLayout = (LinearLayout) findViewById(R.id.inputSide);

        for (int i = 0; i < numberOfInputs; i++) {
            Button inputButton = new Button(this);
            inputButton.setShowSoftInputOnFocus(true);
            inputButton.setShadowLayer(1,1,1, Color.GRAY);

            LayoutParams params = new LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT
            );
            params.setMargins(16, 16, 16, 16);
            inputButton.setLayoutParams(params);

            inputButton.setText("INPUT " + (i + 1));
            inputButton.setTextColor(Color.LTGRAY);
            inputButton.setBackgroundColor(Color.alpha(800));
            inputButton.setId(i + 50);

            inputButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    inputClicked(v);
                    reconfigure();
                }
            });
            inputLayout.addView(inputButton);
        }

        System.out.println("Go into inputSideiNit");
    }

    public void outputSideInit() {
        LinearLayout outputLayout = (LinearLayout) findViewById(R.id.outputSide);

        for (int i = 0; i < actualTable.length - numberOfInputs; i++) {
            TextView output = new TextView(this);
            output.setText("Output" + (i + 1));
            output.setTextColor(Color.rgb(200,0,0));
            output.setId(i);
            outputLayout.addView(output);
        }

        System.out.println("Go into outputsideinit");
    }

    /*
    * This method is called whenever a button is clicked. When some button is clicked, the value
    * should be toggled (along with color) and then the reconfigure method should be called.
    * This method goes through each input button value and then recompiles the output configuration
    * based on what the truth table has
    * */
    public void reconfigure() {
        int rowOfInterest = 0;
        String binary = "";

        for (int i = 0; i < inputValues.length; i++) {
            //put them all together to form a binary number
            if (inputValues[i]) {
                binary = binary + "1";
            } else {
                binary = binary + "0";
            }
        }
        rowOfInterest = Integer.parseInt(binary , 2);//add one
        System.out.println(rowOfInterest);
        boolean[] assignedRow = new boolean[actualTable.length];

        for (int x = 0; x < actualTable.length; x++) { //len (s col) = 8, col (act rows) = 32
            assignedRow[x] = actualTable[x][rowOfInterest];
            System.out.print(assignedRow[x] + " ");
        }
        System.out.println();

        //go to that row, and then use those outputs

        for (int j = numberOfInputs; j < assignedRow.length; j++) {
            TextView currentOutText = (TextView) findViewById(j - (numberOfInputs));

            //System.out.println(currentOutText.getText() + " " + (j - (numberOfInputs)));
            //System.out.println("" + assignedRow[j]);

            if (assignedRow[j]) {
                currentOutText.setTextColor(Color.rgb(0, 200, 0));
            } else {
                currentOutText.setTextColor(Color.rgb(200, 0, 0));
            }
        }


    }

    //This should be able to be called due to any button. This method will see which button was
    //clicked
    public void inputClicked(View view) {
        Button whichButton = (Button) view;
        int indexOfButton = 0;

        for (int i = 0; i < numberOfInputs; i++) {
            if (whichButton.getId() == i + 50) {
                indexOfButton = i;//then we know that it's button i, and we toggle accordingly
                inputValues[i] = !inputValues[i]; //toggled
            }
        }

        whichButton.setTextColor(Color.LTGRAY);

        if (inputValues[indexOfButton]) {
            whichButton.setBackgroundColor(Color.alpha(800));
        } else {
            whichButton.setBackgroundColor(Color.rgb(200, 0, 0));
        }

    }


}
