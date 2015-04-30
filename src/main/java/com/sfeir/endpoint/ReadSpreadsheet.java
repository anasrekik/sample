package com.sfeir.endpoint;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.*;
import com.google.gdata.util.ServiceException;
import com.sfeir.spreadsheet.OAuthScope;
import com.sfeir.spreadsheet.OAuthService;
import com.sfeir.spreadsheet.OAuthServiceImpl;

public class ReadSpreadsheet {
    //Fill in google spreadsheet URI
    public static final String SPREADSHEET_URL = "https://spreadsheets.google.com/feeds/worksheets/15C9eR7gSY1EqGSTVqrBFuf01zuR8-S8NI_n66r9LZR8/private/full";
    public static final String SPREADSHEET_KEY = "15C9eR7gSY1EqGSTVqrBFuf01zuR8-S8NI_n66r9LZR8";

    public static boolean authenticate(String login, String pwd) throws IOException, ServiceException {

        final SpreadsheetService spreadsheetService = new SpreadsheetService("CloudServices");
        spreadsheetService.setConnectTimeout(0);

        final OAuthService oAuthService = new OAuthServiceImpl();
        spreadsheetService.setOAuth2Credentials(oAuthService.getCredential(OAuthScope.SPREADSHEETS));
        // Load spreadsheets
        URL metafeedUrl = new URL(SPREADSHEET_URL);

        final WorksheetFeed worksheetFeed = spreadsheetService.getFeed(metafeedUrl, WorksheetFeed.class);
        final List<WorksheetEntry> entries = worksheetFeed.getEntries();
        URL listFeedUrl = entries.get(0).getListFeedUrl();
        // Logger.getLogger("logger").warning("worksheet id : "+entries.get(0).getId());
        // Get entries
        ListFeed feed = (ListFeed) spreadsheetService.getFeed(listFeedUrl, ListFeed.class);

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