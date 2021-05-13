package com.watson.fantasybaseball.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Properties;

import com.watson.simplesql.DBUtils;
import com.watson.simplesql.SimpleConnection;

public class Helper {
	
    private static String token = "b86d0036dc2b93115e752c9de2e36ba1cee0b5bf1e6a2c13364cfb4f03a5ae4d";
    private static String authType = "Bearer";
    private static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
	private static final String MYSQL_URL = "jdbc:mysql://redhat:30306/fantasybaseball?allowPublicKeyRetrieval=true&autoReconnect=true&useSSL=false";
	private static final String MYSQL_USER = "user";
	private static final String MYSQL_PASSWORD = "password";
	
	public static void main(String[] args) {
	}
	
    private static SimpleConnection getConnection() {
    	String context = "java:comp/env/jdbc/mysql";
        SimpleConnection conn = new SimpleConnection(context);
//    	SimpleConnection conn = new SimpleConnection(MYSQL_DRIVER, MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
        return conn;
    }
    
    public static String getReverseString(String input) {
    	byte [] byteArray = input.getBytes();
    	byte [] result = new byte [byteArray.length];
    	for (int x = 0; x < byteArray.length; x++) {
    		result[x] = byteArray[byteArray.length - x - 1];
    	}
    	return new String(result, StandardCharsets.UTF_8);
    }
	
    public static String getApiResult(String address, Properties props, String type, String body) {
    	String result = "";
    	try {
			URL url = new URL(address);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(type);
            if(null != props) {
            	Enumeration<String> enums = (Enumeration<String>) props.propertyNames();
	            while (enums.hasMoreElements()) {
	                String key = enums.nextElement();
	                String value = props.getProperty(key);
	                conn.setRequestProperty(key, value);
	            }
            }
            if(type.toUpperCase().contains("POST")) {
            	conn.setDoOutput(true);
            	OutputStream os = conn.getOutputStream();
                os.write(body.getBytes());
                os.flush();
            }
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(in);
            String output;
            while ((output = br.readLine()) != null) {
                result += output + "\n";
            }
            conn.disconnect();
            return result;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return result;
    }
    
    public static String getJSON(String address) {
        try {
        	URL url = new URL(address);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", authType + " " + token);
            
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP Error code : "
                		+ conn.getResponseCode());
            }
            InputStreamReader in = new InputStreamReader(conn.getInputStream(), Charset.forName("UTF-8"));
            BufferedReader br = new BufferedReader(in);
            StringBuilder sb = new StringBuilder();
            int rd;
            char[] chars = new char[1024];
            while ((rd = br.read(chars)) != -1) {
                sb.append(chars, 0, rd);
            }
            return sb.toString();
    	} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return null;
    }
    
    public static String postJSON(String address, String body) {
        try {
            URL url = new URL(address);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            os.write(body.getBytes());
            os.flush();
            os.close();

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP Error code : "
                        + conn.getResponseCode());
            }
            InputStreamReader in = new InputStreamReader(conn.getInputStream(), Charset.forName("UTF-8"));
            BufferedReader br = new BufferedReader(in);
            StringBuilder sb = new StringBuilder();
            int rd;
            char[] chars = new char[1024];
            while ((rd = br.read(chars)) != -1) {
                sb.append(chars, 0, rd);
            }
            return sb.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    
    public static String getQueryResult(String query) {
    	SimpleConnection conn = getConnection();
        return DBUtils.getQueryResultAsString(conn, query, ";");
    }
    
    public static int getQueryResultAsInt(String query) {
//    	System.out.println(query);
    	SimpleConnection conn = getConnection();
        return DBUtils.getQueryResultAsInt(conn, query);
    }
    
    public static char getQueryResultAsChar(String query) {
    	SimpleConnection conn = getConnection();
        return DBUtils.getQueryResultAsChar(conn, query);
    }
    
    public static void writeToDb(String statement) {
    	System.out.println(statement);
    	SimpleConnection conn = getConnection();
    	DBUtils.writeStatementToDb(conn, statement);
    }
    
    public static String writeToDbWithReturn(String statement) {
    	SimpleConnection conn = getConnection();
    	return DBUtils.writeStatementToDbWithResult(conn, statement);
    }
    
    
    public static String getQueryResultAsJson(String query) {
    	SimpleConnection conn = getConnection();
    	return DBUtils.getQueryResultAsJSON(conn, query, ";");
    }
}