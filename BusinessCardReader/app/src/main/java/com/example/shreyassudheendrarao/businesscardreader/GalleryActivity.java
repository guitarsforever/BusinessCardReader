package com.example.shreyassudheendrarao.businesscardreader;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    ImageView imageView;
    ImageButton btnProcess;

    Bitmap bitmap;
    private static int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        btnProcess = (ImageButton)findViewById(R.id.imageButton2);


        Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);

        btnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                if(!textRecognizer.isOperational())
                    Log.e("ERROR","Detector dependencies are not yet available");
                else{
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = textRecognizer.detect(frame);
                    StringBuilder stringBuilder = new StringBuilder();
                    Boolean isName = false, isPhone = false, isAddress = false,
                            isEmail = false;
                    List<String> all_lines = new ArrayList<>();
                    for(int i=0;i<items.size();++i)
                    {
                        TextBlock item = items.valueAt(i);
                        List<? extends Text> texts = item.getComponents();
                        for (Text t: texts) {
                            all_lines.add(t.getValue());
                        }
                        if (item.getValue().contains("Phone")|| item.getValue().contains("PHONE") ||item.getValue().contains("Mob")|| item.getValue().contains("p.")|| item.getValue().contains("tel") || item.getValue().contains("+1") || item.getValue().contains("Tel") || item.getValue().contains("P:")
                                ||item.getValue().contains("T.")  || item.getValue().contains("p.")|| item.getValue().contains("Cell")|| item.getValue().matches(".*^(\\d{10})|(([\\(]?([0-9]{3})[\\)]?)?[ \\.\\-]?([0-9]{3})[ \\.\\-]([0-9]{4}))$.*")) {
                            isPhone = true;
                        }
                        if (item.getValue().contains("Name"))  {
                            isName = true;
                        }
                        if (item.getValue().contains("Address") || item.getValue().matches(".*^\\d{5}$*.")){
                            isAddress = true;
                        }
                        if (item.getValue().contains("Email") || item.getValue().contains("@") || item.getValue().contains(".com") ||
                                item.getValue().matches(".*^[-a-z0-9~!$%^&*_=+}{\\'?]+(\\.[-a-z0-9~!$%^&*_=+}{\\'?]+)*@([a-z0-9_][-a-z0-9_]*(\\.[-a-z0-9_]+)*\\.(aero|arpa|biz|com|coop|edu|gov|info|int|mil|museum|name|net|org|pro|travel|mobi|[a-z][a-z])|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,5})?$*.")) {

                            isEmail = true;

                        }
                        stringBuilder.append(item.getValue());
                        stringBuilder.append("\n");
                    }
                    if (isName || isAddress || isPhone ||  isEmail) {

                        Intent newIntent = new Intent (GalleryActivity.this, SavingActivity2.class);
                        newIntent.putExtra("name", stringBuilder.toString());
                        newIntent.putStringArrayListExtra("test", (ArrayList<String>) all_lines);
                        startActivity(newIntent);
                        finish();

                    } else {
                        Toast.makeText(getApplicationContext(), "Could not parse, please try again", Toast.LENGTH_LONG);
                    }
                   // txtResult.setText(stringBuilder.toString());
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            ImageView imageView = (ImageView) findViewById(R.id.image_view);
            bitmap = BitmapFactory.decodeFile(picturePath);
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
    }

    public void OnCancelClick (View view) {
        Intent newIntent = new Intent (this, MainActivity.class);
        startActivity(newIntent);
        finish();
    }

}
