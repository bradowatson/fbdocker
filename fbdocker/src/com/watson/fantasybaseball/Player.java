/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.watson.fantasybaseball;

/**
 *
 * @author Administrator
 */
public class Player {
    
    public String name = "";
    public String team = "";
    public String position = "";
    public int ESPNPrice;
    public int yahooId;
    public String headshotUrl = "";
    
    public Player() {
        }
    
    public Player(String name, String team, String position) {
        this.name = name;
        this.team = team;
        this.position = position;
    }
    
    public Player(String name, String team, String position, int price) {
        this.name = name;
        this.team = team;
        this.position = position;
        this.ESPNPrice = price;
    }
    
    public Player(String name, String team, String position, int price, int yahooId) {
        this.name = name;
        this.team = team;
        this.position = position;
        this.ESPNPrice = price;
        this.yahooId = yahooId;
    }
    
    public Player(String name, String team, String position, int price, int yahooId, String headshotUrl) {
        this.name = name;
        this.team = team;
        this.position = position;
        this.ESPNPrice = price;
        this.yahooId = yahooId;
        this.headshotUrl = headshotUrl;
    }
    
    public int getYahooId() {
		return yahooId;
	}

	public void setYahooId(int yahooId) {
		this.yahooId = yahooId;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getESPNPrice() {
        return ESPNPrice;
    }

    public void setESPNPrice(int ESPNPrice) {
        this.ESPNPrice = ESPNPrice;
    }

	public String getHeadshotUrl() {
		return headshotUrl;
	}

	public void setHeadshotUrl(String headshotUrl) {
		this.headshotUrl = headshotUrl;
	}    
    
}
