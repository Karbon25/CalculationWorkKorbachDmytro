package com.example.calculationworkkorbachdmytro.info;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.calculationworkkorbachdmytro.R;
import com.example.calculationworkkorbachdmytro.database.RegisteredUser;
import com.example.calculationworkkorbachdmytro.database.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class InfoActivity extends AppCompatActivity {
    private InfoModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        viewModel = new ViewModelProvider(this).get(InfoModel.class);
        viewModel.connectDatabase(getApplication());
        viewModel.getUserInfo().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User u) {
                if(u == null){
                    ((TextView)findViewById(R.id.info_full_name)).setText("Користувача не знайдено");
                    ((TextView)findViewById(R.id.info_group)).setText("");
                    ((TextView)findViewById(R.id.info_document)).setText("");
                    ((ImageView)findViewById(R.id.info_image)).setImageBitmap(null);
                }else{
                    viewModel.registerUser(new RegisteredUser(u.getIdUser(), new Date()));
                    ((TextView)findViewById(R.id.info_full_name)).setText(u.getFullName());
                    ((TextView)findViewById(R.id.info_group)).setText("Група: "+u.getGroup());
                    ((TextView)findViewById(R.id.info_document)).setText("Документ: "+u.getDocument());
                    if(u.getPhoto().length() != 0){
                        byte[] decodedBytes = Base64.decode(u.getPhoto(), Base64.DEFAULT);
                        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                        ((ImageView)findViewById(R.id.info_image)).setImageBitmap(decodedBitmap);
                    }else{
                        ((ImageView)findViewById(R.id.info_image)).setImageBitmap(null);
                    }
                }
            }
        });
        viewModel.getErrorMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String error) {
                if(error.length() != 0){
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                }
            }
        });
        viewModel.getCardNumber().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String cardNumber) {
                ((TextView)findViewById(R.id.info_card_number)).setText(cardNumber);
            }
        });
        findViewById(R.id.info_find_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText documnet = findViewById(R.id.info_find_user);
                if(documnet.getText().length() != 0){
                    viewModel.findByDocuments(documnet.getText().toString());
                }else{
                    viewModel.setErrorMessage("Введіть номер документа");
                }
            }
        });

        handleIntent(getIntent());
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();

        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            String nfcData = readNfcData(intent);
            if (nfcData.length() != 0){
                viewModel.findByCardNumber(nfcData);
                viewModel.setCardNumber(nfcData);
            }else{
                viewModel.setErrorMessage("Дані не зчитані. Прикладіть карту ще раз");
                viewModel.setUser(null);
            }
        }
    }

    private String readNfcData(Intent intent) {
        String data = "";
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        if (tag != null) {
            NfcA nfcA = NfcA.get(tag);
            if (nfcA != null) {
                try {
                    nfcA.connect();
                    byte[] uidBytes = nfcA.getTag().getId();
                    data = bytesToHexString(uidBytes);
                } catch (IOException e) {

                } finally {
                    try {
                        nfcA.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return data;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            sb.append(String.format("%02X", aByte));
        }
        return sb.toString();
    }


    private class UpdateDatabaseThread extends Thread{
        Handler handler;
        public UpdateDatabaseThread(){
            handler = new Handler(Looper.myLooper());
        }
        @Override
        public void run() {

            String responseMessage = null;
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://192.168.0.104:4700/users")
                    .build();
            try{
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    throw new IOException(response.code() + " " + response.message());
                }
                responseMessage = response.body().string();
            }catch (Exception e) {
                Log.e("MyTag", e.getMessage());
            }

            if(responseMessage != null) {
                Gson gson = new Gson();
                List<User> usersList = gson.fromJson(responseMessage, new TypeToken<List<User>>() {}.getType());


                handler.post(new Thread() {
                    @Override
                    public void run() {
                        byte[] decodedBytes = Base64.decode(usersList.get(0).getPhoto(), Base64.DEFAULT);
                        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                        ((ImageView)findViewById(R.id.info_image)).setImageBitmap(decodedBitmap);
                    }
                });
            }
        }
    }



}