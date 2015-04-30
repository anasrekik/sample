package com.sfeir.spreadsheet;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.appengine.api.appidentity.AppIdentityService;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.common.collect.Lists;

import java.util.List;

public class OAuthServiceImpl implements OAuthService {

	@Override
	public String getOAuthToken(final OAuthScope scope) {
		final List<String> scopes = Lists.newArrayList(scope.get());
		final AppIdentityService appIdentity = AppIdentityServiceFactory.getAppIdentityService();
		final AppIdentityService.GetAccessTokenResult accessToken = appIdentity.getAccessToken(scopes);

		return accessToken.getAccessToken();
	}

	@Override
	public Credential getCredential(final OAuthScope scope) {
		Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod());
		return credential.setAccessToken(getOAuthToken(scope));
	}
}
