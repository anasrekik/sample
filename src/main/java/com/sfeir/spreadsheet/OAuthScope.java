package com.sfeir.spreadsheet;

public enum OAuthScope {

	CLOUDSTORAGE_READ_ONLY("https://www.googleapis.com/auth/devstorage.read_only"),
	CLOUDSTORAGE_READ_WRITE("https://www.googleapis.com/auth/devstorage.read_write"),
	CLOUDSTORAGE_FULL_CONTROL("https://www.googleapis.com/auth/devstorage.full_control"),

	SPREADSHEETS("https://spreadsheets.google.com/feeds");

	private String scope = "";

	private OAuthScope(String scope) {
		this.scope = scope;
	}

	public String get() {
		return scope;
	}

}
