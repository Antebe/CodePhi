package sams12.code;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Code extends AppCompatActivity {
    public final static int QRcodeWidth = 350;
    public static final int CAMERA_REQUEST = 10;
    Uri fileUri;


    private Bitmap bitmap;
    private EditText enter;
    private String str;
    private Button go;
    private TextView result;
    private Button read;
    private Button write;
    private TextView path;
    private String line;
    private Button paste;
    private ImageView imageView;

    private Button imagebtn;
    private ShareActionProvider mShareActionProvider;

    private SpeechRecognizer recognizer;
    private Intent speechRecognizerIntent;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        /** Inflating the current activity's menu with res/menu/items.xml */
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem shareItem = menu.findItem(R.id.share);
        /** Getting the actionprovider associated with the menu ex_005_item whose id is share */
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);


        Intent intent = getDefaultShareIntent();
        /** Setting a share intent */
        mShareActionProvider.setShareIntent(intent);

        return true;

    }

    private Intent getDefaultShareIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, result.getText().toString());
        return intent;
    }


    public void onInfoClick(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String s = new StringBuilder()
                .append("  \"ENCODE\" : Enter text - get result).\n")
                .append(" \"DECODE\", Enter code - get text).\n")
                .append("\"GO\" get text .\n")
                .toString();
        builder.setTitle("Rules")
                .setMessage(s)
                .setCancelable(false)
                .setNegativeButton("ОК",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.code_layout);
        paste = (Button) findViewById(R.id.paste);
        enter = (EditText) findViewById(R.id.enter);
        result = (TextView) findViewById(R.id.result);
        go = (Button) findViewById(R.id.go);
        write = (Button) findViewById(R.id.write);
        path = (TextView) findViewById(R.id.path);
        imageView = (ImageView) findViewById(R.id.imageView);


        imagebtn = (Button) findViewById(R.id.imagebtn);
        imagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {


                    String value = result.getText().toString();
                    result.setText("");

                    BigInteger resul = new BigInteger(value);
                    byte[] byteArray = resul.toByteArray();

                    if (!value.isEmpty()) {
                        value = value;
                        try {
                            bitmap = TextToImageEncode(value);

                            imageView.setImageBitmap(bitmap);
                            SavePhotoButton(imageView);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        enter.requestFocus();
                        Toast.makeText(Code.this, "Please Enter Your Scanned Test", Toast.LENGTH_LONG).show();
                    }


                }catch (Exception e){e.printStackTrace();}
            }
        });

        paste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ClipboardManager myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData abc = myClipboard.getPrimaryClip();
                    ClipData.Item item = abc.getItemAt(0);

                    String text = item.getText().toString();
                    enter.setText(text);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        go.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                setText();

            }
        });
        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setWrote();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);

        }
        checkPermission();


        ImageButton recorder = (ImageButton) findViewById(R.id.recorder);
        recognizer = SpeechRecognizer.createSpeechRecognizer(this);

        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.CHINA);

        recognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                List<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if (matches != null) {
                    enter.setText(matches.get(0));
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });

        recorder.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        recognizer.stopListening();
                        result.setHint("You will see the input here");
                        break;
                    case MotionEvent.ACTION_DOWN:
                        result.setText("");
                        result.setHint("Listeninig...");
                        recognizer.startListening(speechRecognizerIntent);
                        break;
                }
                return false;
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1000:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Refused", Toast.LENGTH_SHORT).show();
                }
                finish();
        }
    }

    private void setWrote() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");
        String datetime = dateformat.format(c.getTime());
        String filename = datetime;
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), filename);
        try {
            String line = result.getText().toString();

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(line.getBytes());
            fos.close();
            Toast.makeText(Code.this, "The text is saved" + getFilesDir(), Toast.LENGTH_SHORT).show();


        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }


        path.setText(Environment.getExternalStorageDirectory().getAbsolutePath());


    }

    private static final int REQUEST_CODE = 43;

//    private void getRead() {
//Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//intent.setType("text/plain");
//intent.addCategory(Intent.CATEGORY_OPENABLE);
//startActivityForResult(intent, REQUEST_CODE);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
//            if (data != null){
//                Uri uri = data.getData();
//                String path2 = uri.getPath();
//                Toast.makeText(this, "path: " + uri.getPath(), Toast.LENGTH_SHORT
//                ).show();
//                path2 = path2.substring(path2.indexOf(":") + 1);
//                path.setText(uri.getPath());
//                result.setText(readText(path2));
//
//
//            }
//        }
//
//    }


//    private String readText(String input){
//        File file = new File(input);
//        StringBuilder text = new StringBuilder();
//        try {
//            BufferedReader br = new BufferedReader(new FileReader(file));
//            while ((line = br.readLine()) != null){
//                text.append(line);
//                text.append("\n");
//            }
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return text.toString();
//    }

    public void setText() {
        try {
            str = enter.getText().toString();
            char[] arrayOfChar = str.toCharArray();
            int len = arrayOfChar.length;//пошук довжини

            BigInteger[] series = new BigInteger[95 * len + 1];
            series[0] = BigInteger.valueOf(1);
            series[1] = BigInteger.valueOf(1);
            for (int i = 2; i < series.length; i++) {
                series[i] = series[i - 2].add(series[i - 1]);
            }//генерація масиву чисел Фібоначчі


            char[] z = str.toCharArray();
            int[] number = new int[len];
            for (int j = 0; j < z.length; j++) {
                number[j] = (int) z[j] - 32;
            } //генерація алфавіту


            int pos = 1;
            BigInteger resul = BigInteger.valueOf(0);
            for (int i = 0; i < len; i++) {
                int n = 95;
                resul = resul.add(series[number[i] + n * (pos - 1)]);
                pos++;
            } //власне шифрування

            result.setText(resul.toString(10));
            result.setTextIsSelectable(true); //вивід

            BigInteger ratio = BigInteger.valueOf(result.length()).divide(BigInteger.valueOf(enter.length()));
            path.setText("Length of input: " + enter.length() + " Length of code: " + result.length() + " ratio: " + ratio);

            mShareActionProvider.setShareIntent(getDefaultShareIntent());

        } catch (Exception e) {
            result.setText("error");//ексепшн
        }
    }


    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                finish();
            }

        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */


    Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.QRCodeBlackColor) : getResources().getColor(R.color.QRCodeWhiteColor);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 350, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    public void SavePhotoButton(View v) {
        // Get the file, same method as before
        File file = getOutputFile();
        try {
            // Try to open an output stream
            FileOutputStream fos = new FileOutputStream(file);
            // Get the image
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap bm = drawable.getBitmap();
            // save the image to the output stream
            bm.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            // Flush so nothing gets stuck in the cache
            fos.flush();
            // Close the output stream
            fos.close();
            // We have to let everybody know the gallery changed
            addPhotoToGallery(Uri.fromFile(file));
        } catch (Exception ioe) {
            // This is not supposed to happen, so I'm printing the stack trace just
            // in case we need to debug
            ioe.printStackTrace();
        }
        Toast.makeText(this, "QR saved in gallery", Toast.LENGTH_SHORT).show();
    }

    private void addPhotoToGallery(Uri imageUri) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(imageUri);
        this.sendBroadcast(mediaScanIntent);

    }

    public File getOutputFile() {
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), getPackageName());
        // If the directory doesn't exist
        if (!directory.exists()) {
            // try and create it
            if (!directory.mkdirs()) {
                Log.e("ERROR", "Couldn't create the directory");
            }
        }
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        return new File(directory.getPath() + File.separator + "IMG" + timestamp + ".jpg");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {
            Uri photoUri = null;
            if (data == null) {
                // This means it was able to save it where we asked
                Toast.makeText(this, "QR saved", Toast.LENGTH_SHORT).show();
                photoUri = fileUri;
            } else {
                // This means it wasn't able, so I'm gonna print the path for you to see
                Toast.makeText(this, "QR saved to: " + data.getData(), Toast.LENGTH_SHORT).show();
                photoUri = data.getData();
            }
            try {
                // We have to use the one here, anyway
                Bitmap cameraImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                // At this point, we have the image from the camera.
                imageView.setImageBitmap(cameraImage);
            } catch (FileNotFoundException fnfe) {
                fnfe.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}

