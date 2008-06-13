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
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.word.WordConverter;
import org.osgi.framework.Bundle;

/**
 * This manager is used to store and retrieve artifact pre-rendered data to be used during previews. The manager stores
 * and maintains head revision snapshots in a database and revisions other than head revisions in a local cache.
 * 
 * @author Roberto E. Escobar
 */
public class ArtifactSnapshotManager {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(ArtifactSnapshotManager.class);
   private static ArtifactSnapshotManager instance = null;

   private ArtifactSnapshotFactory snapshotFactory;
   private RemoteSnapshotManager remoteSnapshotManager;
   private KeyGenerator keyGenerator;
   private Map<String, ArtifactSnapshot> snapshotLocalCache;

   private ArtifactSnapshotManager() {
      this.snapshotLocalCache = Collections.synchronizedMap(new HashMap<String, ArtifactSnapshot>());
      this.snapshotFactory = new ArtifactSnapshotFactory();
      this.remoteSnapshotManager = new RemoteSnapshotManager();
      this.keyGenerator = snapshotFactory.getKeyGenerator();
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
            logger.log(Level.SEVERE, ex.toString(), ex);
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
      Pair<String, String> keyPair = new Pair<String, String>(namespace, key);
      ArtifactSnapshot snapshot = getSnapshotFromRemoteStorage(keyPair);
      if (snapshot == null || !snapshot.getKey().equals(key)) {
         snapshot = snapshotLocalCache.get(keyGenerator.toLocalCacheKey(keyPair));
      }
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
    * @throws SQLException
    */
   private ArtifactSnapshot doSave(Artifact artifact) throws Exception {
      checkArtifact(artifact);
      ArtifactSnapshot snapshot = null;
      if (isSavingAllowed() != false) {
         snapshot = snapshotFactory.createSnapshot(artifact);
         if (snapshot.isDataValid() != false) {
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
    * Get the Artifact Snapshot from remote repository and update if needed
    * 
    * @param artifact Identifying the snapshot to get
    * @return The artifact snapshot
    * @throws UnsupportedEncodingException
    */
   private ArtifactSnapshot getRemoteSnapshotAndUpdateIfNeeded(Artifact artifact) throws OseeCoreException, SQLException, UnsupportedEncodingException {
      Pair<String, String> snapshotKey = keyGenerator.getKeyPair(artifact);
      ArtifactSnapshot currentSnapshot = getSnapshotFromRemoteStorage(snapshotKey);
      if (currentSnapshot == null) {
         currentSnapshot = getRemoteSnapshotFromParentBranch(artifact);
      }
      if (currentSnapshot == null || currentSnapshot.isStaleComparedTo(artifact) == true) {
         try {
            currentSnapshot = doSave(artifact);
         } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.toString(), ex);
         }
      }
      return currentSnapshot;
   }

   /**
    * Get the Artifact Snapshot from the remote repository using the artifact's parent branch id
    * 
    * @param artifact Identifying the snapshot to get
    * @return The artifact snapshot
    */
   private ArtifactSnapshot getRemoteSnapshotFromParentBranch(Artifact artifact) {
      ArtifactSnapshot toReturn = null;
      try {
         Branch parentBranch = artifact.getBranch().getParentBranch();
         if (parentBranch != null) {
            Pair<String, String> parentKey = keyGenerator.getKeyPair(artifact, parentBranch);
            toReturn = getSnapshotFromRemoteStorage(parentKey);
         }
      } catch (Exception ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      }
      return toReturn;
   }

   /**
    * Retrieve snapshot from remote storage. This method returns null if no snapshot is found in remote storage.
    * 
    * @param keyPair
    * @return snapshot instance from remote storage
    * @throws UnsupportedEncodingException
    */
   private ArtifactSnapshot getSnapshotFromRemoteStorage(Pair<String, String> key) throws UnsupportedEncodingException {
      return remoteSnapshotManager.getSnapshot(key);
   }

   /**
    * Retrieve snapshot from local storage
    * 
    * @param artifact
    * @return snapshot from local cache
    * @throws UnsupportedEncodingException
    */
   private ArtifactSnapshot getSnapshotFromLocalCacheAndUpdateIfNeeded(Artifact artifact) throws OseeCoreException, SQLException, UnsupportedEncodingException {
      Pair<String, String> key = keyGenerator.getKeyPair(artifact);
      String localCacheKey = keyGenerator.toLocalCacheKey(key);
      ArtifactSnapshot toReturn = snapshotLocalCache.get(localCacheKey);
      if (toReturn == null) {
         toReturn = snapshotFactory.createSnapshot(artifact);
         // Do not allow more than 10 cached snapshots at any given time
         if (snapshotLocalCache.size() > 10) {
            snapshotLocalCache.clear();
         }
         if (toReturn.isDataValid() != false) {
            snapshotLocalCache.put(localCacheKey, toReturn);
         }
      }
      return toReturn;
   }

   /**
    * Retrieves snapshot for this artifact checking remote storage and local cache.
    * 
    * @param artifact to search snapshot for
    * @return snapshot
    * @throws UnsupportedEncodingException
    */
   private ArtifactSnapshot getSnapshotForRenderRetrieval(Artifact artifact) throws OseeCoreException, SQLException, UnsupportedEncodingException {
      ArtifactSnapshot data = null;
      try {
         data = getRemoteSnapshotAndUpdateIfNeeded(artifact);
      } catch (UnsupportedEncodingException ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      }
      if (data == null || data.isValidFor(artifact) != true) {
         data = getSnapshotFromLocalCacheAndUpdateIfNeeded(artifact);
      }
      return data;
   }

}