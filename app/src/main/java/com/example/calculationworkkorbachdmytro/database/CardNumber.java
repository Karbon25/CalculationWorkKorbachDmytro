package com.example.calculationworkkorbachdmytro.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cardNumbers")
public class CardNumber {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "idRecord")
    private String cardNumber;
    @NonNull
    @ColumnInfo(name = "idUser")
    private int idUser;

    public CardNumber(@NonNull String cardNumber, @NonNull int idUser) {
        this.cardNumber = cardNumber;
        this.idUser = idUser;
    }


    @NonNull
    public String getCardNumber() {
        return cardNumber;
    }
    @NonNull
    public void setCardNumber(@NonNull String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public int getIdUser() {
        return idUser;
    }
    @NonNull
    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }
}
