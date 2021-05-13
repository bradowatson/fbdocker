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
public class Test {
    
    public static void main(String args[]) {/*
        String text[] = FileManip.getTextFileContents("d:/", "players.txt").split("\n");
        //System.out.println(text);
        ArrayList<String> players = new ArrayList<String>();
        for(String line : text) {
            if(line.contains(";")) {
                //System.out.println(line);
                String split[] = line.split(";");
                for(String s : split) {
                    //System.out.println(s);
                    if(s.contains("$")) {
                        String name = s.substring((s.indexOf(") ") + 2), s.indexOf(","));
                        //System.out.println(name);
                        String price = "";
                        String team = s.substring((s.indexOf(", ") + 2), s.lastIndexOf("(") -1).toUpperCase();
                        String position = s.substring((s.lastIndexOf("(") + 1), s.lastIndexOf(")")).toUpperCase().replace("/", ",");
                        //System.out.print(length);
                        if(((s.lastIndexOf(" ") - s.indexOf("$"))) < 2) {
                            //System.out.println("here");
                            price = s.substring(s.indexOf(") $") + 3, (s.length() - 1));
                        } else {
                            price = s.substring(s.indexOf(") $") + 3, s.lastIndexOf(" "));
                        }
                        //Worker.enterPlayerESPNPrice(name, Integer.parseInt(price));
                        //System.out.println(Worker.getPlayerIdByName(name));
                        if(!Worker.playerExists(name)) {
                            //Worker.enterPlayer(new Player(name, team, position));
                            //System.out.println(name + "," + team + "," + position + "," + price);
                        }
                        Worker.updateTeam(Worker.getPlayerIdByName(name), team);
                        players.add(name);
                        //System.out.println(name + "," + price);
                        
                    }
                    //System.out.println(split[3]);
                }
                /**//*
            }
        }
        /*String playas[] = Worker.getPlayerNamesAsArray();
        for(String player : playas) {
            if(!players.contains(player)) {
                System.out.println(player);
                Worker.enterPlayerESPNPrice(player, 1);
            }
        }*/
        
        //System.out.println(Worker.isExtendable(Worker.getPlayerIdByName("George Springer"), 1, 'Y'));
        
    }
    
}
