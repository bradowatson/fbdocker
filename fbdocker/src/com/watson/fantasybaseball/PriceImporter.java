/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.watson.fantasybaseball;

import static com.watson.fantasybaseball.Worker.getYear;
import static com.watson.fantasybaseball.Worker.insertNewContract;
import com.watson.io.FileManip;

/**
 *
 * @author brads
 */
public class PriceImporter {
    
    public static void main(String args[]) {
        //addFreeAgents("d:/keepers2019.txt");
    	//purgeNonKeepers("d:/keepers2020.txt");
//        yahooImport("D:/OneDrive/Documents/Fantasy Baseball/2021players.txt");
        addDraftPicks("d:/OneDrive/documents/fantasy baseball/draftresults2021.txt");
    }
    
    public static void addDraftPicks(String file) {
        String picks[] = FileManip.getTextFileContents(file).split("\n");
        for(String pick : picks) {
            String name = pick.substring(0, pick.indexOf("(") - 1).trim();
            int index = pick.indexOf("(");
            String mlbTeam = pick.substring(index + 1, index + 4).trim();
            String split[] = pick.split("\\s+");
            int price = 0;
            int x = 0;
            String team = "";
            for (String s: split) {
                if(s.contains("$")) {
                    price = Integer.parseInt(s.replace("$", "").trim());
                    for (int y = (x + 1); y < split.length; y++) {
                        team += split[y] + " ";
                    }
                }
                x++;
            }
            team = team.trim();
            System.out.println(name + "," + mlbTeam + "," + price + "," + team);
            //System.out.println(name);
            //System.out.println(Worker.getPlayerIdByName(name));
            insertNewContract(Worker.getTeamIdByName(team.replace("'", "''")), Worker.getPlayerIdByName(name), price, getYear(), 'Y', 'N');
        }
    }
    
    public static void purgeNonKeepers(String file) {
        String lines[] = FileManip.getTextFileContents(file).split("\n");
        boolean nonKeepers = false;
        for(int x = 0; x < lines.length; x++) {
            //System.out.println(lines[x]);
            int team = 0;
            if(lines[x].contains("Edit Keepers")) {
            	//System.out.println(lines[x+1]);
                team = Worker.getTeamIdByName(lines[x+1].trim().replace("'", "''"));
                System.out.println("Team: " + Worker.getTeamNameById(team));
            }
            if(lines[x].contains("Non-Keepers")) {
                nonKeepers = true;
            }
            while(nonKeepers) {
                if((x+1) < lines.length) {
                    if(lines[x+1].contains("(")) {
                    	String nextLine = lines[x+1].trim();
                        String name = lines[x].trim();//.replace("'", "''");
                        String mlbTeam = nextLine.substring(1, nextLine.indexOf("-") - 1).trim();
                        //System.out.println(name);
                        int id = Worker.getPlayerIdByNameAndTeam(name, mlbTeam);
                        //System.out.println(id);
                        if(id == 0) {
                            System.out.println(lines[x] + name);
                            name = name.replace("�?", "A");
                            name = name.replace("á", "a");
                            name = name.replace("É", "E");
                            name = name.replace("é", "e");
                            name = name.replace("�?", "I");
                            name = name.replace("í", "i");
                            name = name.replace("Ó", "O");
                            name = name.replace("ó", "o");
                            name = name.replace("Ú", "U");
                            name = name.replace("ú", "u");
                            id = Worker.getPlayerIdByNameAndTeam(name, mlbTeam);
                            //System.out.println(name);
                        }
                        if(id == 0) {
                            System.out.println(lines[x] + name);
                        }
                    	try {
                    		//Worker.deleteContract(id, 'Y');
                    		Worker.deactivatePlayer(id, team);
                    	} catch (Exception e) {
                    		
                    	}
//                        if(Worker.isRostered(id)) {
//                        	System.out.println(name + ": " + mlbTeam + ", " + Worker.isRostered(id));
//                        	//Worker.deleteContract(id, team, 'Y');
//
//                        }
                    }
                    if(lines[x].contains("Edit Keepers")) {
                        nonKeepers = false;
                    }
                }
                x++;
            }
        }
    }
    
    public static void addFreeAgents(String file) {
        String lines[] = FileManip.getTextFileContents(file).split("\n");
        boolean keepers = false;
        for(int x = 0; x < lines.length; x++) {
            //System.out.println(lines[x]);
            int team = 0;
            if(lines[x].contains("Edit Keepers")) {
                keepers = true;
                team = Worker.getTeamIdByName(lines[x+1].trim().replace("'", "''"));
                System.out.println("Team: " + Worker.getTeamNameById(team));
            }
            while(keepers) {
                if((x+1) < lines.length) {
                    if(lines[x+1].contains("(")) {
                        String name = lines[x].trim();//.replace("'", "''");
                        //System.out.println(name);
                        int id = Worker.getPlayerIdByName(name);
                        //System.out.println(id);
                        if(id == 0) {
                            System.out.println(lines[x] + name);
                            name = name.replace("�?", "A");
                            name = name.replace("á", "a");
                            name = name.replace("É", "E");
                            name = name.replace("é", "e");
                            name = name.replace("�?", "I");
                            name = name.replace("í", "i");
                            name = name.replace("Ó", "O");
                            name = name.replace("ó", "o");
                            name = name.replace("Ú", "U");
                            name = name.replace("ú", "u");
                            id = Worker.getPlayerIdByName(name);
                            //System.out.println(name);
                        }
                        if(id == 0) {
                            System.out.println(lines[x] + name);
                        }
                        //System.out.println(id);
                        if(!Worker.isRostered(id)) {
                            System.out.println("Keeper: " + name);
                            //*******************************************Free Agents need to start the year prior (pickup year).
                            Worker.insertNewContract(team, id, 1, (Worker.getYear() - 1), 'Y', 'F');
                        } else if(Worker.isExpired(id, team, 'Y')) {
                            System.out.println("Expired: " + name);
                        }
                        /*if(!Worker.playerExists(name)) {
                            System.out.println(name);
                        }*/
                    }
                    if(lines[x].contains("Non-Keepers")) {
                        keepers = false;
                    }
                }
                x++;
            }
        }
    }
    
    public static void yahooImport(String file) {
        String lines[] = FileManip.getTextFileContents(file).split("\n");
        for(int x = 0; x < lines.length; x++) {
            if(lines[x].contains(" - ") && !lines[x].contains("$")) {
                String split[] = lines[x].split(" - ");
                String price = lines[x + 2].trim();
                if(price.contains("$")) {
                    price = lines[x + 3].trim();
                }
                String pos = split[1];
                String split2[] = split[0].split(" ");
                String name = "";
                String team = split2[split2.length - 1];
                for(int y = 0; y < (split2.length - 1); y++) {
                    name += " " + split2[y];
                }
                int p = Integer.parseInt(price);
                if(p == 0) {
                    p = 1;
                }
                name = name.trim();//.replace("'", "''");
                if(!Worker.playerExists(name)) {
                    /*name = name.replace("�?", "A");
                    name = name.replace("á", "a");
                    name = name.replace("É", "E");
                    name = name.replace("é", "e");
                    name = name.replace("�?", "I");
                    name = name.replace("í", "i");
                    name = name.replace("Ó", "O");
                    name = name.replace("ó", "o");
                    name = name.replace("Ú", "U");
                    name = name.replace("ú", "u");*/
                    if(!Worker.playerExists(name)) {
                    //id = Worker.getPlayerIdByName(name);
                    //System.out.println(name);
                    //System.out.println(name + ": " + pos + ", " + p);
                    //name = name.trim().replace("'", "''");
                        Player player = new Player(name, team, pos, p);
                    //System.out.println(name + ": " + pos + ", " + p);
                    //Worker.enterPlayer(player);
                        System.out.println(name + ": " + p + ", " + team + ", " + pos);
                    }
                } else {
                    name = name.trim().replace("'", "''");
                    Worker.enterPlayerESPNPrice(name, team, p);
                    //int id = Worker.getPlayerIdByName(name);
                    //Worker.updatePosition(id, pos);
                    //Worker.updateName(id, name);
                    //System.out.println(name + ": " + p + ", " + Worker.getPlayerOwner(id) + ", " + pos);//Worker.getPlayerPriceForYear(id, 2019, Worker.getPlayerOwner(id)));
                }
                /*System.out.println(name);
                System.out.println(team);
                System.out.println(pos);
                System.out.println(price);*/
            }
        }
    }
    
}
