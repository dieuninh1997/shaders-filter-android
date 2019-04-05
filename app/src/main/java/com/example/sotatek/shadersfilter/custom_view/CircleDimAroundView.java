package com.example.sotatek.shadersfilter.custom_view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.AttributeSet;
import android.view.View;

public class CircleDimAroundView extends View {

    Bitmap bm;
    Canvas cv;
    Paint eraser;

    public CircleDimAroundView(Context context) {
        super(context);
        Init();
    }

    public CircleDimAroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init();
    }

    public CircleDimAroundView(Context context, AttributeSet attrs,
                               int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init();
    }

    private void Init() {
        eraser = new Paint();
        eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        eraser.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        if (w != oldw || h != oldh) {
            bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            cv = new Canvas(bm);
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int w = getWidth();
        int h = getHeight();
        int radius = w > h ? h / 2 : w / 2;

        bm.eraseColor(Color.TRANSPARENT);
        cv.drawARGB(100, 0, 0, 0);
        cv.drawCircle(w / 2, h / 2, radius, eraser);
        canvas.drawBitmap(bm, 0, 0, null);
        super.onDraw(canvas);
    }

    private Bitmap blur(Bitmap original, float radius){
        Bitmap bitmap = Bitmap.createBitmap(original.getWidth(), original.getHeight(), Bitmap.Config.ARGB_8888);

        RenderScript rs= RenderScript.create(getContext());

        Allocation allocationIn = Allocation.createFromBitmap(rs, original);
        Allocation allocationOut = Allocation.createFromBitmap(rs, bitmap);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        blur.setInput(allocationIn);
        blur.setRadius(radius);
        blur.forEach(allocationOut);

        allocationOut.copyTo(bitmap);
        rs.destroy();

        return bitmap;
    }

}
