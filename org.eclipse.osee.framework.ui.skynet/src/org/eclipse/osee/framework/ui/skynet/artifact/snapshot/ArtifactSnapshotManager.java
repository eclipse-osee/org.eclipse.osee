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

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.word.WordConverter;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.osgi.framework.Bundle;

/**
 * This manager is used to store and retrieve artifact pre-rendered data to be used during previews. The manager stores
 * and maintains head revision snapshots in a database and revisions other than head revisions in a local cache.
 * 
 * @author Roberto E. Escobar
 */
public class ArtifactSnapshotManager {
   private static ArtifactSnapshotManager instance = null;

   private ArtifactSnapshotFactory snapshotFactory;
   private RemoteSnapshotManager remoteSnapshotManager;

   private ArtifactSnapshotManager() {
      this.snapshotFactory = new ArtifactSnapshotFactory();
      this.remoteSnapshotManager = new RemoteSnapshotManager();
   }

   public static ArtifactSnapshotManager getInstance() {
      if (instance == null) {
         instance = new ArtifactSnapshotManager();
      }
      return instance;
   }

   /**
    * Retrieves rendered artifact data.
    * 
    * @param artifact get rendered data for this artifact
    * @param forceUpdate artifact data into snapshot repository
    * @return rendered data
    * @throws Exception
    */
   public String getDataSnapshot(Artifact artifact, boolean forceUpdate) throws Exception {
      checkArtifact(artifact);
      ArtifactSnapshot snapshot = null;
      if (forceUpdate == true) {
         try {
            snapshot = doSave(artifact);
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
      } else {
         snapshot = getSnapshotForRenderRetrieval(artifact);
      }
      return snapshotFactory.toAbsoluteUrls(snapshot.getRenderedData());
   }

   /**
    * Retrieves image data from the snapshot repository
    * 
    * @param namespace artifact snapshot namespace
    * @param key artifact snapshot key
    * @param imageKey image to get
    * @param outputStream where image should be sent to
    * @throws Exception
    */
   public void getImageSnapshot(String namespace, String key, String imageKey, OutputStream outputStream) throws Exception {
      ArtifactSnapshot snapshot = getSnapshotFromRemoteStorage(namespace, key);
      try {
         byte[] imageData = snapshot.getBinaryData(imageKey);
         BufferedOutputStream bos = new BufferedOutputStream(outputStream);
         bos.write(imageData, 0, imageData.length);
         bos.flush();
      } catch (Exception ex) {
         if (snapshot == null) {
            throw new Exception("Image snapshot unavailable.");
         } else {
            throw new Exception("Error transmitting Image.", ex);
         }
      }
   }

   /**
    * Check that the requested artifact is valid
    * 
    * @param artifact in question
    * @throws IllegalArgumentException if invalid
    */
   private void checkArtifact(Artifact artifact) {
      if (artifact == null) {
         throw new IllegalArgumentException("Error artifact was null");
      }
   }

   /**
    * Create artifact snapshot and stores data into the snapshot remote repository
    * 
    * @param artifact to store into snapshot repository
    */
   private ArtifactSnapshot doSave(Artifact artifact) throws OseeCoreException, UnsupportedEncodingException {
      checkArtifact(artifact);
      ArtifactSnapshot snapshot = null;
      if (isSavingAllowed()) {
         snapshot = snapshotFactory.createSnapshot(artifact);
         if (snapshot.isDataValid()) {
            new ArtifactSnapshotPersistOperation(remoteSnapshotManager, snapshot).run();
         }
      }
      return snapshot;
   }

   /**
    * Determines whether saving to remote repository is allowed
    * 
    * @return <b>true</b> if saving to remote repository is allowed
    */
   private boolean isSavingAllowed() {
      // TODO Windows dependency needs to be removed once wordML transforms are independent of
      // windows and native transform.
      Bundle bundle = Platform.getBundle("external.osee.xslt.transform.engine");
      return Lib.isWindows() != false && bundle != null && WordConverter.getInstance().isDefaultConverter() == false;
   }

   /**
    * Retrieve snapshot from remote storage. This method returns null if no snapshot is found in remote storage.
    * 
    * @param keyPair
    * @return snapshot instance from remote storage
    * @throws UnsupportedEncodingException
    */
   private ArtifactSnapshot getSnapshotFromRemoteStorage(String guid, String gammaId) throws UnsupportedEncodingException {
      return remoteSnapshotManager.getSnapshot(guid, gammaId);
   }

   /**
    * Retrieves snapshot for this artifact checking remote storage and local cache.
    * 
    * @param artifact to search snapshot for
    * @return snapshot
    * @throws UnsupportedEncodingException
    */
   private ArtifactSnapshot getSnapshotForRenderRetrieval(Artifact artifact) throws OseeCoreException, UnsupportedEncodingException {
      ArtifactSnapshot currentSnapshot = null;
      try {
         currentSnapshot = getSnapshotFromRemoteStorage(artifact.getGuid(), Long.toString(artifact.getGammaId()));
         if (currentSnapshot == null) {
            try {
               currentSnapshot = doSave(artifact);
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            }
         }
      } catch (UnsupportedEncodingException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      return currentSnapshot;
   }
}