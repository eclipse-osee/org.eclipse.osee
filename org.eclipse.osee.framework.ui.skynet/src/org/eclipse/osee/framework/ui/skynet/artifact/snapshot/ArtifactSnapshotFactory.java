/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.artifact.snapshot;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilder;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.httpRequests.HttpImageProcessor;
import org.eclipse.osee.framework.skynet.core.revision.RevisionManager;
import org.eclipse.osee.framework.skynet.core.revision.TransactionData;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.httpRequests.HttpImageRequest;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;

/**
 * Factory class used to create artifact snapshots
 * 
 * @author Roberto E. Escobar
 */
class ArtifactSnapshotFactory {

   private HttpImageRequest httpImageRequest;
   private HttpImageProcessor httpImageProcessor;

   protected ArtifactSnapshotFactory() {
      this.httpImageRequest = HttpImageRequest.getInstance();
      this.httpImageProcessor = HttpImageProcessor.getInstance();
   }

   /**
    * Takes a snapshot of the Artifact
    * 
    * @param artifact source
    * @return snapshot of the artifact
    * @throws UnsupportedEncodingException
    */
   public ArtifactSnapshot createSnapshot(Artifact artifact) throws OseeCoreException, UnsupportedEncodingException {
      long start = System.currentTimeMillis();
      ArtifactSnapshot snapshotData =
            new ArtifactSnapshot(artifact.getGuid(), artifact.getGammaId(), getCreationDate(artifact));
      snapshotData.setRenderedData(RendererManager.renderToHtml(artifact));
      processImageLinks(snapshotData);
      OseeLog.log(SkynetGuiPlugin.class, Level.INFO, String.format(
            "Artifact Snapshot Render Time: [%s] - for artifact: [%s, %s]", System.currentTimeMillis() - start,
            artifact.getGuid(), artifact.getGammaId()));
      return snapshotData;
   }

   private Timestamp getCreationDate(Artifact artifact) throws OseeCoreException {
      List<TransactionData> txData =
            new ArrayList<TransactionData>(RevisionManager.getInstance().getTransactionsPerArtifact(artifact));
      for (TransactionData data : txData) {
         if (artifact.getArtId() == data.getAssociatedArtId()) {
            return data.getTimeStamp();
         }
      }
      return null;
   }

   /**
    * Appends HTTP server address and port information to links.
    * 
    * @param original pre-rendered Artifact data containing images
    * @return modified pre-rendered artifact data
    */
   protected String toAbsoluteUrls(String original) {
      String toReturn = "";
      if (Strings.isValid(original) != true) {
         toReturn = "<HTML><BODY><H3>Empty Contents</H3></BODY></HTML>";
      } else {
         ChangeSet changeSet = new ChangeSet(original);
         String tag = httpImageRequest.getRequestType();
         Pattern pattern = Pattern.compile("src=\"(" + tag.replace(".", "\\.") + ")");
         Matcher matcher = pattern.matcher(original);
         while (matcher.find()) {
            if (matcher.groupCount() > 0) {
               String entry = matcher.group(1);
               if (Strings.isValid(entry)) {
                  try {
                     String prefix = HttpUrlBuilder.getInstance().getSkynetHttpLocalServerPrefix();
                     String result = String.format("src=\"%s%s", prefix, tag);
                     changeSet.replace(matcher.start(), matcher.end(), result);
                  } catch (Exception ex) {
                     OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE,
                           String.format("Error adding http server address."), ex);
                  }
               }
            }
         }

         pattern = Pattern.compile("href=\"(.*)?Define?");
         matcher = pattern.matcher(original);
         while (matcher.find()) {
            try {
               String prefix = HttpUrlBuilder.getInstance().getSkynetHttpLocalServerPrefix();
               changeSet.replace(matcher.start(1), matcher.end(1), prefix);
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, String.format("Error adding http server address."), ex);
            }
         }
         toReturn = changeSet.applyChangesToSelf().toString();
      }
      return toReturn;
   }

   /**
    * Stores image data into snapshot and updates links with namespace/key and image key information. Also adds the HTTP
    * image request to the image url.
    * 
    * @param snapshotData snapshot containing image links
    */
   private void processImageLinks(ArtifactSnapshot snapshotData) {
      if (snapshotData.isDataValid() != false) {
         String original = snapshotData.getRenderedData();
         String tag = httpImageProcessor.getImageProcessingMarker();
         Pattern pattern = Pattern.compile("(" + tag + ".*?)\"");
         Matcher matcher = pattern.matcher(original);
         ChangeSet changeSet = new ChangeSet(original);
         while (matcher.find()) {
            if (matcher.groupCount() > 0) {
               String url = matcher.group(1);
               String imageKey = url.replace(tag, "");
               if (Strings.isValid(imageKey)) {
                  try {
                     ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                     httpImageProcessor.processRequest(imageKey, outputStream);
                     snapshotData.addBinaryData(imageKey, outputStream.toByteArray());
                     String result =
                           httpImageRequest.getRequestUrl(snapshotData.getGuid(),
                                 Long.toString(snapshotData.getGamma()), imageKey);
                     changeSet.replace(matcher.start(), matcher.end(), result + "\"");
                  } catch (Exception ex) {
                     OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, String.format(
                           "Image processing error. Unable to take a snapshot of: [%s]", imageKey), ex);
                  }
               }
            }
         }
         snapshotData.setRenderedData(changeSet.applyChangesToSelf().toString());
      }
   }
}
