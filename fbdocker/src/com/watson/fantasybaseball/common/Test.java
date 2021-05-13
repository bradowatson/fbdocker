package com.watson.fantasybaseball.common;

/**
 * @author brads
 *
 */
public class Test {
	
	public static void main(String[] args ) {
		//String url = "http://bradwatson.ddns.net:81/fantasybaseballREST/rest/ContractService/contracts/team/1";
		String url = "http://bradwatson.ddns.net:81/fantasybaseballREST/rest/Authentication/login";
		String body = "{\r\n" + 
				"	\"name\": \"brad.s.watson@gmail.com\",\r\n" + 
				"	\"password\": \"password\"\r\n" + 
				"}";
		System.out.println(Helper.postJSON(url, body));
	}

}