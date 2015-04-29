package com.sfeir.spreadsheet;

public enum SpreadsheetFeedType {

	SPREADSHEETS("spreadsheets"),
	WORKSHEETS("worksheets"),
	CELLS("cells"),
	LIST("list");

	private final String text;

	private SpreadsheetFeedType(final String text) {
		this.text = text;
	}

	public String get() {
		return text;
	}
}
