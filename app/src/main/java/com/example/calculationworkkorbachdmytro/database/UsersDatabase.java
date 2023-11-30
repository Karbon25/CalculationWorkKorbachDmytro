package com.example.calculationworkkorbachdmytro.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


@Database(entities = {User.class, RegisteredUser.class, CardNumber.class}, version = 1)
public abstract class UsersDatabase extends RoomDatabase {
    public abstract UsersDao usersDao();

    private static volatile UsersDatabase INSTANCE;

    public static UsersDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (UsersDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), UsersDatabase.class, "users").build();;
                }
            }
        }
        return INSTANCE;
    }
}