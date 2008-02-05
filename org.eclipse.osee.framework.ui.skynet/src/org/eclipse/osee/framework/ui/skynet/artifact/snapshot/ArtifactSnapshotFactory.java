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
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.httpRequests.HttpImageProcessor;
import org.eclipse.osee.framework.skynet.core.linking.HttpUrlBuilder;
import org.eclipse.osee.framework.ui.skynet.httpRequests.HttpImageRequest;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.Renderer;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;

/**
 * Factory class used to create artifact snapshots
 * 
 * @author Roberto E. Escobar
 */
public class ArtifactSnapshotFactory {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(ArtifactSnapshotFactory.class);
   private static final ArtifactSnapshotFactory instance = new ArtifactSnapshotFactory();

   private HttpImageRequest httpImageRequest;
   private HttpImageProcessor httpImageProcessor;
   private KeyGenerator keyGenerator;

   private ArtifactSnapshotFactory() {
      this.keyGenerator = new KeyGenerator();
      this.httpImageRequest = HttpImageRequest.getInstance();
      this.httpImageProcessor = HttpImageProcessor.getInstance();
   }

   protected static ArtifactSnapshotFactory getInstance() {
      return ArtifactSnapshotFactory.instance;
   }

   /**
    * Takes a snapshot of the Artifact
    * 
    * @param artifact source
    * @return snapshot of the artifact
    * @throws UnsupportedEncodingException
    */
   public ArtifactSnapshot createSnapshot(Artifact artifact) throws UnsupportedEncodingException {
      Pair<String, String> key = keyGenerator.getKeyPair(artifact);
      ArtifactSnapshot snapshotData = new ArtifactSnapshot(key.getKey(), key.getValue(), artifact);
      snapshotData.setRenderedData(getRenderedArtifactData(artifact));
      processImageLinks(snapshotData);
      return snapshotData;
   }

   /**
    * Get key generator used to create snapshots
    * 
    * @return key generator
    */
   public KeyGenerator getKeyGenerator() {
      return keyGenerator;
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
                     logger.log(Level.SEVERE, String.format("Error adding http server address."), ex);
                  }
               }
            }
         }
         toReturn = changeSet.applyChangesToSelf().toString();
      }
      return toReturn;
   }

   /**
    * Renders artifact
    * 
    * @param artifact to render
    * @return rendered artifact data
    */
   private String getRenderedArtifactData(Artifact artifact) {
      String toReturn = null;
      IRenderer render = RendererManager.getInstance().getBestRenderer(PresentationType.PREVIEW_IN_COMPOSITE, artifact);
      if (render instanceof Renderer) {
         toReturn = ((Renderer) render).generateHtml(artifact, new NullProgressMonitor());
      } else {
         if (!Strings.isValid(toReturn)) {
            toReturn =
                  String.format(
                        "Unable to generate html for: %s on branch %s <br><br><form><input type=button onClick='window.opener=self;window.close()' value='Close'></form>",
                        artifact.getDescriptiveName(), artifact.getBranch());
         }
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
                           httpImageRequest.getRequestUrl(snapshotData.getNamespace(), snapshotData.getKey(), imageKey);
                     changeSet.replace(matcher.start(), matcher.end(), result + "\"");
                  } catch (Exception ex) {
                     logger.log(Level.SEVERE, String.format(
                           "Image processing error. Unable to take a snapshot of: [%s]", imageKey), ex);
                  }
               }
            }
         }
         snapshotData.setRenderedData(changeSet.applyChangesToSelf().toString());
      }
   }

   protected final class KeyGenerator {

      /**
       * Creates namespace/key pair object from an artifact
       * 
       * @param artifact source
       * @return key pair
       * @throws UnsupportedEncodingException
       */
      public Pair<String, String> getKeyPair(Artifact artifact) throws UnsupportedEncodingException {
         String namespace = getNamespace(artifact);
         String key = "GAMMA" + artifact.getPersistenceMemo().getGammaId();
         return new Pair<String, String>(namespace, encode(key));
      }

      /**
       * Generates a namespace key for an artifact
       * 
       * @param artifact artifact to use when generating the namespace
       * @return namespace
       * @throws UnsupportedEncodingException
       */
      private String getNamespace(Artifact artifact) throws UnsupportedEncodingException {
         StringBuffer namespace = new StringBuffer();
         namespace.append(artifact.getGuid());
         namespace.append("BRANCH");
         namespace.append(artifact.getBranch().getBranchId());
         return encode(namespace.toString());
      }

      /**
       * Convert key pair into a local cache key
       * 
       * @param key Pair containing namespace and key information
       * @return local cache key
       */
      public String toLocalCacheKey(Pair<String, String> key) {
         return key.getKey() + "&" + key.getValue();
      }

      /**
       * Convert key pair into a local cache key
       * 
       * @param namespace to use
       * @param key to use
       * @return local cache key
       */
      public String toLocalCacheKey(String namespace, String key) {
         return namespace + "&" + key;
      }

      private String encode(String value) throws UnsupportedEncodingException {
         return URLEncoder.encode(value, "UTF-8");
      }
   }
}
