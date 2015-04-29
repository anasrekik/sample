package com.sfeir.endpoint;

/**
 * Add your first API methods in this class, or you may create another class. In that case, please
 * update your web.xml accordingly.
 **/

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.gdata.util.ServiceException;

import javax.inject.Named;
import java.io.File;
import java.io.IOException;


/**
 * An endpoint class we are exposing
 */
@Api(name = "myApi",
        version = "v1",
        namespace = @ApiNamespace(ownerDomain = "endpoint.sfeir.com",
                ownerName = "endpoint.sfeir.com",
                packagePath = ""))

public class YourFirstAPI {

    @ApiMethod(name = "authenticate")
    public AuthResult authenticate(@Named("login") String login, @Named("pwd") String pwd) {
        AuthResult response = new AuthResult();

        try {
            response.setAuthorized(ReadSpreadsheet.authenticate(login, pwd));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        return response;
    }

    @ApiMethod(name = "saveAvatar")
    public void saveAvatar(@Named("login") String login, @Named("avatar") File avatar) {
        try {
            AvatarStorage.writeToFile(login, avatar);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ApiMethod(name = "getAvatar")
    public File getAvatar(@Named("login") String login) {
        try {
            return AvatarStorage.readFromFile(login);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
