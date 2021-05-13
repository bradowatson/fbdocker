package com.watson.fantasybaseball;

import com.watson.io.FileManip;

public class ProspectHunter {
	
	public static void main(String[] args) {
		String[] prospects = FileManip.getTextFileContents("D:/OneDrive/Documents/prospects.csv").split("\n");
		for(String prospect : prospects) {
			String[] split = prospect.trim().split(",");
			if(!Worker.isRostered(Worker.getPlayerIdByNameAndTeam(split[0], split[1]))) {
				System.out.println(prospect);
			}
		}
	}

}
