package com.example.calculationworkkorbachdmytro.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

@Entity(tableName = "registeredUser")
@TypeConverters({DateConverter.class})
public class RegisteredUser {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @SerializedName("idRecord")
    @ColumnInfo(name = "idRecord")
    private int idRecord;

    @NonNull
    @SerializedName("idUser")
    @ColumnInfo(name = "idUser")
    private int idUser;

    @NonNull
    @SerializedName("dateRegister")
    @ColumnInfo(name = "dateRegister")
    private Date dateRegister;

    public RegisteredUser(@NonNull int idUser, @NonNull Date dateRegister) {
        this.idUser = idUser;
        this.dateRegister = dateRegister;
    }


    public int getIdRecord() {
        return idRecord;
    }

    @NonNull
    public void setIdRecord(int idRecord) {
        this.idRecord = idRecord;
    }

    public int getIdUser() {
        return idUser;
    }
    @NonNull
    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public Date getDateRegister() {
        return dateRegister;
    }
    @NonNull
    public void setDateRegister(Date dateRegister) {
        this.dateRegister = dateRegister;
    }


}
