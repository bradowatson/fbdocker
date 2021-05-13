/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.watson.fantasybaseball;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author brads
 */
public class PlayerUpdater {
    
    private static final String FORMAT = "yyyyMMdd";
    private static String address = "http://lookup-service-prod.mlb.com/json/named.transaction_all.bam?sport_code='mlb'&start_date={YESTERDAY}&end_date={TODAY}";
    
    public static void main(String[] args) {
        
        /*System.out.println(getYesterday());
        System.out.println(getToday());
        System.out.println(getTransactionsJSON());*/
        System.out.println(getTransactionsJSON());
        //parseJSON();
    }
    
    private static void parseJSON() {
            JSONObject jsonObject = new JSONObject(getTransactionsJSON());
            JSONObject trans_all = jsonObject.getJSONObject("transaction_all");
            JSONObject results = trans_all.getJSONObject("queryResults");
            JSONArray ja = (JSONArray) results.get("row");
            for (int i = 0; i < ja.length(); i++)
            {
                String player = ja.getJSONObject(i).getString("player");
                String fromTeam = ja.getJSONObject(i).getString("from_team");
                String newTeam = ja.getJSONObject(i).getString("team");
                int newTeamId = Worker.getMlbTeamIdByName(newTeam);
                if(newTeamId > 0) {
                    int oldTeamId = Worker.getMlbTeamIdByName(fromTeam);
                    if((oldTeamId > 0) && (oldTeamId != newTeamId)) {
                        String oldCode = Worker.getMlbTeamCodeById(oldTeamId);
                        int playerId = Worker.getPlayerIdByNameAndTeam(player, oldCode);
                        if(playerId > 0) {
                            System.out.println("Updating " + player + ".");
                            System.out.println("New team: " + newTeam + "\n");
                            String newCode = Worker.getMlbTeamCodeById(newTeamId);
                            Worker.updateTeam(playerId, newCode);
                        }
                    } else if((fromTeam.trim().length() < 3) && (!player.toUpperCase().trim().equals("WANDER FRANCO"))) {
                        int playerId = Worker.getPlayerIdByName(player);
                        if(playerId > 0) {
                            System.out.println("Updating " + player + ".");
                            System.out.println("New team: " + newTeam + "\n");
                            String newCode = Worker.getMlbTeamCodeById(newTeamId);
                            Worker.updateTeam(playerId, newCode);
                        }
                    }
                }
            }
    }
    
    private static String getTransactionsJSON() {
        try {
            address = address.replace("{YESTERDAY}", getYesterday()).replace("{TODAY}", getToday());
            URL url = new URL(address);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP Error code : "
                        + conn.getResponseCode());
            }
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(in);
            String o;
            String output = "";
            while ((o = br.readLine()) != null ) {
                output += o + "\n";
            }
            return output;
        } catch (MalformedURLException ex) {
            Logger.getLogger(PlayerUpdater.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PlayerUpdater.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private static String getToday() {
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT);
        String date = sdf.format(new Date()); 
        return date;
    }
    
    private static String getYesterday() {
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT);
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return sdf.format(cal.getTime());
    }
    
}
