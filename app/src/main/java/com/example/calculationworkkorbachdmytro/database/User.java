package com.example.calculationworkkorbachdmytro.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
@Entity(tableName = "users")
public class User {
    @PrimaryKey
    @NonNull
    @SerializedName("idUser")
    @ColumnInfo(name = "idUser")
    private int idUser;
    @SerializedName("cardNumbers")
    @Ignore
    private List<String> cardNumbers;
    @NonNull
    @SerializedName("fullName")
    @ColumnInfo(name = "fullName")
    private String fullName;
    @NonNull
    @SerializedName("document")
    @ColumnInfo(name = "document")
    private String document;
    @NonNull
    @SerializedName("group")
    @ColumnInfo(name = "group")
    private String group;
    @SerializedName("photo")
    @ColumnInfo(name = "photo")
    private String photo;

    public User(){
        if(this.cardNumbers == null){
            this.cardNumbers = new ArrayList<>();
        }
        if(this.fullName == null){
            this.fullName = "";
        }
        if(this.document == null){
            this.document = "";
        }
        if(this.group == null){
            this.group = "";
        }
        if(this.photo == null){
            this.photo = "";
        }
    }

    public User(@NonNull int idUser, List<String> cardNumbers, @NonNull String fullName, @NonNull String document, @NonNull String group, String photo) {
        this.idUser = idUser;
        this.cardNumbers = cardNumbers;
        this.fullName = fullName;
        this.document = document;
        this.group = group;
        this.photo = photo;
    }

    public int getIdUser() {
        return idUser;
    }
    @NonNull
    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }


    public List<String> getCardNumbers() {
        return cardNumbers;
    }

    public void setCardNumbers(List<String> cardNumbers) {
        this.cardNumbers = cardNumbers;
    }

    public String getFullName() {
        return fullName;
    }
    @NonNull
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDocument() {
        return document;
    }
    @NonNull
    public void setDocument(String document) {
        this.document = document;
    }
    @NonNull
    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
