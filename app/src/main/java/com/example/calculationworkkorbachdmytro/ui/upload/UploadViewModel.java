package com.example.calculationworkkorbachdmytro.ui.upload;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.calculationworkkorbachdmytro.database.RegisteredUser;
import com.example.calculationworkkorbachdmytro.database.User;
import com.example.calculationworkkorbachdmytro.database.UsersRepository;
import com.example.calculationworkkorbachdmytro.ui.download.DownloadViewModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadViewModel extends ViewModel {

    private MutableLiveData<String> uploadState;
    private MutableLiveData<Integer> uploadDatabaseSize;
    private MutableLiveData<Integer> uploadProgressBar;
    private UsersRepository database;
    private Activity activity;
    public UploadViewModel() {
        uploadState = new MutableLiveData<>();
        uploadDatabaseSize = new MutableLiveData<>();
        uploadProgressBar = new MutableLiveData<>();
    }
    public void connectDatabase(Activity activity){
        this.database = new UsersRepository(activity.getApplication());
    }
    public void setConfigAPI(Activity activity){
        this.activity = activity;
    }
    public LiveData<String> getUploadState() {
        return uploadState;
    }

    public void setUploadState(String uploadState) {
        this.uploadState.setValue(uploadState);
    }

    public LiveData<Integer> getUploadDatabaseSize() {
        return uploadDatabaseSize;
    }

    public void setUploadDatabaseSize(int uploadDatabaseSize) {
        this.uploadDatabaseSize.setValue(Integer.valueOf(uploadDatabaseSize));
    }

    public LiveData<Integer> getUploadProgressBar() {
        return uploadProgressBar;
    }

    public void setUploadProgressBar(int uploadProgressBar) {
        this.uploadProgressBar.setValue(Integer.valueOf(uploadProgressBar));
    }
    public void setUploadDatabaseSize(){
        new GetCountDatabase(database).start();
    }
    public void uploadDatabase(){
        new UploadDatabase(database, activity).start();
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
            int currentSize = database.getCountRegistredUser();
            handler.post(new Thread(){
                @Override
                public void run() {
                    setUploadDatabaseSize(currentSize);
                }
            });

        }
    }


    private class UploadDatabase extends Thread {
        private UsersRepository database;
        private Handler handler;
        private Activity activity;

        private UploadDatabase(UsersRepository database, Activity activity) {
            this.handler = new Handler(Looper.myLooper());
            this.database = database;
            this.activity = activity;
        }

        @Override
        public void run() {
            handler.post(new Thread(){
                @Override
                public void run() {
                    setUploadProgressBar(1);
                    setUploadState("Формуємо запит на відправку даних");
                }
            });
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            List<RegisteredUser> listUserUpload = database.getAllRegisteredUser();
            Gson gson = new Gson();
            String json = gson.toJson(listUserUpload, new TypeToken<List<RegisteredUser>>() {}.getType());
            handler.post(new Thread(){
                @Override
                public void run() {
                    setUploadState("Надсилаємо дані");
                }
            });
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            OkHttpClient client = new OkHttpClient();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
            String addressServer = preferences.getString("addressServer", "127.0.0.1");
            int portServer = preferences.getInt("portServer", 4700);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);
            Request request = new Request.Builder()
                    .url("http://" + addressServer + ":" + Integer.toString(portServer) + "/register")
                    .post(requestBody)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                    throw new IOException(response.code() + " " + response.message());
                }
                if(response.code() == 200){
                    handler.post(new Thread(){
                        @Override
                        public void run() {
                            setUploadState("Очищуємо базу даних");
                        }
                    });
                    database.deleteAllRegisteredUser();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    handler.post(new Thread(){
                        @Override
                        public void run() {
                            setUploadState("Вивантаження завершено!");
                            setUploadDatabaseSize();
                            setUploadProgressBar(0);
                        }
                    });
                }
            } catch (IOException e) {
                handler.post(new Thread() {
                    @Override
                    public void run() {
                        setUploadProgressBar(0);
                        setUploadState(e.getMessage());
                    }
                });
            }
        }
    }
}