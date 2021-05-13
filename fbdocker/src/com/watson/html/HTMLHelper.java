/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.watson.html;

import com.watson.fantasybaseball.Worker;
import java.util.Arrays;

/**
 *
 * @author Administrator
 */
public class HTMLHelper {
    
    public static void main(String args[]) {
        //System.out.println(displayPlayersJSON(1, false, "5955d5f8ea9ae383b72083f8d8059f4f8cfc36266de4ebd179116c1b66564002"));
    	//System.out.println(displayPlayers(1, true, 'A'));
    	//System.out.println(Worker.getContractsForTeam(1, 'N'));
    	System.out.println(displayTradeBox(1, 1));
    }
    
    public static String displayBudgetRow(int teamId, boolean trades)
    {
        int year = Worker.getYear();
        //int year = 2019;
        StringBuilder result = new StringBuilder();
        result.append("     <div class=\"row\">\n");
        result.append("         <div class=\"cell\"><font color=\"#000099\"><b>Budget Totals:</b></font></div>\n");
        result.append("         <div class=\"cell\"></div>\n");
        result.append("         <div class=\"cell\"></div>\n");
        result.append("         <div class=\"cell\"><font color=\"#000099\"><b>$").append(Worker.teamBudgetForYear(teamId, year - 1)).append("</b></font></div>\n");
        result.append("         <div class=\"cell\"><font color=\"#000099\"><b>$").append(Worker.teamBudgetForYear(teamId, year)).append("</b></font></div>\n");
        result.append("         <div class=\"cell\"><font color=\"#000099\"><b>$").append(Worker.teamBudgetForYear(teamId, year + 1)).append("</b></font></div>\n");
        result.append("         <div class=\"cell\"><font color=\"#000099\"><b>$").append(Worker.teamBudgetForYear(teamId, year + 2)).append("</b></font></div>\n");
        result.append("         <div class=\"cell\"><font color=\"#000099\"><b>$").append(Worker.teamBudgetForYear(teamId, year + 3)).append("</b></font></div>\n");
        result.append("         <div class=\"cell\"><font color=\"#000099\"><b>$").append(Worker.teamBudgetForYear(teamId, year + 4)).append("</b></font></div>\n");
        result.append("         <div class=\"cell\"><font color=\"#000099\"><b>$").append(Worker.teamBudgetForYear(teamId, year + 5)).append("</b></font></div>\n");
        result.append("         <div class=\"cell\"></div>\n").append("     </div>");
//        if(Worker.teamInactiveRosterCount(teamId) > 0) {
//            result.append("         <div class=\"cell\"><font color=\"#000099\"><b>Totals include unrostered players listed below.</b></font></div>\n");
//        } else {
//            result.append("         <div class=\"cell\"></div>\n");
//        }
//        if(trades) {
//            result.append("<div class=\"cell\"></div>\n");
//        }
//        result.append("     </div>\n");
        return result.toString();
    }
    
    public static String displayBudgetInfo(int teamId)
    {
        StringBuilder result = new StringBuilder();
        result.append("<div class=\"table\">\n");
	result.append("     <div class=\"row header green\">\n");
        result.append("     	<div class=\"cell\">Starting Budget</div><div class=\"cell\">Current Costs</div><div class=\"cell\">Open Roster Spots</div><div class=\"cell\">Draft $ Available</div><div class=\"cell\">Max Bid</div><div class=\"cell\">Avg Bid</div>\n");
	result.append("     </div>\n");
        result.append("     <div class=\"row\">\n");
        result.append("          <div class=\"cell\">$").append(Worker.STARTING_BUDGET).append("</div>\n");
        result.append("          <div class=\"cell\">$").append(Worker.teamBudget(teamId)).append("</div>\n");
        result.append("          <div class=\"cell\">").append(Worker.teamOpenRosterSpots(teamId)).append("</div>\n");
        result.append("          <div class=\"cell\">$").append(Worker.teamAvailableDollars(teamId)).append("</div>\n");
        result.append("          <div class=\"cell\">$").append(Worker.maxBid(teamId)).append("</div>\n");
        result.append("          <div class=\"cell\">$").append(Worker.avgBid(teamId)).append("</div>\n");
        result.append("     </div>\n");
        result.append("</div>");
        return result.toString();
    }
    
    public static String displayTeamSelectBox(int teamId, String jsp) {
        StringBuilder result = new StringBuilder();
        result.append("<center><table>\n");
        result.append("     <tr class=\"new\"><td align=\"right\"><form name=\"dropdown\" action=\"").append(jsp).append("\" method=\"POST\">\n");
        result.append("     <select style=\"width: auto;\" name=\"team\" onchange=\"this.form.submit()\">\n");
        String teams[] = Worker.getTeamList().split("\n");
        for(String team : teams) {
            String split[] = team.split(";");
            result.append("          <option value=\"").append(split[0]).append("\"");
            if(teamId == Integer.parseInt(split[0])) {
                result.append(" selected");
            }
            result.append(">").append(split[1]).append("</option>\n");
        }
        result.append("     </select>\n")
            .append("</form></td>").append("<td class=\"new\" align=\"right\"></td></tr>")
            .append("</table></center>");
        return result.toString();
    }
    
    public static String displayTradeBox(int playerId, int teamId) {
        
        StringBuilder result = new StringBuilder();
        result.append("<form name=\"tradeDropDown\" action=\"./ContractChanges.jsp\" method=\"POST\">\n")
            .append("     <select name=\"moveContract\" onchange=\"this.form.submit()\">\n")
            .append("          <option value=\"nothing\"></option>\n");
        int[] teams = Worker.getTeamIdsList();
        for(int team : teams) {
            result.append("          <option value=\"").append(playerId).append(",").append(team).append("\">").append(Worker.getTeamNameById(team)).append("</option>");
        }
        result.append("          <option value=\"").append(playerId).append(",ACTIVATE\">Activate Player</option>\n");
        result.append("          <option value=\"").append(playerId).append(",DROP\">Drop Player!</option>\n");
        result.append("          <option value=\"").append(playerId).append(",DELETE\">Delete Contract!</option>\n");
        result.append("     </select>\n")
            .append("     <input type=\"hidden\" name=\"team\" value=\"").append(teamId).append("\">\n")
            .append("</form>");
        return result.toString();
    }
    
    public static String displayExtensionBox(int playerId, int teamId, char rostered, int contractStartYear, int extensionStartYear, char contractType, int extensionLength) {
        
        StringBuilder result = new StringBuilder();
        boolean untouched = true;
        int year = Worker.getYear();
        //int contractStartYear = Worker.contractStartYear(playerId, teamId);
        //int extensionStartYear = Worker.extensionStartYear(playerId, teamId);
        //char contractType = Worker.contractType(playerId, teamId, rostered);
        int price = Worker.extensionPrice(playerId, teamId);
        //int extensionLength = Worker.contractExtensionLength(teamId, playerId);
        String option = "                       <option value=\"";
        String selected = "                       <option selected value=\"";
        result.append("\n               <form name=\"extensionDropdown\" action=\"./Contracts.jsp\" method=\"POST\">\n")
            .append("                   <select name=\"extension\" style=\"width: 350px\" onchange=\"this.form.submit()\">\n")
            .append("                       <option value=\"nothing\"></option>\n");
        //if((contractType == 'F') && ((contractStartYear == (year - 1)) || (extensionStartYear == year))) {
        if(contractType == 'F') {
            if(extensionLength == 1) {
                result.append(selected);
                untouched = false;
            } else {
                result.append(option);
            }
            result.append(playerId).append(",1,").append(contractType).append("\">Keep for current year only</option>\n");
            if(extensionLength == 2) {
                result.append(selected);
                untouched = false;
            } else {
                result.append(option);
            }
            result.append(playerId).append(",2,").append(contractType).append("\">Extend 2 years through ").append(year + 1).append(" at $").append(price).append(". (Up for renewal in ").append(year + 1).append(")</option>\n");
        //} else if((contractType == 'N') && ((contractStartYear == year) || (extensionStartYear == (year + 1)))) {
        } else if(contractType == 'N') {
            if(extensionLength == 2) {
                result.append(selected);
                untouched = false;
            } else {
                result.append(option);
            }
            result.append(playerId).append(",2,").append(contractType).append("\">Sign 2 years through ").append(year + 1).append(" at $").append(price).append(". (Up for renewal in ").append(year + 1).append(")</option>\n");
            if(extensionLength == 3) {
                result.append(selected);
                untouched = false;
            } else {
                result.append(option);
            }
            result.append(playerId).append(",3,").append(contractType).append("\">Sign 3 years through ").append(year + 2).append(" at $").append(price).append(". (Up for renewal in ").append(year + 2).append(")</option>\n");
            if(extensionLength == 5) {
                result.append(selected);
                untouched = false;
            } else {
                result.append(option);
            }
            result.append(playerId).append(",5,").append(contractType).append("\">Sign 5 years through ").append(year + 4).append(" at $").append(price).append(". (Up for renewal in ").append(year + 4).append(")</option>\n");
        } else {
            if(extensionLength == 1) {
                result.append(selected);
                untouched = false;
            } else {
                result.append(option);
            }
            result.append(playerId).append(",1,").append(contractType).append("\">Renew 1 year through ").append(year + 1).append(" at $").append(price).append(". (Non-renewable)</option>\n");
            if((extensionLength == 2) && (extensionStartYear != (year - 1))) {
                result.append(selected);
                untouched = false;
            } else {
                result.append(option);
            }
            result.append(playerId).append(",2,").append(contractType).append("\">Renew 2 years through ").append(year + 2).append(" at $").append(price).append(". (Up for renewal in ").append(year + 2).append(")</option>\n");
            if((extensionLength == 3) && (extensionStartYear == (year + 1))) {
                result.append(selected);
                untouched = false;
            } else {
                result.append(option);
            }
            result.append(playerId).append(",3,").append(contractType).append("\">Renew 3 years through ").append(year + 3).append(" at $").append(price).append(". (Up for renewal in ").append(year + 3).append(")</option>\n");
            if((extensionLength == 5) && (extensionStartYear == (year + 1))) {
                result.append(selected);
                untouched = false;
            } else {
                result.append(option);
            }
            result.append(playerId).append(",5,").append(contractType).append("\">Renew 5 years through ").append(year + 5).append(" at $").append(price).append(". (Up for renewal in ").append(year + 5).append(")</option>\n");
        }
        if(extensionLength == 0 || untouched) {
            result.append(selected);
        } else {
            result.append(option);
        }
        if(contractType == 'F') {
        	result.append(playerId).append(",expire\">Do not keep</option>\n");
        } else {
        	result.append(playerId).append(",expire\">Release after ").append(year).append("</option>\n");
        }
        result.append("                   </select>\n")
            .append("                   <input type=\"hidden\" name=\"team\" value=\"").append(teamId).append("\">\n")
            .append("               </form>\n");
        return result.toString();
    }
    
    public static String displayPlayersJSON(int teamId, boolean trades, String key)
    {
        char rostered = ' ';
        String header = "     <div class=\"row header\">\n";
        String contractText = Worker.getContractsForTeamFromJSON(teamId, key);
        if( null == contractText) {
            return "";
        }
        String tradeHeader = "";
        String contractHeader = "<div class=\"cell\">Contract</div>";
        if(trades) {
            tradeHeader = "<div class=\"cell\">Move Contract</div>";
        }
        String contracts[] = contractText.split("\n");
        StringBuilder result = new StringBuilder();
        result.append("<div class=\"table\">\n");
        result.append(header);
        result.append("         <div class=\"cell\">Player Name</div><div class=\"cell\">Team</div><div class=\"cell\">Position</div><div class=\"cell\">2020 Price</div><div class=\"cell\">2021 Price</div><div class=\"cell\">2022 Price</div><div class=\"cell\">2023 Price</div><div class=\"cell\">2024 Price</div><div class=\"cell\">2025 Price</div><div class=\"cell\">2026 Price</div>");
        result.append(contractHeader).append(tradeHeader).append("\n");
        result.append("     </div>\n");
        if(!trades) {
            result.append(displayBudgetRow(teamId, trades)).append("\n");
        }
        int count = 0;
        for(String contract : contracts) {
        	if(contract.trim().length() > 0) {
	            String split[] = contract.split(";");
	            int playerId = Integer.parseInt(split[0]);
	            char conType = split[12].trim().charAt(0);
	            boolean active = true;
	            if(split[18].toUpperCase().trim().equals("N")) {
	            	active = false;
	            }
	            int contractLength = Integer.parseInt(split[13]);
	            if(contractLength == 0) {
	                contractLength = Integer.parseInt(split[15]);
	            }
	            int extensionStartYear = Integer.parseInt(split[16]);
	            int extensionLength = Integer.parseInt(split[15]);
	            int expirationYear = Worker.expirationYear(playerId, teamId, contractLength, extensionStartYear, extensionLength);
	            boolean isExtendable = Worker.isExtendable(playerId, teamId, rostered, extensionStartYear, expirationYear, extensionLength, contractLength, active, conType);
	            boolean franchise = false;
	            if(split[11].toUpperCase().trim().contains("Y")) {
	                franchise = true;
	            }
	            String td = "<div class=\"cell\" align=\"left\">";
	            String tdEnd = "</div>\n";
	            result.append("     <div class=\"row\">\n");
	            for(int x = 1; x < 11; x++) {
	                if(x == 1 || x == 11) {
	                    td = "<div class=\"cell\" align=\"left\">";
	                } else {
	                    td = "<div class=\"cell\" align=\"center\">";
	                }
	                if(x > 3) {
	                    split[x] = "$" + split[x];
	                }
	                if(x == 1) {
	                	td += "<img id=\"img" + count + "\" src=\"" + split[17] + "\" onerror=\"standby('img" + count + "')\">";
	                }
	                if(!active) {
		            	td += "<font color=\"#6E0E0E\">";
		            }
	                if(franchise) {
	                    td += "<b>";
	                }
	                result.append("          ").append(td).append(split[x]);
	                if(franchise) {
	                	result.append("</b>");
	                }
	                if(!active) {
	                	result.append("</font>");
		            }
	                result.append(tdEnd);
	            }
	            int year = Worker.getYear();
	            td = "<div class=\"cell\" align=\"left\">";
	            if(active) {
	                if(franchise) {
	                    td += "<b>";
	                }
	                if(trades) {
	                    result.append("          ").append(td).append(displayTradeBox(playerId, teamId)).append(tdEnd);
	                } else if(conType == 'N' || conType == 'F') {
	                	result.append("          ").append(td).append("Up for extension in ").append(year + 1);
	                } else if(Worker.getPlayerPriceForYear(playerId, year, teamId) == 0) {
	                    result.append("          ").append(td).append("EXPIRED!");
	                } else if(expirationYear == year) {
	                    if(!isExtendable) {
	                        result.append("          ").append(td).append("Expiring after current season");
	                    } else {
	                        result.append("          ").append(td).append("Expiring after current season");
	                    }
	                } else if(extensionStartYear == (year + 1)) {
	                    
	                    if(!isExtendable) {
	                        result.append("          ").append(td).append("Expiring following ").append(expirationYear);
	                    } else if((contractLength > 1) || (extensionLength > 1)){
	                        result.append("          ").append(td).append("Up for extension in ").append(expirationYear);
	                    } else {
	                        result.append("          ").append(td).append("Expiring following ").append(expirationYear);
	                    }
	                } else if((extensionLength == 1) || (extensionStartYear == year)) {
	                    if(!isExtendable) {
	                        result.append("          ").append(td).append("Expiring following ").append(expirationYear);
	                    } else if((contractLength > 1) || (extensionLength > 1)){
	                        result.append("          ").append(td).append("Up for extension in ").append(expirationYear);
	                    } else {
	                        result.append("          ").append(td).append("Expiring following ").append(expirationYear);
	                    }
	                } else {
	                    result.append("          ").append(td).append("Up for extension in ").append(expirationYear);
	                }
	                if(franchise) {
	                	result.append("</b>");
	                }
	                result.append(tdEnd);
	            } else {
	            	tdEnd = "</font></div>\n";
	                td += "<font color=\"#6E0E0E\">";
	                if(franchise) {
	                    td += "<b>";
	                }
	                result.append("          ").append(td).append("INACTIVE!");
	                if(franchise) {
	                	result.append("</b>");
	                }
	                result.append(tdEnd);
	                if(trades) {
	                    result.append("          ").append(td).append(displayTradeBox(playerId, teamId)).append(tdEnd);
	                }
	            }
	            result.append("     </div>\n");
	            count++;
        	}
        }
        if(!trades) {
            result.append(displayBudgetRow(teamId, trades)).append("\n");
        }
        result.append("</div>");
        return result.toString();
    }
    
    public static String displayPlayersWithEditsJSON(int teamId, boolean trades, String key)
    {
        char rostered = ' ';
        String contractText = Worker.getContractsForTeamFromJSON(teamId, key);
        if( null == contractText) {
            return "";
        }
        String header = "     <div class=\"row header\">\n";
        String contractHeader = "<div class=\"cell\">Contract</div>";
        String contracts[] = contractText.split("\n");
        StringBuilder result = new StringBuilder();
        result.append("<div class=\"table\">\n");
        result.append(header);
        result.append("         <div class=\"cell\">Player Name</div><div class=\"cell\">Team</div><div class=\"cell\">Position</div><div class=\"cell\">2020 Price</div><div class=\"cell\">2021 Price</div><div class=\"cell\">2022 Price</div><div class=\"cell\">2023 Price</div><div class=\"cell\">2024 Price</div><div class=\"cell\">2025 Price</div><div class=\"cell\">2026 Price</div>");
        result.append(contractHeader).append("\n");
        result.append("     </div>\n");
        if(!trades) {
            result.append(displayBudgetRow(teamId, trades)).append("\n");
        }
        int count = 0;
        for(String contract : contracts) {
        	if(contract.trim().length() > 0) {
	            String split[] = contract.split(";");
	            int playerId = Integer.parseInt(split[0]);
	            char conType = split[12].trim().charAt(0);
	            boolean active = true;
	            if(split[18].toUpperCase().trim().equals("N")) {
	            	active = false;
	            }
	            int contractLength = Integer.parseInt(split[13]);
	            if(contractLength == 0) {
	                contractLength = Integer.parseInt(split[15]);
	            }
	            int extensionStartYear = Integer.parseInt(split[16]);
	            int extensionLength = Integer.parseInt(split[15]);
	            int expirationYear = Worker.expirationYear(playerId, teamId, contractLength, extensionStartYear, extensionLength);
	            boolean isExtendable = Worker.isExtendable(playerId, teamId, rostered, extensionStartYear, expirationYear, extensionLength, contractLength, active, conType);
	            boolean franchise = false;
	            if(split[11].toUpperCase().trim().contains("Y")) {
	                franchise = true;
	            }
	            String td = "<div class=\"cell\" align=\"left\">";
	            String tdEnd = "</div>\n";
	            result.append("     <div class=\"row\">\n");
	            for(int x = 1; x < 11; x++) {
	                if(x == 1 || x == 11) {
	                    td = "<div class=\"cell\" align=\"left\">";
	                } else {
	                    td = "<div class=\"cell\" align=\"center\">";
	                }
	                if(x > 3) {
	                    split[x] = "$" + split[x];
	                }
	                if(x == 1) {
	                	td += "<img id=\"img" + count + "\" src=\"" + split[17] + "\" onerror=\"standby('img" + count + "')\">";
	                }
	                if(!active) {
		            	td += "<font color=\"#6E0E0E\">";
		            }
	                if(franchise) {
	                    td += "<b>";
	                }
	                result.append("          ").append(td).append(split[x]);
	                if(franchise) {
	                	result.append("</b>");
	                }
	                if(!active) {
	                	result.append("</font>");
		            }
	                result.append(tdEnd);
	            }
	            int year = Worker.getYear();
	            int contractStartYear = year + 1;
	            td = "<div class=\"cell\" align=\"left\">";
	            if(active) {
	                if(franchise) {
	                    td += "<b>";
	                }
	                if(conType == 'N' || conType == 'F') {
	                    result.append("          ").append(td).append(displayExtensionBox(playerId, teamId, rostered, contractStartYear, extensionStartYear, conType, extensionLength));
	                } else if(Worker.getPlayerPriceForYear(playerId, year, teamId) == 0) {
	                    result.append("          ").append(td).append("EXPIRED!");
	                } else if(expirationYear == year) {
	                    if(!isExtendable) {
	                        result.append("          ").append(td).append("Expiring after current season");
	                    } else {
	                        result.append("          ").append(td).append(displayExtensionBox(playerId, teamId, rostered, contractStartYear, extensionStartYear, conType, extensionLength)).append("          ");
	                    }
	                } else if(extensionStartYear == (year + 1)) {
	                    
	                    if(!isExtendable) {
	                        result.append("          ").append(td).append(displayExtensionBox(playerId, teamId, rostered, contractStartYear, extensionStartYear, conType, extensionLength)).append("          ");
	                    } else if((contractLength > 1) || (extensionLength > 1)){
	                        result.append("          ").append(td).append(displayExtensionBox(playerId, teamId, rostered, contractStartYear, extensionStartYear, conType, extensionLength)).append("          ");
	                    } else {
	                        result.append("          ").append(td).append(displayExtensionBox(playerId, teamId, rostered, contractStartYear, extensionStartYear, conType, extensionLength)).append("          ");
	                    }
	                } else if((extensionLength == 1) || (extensionStartYear == year)) {
	                    if(!isExtendable) {
	                        result.append("          ").append(td).append(displayExtensionBox(playerId, teamId, rostered, contractStartYear, extensionStartYear, conType, extensionLength)).append("          ");
	                    } else if((contractLength > 1) || (extensionLength > 1)){
	                        result.append("          ").append(td).append("Up for extension in ").append(expirationYear);
	                    } else {
	                        result.append("          ").append(td).append(displayExtensionBox(playerId, teamId, rostered, contractStartYear, extensionStartYear, conType, extensionLength));
	                    }
	                } else {
	                    result.append("          ").append(td).append("Up for extension in ").append(expirationYear);
	                }
	                if(franchise) {
	                	result.append("</b>");
	                }
	                result.append(tdEnd);
	            } else {
	            	tdEnd = "</font></div>\n";
	                td += "<font color=\"#6E0E0E\">";
	                if(franchise) {
	                    td += "<b>";
	                }
	                result.append("          ").append(td).append("INACTIVE!");
	                if(franchise) {
	                	result.append("</b>");
	                }
	                result.append(tdEnd);
	                if(trades) {
	                    result.append("          ").append(td).append(displayTradeBox(playerId, teamId)).append(tdEnd);
	                }
	            }
	            result.append("     </div>\n");
	            count++;
        	}
        }
        if(!trades) {
            result.append(displayBudgetRow(teamId, trades)).append("\n");
        }
        result.append("</div>");
        return result.toString();
    }
    
    public static String displayPlayers(int teamId, boolean trades, char type)
    {
        String text = "";
        char rostered = ' ';
        String header = "     <div class=\"row header\">\n";
        boolean active = true;
        if(type == 'A') {
            text = Worker.getContractsForTeam(teamId, 'Y');
            rostered = 'Y';
        } else {
            text = Worker.getContractsForTeam(teamId, 'N');
            header = "     <div class=\"row header red\">\n";
            rostered = 'N';
            active = false;
        }
        if( null == text) {
            return "";
        }
        String tradeHeader = "";
        String contractHeader = "";
        if(trades) {
            tradeHeader = "<div class=\"cell\">Move Contract</div>";
        } else if(type == 'A') {
            contractHeader = "<div class=\"cell\">Contract</div>";
        }
        String contracts[] = text.split("\n");
        StringBuilder result = new StringBuilder();
        result.append("<div class=\"table\">\n");
        result.append(header);
        result.append("         <div class=\"cell\">Player Name</div><div class=\"cell\">Team</div><div class=\"cell\">Position</div><div class=\"cell\">2020 Price</div><div class=\"cell\">2021 Price</div><div class=\"cell\">2022 Price</div><div class=\"cell\">2023 Price</div><div class=\"cell\">2024 Price</div><div class=\"cell\">2025 Price</div><div class=\"cell\">2026 Price</div>");
        result.append(contractHeader).append(tradeHeader).append("\n");
        result.append("     </div>\n");
        Arrays.sort(contracts);
        int count = 0;
        for(String contract : contracts) {
        	if(contract.trim().length() > 0) {
	            String split[] = contract.split(";");
	            System.out.println(contract);
	            int playerId = Worker.getPlayerIdByNameAndTeam(split[0], split[1]);
	            char conType = split[11].trim().charAt(0);
	            int contractLength = Integer.parseInt(split[12]);
	            if(contractLength == 0) {
	                contractLength = Integer.parseInt(split[14]);
	            }
	            int extensionStartYear = Integer.parseInt(split[15]);
	            int extensionLength = Integer.parseInt(split[14]);
	            int expirationYear = Worker.expirationYear(playerId, teamId, contractLength, extensionStartYear, extensionLength);
	            boolean isExtendable = Worker.isExtendable(playerId, teamId, rostered, extensionStartYear, expirationYear, extensionLength, contractLength, active, conType);
	            boolean franchise = false;
	            if(split[10].toUpperCase().trim().contains("Y")) {
	                franchise = true;
	            }
	            String td = "<div class=\"cell\" align=\"left\">";
	            String tdEnd = "</div>\n";
	            result.append("     <div class=\"row\">\n");
	            if(franchise) {
	                tdEnd = "</b></div>\n";
	            }
	            for(int x = 0; x < 10; x++) {
	                if(x == 0 || x == 10) {
	                    td = "<div class=\"cell\" align=\"left\">";
	                } else {
	                    td = "<div class=\"cell\" align=\"center\">";
	                }
	                if(franchise && (type == 'A')) {
	                    td += "<b>";
	                }
	                if(x > 2) {
	                    split[x] = "$" + split[x];
	                }
	                if(x == 0) {
	                	td += "<img id=\"img" + count + "\" src=\"" + split[16] + "\" onerror=\"standby('img" + count + "')\">";
	                }
	                result.append("          ").append(td).append(split[x]).append(tdEnd);
	            }
	            int year = Worker.getYear();
	            if(type == 'A') {
	                td = "<div class=\"cell\" align=\"left\">";
	                if(franchise) {
	                    td += "<b>";
	                }
	                if(trades) {
	                    result.append("          ").append(td).append(displayTradeBox(playerId, teamId)).append(tdEnd);
	                } else if(conType == 'N' || conType == 'F') {
	                	result.append("          ").append(td).append("Up for extension in ").append(year + 1).append(tdEnd);
	                } else if(Worker.getPlayerPriceForYear(playerId, year, teamId) == 0) {
	                    result.append("          ").append(td).append("EXPIRED!");
	                } else if(expirationYear == year) {
	                    if(!isExtendable) {
	                        result.append("          ").append(td).append("Expiring after current season").append(tdEnd);
	                    } else {
	                        result.append("          ").append(td).append("Expiring after current season").append(tdEnd);
	                    }
	                } else if(extensionStartYear == (year + 1)) {
	                    
	                    if(!isExtendable) {
	                        result.append("          ").append(td).append("Expiring following ").append(expirationYear).append(tdEnd);
	                    } else if((contractLength > 1) || (extensionLength > 1)){
	                        result.append("          ").append(td).append("Up for extension in ").append(expirationYear).append(tdEnd);
	                    } else {
	                        result.append("          ").append(td).append("Expiring following ").append(expirationYear).append(tdEnd);
	                    }
	                } else if((extensionLength == 1) || (extensionStartYear == year)) {
	                    if(!isExtendable) {
	                        result.append("          ").append(td).append("Expiring following ").append(expirationYear).append(tdEnd);
	                    } else if((contractLength > 1) || (extensionLength > 1)){
	                        result.append("          ").append(td).append("Up for extension in ").append(expirationYear).append(tdEnd);
	                    } else {
	                        result.append("          ").append(td).append("Expiring following ").append(expirationYear).append(tdEnd);
	                    }
	                } else {
	                    result.append("          ").append(td).append("Up for extension in ").append(expirationYear).append(tdEnd);
	                }
	            } else {
	                td = "<div class=\"cell\" align=\"left\">";
	                if(franchise) {
	                    td += "<b>";
	                }
	                if(trades) {
	                    result.append("          ").append(td).append(displayTradeBox(playerId, teamId)).append(tdEnd);
	                }
	            }
	            result.append("     </div>\n");
	            count++;
        	}
        }
        if(type == 'A' && !trades) {
            result.append(displayBudgetRow(teamId, trades)).append("\n");
        }
        result.append("</div>");
        return result.toString();
    }
    
    /*
    public static String displayPlayersWithEdits(int teamId, boolean trades, char type)
    {
        String text = "";
        char rostered = ' ';
        String header = "     <div class=\"row header\">\n";
        boolean active = true;
        if(type == 'A') {
            text = Worker.getContractsForTeam(teamId, 'Y');
            rostered = 'Y';
        } else {
            text = Worker.getContractsForTeam(teamId, 'N');
            header = "     <div class=\"row header red\">\n";
            rostered = 'N';
            active = false;
        }
        if(text.isEmpty() || null == text) {
            return "";
        }
        String contractHeader = "";
        if(type == 'A') {
            contractHeader = "<div class=\"cell\">Contract</div>";
        }
        String tradeHeader = "";
        if(trades) {
            tradeHeader = "<div class=\"cell\">Move Contract</div>";
        }
        String contracts[] = text.split("\n");
        StringBuilder result = new StringBuilder();
        result.append("<div class=\"table\">\n");
        result.append(header);
        result.append("         <div class=\"cell\">Player Name</div><div class=\"cell\">Team</div><div class=\"cell\">Position</div><div class=\"cell\">2020 Price</div><div class=\"cell\">2021 Price</div><div class=\"cell\">2022 Price</div><div class=\"cell\">2023 Price</div><div class=\"cell\">2024 Price</div><div class=\"cell\">2025 Price</div><div class=\"cell\">2026 Price</div>");
        result.append(contractHeader).append(tradeHeader).append("\n");
        result.append("     </div>\n");
        Arrays.sort(contracts);
        int count = 0;
        for(String contract : contracts) {
            String split[] = contract.split(";");
            int playerId = Worker.getPlayerIdByNameAndTeam(split[0], split[1]);
            char conType = split[11].trim().charAt(0);
            int contractLength = Integer.parseInt(split[12]);
            if(contractLength == 0) {
                contractLength = Integer.parseInt(split[14]);
            }
            int contractStartYear = Integer.parseInt(split[13]);
            int extensionStartYear = Integer.parseInt(split[15]);
            int extensionLength = Integer.parseInt(split[14]);
            int expirationYear = Worker.expirationYear(playerId, teamId, contractLength, extensionStartYear, extensionLength);
            boolean isExtendable = Worker.isExtendable(playerId, teamId, rostered, extensionStartYear, expirationYear, extensionLength, contractLength, active, conType);
            boolean franchise = false;
            if(split[10].toUpperCase().trim().contains("Y")) {
                franchise = true;
            }
            String td = "<div class=\"cell\" align=\"left\">";
            String tdEnd = "</div>\n";
            result.append("     <div class=\"row\">\n");
            if(franchise) {
                tdEnd = "</b></div>\n";
            }
            for(int x = 0; x < 10; x++) {
                if(x == 0 || x == 10) {
                    td = "<div class=\"cell\" align=\"left\">";
                } else {
                    td = "<div class=\"cell\" align=\"center\">";
                }
                if(franchise && (type == 'A')) {
                    td += "<b>";
                }
                if(x > 2) {
                    split[x] = "$" + split[x];
                }
                if(x == 0) {
                	td += "<img id=\"img" + count + "\" src=\"" + split[16] + "\" onerror=\"standby('img" + count + "')\">";
                }
                result.append("          ").append(td).append(split[x]).append(tdEnd);
            }
            int year = Worker.getYear();
            if(type == 'A') {
                td = "<div class=\"cell\" align=\"left\">";
                if(franchise) {
                    td += "<b>";
                }
                if(conType == 'N' || conType == 'F') {
                    result.append("          ").append(td).append(displayExtensionBox(playerId, teamId, rostered, contractStartYear, extensionStartYear, conType, extensionLength)).append(tdEnd);
                } else if(Worker.getPlayerPriceForYear(playerId, year, teamId) == 0) {
                    result.append("          ").append(td).append("EXPIRED!");
                } else if(expirationYear == year) {
                    if(!isExtendable) {
                        result.append("          ").append(td).append("Expiring after current season").append(tdEnd);
                    } else {
                        result.append("          ").append(td).append(displayExtensionBox(playerId, teamId, rostered, contractStartYear, extensionStartYear, conType, extensionLength)).append("          ").append(tdEnd);
                    }
                } else if(extensionStartYear == (year + 1)) {
                    
                    if(!isExtendable) {
                        result.append("          ").append(td).append(displayExtensionBox(playerId, teamId, rostered, contractStartYear, extensionStartYear, conType, extensionLength)).append("          ").append(tdEnd);
                    } else if((contractLength > 1) || (extensionLength > 1)){
                        result.append("          ").append(td).append(displayExtensionBox(playerId, teamId, rostered, contractStartYear, extensionStartYear, conType, extensionLength)).append("          ").append(tdEnd);
                    } else {
                        result.append("          ").append(td).append(displayExtensionBox(playerId, teamId, rostered, contractStartYear, extensionStartYear, conType, extensionLength)).append("          ").append(tdEnd);
                    }
                } else if((extensionLength == 1) || (extensionStartYear == year)) {
                    if(!isExtendable) {
                        result.append("          ").append(td).append(displayExtensionBox(playerId, teamId, rostered, contractStartYear, extensionStartYear, conType, extensionLength)).append("          ").append(tdEnd);
                    } else if((contractLength > 1) || (extensionLength > 1)){
                        result.append("          ").append(td).append("Up for extension in ").append(expirationYear).append(tdEnd);
                    } else {
                        result.append("          ").append(td).append(displayExtensionBox(playerId, teamId, rostered, contractStartYear, extensionStartYear, conType, extensionLength)).append(tdEnd);
                    }
                } else {
                    result.append("          ").append(td).append("Up for extension in ").append(expirationYear).append(tdEnd);
                }
            }
            if(trades) {
                result.append("          ").append(td).append(displayTradeBox(playerId, teamId)).append(tdEnd);
            }
            result.append("     </div>\n");
            count++;
        }
        if(type == 'A') {
            result.append(displayBudgetRow(teamId, trades)).append("\n");
        }
        result.append("</div>");
        return result.toString();
    }
    */
    
}