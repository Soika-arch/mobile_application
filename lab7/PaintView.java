package com.labs.javaa27;


import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.IntRange;
import androidx.annotation.Size;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Random;


public class PaintView extends View {

    private static final float paint_tolerance = 10;
    private Bitmap bmp;

    private Canvas bmp_canvas;
    private final Paint paint_for_screen;
    private final Paint paint_for_line;
    private final HashMap<Integer, Path> map_pid_path;
    private final HashMap<Integer, Point> map_pid_point_prev;


    public PaintView(Context context){
        super(context);
        paint_for_screen = new Paint();
        paint_for_line = new Paint();
        paint_for_line.setColor(toRGB(27f,207f,37f));


        paint_for_line.setAntiAlias(true);
        paint_for_line.setStrokeWidth(6);
        paint_for_line.setStyle(Paint.Style.STROKE);
        paint_for_line.setStrokeCap(Paint.Cap.ROUND);
        paint_for_line.setShadowLayer(5,1,1,Color.BLACK);
        map_pid_path = new HashMap<Integer, Path>();
        map_pid_point_prev = new HashMap<Integer, Point>();
    }

    @Override
    public void onSizeChanged(int w, int h, int oldW, int oldH)
    {
      bmp = Bitmap.createBitmap(getWidth(),
              getHeight(),Bitmap.Config.ARGB_8888);
      bmp_canvas = new Canvas (bmp);
      bmp.eraseColor(Color.BLACK);
    }
    void clear()
    {
        map_pid_path.clear();
        map_pid_point_prev.clear();
        bmp.eraseColor(Color.BLACK);
        invalidate();
    }
    public void set_line_color(int color)
    {
        paint_for_line.setColor(color);
    }
    public int get_line_color()
    {
     return paint_for_line.getColor();
    }
    public void set_line_width(int width)
    {
     paint_for_line.setStrokeWidth(width);
    }
    public int get_line_width()
    {
        return (int) paint_for_line.getStrokeWidth();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.drawBitmap(bmp, 0 ,0,paint_for_screen);
        for (Integer key : map_pid_path.keySet())
            canvas.drawPath(map_pid_path.get(key),paint_for_line);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        int action = event.getActionMasked();
        int actionIndex = event.getActionIndex();
            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN)
            {
                touchStarted(event.getX(actionIndex), event.getY(actionIndex),
                        event.getPointerId(actionIndex));
            }
            else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP)
            {
                touchEnded(event.getPointerId(actionIndex));
            }
            else
            {
               touchMoved(event);
            }
            invalidate();
            return true;
    }

    private void touchStarted(float x, float y, int lineID)
    {
        Path path;
        Point point;

        if (map_pid_path.containsKey(lineID))
        {
            path = map_pid_path.get(lineID);
            path.reset();
            point = map_pid_point_prev.get(lineID);
        }
        else
        {
            path = new Path();
            map_pid_path.put(lineID, path);
            point = new Point();
            map_pid_point_prev.put(lineID, point);
        }
        path.moveTo(x, y);  point.x = (int) x;  point.y = (int) y;
    }


    private void touchMoved(MotionEvent event)
    {
        for (int i = 0; i < event.getPointerCount();i++)
        {
            int pid = event.getPointerId(i);
            int pointerIndex = event.findPointerIndex(pid);

            if (map_pid_path.containsKey(pid))
            {
                float newX = event.getX(pointerIndex);
                float newY = event.getY(pointerIndex);

                Path path = map_pid_path.get(pid);
                Point point = map_pid_point_prev.get(pid);

                float deltaX = Math.abs(newX - point.x);
                float deltaY = Math.abs(newY - point.y);
                if (deltaX >= paint_tolerance || deltaY >= paint_tolerance)
                {
                     path.quadTo(point.x,point.y, (newX + point.x)/2,
                             (newY + point.y)/2);
                     point.x = (int) newX;
                     point.y = (int) newY;

                }
            }
        }
    }

    private void touchEnded (int lineID)
    {
       Path path = map_pid_path.get(lineID);
       bmp_canvas.drawPath(path,paint_for_line);
       path.reset();
    }

    public void save_image()
    {
         String fileName = "painting" + (System.currentTimeMillis()/1000);
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        values.put(MediaStore.Images.Media.DATE_ADDED,System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/PNG");

        Uri uri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        try
        {
            OutputStream outStream =
                    getContext().getContentResolver().openOutputStream(uri);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
            Toast message = Toast.makeText(getContext(),
                    "Save" + getResources().getString(R.string.painting_saved)
                            + "" + fileName, Toast.LENGTH_SHORT);

            message.setGravity(Gravity.CENTER,message.getXOffset() / 2,message.getYOffset() / 2);
            message.show();
        }
        catch (IOException ex)
        {
            Toast message = Toast.makeText(getContext(),
                    "error! "+ ex,Toast.LENGTH_SHORT);
            message.setGravity(Gravity.CENTER,message.getXOffset() / 2,message.getYOffset() / 2);
            message.show();

        }

    }


    private static float HueToRGB(float p, float q, float h) {
        if (h < 0) h += 1;
        if (h > 1) h -= 1;
        if (6 * h < 1) {
            return p + ((q - p) * 6 * h);
        }
        if (2 * h < 1) {
            return q;
        }
        if (3 * h < 2) {
            return p + ((q - p) * 6 * ((2.0f / 3.0f) - h));
        }
        return p;
    }
    /**
     *
     * @param h - hue from 0 to 360
     * @param s - saturation from 0 to 100
     * @param l - lightness from 0 to 100
     * @return Color (int
     */
    private static int toRGB(float h, float s, float l) {
        h = h % 360.0f;
        h /= 360f;
        s /= 100f;
        l /= 100f;

        float q = 0;

        if (l < 0.5)
            q = l * (1 + s);
        else
            q = (l + s) - (s * l);

        float p = 2 * l - q;

        float r = Math.max(0, HueToRGB(p, q, h + (1.0f / 3.0f)));
        float g = Math.max(0, HueToRGB(p, q, h));
        float b = Math.max(0, HueToRGB(p, q, h - (1.0f / 3.0f)));

        r = Math.min(r, 1.0f);
        g = Math.min(g, 1.0f);
        b = Math.min(b, 1.0f);

        int red = (int) (r * 255);
        int green = (int) (g * 255);
        int blue = (int) (b * 255);
        return Color.rgb(red, green, blue);
    }

}
