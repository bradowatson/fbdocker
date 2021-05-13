/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.watson.fantasybaseball;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.watson.fantasybaseball.common.Helper;
import com.watson.fantasybaseball.common.NodeTools;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Brad Watson
 */
public class YahooUpdater {

    private static final String TOKEN_URL = "https://api.login.yahoo.com/oauth2/get_token";
    private static final String TXNS_URL = "https://fantasysports.yahooapis.com/fantasy/v2/league/404.l.8549/transactions";
    private static final String TXNS_URL_2020 = "https://fantasysports.yahooapis.com/fantasy/v2/league/404.l.8549/transactions";
    private static final String TXNS_URL_2019 = "https://fantasysports.yahooapis.com/fantasy/v2/league/388.l.3531/transactions";
    private static final String PLYR_URL = "https://fantasysports.yahooapis.com/fantasy/v2/league/404.l.8549/players;start=";
    private static final String ROSTER_URL = "https://fantasysports.yahooapis.com/fantasy/v2/team/404.l.8549.t.";
    private static final String APP_ID = "atLjLg34";
    private static final String CONSUMER_KEY = "dj0yJmk9bmtFQVBQdHZSM3NlJnM9Y29uc3VtZXJzZWNyZXQmc3Y9MCZ4PTE0";
    private static final String CONSUMER_SECRET = "441d7c74e9fcc576c8a77af0b11936ac8cdae374";
    private static final String AUTH_CODE_BODY = "refresh_token=AKHnmFxdxnU4H0w.dg_1m3mCV0iTYc2LDoMFks7croeVhq_WqvM-";
    private static final String BASE64_AUTH_HEADER = "ZGoweUptazlibXRGUVZCUWRIWlNNM05sSm5NOVkyOXVjM1Z0WlhKelpXTnlaWFFtYzNZOU1DWjRQVEUwOjQ0MWQ3Yzc0ZTlmY2M1NzZjOGE3N2FmMGIxMTkzNmFjOGNkYWUzNzQ=";
    private static final String REDIRECT_URI_BODY = "redirect_uri=oob";
    private static String token;
    private static String refToken;
    
    public static void main(String[] args) {
        getAuthToken();
        //System.out.println(token);
        //updateTeamLogos();
        updatePlayers();
        String xml = getXML(TXNS_URL);
        processTransactions(xml);
    }
    
    public static Node[] sortNodes(String xml, String tag1, String tag2) {
    	xml = xml.trim().replaceFirst("^([\\W]+)<","<");
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xml));
            Document doc = dBuilder.parse(is);
            doc.getDocumentElement().normalize();
            return NodeTools.sortNodes(doc.getElementsByTagName(tag1), tag2, true, Integer.class);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(YahooUpdater.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(YahooUpdater.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(YahooUpdater.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static void processDrop(Element element) {
		try {
			String name = new String (element.getElementsByTagName("full").item(0).getTextContent().trim().getBytes(), "UTF-8");
	    	int yahooId = Integer.parseInt(element.getElementsByTagName("player_id").item(0).getTextContent().trim());
	    	int id = Worker.getPlayerIdByYahooId(yahooId);
	        if(Worker.isRostered(id)) {
	            int teamId = Worker.getPlayerOwner(id);
	            String team = Worker.getTeamNameById(teamId);
	            System.out.println(team + " dropped " + name + ".");
	            Worker.deactivatePlayer(id, teamId);
	            Worker.deleteFreeAgentContractsForPlayer(id);
	        }
		} catch (UnsupportedEncodingException | DOMException e) {
			e.printStackTrace();
		}
    }
    
    public static void processAddDrop(Element element) {
    	int price = 1;
    	if(null != element.getElementsByTagName("faab_bid").item(0)) {
    		price = Integer.parseInt(element.getElementsByTagName("faab_bid").item(0).getTextContent().trim());
    	}
		NodeList nList = element.getElementsByTagName("player");
		for (int x = 0; x < nList.getLength(); x++) {
            Node nNode = nList.item(x);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
				String type = eElement.getElementsByTagName("type").item(0).getTextContent().toLowerCase().trim();
				if(type.equalsIgnoreCase("drop")) {
					processDrop(eElement);
				} else {
					processAdd(eElement, price);
				}
            }
		}
    }
    
    public static void processAdd(Element element, int price) {
		try {
			int yahooId = Integer.parseInt(new String (element.getElementsByTagName("player_id").item(0).getTextContent().trim().getBytes(), "UTF-8"));
        	int id = Worker.getPlayerIdByYahooId(yahooId);
        	if(!Worker.isRostered(id)) {
				String sourceType = new String (element.getElementsByTagName("source_type").item(0).getTextContent().trim().getBytes(), "UTF-8");
	            String name = new String (element.getElementsByTagName("full").item(0).getTextContent().trim().getBytes(), "UTF-8");
	            String teamName = new String (element.getElementsByTagName("destination_team_name").item(0).getTextContent().trim().getBytes(), "UTF-8");
	            int teamId = Worker.getTeamIdByName(teamName);
	            if(sourceType.toLowerCase().contains("waivers")) {
	            	if(null != element.getElementsByTagName("faab_bid").item(0)) {
	            		price = Integer.parseInt(element.getElementsByTagName("faab_bid").item(0).getTextContent().trim());
	            	}
	            	System.out.println(teamName + " added " + name + " off waivers for $" + price + ".");
	            } else {
	            	System.out.println(teamName + " added " + name + ".");
	            }
	            Worker.insertNewContract(teamId, id, price, (Worker.getYear()), 'Y', 'F');
        	}
		} catch (UnsupportedEncodingException | DOMException e) {
			e.printStackTrace();
		}
    }
    
    public static void processTrade(Element element) {
		try {
			NodeList nList = element.getElementsByTagName("player");
			for (int x = 0; x < nList.getLength(); x++) {
	            Node nNode = nList.item(x);
	            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	                Element eElement = (Element) nNode;
					int yahooId = Integer.parseInt(new String (eElement.getElementsByTagName("player_id").item(0).getTextContent().trim().getBytes(), "UTF-8"));
					String name = new String (eElement.getElementsByTagName("full").item(0).getTextContent().trim().getBytes(), "UTF-8");
			        int id = Worker.getPlayerIdByYahooId(yahooId);
			        if(Worker.isRostered(id)) {
			            int teamId = Worker.getPlayerOwner(id);
			            String team = Worker.getTeamNameById(teamId);
			            String fromTeam = new String (eElement.getElementsByTagName("source_team_name").item(0).getTextContent().trim().getBytes(), "UTF-8");
			            if(team.toLowerCase().equals(fromTeam.toLowerCase())) {
			                String newTeam = new String (eElement.getElementsByTagName("destination_team_name").item(0).getTextContent().trim().getBytes(), "UTF-8");
			                int newTeamId = Worker.getTeamIdByName(newTeam);
			                System.out.println(team + " traded " + name + " to " + newTeam + ".");
			                Worker.moveContract(teamId, newTeamId, id);
			            }
			        }
	            }
			}
		} catch (UnsupportedEncodingException | DOMException e) {
			e.printStackTrace();
		}
    }
    
    public static void processTransactions(String xml) {
        xml = xml.trim().replaceFirst("^([\\W]+)<","<");
        int lastId = Worker.getLastYahooTransactionId();
        Node[] nList = sortNodes(xml, "transaction", "transaction_id");
        for (int temp = 0; temp < nList.length; temp++) {
            Node nNode = nList[temp];
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                int id = Integer.parseInt(eElement.getElementsByTagName("transaction_id").item(0).getTextContent().toLowerCase().trim());
	            if(id > lastId) {
	                String type = eElement.getElementsByTagName("type").item(0).getTextContent().toLowerCase().trim();
	                if(type.equals("drop")) {
	                    processDrop(eElement);
	                } else if (type.equals("trade")) {
	                    processTrade(eElement);
	                } else if (type.equals("add/drop")) {
	                	processAddDrop(eElement);
	                } else if (type.equals("add")) {
	                	processAdd(eElement, 1);
	                }
	                Worker.updateLastYahooTransactionId(id);
                }
            }
        }
    }
    
    private static void getAuthToken() {
    	String body = "grant_type=refresh_token&" + AUTH_CODE_BODY + "&" + REDIRECT_URI_BODY;
    	Properties props = new Properties();
    	props.put("Authorization", "Basic " + BASE64_AUTH_HEADER);
    	props.put("Content-Type", "application/x-www-form-urlencoded");
    	String json = Helper.getApiResult(TOKEN_URL, props, "POST", body);
    	JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
        token = jsonObject.get("access_token").getAsString();
        refToken = jsonObject.get("refresh_token").getAsString();
    }
    
    private static String getXML(String url) {
    	Properties props = new Properties();
    	props.put("Authorization", "Bearer " + token);
    	return Helper.getApiResult(url, props, "GET", "");
    }
    
    private static void updatePlayers() {
    	int count = 0;
    	boolean finished = false;
    	while (!finished) {
    		String xml = getPlayerXML(count);
    		if(!xml.contains("<player>")) {
    			finished = true;
    		} else {
    			updatePlayersFromXML(xml);
    		}
    		count += 25;
    	}
    }
    
    private static void updatePlayersFromXML(String xml) {
    	xml = xml.trim().replaceFirst("^([\\W]+)<","<");
    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(xml));
	        Document doc = dBuilder.parse(is);
	        doc.getDocumentElement().normalize();
	        NodeList nList = doc.getElementsByTagName("player");
	        for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    int yahooId = Integer.parseInt(eElement.getElementsByTagName("player_id").item(0).getTextContent().trim());
                    String name = new String(eElement.getElementsByTagName("full").item(0).getTextContent().trim().getBytes(), "UTF-8");
                    String team = eElement.getElementsByTagName("editorial_team_abbr").item(0).getTextContent().trim().toUpperCase();
                    String position = eElement.getElementsByTagName("display_position").item(0).getTextContent().trim().toUpperCase();
                	String url = eElement.getElementsByTagName("url").item(0).getTextContent().trim();
                	url = new String(url.getBytes(), "UTF-8");
                    try {
	                    if(!Worker.playerExists(yahooId)) {
	                    	Player player = new Player(name, team, position, 1, yahooId, url);
	                    	System.out.println("Player not Found: " + name + ": " + team);
	                    	Worker.enterPlayer(player);
	                    } else {
	                    	if(!Worker.getPlayerMlbTeamByYahooId(yahooId).trim().equalsIgnoreCase(team)) {
	                    		Worker.updateYahooData("Team", team, yahooId);
	                    	}
	                    	if(!Worker.getPlayerPositionByYahooId(yahooId).trim().equalsIgnoreCase(position)) {
	                    		Worker.updateYahooData("Position", position, yahooId);
	                    	}
	                    	if(!Worker.getPlayerHeadshotByYahooId(yahooId).trim().equalsIgnoreCase(url)) {
	                    		Worker.updateYahooData("HeadshotURL", url, yahooId);
	                    	}
	                    	if(!Worker.getPlayerNameByYahooId(yahooId).trim().equalsIgnoreCase(name)) {
	                    		Worker.updateYahooData("Name", name, yahooId);
	                    	}
	                    }
                    } catch (Exception e) {
                    	System.out.println("EXCEPTION - Player not found? " + name + ": " + team);
                    	//Player player = new Player(name, team, position, 1, yahooId);
                    	e.printStackTrace();
                    	//Worker.enterPlayer(player);
                    }
                }
	        }
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
    
    private static void getFAABClaims(String url) {
    	String xml = getXML(url);
        xml = xml.trim().replaceFirst("^([\\W]+)<","<");
        ArrayList<String> yahooIds = new ArrayList<>();
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xml));
            Document doc = dBuilder.parse(is);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("transaction");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    if(null != eElement.getElementsByTagName("faab_bid").item(0)) {
                    	
                    	int yahooId = Integer.parseInt(eElement.getElementsByTagName("player_id").item(0).getTextContent().toLowerCase().trim());
                    	if(!yahooIds.contains(yahooId + "")) {
                    		yahooIds.add(yahooId + "");
                    		int bid = Integer.parseInt(eElement.getElementsByTagName("faab_bid").item(0).getTextContent().toLowerCase().trim());
                    		int playerID = Worker.getPlayerIdByYahooId(yahooId);
                    		Worker.updatePlayerPriceForYear(playerID, Worker.getYear(), bid);
                    	}
                    }
                }
            }
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(YahooUpdater.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(YahooUpdater.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(YahooUpdater.class.getName()).log(Level.SEVERE, null, ex);
        }
    	
    }
    
    private static String getPlayerXML(int count) {
    	Properties props = new Properties();
    	props.put("Authorization", "Bearer " + token);
    	return Helper.getApiResult(PLYR_URL + count, props, "GET", "");
    }
    
    private static void updateHeadshotUrls() {
    	int count = 0;
    	boolean finished = false;
    	while (!finished) {
    		String xml = getPlayerXML(count);
    		if(!xml.contains("<player>")) {
    			finished = true;
    		} else {
    			updateHeadshot(xml);
    		}
    		count += 25;
    	}
    }
    
    private static void updateYahooIds() {
    	int count = 0;
    	boolean finished = false;
    	while (!finished) {
    		String xml = getPlayerXML(count);
    		if(!xml.contains("<player>")) {
    			finished = true;
    		} else {
    			updateYahooId(xml);
    		}
    		count += 25;
    	}
    }
    
    private static void updateYahooData(String column, String yahooField) {
    	int count = 0;
    	boolean finished = false;
    	while (!finished) {
    		String xml = getPlayerXML(count);
    		if(!xml.contains("<player>")) {
    			finished = true;
    		} else {
    			updateYahooDataField(xml, column, yahooField);
    		}
    		count += 25;
    	}
    }
    
    private static void updateYahooDataField(String xml, String column, String yahooField) {
    	xml = xml.trim().replaceFirst("^([\\W]+)<","<");
    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(xml));
	        Document doc = dBuilder.parse(is);
	        doc.getDocumentElement().normalize();
	        NodeList nList = doc.getElementsByTagName("player");
	        for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String name = eElement.getElementsByTagName("full").item(0).getTextContent().trim();
                    name = new String(name.getBytes(), "UTF-8");
                    String team = eElement.getElementsByTagName("editorial_team_abbr").item(0).getTextContent().trim().toUpperCase();
                    team = new String(team.getBytes(), "UTF-8");
                    int yahooId = Integer.parseInt(eElement.getElementsByTagName("player_id").item(0).getTextContent().trim());
                    try {
	                    if(Worker.playerExists(yahooId)) {
	                    	String value = eElement.getElementsByTagName(yahooField).item(0).getTextContent().trim().toUpperCase();
	                    	if(Worker.playerExists(name)) {
	                    		Worker.updateYahooData(column, value, yahooId);
	                    	}
	                    } else {
	                    	System.out.println("Couldn't find player: " + name + ": " + team);
	                    }
                    } catch (Exception e) {
                    	System.out.println("EXCEPTION: " + name + ": " + team);
                    }
                }
	        }
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
    
    private static void updateYahooId(String xml) {
    	xml = xml.trim().replaceFirst("^([\\W]+)<","<");
    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(xml));
	        Document doc = dBuilder.parse(is);
	        doc.getDocumentElement().normalize();
	        NodeList nList = doc.getElementsByTagName("player");
	        for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String name = eElement.getElementsByTagName("full").item(0).getTextContent().trim();
                    name = new String(name.getBytes(), "UTF-8");
                    String team = eElement.getElementsByTagName("editorial_team_abbr").item(0).getTextContent().trim().toUpperCase();
                    team = new String(team.getBytes(), "UTF-8");
                    try {
//                    	if(!Worker.playerExists(name)) {
//                    		System.out.println(name);
//                    	}
	                    if(!Worker.playerExists(name, team)) {
	                    	//String pos = eElement.getElementsByTagName("primary_position").item(0).getTextContent().trim().toUpperCase();
	                        //pos = new String(pos.getBytes(), "UTF-8");
	                    	if(!Worker.playerExists(name)) {
	                    		System.out.println(name);
	                    	}
	                    	System.out.println(name + ": " + team);
		                    //int id = Worker.getPlayerIdByName(name);
	                    	//Worker.updateTeam(id, team);
		                    //Player player = new Player(name, team, pos, 1);
		                    //Worker.enterPlayer(player);
	                    } else {
	                    	int playerID = Worker.getPlayerIdByNameAndTeam(name, team);
	                    	int yahooId = Integer.parseInt(eElement.getElementsByTagName("player_id").item(0).getTextContent().trim());
	                    	Worker.updateYahooId(playerID, yahooId);
//	                    	String url = eElement.getElementsByTagName("url").item(0).getTextContent().trim();
//	                    	url = new String(url.getBytes(), "UTF-8");
//	                    	Worker.updateHeadshot(playerID, url);
	                    }
                    } catch (Exception e) {
                    	System.out.println("EXCEPTION: " + name + ": " + team);
                    	//Worker.updateTeam(Worker.getPlayerIdByName(name), team);
                    	//int id = Worker.getPlayerIdByName(name);
                    	//Worker.updateTeam(id, team);
                    	/*String pos = eElement.getElementsByTagName("primary_position").item(0).getTextContent().trim().toUpperCase();
                        pos = new String(pos.getBytes(), "UTF-8");
                    	Player player = new Player(name, team, pos, 1);
	                    Worker.enterPlayer(player);*/
                    }
                }
	        }
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
    
    private static void updateTeamLogos() {
    	for(int x = 1; x < 13; x++) {
    		String xml = getXML(ROSTER_URL + x);
    		updateTeamLogo(xml);
    	}
    }
    
    private static void updateTeamLogo(String xml) {
    	xml = xml.trim().replaceFirst("^([\\W]+)<","<");
    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(xml));
	        Document doc = dBuilder.parse(is);
	        doc.getDocumentElement().normalize();
	        NodeList nList = doc.getElementsByTagName("team");
	        for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String name = eElement.getElementsByTagName("name").item(0).getTextContent().trim();
                    name = new String(name.getBytes(), "UTF-8");
                    int teamId = Worker.getTeamIdByName(name.replace("'", "''"));
                    NodeList logoList = eElement.getElementsByTagName("team_logos");
                    for(int x = 0; x < logoList.getLength(); x++) {
                    	Node lNode = logoList.item(x);
                    	if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    		Element logo = (Element) lNode;
                    		String url = logo.getElementsByTagName("url").item(0).getTextContent().trim();
                    		Worker.updateTeamLogo(teamId, url);
                    	}
                    }
                }
	        }
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
    
    private static void updateHeadshot(String xml) {
    	xml = xml.trim().replaceFirst("^([\\W]+)<","<");
    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(xml));
	        Document doc = dBuilder.parse(is);
	        doc.getDocumentElement().normalize();
	        NodeList nList = doc.getElementsByTagName("player");
	        for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String name = eElement.getElementsByTagName("full").item(0).getTextContent().trim();
                    name = new String(name.getBytes(), "UTF-8");
                    String team = eElement.getElementsByTagName("editorial_team_abbr").item(0).getTextContent().trim().toUpperCase();
                    team = new String(team.getBytes(), "UTF-8");
                    try {
	                    if(!Worker.playerExists(name, team)) {
	                    	//String pos = eElement.getElementsByTagName("primary_position").item(0).getTextContent().trim().toUpperCase();
	                        //pos = new String(pos.getBytes(), "UTF-8");
		                    System.out.println(name + ": " + team);
		                    //int id = Worker.getPlayerIdByName(name);
	                    	//Worker.updateTeam(id, team);
		                    //Player player = new Player(name, team, pos, 1);
		                    //Worker.enterPlayer(player);
	                    } else {
	                    	int playerID = Worker.getPlayerIdByNameAndTeam(name, team);
	                    	String url = eElement.getElementsByTagName("url").item(0).getTextContent().trim();
	                    	url = new String(url.getBytes(), "UTF-8");
	                    	Worker.updateHeadshot(playerID, url);
	                    }
                    } catch (Exception e) {
                    	System.out.println(name + ": " + team);
                    	//int id = Worker.getPlayerIdByName(name);
                    	//Worker.updateTeam(id, team);
                    	/*String pos = eElement.getElementsByTagName("primary_position").item(0).getTextContent().trim().toUpperCase();
                        pos = new String(pos.getBytes(), "UTF-8");
                    	Player player = new Player(name, team, pos, 1);
	                    Worker.enterPlayer(player);*/
                    }
                }
	        }
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }

}