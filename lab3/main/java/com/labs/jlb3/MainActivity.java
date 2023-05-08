package com.labs.jlb3;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onEditText_click_setBtnText(View view){
        ((Button)findViewById(R.id.button2)).setText(((EditText)view).getText());
    }

    public void onToggleVisibilityButtonClick(View view){
        boolean checked = ((ToggleButton) view).isChecked();
        findViewById(R.id.button3).setVisibility(checked?View.VISIBLE:View.GONE);
    }
}