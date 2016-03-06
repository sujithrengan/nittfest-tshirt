package org.delta.nittfest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

/**
 * Created by HP on 06-03-2016.
 */
public class PopView extends SurfaceView implements SurfaceHolder.Callback{
    Paint p;
    int i=0;
    ArrayList<block> blocks;

    public class block{
        float x;
        float y;
        block(float x,float y){
            this.x=x;
            this.y=y;
        }
    }
    public PopView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);

        this.setZOrderOnTop(true);
        this.getHolder().setFormat(PixelFormat.TRANSPARENT);
        blocks=new ArrayList<block>();

        blocks.add(new block(129.5f, 55.5f));
        blocks.add(new block(219.5f,38.5f));
        blocks.add(new block(303.5f,47.5f));
        blocks.add(new block(322.5f,137.5f));
        blocks.add(new block(231.5f,136.5f));
        blocks.add(new block(140.5f,150.5f));
        blocks.add(new block(110.5f,239.5f));
        blocks.add(new block(192.5f,234.5f));
        blocks.add(new block(294.5f,229.5f));
        blocks.add(new block(320.5f,340.5f));
        blocks.add(new block(236.5f,323.5f));
        blocks.add(new block(139.5f,340.5f));

        p=new Paint();
        p.setColor(Color.parseColor("#d4d4d4"));



    }


    public PopView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //Log.e("popview",String.valueOf(x) );


        canvas.drawCircle(blocks.get(i % 12).x, blocks.get(i % 12).y, 95f, p);
        //i++;
        invalidate();
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
