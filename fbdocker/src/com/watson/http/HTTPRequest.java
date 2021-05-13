/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.watson.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class HTTPRequest {
    
    public URL url = null;
    
    public static void main(String args[]) throws MalformedURLException {
        URL url = new URL ("http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20fantasysports.leagues%20where%20league_key%3D\'238.l.627060\'&diagnostics=true");
        System.out.println(getUrlContents(url));
    }
    
    public static String getUrlContents(URL url) {
        try {
            HttpURLConnection httpConnection = null;
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setReadTimeout(60000);
            System.out.println("Response code is " + httpConnection.getResponseCode() + " for " + url.toString() + ".");
            if (httpConnection.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) 
                System.out.println(inputLine);
                in.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(HTTPRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
