package com.example.ocr3;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class OverlayView extends View {
    private Paint borderPaint;

    public OverlayView(Context context) {
        super(context);
        init();
    }

    public OverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        borderPaint = new Paint();
        borderPaint.setColor(Color.RED);
        borderPaint.setStrokeWidth(5f);
        borderPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Mendapatkan ukuran overlay view
        int viewWidth = getWidth();
        int viewHeight = getHeight();

        // Menghitung koordinat bingkai yang diinginkan
        int left = 300;  // Koordinat kiri bingkai \kurang 50
        int top = 200;   // Koordinat atas bingkai \naik 50
        int right = 800; // Koordinat kanan bingkai \kurang 50
        int bottom = 300; // Koordinat bawah bingkai \naik 100

        // Menggambar bingkai di sekitar area yang diinginkan
        canvas.drawRect(left, top, right, bottom, borderPaint);
    }
}

