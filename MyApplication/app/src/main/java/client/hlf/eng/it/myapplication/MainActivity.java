package client.hlf.eng.it.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import it.eng.hlf.android.client.FabricLedgerClient;
import it.eng.hlf.android.client.LedgerClient;
import it.eng.hlf.android.client.exception.HLFClientException;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MY-APPLICATION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            int permissionCheck = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                //this means permission is granted and you can do read and write
            } else {
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
            LedgerClient ledgerClient = new FabricLedgerClient();

            final Button putData = findViewById(R.id.putData);
            putData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        ledgerClient.putData("0","Lasciate ogne speranza, voi ch'intrate");
                    } catch (HLFClientException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }

            });

            final Button getData = findViewById(R.id.getData);
            getData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String getDataString = ledgerClient.getData(String.valueOf(0));
                        final TextView textViewgetData = (TextView) findViewById(R.id.textViewGetData);
                        textViewgetData.setText(getDataString);

                    } catch (HLFClientException e) {
                        Log.e(TAG, e.getMessage());
                    }

                }

            });

        } catch (HLFClientException e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
