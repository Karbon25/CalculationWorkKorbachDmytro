package com.example.calculationworkkorbachdmytro.ui.download;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.calculationworkkorbachdmytro.StartActivity;
import com.example.calculationworkkorbachdmytro.database.CardNumber;
import com.example.calculationworkkorbachdmytro.database.User;
import com.example.calculationworkkorbachdmytro.database.UsersRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadViewModel extends ViewModel {
    private UsersRepository database;
    private MutableLiveData<Integer> statusProgressBar;
    private MutableLiveData<String> updateStatus;
    private MutableLiveData<Integer> currentSizeDatabase;
    private String addressServer;
    private int portServer;
    private Activity activity;

    public DownloadViewModel() {
        updateStatus = new MutableLiveData<>();
        currentSizeDatabase = new MutableLiveData<>();
        statusProgressBar = new MutableLiveData<>();
    }
    public void connectDatabase(Activity activity){
        this.database = new UsersRepository(activity.getApplication());
    }
    public void setConfigAPI(Activity activity){
        this.activity = activity;
    }
    public LiveData<String> getUpdateStatus() {
        return updateStatus;
    }
    public LiveData<Integer> getCurrentSizeDatabase(){
        return currentSizeDatabase;
    }
    public LiveData<Integer> getStatusProgressBar(){
        return statusProgressBar;
    }
    public void setStatusProgressBar(int status){
        statusProgressBar.setValue(Integer.valueOf(status));
    }
    public void setCurrentSizeDatabase(int sizeDatabase){
        currentSizeDatabase.setValue(Integer.valueOf(sizeDatabase));
    }
    public void setCurrentSizeDatabase(){
       new GetCountDatabase(database).start();
    }
    public void setUpdateStatus(String status){
        updateStatus.setValue(status);
    }
    public void updateDatabase(){
        new UpdateDatabase(database, activity).start();
    }



    private class GetCountDatabase extends Thread {
        private UsersRepository database;
        private Handler handler;

        private GetCountDatabase(UsersRepository database) {
            this.handler = new Handler(Looper.myLooper());
            this.database = database;
        }

        @Override
        public void run() {
            int currentSize = database.getCountUser();
            handler.post(new Thread(){
                @Override
                public void run() {
                    setCurrentSizeDatabase(currentSize);
                }
            });

        }
    }
    private class UpdateDatabase extends Thread{
        private UsersRepository database;
        private Activity activity;
        private Handler handler;
        private UpdateDatabase(UsersRepository database, Activity activity){
            this.handler = new Handler(Looper.myLooper());
            this.database = database;
            this.activity = activity;
        }
        @Override
        public void run() {
            handler.post(new Thread(){
                @Override
                public void run() {
                    setStatusProgressBar(1);
                    setUpdateStatus("Запит на отримання даних");
                }
            });
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
            String addressServer = preferences.getString("addressServer", "127.0.0.1");
            int portServer = preferences.getInt("portServer", 4700);
            String responseMessage = null;
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url("http://"+addressServer+":"+Integer.toString(portServer)+"/users").build();
            try{
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    throw new IOException(response.code() + " " + response.message());
                }
                responseMessage = response.body().string();
            }catch (Exception e) {
                handler.post(new Thread(){
                    @Override
                    public void run() {
                        setStatusProgressBar(0);
                        setUpdateStatus(e.getMessage());
                    }
                });
            }

            if(responseMessage != null) {
                handler.post(new Thread(){
                    @Override
                    public void run() {
                        setUpdateStatus("Дані отримано. Обробка");
                    }
                });
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Gson gson = new Gson();
                List<User> usersList = gson.fromJson(responseMessage, new TypeToken<List<User>>() {}.getType());
                database.deleteAllCardNumbers();
                database.deleteAllUsers();
                for(User u: usersList){
                    for(String card:u.getCardNumbers()){
                        database.insertCard(new CardNumber(card, u.getIdUser()));
                    }
                    database.insertUsers(u);
                }
                handler.post(new Thread(){
                    @Override
                    public void run() {
                        setStatusProgressBar(0);
                        setUpdateStatus("Оновлення завершено!");
                        setCurrentSizeDatabase();
                    }
                });
            }

        }
    }
}