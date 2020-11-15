package sams12.code;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class Open extends AppCompatActivity   {
private Button intentDecode;
private Button intentCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open_layout);
        intentCode = (Button) findViewById(R.id.intent_code);
        intentDecode = (Button) findViewById(R.id.intent_decode);

        intentCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Open.this, Code.class);
                startActivity(intent);
            }
        });

        intentDecode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(Open.this, Decode.class);
                startActivity(intent2);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    public void onInfoClick(MenuItem item){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String s = new StringBuilder()
                .append("  \"ENCODE\" : Enter text - get result).")
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
}
