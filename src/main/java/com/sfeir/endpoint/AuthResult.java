package com.sfeir.endpoint;

/**
 * Created by anasrekik on 29/04/15.
 */
public class AuthResult {

    private boolean authorized;

    public AuthResult() {
    }

    public AuthResult(boolean authorized) {

        this.authorized = authorized;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }
}
