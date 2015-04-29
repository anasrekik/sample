package com.sfeir.spreadsheet;

import com.google.api.client.auth.oauth2.Credential;

public interface OAuthService {

	String getOAuthToken(OAuthScope scope);

	Credential getCredential(OAuthScope scope);

}
