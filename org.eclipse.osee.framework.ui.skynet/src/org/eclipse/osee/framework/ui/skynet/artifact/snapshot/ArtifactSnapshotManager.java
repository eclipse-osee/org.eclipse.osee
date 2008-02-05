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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.SnapshotPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.word.WordConverter;
import org.eclipse.osee.framework.ui.skynet.artifact.snapshot.ArtifactSnapshotFactory.KeyGenerator;
import org.osgi.framework.Bundle;

/**
 * This manager is used to store and retrieve artifact pre-rendered data to be used during previews. The manager stores
 * and maintains head revision snapshots in a database and revisions other than head revisions in a local cache.
 * 
 * @author Roberto E. Escobar
 */
public class ArtifactSnapshotManager {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(ArtifactSnapshotManager.class);
   private static final String PREVIEW_DATA = "artifact.preview.data";
   private static ArtifactSnapshotManager instance = null;

   private SnapshotPersistenceManager snapshotRemoteRepository;
   private ArtifactSnapshotFactory snapshotFactory;
   private KeyGenerator keyGenerator;
   private Map<String, ArtifactSnapshot> snapshotLocalCache;

   private ArtifactSnapshotManager() {
      this.snapshotLocalCache = Collections.synchronizedMap(new HashMap<String, ArtifactSnapshot>());
      this.snapshotFactory = ArtifactSnapshotFactory.getInstance();
      this.keyGenerator = snapshotFactory.getKeyGenerator();
      this.snapshotRemoteRepository = SnapshotPersistenceManager.getInstance();
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
      if (forceUpdate == true) {
         try {
            doSave(artifact);
         } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.toString(), ex);
         }
      }
      ArtifactSnapshot snapshot = getSnapshotForRenderRetrieval(artifact);
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
    * Determine whether a delete in the remote storage is needed.
    * 
    * @param snapshot to store
    * @return <b>true</b> if deletion is needed, otherwise <b>false</b>
    * @throws SQLException
    */
   private boolean isDeleteRequired(ArtifactSnapshot snapshot) throws SQLException {
      boolean oneOrMoreExists = snapshotRemoteRepository.getKeys(snapshot.getNamespace()).size() > 0;
      boolean snapshotIsNotOneOfThem =
            snapshotRemoteRepository.getSnapshot(snapshot.getNamespace(), snapshot.getKey()) == null;
      return oneOrMoreExists && snapshotIsNotOneOfThem;
   }

   /**
    * Create artifact snapshot and stores data into the snapshot remote repository
    * 
    * @param artifact to store into snapshot repository
    * @throws SQLException
    */
   private void doSave(Artifact artifact) throws Exception {
      checkArtifact(artifact);
      if (isSavingAllowed() != false) {
         ArtifactSnapshot snapshot = snapshotFactory.createSnapshot(artifact);
         if (snapshot.isDataValid() != false) {
            if (true == isDeleteRequired(snapshot)) {
               snapshotRemoteRepository.deleteAll(snapshot.getNamespace());
            }
            snapshotRemoteRepository.persistSnapshot(snapshot.getNamespace(), PREVIEW_DATA, snapshot);
         }
      }
   }

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
   private ArtifactSnapshot getRemoteSnapshotAndUpdateIfNeeded(Artifact artifact) throws UnsupportedEncodingException {
      Pair<String, String> snapshotKey = keyGenerator.getKeyPair(artifact);
      ArtifactSnapshot currentSnapshot = getSnapshotFromRemoteStorage(snapshotKey);
      if (currentSnapshot == null || currentSnapshot.isStaleComparedTo(artifact) == true) {
         try {
            doSave(artifact);
            currentSnapshot = getSnapshotFromRemoteStorage(snapshotKey);
         } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.toString(), ex);
         }
      }
      return currentSnapshot;
   }

   /**
    * Retrieve snapshot from remote storage. This method returns null if no snapshot is found in remote storage.
    * 
    * @param keyPair
    * @return snapshot instance from remote storage
    * @throws UnsupportedEncodingException
    */
   private ArtifactSnapshot getSnapshotFromRemoteStorage(Pair<String, String> key) throws UnsupportedEncodingException {
      ArtifactSnapshot toReturn = null;
      Pair<Object, Date> data = snapshotRemoteRepository.getSnapshot(key.getKey(), PREVIEW_DATA);
      if (data != null) {
         Object object = data.getKey();
         if (object != null) {
            if (object instanceof ArtifactSnapshot) {
               toReturn = (ArtifactSnapshot) data.getKey();
            }
         }
      }
      return toReturn;
   }

   /**
    * Retrieve snapshot from local storage
    * 
    * @param artifact
    * @return snapshot from local cache
    * @throws UnsupportedEncodingException
    */
   private ArtifactSnapshot getSnapshotFromLocalCacheAndUpdateIfNeeded(Artifact artifact) throws UnsupportedEncodingException {
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
   private ArtifactSnapshot getSnapshotForRenderRetrieval(Artifact artifact) throws UnsupportedEncodingException {
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