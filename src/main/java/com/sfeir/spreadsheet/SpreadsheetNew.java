package com.sfeir.spreadsheet;

import com.google.common.collect.Table;

import java.util.Map;

public interface SpreadsheetNew {

	Table<Integer, Integer, String> getCells(String key) ;

	Table<Integer, Integer, String> getCells(String key, String worksheetTitle);

	void insertRow(Map<String, String> columnHeader2cellValues, String key, String applicationName);

	void insertRow(Map<String, String> columnHeader2cellValues, String key, String worksheetTitle, String applicationName);

	Map<String, String> listWorksheets(String key);

	String findSpreadsheetKeyFromTitle(String spreadsheetTitle);
}
