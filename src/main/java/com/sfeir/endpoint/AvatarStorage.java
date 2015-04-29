package com.sfeir.endpoint;

import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.Channels;

/**
 * Created by oleksiitumanov on 4/29/15.
 */
public class AvatarStorage {

    public static final String AVATAR = "avatar";
    private static final GcsService gcsService =
            GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());


    protected static void writeToFile(String login, File content) throws IOException {
        GcsOutputChannel outputChannel =
                gcsService.createOrReplace(new GcsFilename(AVATAR, login), GcsFileOptions.getDefaultInstance());
        ObjectOutputStream oout =
                new ObjectOutputStream(Channels.newOutputStream(outputChannel));
        oout.writeObject(content);
        oout.close();
    }

    protected static File readFromFile(String login)
            throws IOException, ClassNotFoundException {
        GcsInputChannel readChannel = gcsService.openPrefetchingReadChannel(new GcsFilename(AVATAR, login), 0, 1024 * 1024);
        try (ObjectInputStream oin = new ObjectInputStream(Channels.newInputStream(readChannel))) {
            return (File) oin.readObject();
        }
    }

}
