package com.example.calculationworkkorbachdmytro.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UsersDao {
    @Query("SELECT * FROM RegisteredUser")
    List<RegisteredUser> getAllRegisteredUser();

    @Query("DELETE FROM RegisteredUser")
    void deleteAllRegisteredUser();
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void registerUser(RegisteredUser register);

    @Query("SELECT COUNT(idRecord) FROM RegisteredUser")
    int getCountRegistredUser();



    @Query("SELECT COUNT(idUser) FROM users")
    int getCountUser();
    @Query("SELECT * FROM users WHERE document = :document")
    User getUsersByDocuments(String document);
    @Query("SELECT users.* FROM users INNER JOIN cardNumbers ON users.idUser = cardNumbers.idUser WHERE cardNumbers.idRecord = :cardNumber")
    User getUsersByCard(String cardNumber);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUsers(User user);
    @Query("DELETE FROM users")
    void deleteAllUsers();



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCard(CardNumber card);
    @Query("DELETE FROM cardNumbers")
    void deleteAllCardNumbers();
}