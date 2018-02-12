package com.example.shreyassudheendrarao.businesscardreader;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CameraActivity extends AppCompatActivity {
    SurfaceView cameraView;
    TextView textView;
    CameraSource cameraSource;
    final int RequestCameraPermissionID = 1001;
    Boolean flag =true;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
            break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        cameraView = (SurfaceView) findViewById(R.id.surface_view);
        textView = (TextView) findViewById(R.id.text_view);
        flag = true;
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational()) {
            Log.w("MainActivity", "Detector dependencies are not yet available");
        } else {

            cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(2.0f)
                    .setAutoFocusEnabled(true)
                    .build();
            cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {

                    try {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(CameraActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    RequestCameraPermissionID);
                            return;
                        }
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                    cameraSource.stop();
                }
            });

            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {

                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if(items.size() != 0)
                    {
                       // textView.post(new Runnable() {
                         //   @Override
                           // public void run() {
                                StringBuilder stringBuilder = new StringBuilder();
                                Boolean isName = false, isPhone = false, isAddress = false,
                                isEmail = false;
                                List<String> all_lines = new ArrayList<>();
                                for(int i =0;i<items.size();++i)
                                {
                                    TextBlock item = items.valueAt(i);
                                    List<? extends Text> texts = item.getComponents();
                                    for (Text t: texts) {
                                        all_lines.add(t.getValue());
                                    }

//                                    if (item.getValue().contains("Name") && item.getValue().contains("Phone")
//                                            && item.getValue().contains("Address") && item.getValue().contains("Email")) {
//                                        isName = true;
//                                        isPhone = true;
//                                        isAddress = true;
//                                        isEmail = true;
//                                    }

                                    if (item.getValue().contains("Phone")|| item.getValue().contains("PHONE")|| item.getValue().contains("Mob") || item.getValue().contains("tel") || item.getValue().contains("+1") || item.getValue().contains("Tel") || item.getValue().contains("P:")
                                            || item.getValue().contains("T.")  || item.getValue().contains("p.")|| item.getValue().contains("Cell")|| item.getValue().matches(".*^(\\d{10})|(([\\(]?([0-9]{3})[\\)]?)?[ \\.\\-]?([0-9]{3})[ \\.\\-]([0-9]{4}))$.*")) {
                                        isPhone = true;
                                    }
                                    if (item.getValue().contains("Name"))  {
                                        isName = true;
                                    }
                                    if (item.getValue().contains("Address")
                                            || item.getValue().matches(".*^\\d{5}$*.")){
                                        isAddress = true;
                                    }
                                    if (item.getValue().contains("Email") || item.getValue().contains("@") || item.getValue().contains(".com") ||
                                            item.getValue().matches(".*^[-a-z0-9~!$%^&*_=+}{\\'?]+(\\.[-a-z0-9~!$%^&*_=+}{\\'?]+)*@([a-z0-9_][-a-z0-9_]*(\\.[-a-z0-9_]+)*\\.(aero|arpa|biz|com|coop|edu|gov|info|int|mil|museum|name|net|org|pro|travel|mobi|[a-z][a-z])|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,5})?$*.")) {

                                        isEmail = true;

                                    }

                                    stringBuilder.append(item.getValue());
                                    stringBuilder.append("\n");
                                }
                                if ((isName || isAddress || isPhone || (isPhone && isEmail)) && flag) {
                                        // Sleep for 200 milliseconds.
                                        //Just to display the progress slowly
                                        flag =false;
                                        release();
                                        Intent newIntent = new Intent (CameraActivity.this, SavingActivity2.class);
                                        newIntent.putExtra("name", stringBuilder.toString());
                                        newIntent.putStringArrayListExtra("test", (ArrayList<String>) all_lines);
                                        startActivity(newIntent);
                                        finish();
                                        return;

                                  //  return;
                                }

                                //textView.setText(stringBuilder.toString());
                            }
                    //    });

                   // }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        flag = true;
    }
}
