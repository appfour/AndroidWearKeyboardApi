package com.appfour.wearkeyboard;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.view.inputmethod.*;
import android.widget.*;

public class DemoKeyboardActivity extends Activity {

    private String typedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get text from calling app
        final String callingPackageName = getCallingPackage();
        final String token = getIntent().getStringExtra("Token");
        String text = getIntent().getStringExtra("Text");
        int selectionStart = getIntent().getIntExtra("SelectionStart", 0);
        int selectionEnd = getIntent().getIntExtra("SelectionEnd", 0);
        String hintText = getIntent().getStringExtra("HintText");
        int inputType = getIntent().getIntExtra("InputType", 0);
        int action = getIntent().getIntExtra("Action", EditorInfo.IME_ACTION_NONE);

        LinearLayout layout = new LinearLayout(this);
        layout.setGravity(Gravity.BOTTOM);
        setContentView(layout);

        // The "A" key
        TextView textView = new TextView(this);
        layout.addView(textView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (100 * getResources().getDisplayMetrics().density)));
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(40);
        textView.setBackgroundColor(0xffeeeeee);
        textView.setTextColor(0xff333333);
        textView.setText("A");

        typedText = text;
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typedText += "A";

                // Send typed text
                Intent data = new Intent("appfour.intent.action.UPDATE_INPUT");
                data.setPackage(callingPackageName);
                data.putExtra("Token", token);
                data.putExtra("Text", typedText);
                data.putExtra("SelectionStart", typedText.length());
                data.putExtra("SelectionEnd", typedText.length());
                sendBroadcast(data);
            }
        });

        if (action != EditorInfo.IME_ACTION_NONE) {
            // Perform keyboard action on long press
            textView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Intent data = new Intent();
                    data.putExtra("PerformAction", true);
                    setResult(RESULT_OK, data);
                    finish();

                    return true;
                }
            });
        }
    }
}
