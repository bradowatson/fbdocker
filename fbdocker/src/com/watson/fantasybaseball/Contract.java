package com.watson.fantasybaseball;

public class Contract {

    private String name;
    private String team;
    private String pos;
    private String url;
    private int curLength;
    private int length;
    private int price;
    private int expYear;
    private int curPrice;
    private int curExpYear;
    private int futStartYear;
    private int futLength;
    private int futPrice;
    private int futExpYear;
    private boolean active;

    public Contract() {
    }

    public Contract(String name, String team, String pos, String url, int curLength, int length, int price, int curPrice, int curExpYear, int futLength, int futPrice, int futExpYear, boolean active) {
        this.name = name;
        this.team = team;
        this.pos = pos;
        this.url = url;
        this.curLength = curLength;
        this.length = length;
        this.price = price;
        this.curPrice = curPrice;
        this.curExpYear = curExpYear;
        this.futStartYear = curExpYear + 1;
        this.futLength = futLength;
        this.futPrice = futPrice;
        this.futExpYear = futExpYear;
        this.active = active;
    }

    public Contract(String name, String team, String pos, String url, int length, int price, int expYear, boolean active) {
        this.name = name;
        this.team = team;
        this.pos = pos;
        this.url = url;
        this.length = length;
        this.price = price;
        this.expYear = expYear;
        this.active = active;
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

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getCurLength() {
        return curLength;
    }

    public void setCurLength(int curLength) {
        this.curLength = curLength;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getExpYear() {
        return expYear;
    }

    public void setExpYear(int expYear) {
        this.expYear = expYear;
    }

    public int getCurPrice() {
        return curPrice;
    }

    public void setCurPrice(int curPrice) {
        this.curPrice = curPrice;
    }

    public int getCurExpYear() {
        return curExpYear;
    }

    public void setCurExpYear(int curExpYear) {
        this.curExpYear = curExpYear;
    }

    public int getFutStartYear() {
        return futLength;
    }

    public void setFutStartYear(int futStartYear) {
        this.futStartYear = futStartYear;
    }

    public int getFutLength() {
        return futLength;
    }

    public void setFutLength(int futLength) {
        this.futLength = futLength;
    }

    public int getFutPrice() {
        return futPrice;
    }

    public void setFutPrice(int futPrice) {
        this.futPrice = futPrice;
    }

    public int getFutExpYear() {
        return futExpYear;
    }

    public void setFutExpYear(int futExpYear) {
        this.futExpYear = futExpYear;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
