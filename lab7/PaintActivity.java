package com.labs.javaa27;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


public class PaintActivity extends AppCompatActivity {
    static final int MENU_COLOR_WHITE = Menu.FIRST +1;
    static final int MENU_NEW_IMAGE = Menu.FIRST +2;
    static final int MENU_SAVE = Menu.FIRST +3;

    PaintView paintView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        paintView = new PaintView (this);
        setContentView(paintView);

    }
    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
     super.onCreateOptionsMenu(menu);
     menu.add(Menu.NONE, MENU_COLOR_WHITE, Menu.NONE, R.string.menu_erase);
     menu.add(Menu.NONE, MENU_NEW_IMAGE, Menu.NONE, R.string.clear);
     menu.add(Menu.NONE, MENU_SAVE, Menu.NONE, R.string.save);
     return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case MENU_COLOR_WHITE:
                paintView.set_line_color(Color.WHITE);
                return true;
            case MENU_NEW_IMAGE:
                paintView.clear();
                return true;
            case MENU_SAVE:
                paintView.save_image();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}