package com.sfeir.avatar;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.gdata.util.ServiceException;
import com.sfeir.endpoint.ReadSpreadsheet;

import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by oleksiitumanov on 4/30/15.
 */
public class AvatarServletUpload  extends HttpServlet {
    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        try {
            ReadSpreadsheet.authenticate("rekika", "rekik");
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        Map<String, List<BlobKey>> blob = blobstoreService.getUploads(req);
        List<BlobKey> blobKeys = blob.get("avatar");
        if (blobKeys == null || blobKeys.isEmpty()) {
            res.setStatus(res.SC_BAD_REQUEST);
        } else {
            res.setStatus(res.SC_OK);
            try {
                ReadSpreadsheet.saveBlobKey(req.getParameter("login"), blobKeys.get(0).getKeyString());
            } catch (ServiceException e) {
                e.printStackTrace();
            }
        }
    }
}