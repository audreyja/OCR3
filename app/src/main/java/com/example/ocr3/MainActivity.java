package com.example.ocr3;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import android.Manifest;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private SurfaceView surfaceView;
    private TextView textView;
    private CameraSource cameraSource;
    private static final int PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        surfaceView = findViewById(R.id.camera);
        textView = findViewById(R.id.text);

        startCameraSource();
    }

    private void startCameraSource(){
        final TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        if(!textRecognizer.isOperational()){
            Log.w("Tag", "Dependencies not loaded yet");
        }else{
            cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer).setFacing(CameraSource.CAMERA_FACING_BACK).setRequestedPreviewSize(1280, 1024).setAutoFocusEnabled(true).setRequestedFps(2.0f).build();

            surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
                    try{
                        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION);
                            return;
                        }
                        cameraSource.start(surfaceView.getHolder());
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                    // Release source for cameraSource
                }

                @Override
                public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
                    cameraSource.stop();
                }
            });

            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {
                    //Detect all text from camera
                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                    final SparseArray<TextBlock> items = detections.getDetectedItems();

                    if (items.size() != 0) {
                        textView.post(new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder stringBuilder = new StringBuilder();
                                OverlayView overlayView = findViewById(R.id.overlay_view);

                                for (int i = 0; i < items.size(); i++) {
                                    TextBlock item = items.valueAt(i);

                                    // Mendapatkan informasi konfigurasi kamera
                                    Size previewSize = cameraSource.getPreviewSize();
                                    int cameraWidth = previewSize.getWidth();
                                    int cameraHeight = previewSize.getHeight();

                                    // Mendapatkan dimensi tampilan overlay
                                    int overlayWidth = overlayView.getWidth();
                                    int overlayHeight = overlayView.getHeight();

                                    // Menghitung faktor skala untuk aspek rasio tampilan
                                    float scaleX = (float) cameraWidth / overlayWidth;
                                    float scaleY = (float) cameraHeight / overlayHeight;

                                    // Menghitung koordinat kamera yang sesuai dengan bingkai overlay
                                    int left = (int) (300 * scaleX);
                                    int top = (int) (200 * scaleY);
                                    int right = (int) (800 * scaleX);
                                    int bottom = (int) (400 * scaleY);

                                    // Periksa apakah teks terletak di dalam bingkai yang diinginkan
                                    if (item.getBoundingBox().left >= left &&
                                            item.getBoundingBox().top >= top &&
                                            item.getBoundingBox().right <= right &&
                                            item.getBoundingBox().bottom <= bottom) {
                                        stringBuilder.append(item.getValue());
                                        stringBuilder.append("\n");
                                    }
                                }

                                // Menampilkan teks yang berada di dalam bingkai ke dalam TextView
                                textView.setText(stringBuilder.toString());
                            }
                        });
                    }
                }
            });
        }
    }
}