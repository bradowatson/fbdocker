/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.watson.fantasybaseball;

import com.watson.fantasybaseball.common.Helper;
import com.watson.io.FileManip;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Brad Watson
 */
public class Worker {
    
    public static final int STARTING_BUDGET = 275;
    public static final int ROSTER_SPOTS = 28;
    public static final int FINAL_YEAR = 2027;
	private static final String MYSQL_URL = "redhat:30306/fantasybaseball??allowPublicKeyRetrieval=TRUE&autoReconnect=true&useSSL=FALSE";
	private static final String MYSQL_USER = "root";
	private static final String MYSQL_PASSWORD = "clemson";
    
    public static void main (String args[]) throws ParseException, SQLException {
    	//System.out.println(getContractsForTeamFromJSON(1, "5955d5f8ea9ae383b72083f8d8059f4f8cfc36266de4ebd179116c1b66564002"));
//    	System.out.println(getPlayerPriceForYear(12, (getYear() - 1), 1));
//    	System.out.println(getTeamNameById(1));
//    	int[] teams = Worker.getTeamIdsList();
//        for(int team : teams) {
//            System.out.println(team);
//        }
//    	List<JSONObject> contracts = getTeamContractsJSON(1, "ffcd3cf2cc203aee01400ce01ee3f433a529f1d968dd6834f993444664b330f0");
//    	for(JSONObject contract : contracts) {
//    		System.out.println(contract.get("Name"));
//    	}
//    	deleteExpiredContracts();
    	updateAllContractAdditionTypes();
    }
    
    public static String getNormalizedString(String s) {
        s = s.replace("á", "a");
        s = s.replace("É", "E");
        s = s.replace("é", "e");
        s = s.replace("�?", "I");
        s = s.replace("í", "i");
        s = s.replace("Ó", "O");
        s = s.replace("ó", "o");
        s = s.replace("Ú", "U");
        s = s.replace("ú", "u");
        s = s.replace("Ǹ", "N");
        s = s.replace("ǹ", "n");
        return s;
    }
    
    public static int getTeamIdByName(String name) {
    	name = name.replace("'", "''");
        return Helper.getQueryResultAsInt("SELECT TeamID FROM fantasybaseball.teams WHERE TeamName = '" + name.trim() + "'");
    }
    
    public static int getMlbTeamIdByCode(String code) {
        return Helper.getQueryResultAsInt("SELECT id FROM fantasybaseball.mlbteams WHERE code = '" + code.trim() + "'");
    }
    
    public static int getMlbTeamIdByName(String name) {
        return Helper.getQueryResultAsInt("SELECT id FROM fantasybaseball.mlbteams WHERE name = '" + name.trim() + "'");
    }
    
    public static int getPlayerOwner(int playerId) {
        return Helper.getQueryResultAsInt("SELECT TeamID FROM fantasybaseball.contracts WHERE PlayerID = '" + playerId + "' AND CurrentlyRostered = 'Y'");
    }
    
    public static String getMlbTeamNameById(int ID) {
        return Helper.getQueryResult("SELECT name FROM fantasybaseball.mlbteams WHERE ID = '" + ID + "'");
    }
    
    public static String getMlbTeamNameByCode(String code) {
        return Helper.getQueryResult("SELECT name FROM fantasybaseball.mlbteams WHERE code = '" + code + "'");
    }
    
    public static String getMlbTeamCodeById(int ID) {
        return Helper.getQueryResult("SELECT code FROM fantasybaseball.mlbteams WHERE ID = '" + ID + "'");
    }
    
    public static String getMlbTeamCodeByName(String name) {
        return Helper.getQueryResult("SELECT code FROM fantasybaseball.mlbteams WHERE code = '" + name + "'");
    }
    
    public static String getTeamNameById(int ID) {
        return Helper.getQueryResult("SELECT TeamName FROM fantasybaseball.teams WHERE TeamID = '" + ID + "'");
    }
    
    public static String[] getTeamsList() {
        return Helper.getQueryResult("SELECT TeamName FROM fantasybaseball.teams").split("\n");
    }
    
    public static int[] getTeamIdsList() {
        String[] teams = Helper.getQueryResult("SELECT TeamID FROM fantasybaseball.teams WHERE Display = 1").split("\n");
        int teamIds[] = new int[teams.length];
        for(int x = 0; x < teamIds.length; x++) {
            teamIds[x] = Integer.parseInt(teams[x]);
        }
        return teamIds;
    }
    
    public static void updateAllContractAdditionTypes() {
        Helper.writeToDb("UPDATE fantasybaseball.contracts SET AdditionType = 'C'");
    }
    
    public static void enterPlayerESPNPrice(String player, String team, int price) {
        int id = getPlayerIdByNameAndTeam(player, team);
        Helper.writeToDb("UPDATE fantasybaseball.players SET ESPNPrice = '" + price + "', Team = '" + team + "' WHERE PlayerID = '" + id + "';");
    }
    
    public static int getPlayerPriceForYear(int playerId, int year, int team) {
        return Helper.getQueryResultAsInt("SELECT Price" + year + " FROM fantasybaseball.contracts WHERE PlayerID = '" + playerId + "' AND TeamID = '" + team + "' AND CurrentlyRostered = 'Y'");
    }
    
    public static int ESPNPlayerPrice(int playerId) {
        return Helper.getQueryResultAsInt("SELECT ESPNPrice FROM fantasybaseball.players WHERE PlayerID = '" + playerId + "'");
    }
    
    public static void updatePlayerPriceForYear(int playerID, int year, int price) {
        Helper.writeToDb("UPDATE fantasybaseball.contracts SET Price" + year + " = '" + price + "' WHERE PlayerID = '" + playerID + "' AND CurrentlyRostered = 'Y'");
    }
    
    public static void updatePosition(int playerID, String position) {
        Helper.writeToDb("UPDATE fantasybaseball.players SET Position = '" + position + "' WHERE PlayerID = '" + playerID + "';");
    }
    
    public static void updateName(int playerID, String name) {
        Helper.writeToDb("UPDATE fantasybaseball.players SET Name = '" + name + "' WHERE PlayerID = '" + playerID + "';");
    }
    
    public static void updateTeam(int playerID, String team) {
        Helper.writeToDb("UPDATE fantasybaseball.players SET Team = '" + team + "' WHERE PlayerID = '" + playerID + "';");
    }
    
    public static void updateHeadshot(int playerID, String url) {
        Helper.writeToDb("UPDATE fantasybaseball.players SET HeadshotURL = '" + url + "' WHERE PlayerID = '" + playerID + "';");
    }
    
    public static void updateTeamLogo(int teamID, String url) {
        Helper.writeToDb("UPDATE fantasybaseball.teams SET LogoURL = '" + url + "' WHERE TeamID = '" + teamID + "';");
    }
    
    public static void updateYahooId(int playerID, int yahooId) {
        Helper.writeToDb("UPDATE fantasybaseball.players SET YahooID = '" + yahooId + "' WHERE PlayerID = '" + playerID + "';");
    }
    
    public static void updateYahooData(String column, String value, int yahooId) {
        Helper.writeToDb("UPDATE fantasybaseball.players SET " + column + " = '" + value + "' WHERE YahooID = '" + yahooId + "';");
    }
      
    public static String getPlayerList() {
        return Helper.getQueryResult("SELECT Name, Team FROM fantasybaseball.players");
    }
    
    public static String getPlayerNames() {
        return Helper.getQueryResult("SELECT Name from fantasybaseball.players");
    }
    
    public static String[] getPlayerNamesAsArray() {
        return Helper.getQueryResult("SELECT Name from fantasybaseball.players").split("\n");
    }
    
    public static String[] getContractsForPlayer(int playerId) {
        return Helper.getQueryResult("SELECT * from fantasybaseball.contracts where PlayerID='" + playerId + "'").split("\n");
    }
    
    public static char getContractType(int contractId) {
    	return Helper.getQueryResultAsChar("SELECT AdditionType from fantasybaseball.contracts WHERE Number = '" + contractId + "'");
    }
    
    public static String getTeamList() {
        return Helper.getQueryResult("SELECT TeamID, TeamName FROM fantasybaseball.teams WHERE Display = '1'");
    }
    
    public static String getPlayerListWithPositions() {
        return Helper.getQueryResult("SELECT PlayerID, Name, Team, Position FROM fantasybaseball.players");
    }
    
    public static int getPlayerIdByName(String name) {
        return Helper.getQueryResultAsInt("SELECT PlayerID FROM fantasybaseball.players WHERE Name = '" + name.trim().replace("'", "''") + "'");
    }
    
    public static String getPlayerNameByYahooId(int yahooId) {
        return Helper.getQueryResult("SELECT Name FROM fantasybaseball.players WHERE YahooID = '" + yahooId + "'");
    }
    
    public static int getPlayerIdByYahooId(int yahooId) {
        return Helper.getQueryResultAsInt("SELECT PlayerID FROM fantasybaseball.players WHERE YahooID = '" + yahooId + "'");
    }
    
    public static int getLastYahooTransactionId() {
        return Helper.getQueryResultAsInt("SELECT LastYahooTransaction FROM fantasybaseball.updates");
    }
    
//    public static int getPlayerTeamByYahooId(int yahooId) {
//        return Helper.getQueryResultAsInt("SELECT Name FROM fantasybaseball.players WHERE YahooID = '" + yahooId + "'");
//    }
    
    public static String getPlayerMlbTeamByYahooId(int yahooId) {
        return Helper.getQueryResult("SELECT Team FROM fantasybaseball.players WHERE YahooID = '" + yahooId + "'");
    }
    
    public static String getPlayerPositionByYahooId(int yahooId) {
        return Helper.getQueryResult("SELECT Position FROM fantasybaseball.players WHERE YahooID = '" + yahooId + "'");
    }
    
    public static String getPlayerHeadshotByYahooId(int yahooId) {
        return Helper.getQueryResult("SELECT HeadshotURL FROM fantasybaseball.players WHERE YahooID = '" + yahooId + "'");
    }
    
    public static int getPlayerIdByNameAndTeam(String name, String team) {
        int id = Helper.getQueryResultAsInt("SELECT PlayerID FROM fantasybaseball.players WHERE Name = '" + name.trim().replace("'", "''") + "' AND Team ='" + team + "'");
        if (id < 1) 
        {
            return getPlayerIdByName(name);
        }
        return id;
    }
    
    public static String getConfigProperty(String key) {
    	Properties props = new Properties();
    	try {
    		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    		InputStream input = classLoader.getResourceAsStream("conf/config.properties");
			props.load(input);
			return props.getProperty(key);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    }
    
    public static int getConfigPropertyAsInt(String key) {
    	return Integer.parseInt(getConfigProperty(key));
    }
    
    public static String login(String user, String password) {
        try {
            String url = getConfigProperty("rest.host") + "/" + getConfigProperty("auth.path");
            String body = "{\n" +
                    "\t\"name\": \"" + user + "\",\n" +
                    "\t\"password\": \"" + password + "\"\n" +
                    "}";
            Properties props = new Properties();
            props.put("Accept", "application/json");
            props.put("Content-Type", "application/json");
            String json = Helper.getApiResult(url, props, "POST", body);
            if((null != json) && ((json.contains("result")) && json.contains("Key"))) {
                JSONObject jsonObject = new JSONObject(json);
                JSONObject result = jsonObject.getJSONObject("result");
                String key = result.getString("Key");
                int teamId = result.getInt("TeamId");
                return teamId + "," + key;
            }
        } catch (Exception ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return null;
    }
    
    public static boolean playerExists(int yahooId) {
        int x = Helper.getQueryResultAsInt("SELECT PlayerID FROM fantasybaseball.players WHERE YahooID = '" + yahooId + "'");
        if(x > 0) {
            return true;
        }
        return false;
    }
    
    public static boolean playerExists(String name) {
        int x = Helper.getQueryResultAsInt("SELECT PlayerID FROM fantasybaseball.players WHERE Name = '" + name.trim().replace("'", "''") + "'");
        if(x > 0) {
            return true;
        }
        return false;
    }
    
    public static boolean playerExists(String name, String team) {
        int x = Helper.getQueryResultAsInt("SELECT PlayerID FROM fantasybaseball.players WHERE Name = '" + name.trim().replace("'", "''") + "' AND Team = '" + team.trim().replace("'", "''") + "'");
        if(x > 0) {
            return true;
        }
        return false;
    }
    
    public static String getPlayerNameTeamAndPositionById(int playerID) {
        return Helper.getQueryResult("SELECT Name, Team, Position FROM fantasybaseball.players WHERE PlayerID = '" + playerID + "';");
    }
    
    public static String getPlayerNameById(int playerID) {
        return Helper.getQueryResult("SELECT Name FROM fantasybaseball.players WHERE PlayerID = '" + playerID + "';");
    }
    
    public static int teamBudget(int teamId) {
        return teamBudgetForYear(teamId, getYear());
    }
    
    public static int teamBudgetForYear(int teamId, int year) {
        String prices[] = Helper.getQueryResult("SELECT Price" + year + " FROM fantasybaseball.contracts WHERE TeamID = '" + teamId + "';").split("\n");
        int total = 0;
        for(String price : prices) {
            total += Integer.parseInt(price);
        }
        return total;
    }
    
    public static int teamOpenRosterSpots(int teamId) {
        return (ROSTER_SPOTS - Helper.getQueryResultAsInt("SELECT COUNT(*) FROM fantasybaseball.contracts WHERE TeamID = '" + teamId + "' AND CurrentlyRostered = 'Y';"));
    }
    
    public static int teamActiveRosterCount(int teamId) {
        return Helper.getQueryResultAsInt("SELECT COUNT(*) FROM fantasybaseball.contracts WHERE TeamID = '" + teamId + "' AND CurrentlyRostered = 'Y';");
    }
    
    public static int teamInactiveRosterCount(int teamId) {
        return Helper.getQueryResultAsInt("SELECT COUNT(*) FROM fantasybaseball.contracts WHERE TeamID = '" + teamId + "' AND CurrentlyRostered = 'N';");
    }
    
    public static int teamAvailableDollars(int teamId) {
        return (STARTING_BUDGET - teamBudget(teamId));
    }
    
    public static int contractExtensionLength(int teamId, int playerId) {
        return Helper.getQueryResultAsInt("SELECT ExtensionLength FROM fantasybaseball.contracts WHERE PlayerID = '" + playerId + "' AND TeamID = '" + teamId + "' AND CurrentlyRostered = 'Y'");
    }
    
    public static int maxBid(int teamId) {
        if(teamOpenRosterSpots(teamId) > 0) {        
            return (teamAvailableDollars(teamId) - teamOpenRosterSpots(teamId) + 1);
        }
        return 0;
    }

    public static int avgBid(int teamId) {
        if(teamOpenRosterSpots(teamId) > 0) {
            return (teamAvailableDollars(teamId) / teamOpenRosterSpots(teamId));
        }
        return 0;
    }
    
    public static String getContractsForTeam(int teamId) {
        StringBuilder stmt = new StringBuilder();
        stmt.append("SELECT fantasybaseball.players.Name, fantasybaseball.players.Team, fantasybaseball.players.Position, fantasybaseball.contracts.Price2017, fantasybaseball.contracts.Price2018, fantasybaseball.contracts.Price2019\n")
            .append("FROM fantasybaseball.contracts\n")
            .append("INNER JOIN fantasybaseball.players\n")
            .append("ON fantasybaseball.players.PlayerID=fantasybaseball.contracts.PlayerID\n")
            .append("WHERE fantasybaseball.contracts.TeamID = '").append(teamId).append("'");
        String result = Helper.getQueryResult(stmt.toString());
        return result.replaceAll("null", "0").replaceAll("NULL", "0").trim();
    }
    
    public static String getContractsForTeam(int teamId, char active) {
        StringBuilder stmt = new StringBuilder();
        stmt.append("SELECT fantasybaseball.players.Name, fantasybaseball.players.Team, fantasybaseball.players.Position, fantasybaseball.contracts.Price2019, fantasybaseball.contracts.Price2020, fantasybaseball.contracts.Price2021, fantasybaseball.contracts.Price2022, fantasybaseball.contracts.Price2023, fantasybaseball.contracts.Price2024, fantasybaseball.contracts.Price2025, fantasybaseball.contracts.Franchise, fantasybaseball.contracts.AdditionType, fantasybaseball.contracts.Length, fantasybaseball.contracts.StartYear, fantasybaseball.contracts.ExtensionLength, fantasybaseball.contracts.ExtensionStartYear, fantasybaseball.players.HeadshotURL\n")
            .append("FROM fantasybaseball.contracts\n")
            .append("INNER JOIN fantasybaseball.players\n")
            .append("ON fantasybaseball.players.PlayerID=fantasybaseball.contracts.PlayerID\n")
            .append("WHERE fantasybaseball.contracts.TeamID = '").append(teamId).append("'\n")
            .append("AND fantasybaseball.contracts.CurrentlyRostered = '" + active + "'");
        try {
	        String result = Helper.getQueryResult(stmt.toString());
	        return result.replaceAll("null", "0").replaceAll("NULL", "0").trim();
        } catch (Exception e) {
        	System.out.println("Error executing query: " + stmt.toString());
        	e.printStackTrace();
        	
        }
        return "";
    }
    
    public static String getContractsForTeamFromJSON(int teamId, String key) {
    	StringBuilder sb = new StringBuilder();
    	List<JSONObject> contracts = getTeamContractsJSON(teamId, key);
    	for(int x = 0; x < contracts.size(); x++) {
    		sb.append(contracts.get(x).get("PlayerID")).append(";");
    		sb.append(contracts.get(x).get("Name")).append(";");
    		sb.append(contracts.get(x).get("Team")).append(";");
    		sb.append(contracts.get(x).get("Position")).append(";");
    		sb.append(contracts.get(x).get("Price" + (getYear() - 1))).append(";");
    		sb.append(contracts.get(x).get("Price" + getYear())).append(";");
    		sb.append(contracts.get(x).get("Price" + (getYear() + 1))).append(";");
    		sb.append(contracts.get(x).get("Price" + (getYear() + 2))).append(";");
    		sb.append(contracts.get(x).get("Price" + (getYear() + 3))).append(";");
    		sb.append(contracts.get(x).get("Price" + (getYear() + 4))).append(";");
    		sb.append(contracts.get(x).get("Price" + (getYear() + 5))).append(";");
    		sb.append(contracts.get(x).get("Franchise")).append(";");
    		sb.append(contracts.get(x).get("AdditionType")).append(";");
    		sb.append(contracts.get(x).get("Length")).append(";");
    		sb.append(contracts.get(x).get("StartYear")).append(";");
    		sb.append(contracts.get(x).get("ExtensionLength")).append(";");
    		sb.append(contracts.get(x).get("ExtensionStartYear")).append(";");
    		sb.append(contracts.get(x).get("HeadshotURL")).append(";");
    		sb.append(contracts.get(x).get("CurrentlyRostered"));
    		if((x + 1) < contracts.size()) {
    			sb.append("\n");
    		}
    	}
    	return sb.toString();
    }
    
    public static List<JSONObject> getSortedContracts(List<JSONObject> contracts) {
    	Collections.sort(contracts, new Comparator<JSONObject>() {
        	@Override
            public int compare(JSONObject a, JSONObject b) {
        		int active1 = a.get("CurrentlyRostered").equals("Y") ? 1 : 0;
        		int active2 = b.get("CurrentlyRostered").equals("Y") ? 1 : 0;
        		int x = Integer.valueOf(active2).compareTo(active1);
        		if(x == 0) {
        			String thisYear = "Price" + getYear();
            		String nextYear = "Price" + (getYear() + 1);
            		int a1 = Integer.parseInt(a.get(thisYear).toString());
            		int a2 = Integer.parseInt(a.get(nextYear).toString());
            		int b1 = Integer.parseInt(b.get(thisYear).toString());
            		int b2 = Integer.parseInt(b.get(nextYear).toString());
            		int p1 = 1;
            		int p2 = 1;
            		if(a1 >= a2) {
            			p1 = a1;
            		} else {
            			p1 = a2;
            		}
            		if(b1 >= b2) {
            			p2 = b1;
            		} else {
            			p2 = b2;
            		}
                	x = Integer.valueOf(p2).compareTo(p1);
                }
                return x;
            }
        });
        return contracts;
    }
    
	public static List<JSONObject> getTeamContractsJSON(int teamId, String key) {
    	try {
            String url = getConfigProperty("rest.host") + "/" + getConfigProperty("team.path") + "/" + teamId;
            Properties props = new Properties();
            props.put("Accept", "application/json");
            props.put("Content-Type", "application/json");
            props.put("Authorization", "Bearer " + key);
            String json = Helper.getApiResult(url, props, "GET", null);
            JSONArray ja = new JSONArray(json);
            List<JSONObject> contracts = new ArrayList<>();
            for(int i = 0; i < ja.length(); i++) {
               contracts.add(ja.getJSONObject(i));
            }
            return getSortedContracts(contracts);
        } catch (Exception ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public static void enterPlayer(Player player) {
        Helper.writeToDb("INSERT into fantasybaseball.players values (null, '" + player.getName().replace("'", "''") + "', '" + player.getTeam() + "', '" + player.getPosition() + "', " + player.getESPNPrice() + ", '" + player.getHeadshotUrl() + "', " + player.getYahooId() + ")");
    }
    
    public static int getYear() {
        Calendar calendar = new GregorianCalendar();
        return calendar.get(Calendar.YEAR);
    }
    
    public static String insertContract(int teamId, String name, String team, int price, int length, int startYear, char active, char type) {
        StringBuilder statement = new StringBuilder();
        int year = getYear();
        char franchise = ' ';
        if(length == 5) {
            franchise = 'Y';
        } else {
            franchise = 'N';
        }
        statement.append("INSERT INTO fantasybaseball.contracts (TeamID, PlayerID, Price").append(year).append(", ");
        for(int x = 1; x < length; x++) {
            statement.append(year + x).append("Price, ");
        }
        statement.append("CurrentlyRostered, Length, StartYear, Franchise, AdditionType)\n")
            .append("SELECT '").append(teamId).append("', fantasybaseball.players.PlayerID, '");
        for(int x = 0; x < length; x++) {
            statement.append(price).append("', '");
        }
        statement.append(active).append("', '").append(length).append("', '").append(startYear).append("', '")
            .append(franchise).append("', '").append(type).append("'\n")
            .append("FROM fantasybaseball.players").append("\n")
            .append("WHERE Name='").append(name).append("' AND Team='").append(team).append("';").append("\n\n");
        return Helper.writeToDbWithReturn(statement.toString());
    }
    
    public static String moveContract(int oldTeamId, int newTeamId, int playerId) {
        if((isFranchisePlayer(playerId, oldTeamId)) && (franchiseContractCount(newTeamId) > 4)) {
            return "Unable to move " + getPlayerNameById(playerId) + " to " + getTeamNameById(newTeamId) + " because franchise contract limit of 5 would be exceeded!";
        }
        StringBuilder statement = new StringBuilder();
        statement.append("UPDATE fantasybaseball.contracts\n");
        statement.append("SET TeamID = '").append(newTeamId).append("'\n");
        statement.append("WHERE fantasybaseball.contracts.PlayerId = '").append(playerId).append("'\n")
            .append("AND fantasybaseball.contracts.CurrentlyRostered = 'Y'\n")
            .append("AND fantasybaseball.contracts.TeamID = '").append(oldTeamId).append("'");
        Helper.writeToDb(statement.toString());
        return statement.toString();
    }
    
    public static String updateContract(int teamId, int playerId, int price, int length, int startYear, char rostered, char type) {
        StringBuilder statement = new StringBuilder();
        char franchise = 'N';
        if(length == 5) {
            franchise = 'Y';
        }
        int year = getYear();
        int finalYear;
        if(type == 'F') {
            finalYear = startYear + length;
        } else {
            finalYear = startYear + length - 1;
        }
        statement.append("UPDATE fantasybaseball.contracts\n");
        statement.append("SET TeamID = '").append(teamId).append("', PlayerID = '").append(playerId).append("', AdditionType = '").append(type).append("', ");
        for(int x = year; x <= FINAL_YEAR; x++) {
            if(x > finalYear) {
                price = 0;
            }
            statement.append(x).append("Price = '").append(price).append("', ");
        }
        statement.append("Franchise = '").append(franchise).append("', StartYear = '").append(startYear).append("', ExtensionLength = '").append(length).append("'\n");
        statement.append("WHERE fantasybaseball.contracts.PlayerId = '").append(playerId).append("'\n")
            .append("AND fantasybaseball.contracts.CurrentlyRostered = '").append(rostered).append("'\n")
            .append("AND fantasybaseball.contracts.TeamID = '").append(teamId).append("'");
        return Helper.writeToDbWithReturn(statement.toString());
    }
    
    public static int franchiseContractCount(int teamId) {
        return Helper.getQueryResultAsInt("SELECT COUNT(*) FROM fantasybaseball.contracts WHERE TeamID = '" + teamId + "' AND CurrentlyRostered = 'Y' AND Franchise ='Y';");
    }
    
    public static String expireContract(int teamId, int playerId) {
        int curYear = getYear();
        char type = contractType(playerId, teamId, 'Y');
        if(type == 'F') {
        	return dropFA(teamId, playerId);
        }
        StringBuilder builder = new StringBuilder();
        builder.append("UPDATE fantasybaseball.contracts SET\n")
            .append("ExtensionLength = '0',\n");
            builder.append("ExtensionStartYear = null,\n");
        int x = curYear + 1;
        for(int year = x; year <= FINAL_YEAR; year++) {
            builder.append("Price").append(year).append(" = '0'");
            if(year < FINAL_YEAR) {
                builder.append(",\n");
            }
        }
        builder.append("\n").append("WHERE fantasybaseball.contracts.PlayerId = '").append(playerId).append("'\n")
            .append("AND fantasybaseball.contracts.CurrentlyRostered = 'Y'\n")
            .append("AND fantasybaseball.contracts.TeamID = '").append(teamId).append("'");
        Helper.writeToDb(builder.toString());
        unFranchisePlayer(playerId, teamId);
        return getPlayerNameById(playerId) + " has been set to expire following " + (curYear) + ".";
    }
    
    public static String dropFA(int teamId, int playerId) {
        int curYear = getYear();
        char type = contractType(playerId, teamId, 'Y');
        StringBuilder builder = new StringBuilder();
        builder.append("UPDATE fantasybaseball.contracts SET\n")
            .append("ExtensionLength = '0',\n");
            builder.append("ExtensionStartYear = null,\n");
        int x = curYear;
        for(int year = x; year <= FINAL_YEAR; year++) {
            builder.append("Price").append(year).append(" = '0'");
            if(year < FINAL_YEAR) {
                builder.append(",\n");
            }
        }
        builder.append("\n").append("WHERE fantasybaseball.contracts.PlayerId = '").append(playerId).append("'\n")
            .append("AND fantasybaseball.contracts.CurrentlyRostered = 'Y'\n")
            .append("AND fantasybaseball.contracts.TeamID = '").append(teamId).append("'");
        Helper.writeToDb(builder.toString());
        unFranchisePlayer(playerId, teamId);
        return getPlayerNameById(playerId) + " will not be kept.";
    }
    
    public static String extendContract(int teamId, int playerId, int length, int extensionStartYear, int expirationYear, int extensionLength, boolean active, char type) {
        int curYear = getYear();
        if((franchiseContractCount(teamId) >= 5) && !isFranchisePlayer(playerId, teamId) && (length == 5)) {
            return "Unable to sign " + getPlayerNameById(playerId) + " for 5 years because " + getTeamNameById(teamId) + " already has 5 franchise players!";
        }
        if(isExtendable(playerId, teamId, 'Y', extensionStartYear, expirationYear, extensionLength, length, active, type)) {
            int price;
            StringBuilder builder = new StringBuilder();
            builder.append("UPDATE fantasybaseball.contracts SET\n")
                .append("ExtensionLength = '").append(length).append("',\n");
            if((getPlayerPriceForYear(playerId, (curYear - 1), teamId) == 0) || type == 'F') {
                builder.append("ExtensionStartYear = '").append(curYear).append("',\n");
                price = currentPrice(playerId, teamId, type);
            } else {
                builder.append("ExtensionStartYear = '").append(curYear + 1).append("',\n");
                price = ESPNPlayerPrice(playerId);
            } 
            for(int x = 0; x < length; x++) {
                int year;
                if(((type == 'F') && (contractStartYear(playerId, teamId) == (curYear - 1))) || type == 'N') {
                    year = curYear + x;
                } else {
                    year = curYear + x + 1;
                }
                builder.append("Price").append(year).append(" = '").append(price).append("'");
                if(year < FINAL_YEAR) {
                    builder.append(",");
                }
                builder.append("\n");
            }
            int x = curYear + 1 + length;
            if(((type == 'F') && (contractStartYear(playerId, teamId) == (curYear - 1))) || type == 'N') {
                x -= 1;
            }
            for(int year = x; year <= FINAL_YEAR; year++) {
                builder.append("Price").append(year).append(" = '0'");
                if(year < FINAL_YEAR) {
                    builder.append(",\n");
                }
            }
            builder.append("\n").append("WHERE fantasybaseball.contracts.PlayerId = '").append(playerId).append("'\n")
                .append("AND fantasybaseball.contracts.CurrentlyRostered = 'Y'\n")
                .append("AND fantasybaseball.contracts.TeamID = '").append(teamId).append("'");
            Helper.writeToDb(builder.toString());
            if(length == 5) {
                setFranchisePlayer(playerId, teamId);
            } else {
                unFranchisePlayer(playerId, teamId);
            }
            if(type == 'N' || type == 'F') {
                return getPlayerNameById(playerId) + " extended through " + (curYear + length - 1) + ".";
            }
            return getPlayerNameById(playerId) + " extended through " + (curYear + length) + ".";
        } 
        return "Unable to extend " + getPlayerNameById(playerId);
    }
    
    public static boolean isRostered(int playerId) {
        boolean rostered = false;
        String result = Helper.getQueryResult("SELECT CurrentlyRostered FROM fantasybaseball.contracts WHERE PlayerID = '" + playerId + "'");
        if(result.toUpperCase().trim().contains("Y")) {
            rostered = true;
        }
        return rostered;
    }
    
    public static boolean isUnderContract(int playerId) {
        int price = Helper.getQueryResultAsInt("SELECT " + getYear() + "Price FROM fantasybaseball.contracts WHERE PlayerID = '" + playerId + "'");
        if(price > 0) {
            return true;
        } else {
            price = Helper.getQueryResultAsInt("SELECT " + (getYear() + 1) + "Price FROM fantasybaseball.contracts WHERE PlayerID = '" + playerId + "'");
            if(price > 0) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isRosteredOnTeam(int playerId, int teamId) {
        boolean rostered = false;
        String result = Helper.getQueryResult("SELECT CurrentlyRostered FROM fantasybaseball.contracts WHERE PlayerID = '" + playerId + "' AND TeamID = '" + teamId + "'");
        if(result.toUpperCase().trim().contains("Y")) {
            rostered = true;
        }
        return rostered;
    }
    
    public static boolean isFranchisePlayer(int playerId, int teamId) {
        boolean franchise = false;
        String result = Helper.getQueryResult("SELECT Franchise FROM fantasybaseball.contracts WHERE PlayerID = '" + playerId + "' AND TeamID = '" + teamId + "' AND CurrentlyRostered = 'Y'");
        if(result.toUpperCase().trim().contains("Y")) {
            franchise = true;
        }
        return franchise;
    }
    
    public static boolean isBeingExtended(int playerId, int teamId, int extensionLength) {
        boolean extended = false;
        if(extensionLength > 0) {
            extended = true;
        }
        return extended;
    }
    
    public static void deleteContract(int playerId, char rostered) {
        Helper.writeToDb("DELETE FROM fantasybaseball.contracts WHERE PlayerID = '" + playerId + "' AND CurrentlyRostered = '" + rostered + "';");
    }
    
    public static void deleteContract(int playerId, int team, char rostered) {
        Helper.writeToDb("DELETE FROM fantasybaseball.contracts WHERE PlayerID = '" + playerId + "' AND TeamID = '" + team + "' AND CurrentlyRostered = '" + rostered + "';");
    }
    
    public static void deleteFreeAgentContractsForPlayer(int playerId) {
        Helper.writeToDb("DELETE FROM fantasybaseball.contracts WHERE PlayerID = '" + playerId + "' AND AdditionType = 'F';");
    }
    
    public static void deleteExpiredContracts() {
        Helper.writeToDb("DELETE FROM fantasybaseball.contracts WHERE Price" + getYear() + " = '0';");
    }
    
    public static void deleteExpiredInactiveContracts() {
        Helper.writeToDb("DELETE FROM fantasybaseball.contracts WHERE " + getYear() + "Price = '0' and CurrentlyRostered = 'N';");
    }
    
    public static void updateLastYahooTransactionId(int id) {
        Helper.writeToDb("UPDATE fantasybaseball.updates SET LastYahooTransaction = '" + id + "';");
    }
    
    public static void setFranchisePlayer(int playerId, int team) {
        Helper.writeToDb("UPDATE fantasybaseball.contracts SET Franchise = 'Y' WHERE PlayerID = '" + playerId + "' AND TeamID = '" + team + "' AND CurrentlyRostered = 'Y';");
    }
    
    public static void deactivatePlayer(int playerId, int team) {
        Helper.writeToDb("UPDATE fantasybaseball.contracts SET CurrentlyRostered = 'N' WHERE PlayerID = '" + playerId + "' AND TeamID = '" + team + "';");
    }
    
    public static void activatePlayer(int playerId, int team) {
        Helper.writeToDb("UPDATE fantasybaseball.contracts SET CurrentlyRostered = 'Y' WHERE PlayerID = '" + playerId + "' AND TeamID = '" + team + "';");
    }
    
    public static void unFranchisePlayer(int playerId, int team) {
        Helper.writeToDb("UPDATE fantasybaseball.contracts SET Franchise = 'N' WHERE PlayerID = '" + playerId + "' AND TeamID = '" + team + "' AND CurrentlyRostered = 'Y';");
    }
    
    public static int contractStartYear(int playerId, int teamId) {
        return Helper.getQueryResultAsInt("SELECT StartYear FROM fantasybaseball.contracts WHERE PlayerID = '" + playerId + "' AND TeamID = '" + teamId + "'");
    }
    
    public static int extensionStartYear(int playerId, int teamId) {
        return Helper.getQueryResultAsInt("SELECT ExtensionStartYear FROM fantasybaseball.contracts WHERE PlayerID = '" + playerId + "' AND TeamID = '" + teamId + "'");
    }
    
    public static int contractLength(int playerId, int teamId) {
        int length = Helper.getQueryResultAsInt("SELECT Length FROM fantasybaseball.contracts WHERE PlayerID = '" + playerId + "' AND TeamID = '" + teamId + "' AND CurrentlyRostered = 'Y'");
        if(length == 0) {
            return extensionLength(playerId, teamId);
        }
        return length;
    }
    
    public static int extensionLength(int playerId, int teamId) {
        return Helper.getQueryResultAsInt("SELECT ExtensionLength FROM fantasybaseball.contracts WHERE PlayerID = '" + playerId + "' AND TeamID = '" + teamId + "' AND CurrentlyRostered = 'Y'");
    }
    
    public static char contractType(int playerId, int teamId, char rostered) {
        return Helper.getQueryResultAsChar("SELECT AdditionType FROM fantasybaseball.contracts WHERE PlayerID = '" + playerId + "' AND TeamID = '" + teamId + "' AND CurrentlyRostered = '" + rostered + "'");
    }
    
    public static int expirationYear(int playerId, int teamId, int contractLength, int extensionStartYear, int extensionLength) {
        int curYear = getYear();
        if(isBeingExtended(playerId, teamId, extensionLength)) {
            int year = ((extensionStartYear + extensionLength) - 1);
            if(year < curYear) {
                return curYear;
            }
            return year;
        }
        if(contractLength == 0) {
            return curYear;
        }
        int year = ((contractStartYear(playerId, teamId) + contractLength) - 1);
        if(year < curYear) {
            return curYear;
        }
        return year;
    }
    
    public static int currentPrice(int playerId, int teamId, char type) {
    	if(type=='F') {
    		return getPlayerPriceForYear(playerId, (getYear() - 1), teamId);
        }
        return getPlayerPriceForYear(playerId, getYear(), teamId);
    }
    
    public static int extensionPrice(int playerId, int teamId) {
        char rostered = 'Y';
        char type = contractType(playerId, teamId, rostered);
        if(type=='F' || type == 'N') {
            return currentPrice(playerId, teamId, type);
        }
        return ESPNPlayerPrice(playerId);
    }
    
    public static boolean isExpired(int playerId, int teamId, char rostered) {
        if(Worker.getPlayerPriceForYear(playerId, getYear(), teamId) == 0) {
            return true;
        }
        return false;
    }
    
    public static boolean isExtendable(int playerId, int teamId, char rostered, int extensionStartYear, int expirationYear, int extensionLength, int contractLength, boolean active, char type) {
        if(type == 'N' || type == 'F') {
            return true;
        }
        int curYear = getYear();
        if(active) {
            if((extensionLength == 1)) {
                return true;
            } else if(extensionStartYear == (curYear + 1)) {
                return true;
            } else if((extensionStartYear == 0) && (contractLength > 1)) {
                return true;
            } else if((expirationYear == curYear) && (extensionLength > 1)) {
                return true;
            } else if(extensionLength > 1) {
                return true;
            } else if(extensionStartYear == 0) {
                return true;
            }
        }
        return false;
    }

    public static void insertNewContract(int team, int id, int price, int year, char rostered, char type) {
        StringBuilder statement = new StringBuilder();
        statement.append("INSERT INTO fantasybaseball.contracts (TeamID, PlayerID, Price").append(year).append(", StartYear, CurrentlyRostered, AdditionType)\n")
            .append("VALUES ('").append(team).append("', '").append(id)
            .append("', '").append(price).append("', '").append(year).append("', '").append(rostered)
            .append("', '").append(type).append("')");
        Helper.writeToDb(statement.toString());
    }
}