package com.example.calculationworkkorbachdmytro.database;

import android.app.Application;

import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

public class UsersRepository {
    private UsersDao universityDao;

    public UsersRepository(Application application) {
        UsersDatabase db = UsersDatabase.getDatabase(application);
        universityDao = db.usersDao();
    }

    public List<RegisteredUser> getAllRegisteredUser(){
        return universityDao.getAllRegisteredUser();
    }
    public void deleteAllRegisteredUser(){
        universityDao.deleteAllRegisteredUser();
    }
    public void registerUser(RegisteredUser register){
        universityDao.registerUser(register);
    }
    public User getUsersByDocuments(String document){
        return universityDao.getUsersByDocuments(document);
    }
    public User getUsersByCard(String cardNumber){
        return universityDao.getUsersByCard(cardNumber);
    }
    public void insertUsers(User user){
        universityDao.insertUsers(user);
    }
    public void deleteAllUsers(){
        universityDao.deleteAllUsers();
    }
    public void insertCard(CardNumber card){
        universityDao.insertCard(card);
    }
    public void deleteAllCardNumbers(){
        universityDao.deleteAllCardNumbers();
    }
    public int getCountRegistredUser(){
        return universityDao.getCountRegistredUser();
    }
    public int getCountUser(){
        return universityDao.getCountUser();
    }

}