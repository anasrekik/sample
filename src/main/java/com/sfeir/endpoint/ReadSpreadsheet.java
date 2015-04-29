package com.sfeir.endpoint;

import java.io.IOException;
import java.net.URL;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.ServiceException;

public class ReadSpreadsheet {

	public static final String GOOGLE_ACCOUNT_USERNAME = ""; // Fill in google account username
	public static final String GOOGLE_ACCOUNT_PASSWORD = ""; // Fill in google account password
	public static final String SPREADSHEET_URL = "https://spreadsheets.google.com/feeds/spreadsheets/15C9eR7gSY1EqGSTVqrBFuf01zuR8-S8NI_n66r9LZR8"; //Fill in google spreadsheet URI

	public static ListFeed readSP() throws IOException, ServiceException {
		/** Our view of Google Spreadsheets as an authenticated Google user. */
		SpreadsheetService service = new SpreadsheetService("Print Google Spreadsheet Demo");

		// Login and prompt the user to pick a sheet to use.
		service.setUserCredentials(GOOGLE_ACCOUNT_USERNAME, GOOGLE_ACCOUNT_PASSWORD);

		// Load sheet
		URL metafeedUrl = new URL(SPREADSHEET_URL);
		SpreadsheetEntry spreadsheet = service.getEntry(metafeedUrl, SpreadsheetEntry.class);
		URL listFeedUrl = ((WorksheetEntry) spreadsheet.getWorksheets().get(0)).getListFeedUrl();

		// Print entries
		ListFeed feed = (ListFeed) service.getFeed(listFeedUrl, ListFeed.class);

		return feed;
	}


	public static boolean authenticate(String login, String pwd) throws IOException, ServiceException {
		/** Our view of Google Spreadsheets as an authenticated Google user. */
		SpreadsheetService service = new SpreadsheetService("Print Google Spreadsheet Demo");

		// Login and prompt the user to pick a sheet to use.
		service.setUserCredentials(GOOGLE_ACCOUNT_USERNAME, GOOGLE_ACCOUNT_PASSWORD);

		// Load sheet
		URL metafeedUrl = new URL(SPREADSHEET_URL);
		SpreadsheetEntry spreadsheet = service.getEntry(metafeedUrl, SpreadsheetEntry.class);
		URL listFeedUrl = ((WorksheetEntry) spreadsheet.getWorksheets().get(0)).getListFeedUrl();

		// Print entries
		ListFeed feed = (ListFeed) service.getFeed(listFeedUrl, ListFeed.class);

		for (ListEntry entry : feed.getEntries()) {
			if (login.equals(entry.getCustomElements().getValue("Login"))) {
				if (pwd.equals(entry.getCustomElements().getValue("Password"))) {
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}
}