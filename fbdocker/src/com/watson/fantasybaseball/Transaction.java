/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.watson.fantasybaseball;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Administrator
 */
public class Transaction {
    
    public Transaction(String date, String move) throws ParseException {
        //Sep 22 12:37pm
        SimpleDateFormat ft = new SimpleDateFormat ("MMM dd h:mm a");
        this.date = ft.parse(date);
        this.move = move;
    }
    
    public Date date;
    public String move = "";

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMove() {
        return move;
    }

    public void setMove(String move) {
        this.move = move;
    }
    
    public int compareTo(Transaction transaction) {
        return getDate().compareTo(transaction.getDate());
    }
    
}
