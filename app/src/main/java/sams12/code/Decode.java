package sams12.code;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Decode extends AppCompatActivity {
    private EditText enter;
    private static final int REQUEST_CODE = 43;
    private String str;
    private Button go;
    private TextView result;
    private TextView path;
    private Button read;
    private Button write;
    private String text;
    private String line;
    private Button paste;
   private ImageButton camera;
   private ResultTask task;
   private String textResult;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.decode_layout);
        path = (TextView) findViewById(R.id.pathD);
        write = (Button) findViewById(R.id.write);
        paste = (Button) findViewById(R.id.paste);
        task = new ResultTask();

        enter = (EditText) findViewById(R.id.enterD);
        result = (TextView) findViewById(R.id.resultD);
        go = (Button) findViewById(R.id.goD);
        camera = (ImageButton) findViewById(R.id.imageButton);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(Decode.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();

            }
        });



        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTextD();
            }
        });
        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               setWrote();
            }
        });
        paste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                ClipboardManager myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData abc = myClipboard.getPrimaryClip();
                ClipData.Item item = abc.getItemAt(0);

                String text = item.getText().toString();
                enter.setText(text);}catch (Exception e){e.printStackTrace();}
            }
        });



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);

        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case  1000:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "Refused", Toast.LENGTH_SHORT).show();
                }
                finish();
        }

    }

    public void setTextD() {



        task = new ResultTask();
        task.execute(enter.getText().toString());



        try {
            textResult = task.get();
            result.setText(textResult);
            line = result.getText().toString();
            mShareActionProvider2.setShareIntent(getDefaultShareIntent());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }







    }


    public void setWrote(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");
        String datetime = dateformat.format(c.getTime());
        String filename = datetime;
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), filename);
        try {



            FileOutputStream fos = new FileOutputStream(file);
            fos.write(line.getBytes());
            fos.close();
            Toast.makeText(Decode.this, "The text is saved" + getFilesDir(), Toast.LENGTH_SHORT).show();
            path.setText(Environment.getExternalStorageDirectory().getAbsolutePath());


        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }

    }


//
//    public void getFile(){
//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        intent.setType("text/plain");
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        startActivityForResult(intent, REQUEST_CODE);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
//            if (data != null) {
//                Uri uri = data.getData();
//                Toast.makeText(this, "Uri: " + uri, Toast.LENGTH_LONG).show();
//                Toast.makeText(this, "path: " + uri.getPath(), Toast.LENGTH_LONG
//                ).show();
//                String way = uri.getPath();
//                path.setText(way.toString());
//                text = readText(way);
//
//
//
//            }
//        }
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

    public static BigInteger findIndex(BigInteger n) {

            // if Fibonacci number is less
            // than 2, its index will be
            // same as number
            if (n.compareTo(BigInteger.ONE) == -1 || n.compareTo(BigInteger.ONE) == 0)
                return n.add(BigInteger.ONE);

            BigInteger a = BigInteger.ZERO, b = BigInteger.ONE, c = BigInteger.ONE;
            BigInteger res = BigInteger.ONE;

            // iterate until generated fibonacci
            // number is less than given
            // fibonacci number
            while (c.compareTo(n) == -1) {
                c = a.add(b);

                // res keeps track of number of
                // generated fibonacci number
                res = res.add(BigInteger.ONE);
                a = b;
                b = c;
            }

        return res;
    }


    private ShareActionProvider mShareActionProvider2;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        /** Inflating the current activity's menu with res/menu/items.xml */
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem shareItem = menu.findItem(R.id.share);
        /** Getting the actionprovider associated with the menu ex_005_item whose id is share */
        mShareActionProvider2 = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);


        Intent intent = getDefaultShareIntent();
        /** Setting a share intent */
        mShareActionProvider2.setShareIntent(intent);

        return true;

    }

    private Intent getDefaultShareIntent(){
        Intent intent2= new Intent(Intent.ACTION_SEND);
        intent2.setType("text/plain");
        intent2.putExtra(Intent.EXTRA_TEXT, result.getText().toString());
        return intent2;
    }


    public void onInfoClick(MenuItem item){
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.e("Scan*******", "Cancelled scan");

            } else {
                Log.e("Scan", "Scanned");

                enter.setText(result.getContents());
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private class ResultTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            String result = null;

            try {

                BigInteger alphab = BigInteger.valueOf(95);
                String value = strings[0];
                BigInteger big = new BigInteger(value);

                int amount = value.length() * 95 + 1;
                BigInteger[] series = new BigInteger[amount];
                series[0] = BigInteger.valueOf(1);
                series[1] = BigInteger.valueOf(1);
                for (
                        int i = 2; i < series.length; i++) {
                    series[i] = series[i - 2].add(series[i - 1]);
                }//генерація масиву чисел Фібоначчі

                BigInteger[] massive = new BigInteger[amount];

                int order = amount - 1;
                BigInteger closest = BigInteger.ZERO;

                while (big.compareTo(BigInteger.ZERO) == 1) {

                    if (big.compareTo(series[order]) == 1) {
                        closest = series[order];
                        big = big.subtract(closest);

                        massive[order] = findIndex(closest).subtract(BigInteger.ONE);


                    } else {
                        order--;
                    }
                    if (big.compareTo(series[order]) == 0) {

                        massive[order] = findIndex(big).subtract(BigInteger.ONE);
                        big = big.subtract(big);

                    }

                }


                List<BigInteger> list = new ArrayList<BigInteger>(Arrays.asList(massive));
                list.removeAll(Arrays.asList("", null));
                BigInteger[] finish = list.toArray(new BigInteger[0]);
                int[] finish2 = new int[finish.length];
                char[] out = new char[finish2.length];

                for (int i = 0; i < finish.length; i++) {
                    BigInteger temp = BigInteger.valueOf(i).multiply(alphab);
                    finish[i] = finish[i].subtract(temp).add(BigInteger.valueOf(32));
                    finish2[i] = finish[i].intValue();
                    out[i] = (char) finish2[i];

                }

                result = Arrays.toString(out).replace(", ", "").replace("[", "").replace("]", "");
            } catch (Exception e) {
                result = "error";
            }

            return result;
        }

    }



}