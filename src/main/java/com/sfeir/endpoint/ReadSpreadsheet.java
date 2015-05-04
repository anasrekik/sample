package com.sfeir.endpoint;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.google.gdata.util.ServiceException;
import com.sfeir.spreadsheet.OAuthScope;
import com.sfeir.spreadsheet.OAuthService;
import com.sfeir.spreadsheet.OAuthServiceImpl;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

public class ReadSpreadsheet {
    //Fill in google spreadsheet URI
    public static final String SPREADSHEET_URL = "https://spreadsheets.google.com/feeds/worksheets/15C9eR7gSY1EqGSTVqrBFuf01zuR8-S8NI_n66r9LZR8/private/full";
    public static final String SPREADSHEET_KEY = "15C9eR7gSY1EqGSTVqrBFuf01zuR8-S8NI_n66r9LZR8";
    public static final String DEFAULT_APPLICATION_NAME = "MyApplication";

    private static SpreadsheetService getSpreadsheetService(String applicationName) {
        final SpreadsheetService spreadsheetService = new SpreadsheetService(applicationName);
        spreadsheetService.setConnectTimeout(0);
        final OAuthService oAuthService = new OAuthServiceImpl();
        spreadsheetService.setOAuth2Credentials(oAuthService.getCredential(OAuthScope.SPREADSHEETS));
        return spreadsheetService;
    }

    private static List<WorksheetEntry> getSpreadsheet(SpreadsheetService spreadsheetService) throws IOException, ServiceException {
        URL metafeedUrl = new URL(SPREADSHEET_URL);
        final WorksheetFeed worksheetFeed = spreadsheetService.getFeed(metafeedUrl, WorksheetFeed.class);
        final List<WorksheetEntry> entries = worksheetFeed.getEntries();
        return entries;
    }

    private static ListFeed getListFeed(SpreadsheetService spreadsheetService, List<WorksheetEntry> entries) throws IOException, ServiceException {
        URL listFeedUrl = entries.get(0).getListFeedUrl();
        Logger.getLogger("logger").info("worksheet id : " + entries.get(0).getId());
        Logger.getLogger("logger").info("worksheet title : " + entries.get(0).getTitle().getPlainText());
        // Get entries
        return (ListFeed) spreadsheetService.getFeed(listFeedUrl, ListFeed.class);
    }

    public static boolean authenticate(String login, String pwd) throws IOException, ServiceException {
        final SpreadsheetService spreadsheetService = getSpreadsheetService(DEFAULT_APPLICATION_NAME);
        for (ListEntry entry : getListFeed(spreadsheetService, getSpreadsheet(spreadsheetService)).getEntries()) {
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

    public static void saveBlobKey(String login, String key) throws IOException, ServiceException {
        final SpreadsheetService spreadsheetService = getSpreadsheetService(DEFAULT_APPLICATION_NAME);
        for (ListEntry entry : getListFeed(spreadsheetService, getSpreadsheet(spreadsheetService)).getEntries()) {
            if (login.equals(entry.getCustomElements().getValue("Login"))) {
                entry.getCustomElements().setValueLocal("PhotoKey",key);
            }
        }
    }
}