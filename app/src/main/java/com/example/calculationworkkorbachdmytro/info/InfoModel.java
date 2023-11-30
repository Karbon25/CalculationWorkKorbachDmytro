package com.example.calculationworkkorbachdmytro.info;

import android.app.Application;

import android.os.Handler;
import android.os.Looper;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.calculationworkkorbachdmytro.database.RegisteredUser;
import com.example.calculationworkkorbachdmytro.database.User;
import com.example.calculationworkkorbachdmytro.database.UsersRepository;


public class InfoModel extends ViewModel {
    private UsersRepository database;
    private MutableLiveData<User> currentUser;
    private MutableLiveData<String> errorMessage;
    private MutableLiveData<String> cardNumber;

    public void connectDatabase(Application app){
        database = new UsersRepository(app);
    }
    public InfoModel() {
        currentUser = new MutableLiveData<>();
        errorMessage = new MutableLiveData<>();
        cardNumber = new MutableLiveData<>();
    }

    public LiveData<User> getUserInfo() {
        return currentUser;
    }
    public void findByCardNumber(String cardNumber){
        if(database != null) {
            new DatabaseGetUserByCardNumber(database, cardNumber).start();
        }else{
            setUser(null);
        }
    }
    public void findByDocuments(String documentNumber){
        if(database != null) {
            new DatabaseGetUserByDocument(database, documentNumber).start();
        }else{
            setUser(null);
        }
    }
    public void setUser(User u){
        currentUser.setValue(u);
    }
    public void registerUser(RegisteredUser u){
        new DatabaseRegisterUser(database, u).start();

    }
    public void setErrorMessage(String error){
        errorMessage.setValue(error);
    }
    public LiveData<String> getErrorMessage(){
        return errorMessage;
    }
    public MutableLiveData<String> getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber.setValue(cardNumber);
    }
    private class DatabaseGetUserByCardNumber extends Thread{
        private Handler handler;
        private String cardNumber;
        private UsersRepository database;
        public DatabaseGetUserByCardNumber(UsersRepository database, String cardNumber){
            this.handler = new Handler(Looper.getMainLooper());
            this.database = database;
            this.cardNumber = cardNumber;
        }
        @Override
        public void run() {
            final User findUser = database.getUsersByCard(cardNumber);
            handler.post(new Thread() {
                @Override
                public void run() {
                    setUser(findUser);
                }
            });
        }
    }
    private class DatabaseGetUserByDocument extends Thread{
        private Handler handler;
        private String document;
        private UsersRepository database;
        public DatabaseGetUserByDocument(UsersRepository database, String document){
            this.handler = new Handler(Looper.getMainLooper());
            this.database = database;
            this.document = document;
        }
        @Override
        public void run() {
            final User findUser = database.getUsersByDocuments(document);
            handler.post(new Thread() {
                @Override
                public void run() {
                    setUser(findUser);
                }
            });
        }
    }
    private class DatabaseRegisterUser extends Thread{
        private Handler handler;
        private RegisteredUser user;
        private UsersRepository database;
        public DatabaseRegisterUser(UsersRepository database, RegisteredUser user){
            this.handler = new Handler(Looper.getMainLooper());
            this.database = database;
            this.user = user;
        }
        @Override
        public void run() {
            database.registerUser(this.user);
        }
    }
}
