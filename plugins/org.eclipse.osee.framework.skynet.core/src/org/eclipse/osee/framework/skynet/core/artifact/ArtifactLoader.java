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
package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.sql.OseeSql;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.framework.skynet.core.utility.Id4JoinQuery;
import org.eclipse.osee.framework.skynet.core.utility.JoinUtility;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * @author Ryan D. Brooks
 */
public final class ArtifactLoader {

   private static final ConcurrentHashMap<ArtifactToken, ReentrantLock> loadingActiveMap =
      new ConcurrentHashMap<>(1000);

   /**
    * (re)loads the artifacts selected by sql and then returns them in a list
    */
   public static List<Artifact> getArtifacts(String sql, Object[] queryParameters, int artifactCountEstimate, LoadLevel loadLevel, LoadType reload, ISearchConfirmer confirmer, TransactionRecord transactionId, DeletionFlag allowDeleted, boolean isArchived) throws OseeCoreException {
      List<ArtifactToken> toLoad = selectArtifacts(sql, queryParameters, artifactCountEstimate);
      List<Artifact> artifacts =
         loadSelectedArtifacts(toLoad, loadLevel, reload, allowDeleted, transactionId, isArchived);

      if (confirmer != null) {
         confirmer.canProceed(artifacts.size());
      }
      return new LinkedList<Artifact>(artifacts);
   }

   public static List<Artifact> loadArtifacts(Collection<? extends ArtifactId> artIds, BranchId branch, LoadLevel loadLevel, LoadType reload, DeletionFlag allowDeleted) {
      return loadArtifacts(artIds, branch, loadLevel, reload, allowDeleted, TransactionId.SENTINEL);
   }

   public static List<Artifact> loadArtifacts(Collection<? extends ArtifactId> artIds, BranchId branch, LoadLevel loadLevel, LoadType reload, DeletionFlag allowDeleted, TransactionId transactionId) {
      List<ArtifactToken> toLoad = new LinkedList<>();
      for (ArtifactId artId : new HashSet<>(artIds)) {
         toLoad.add(ArtifactToken.valueOf(artId, branch));
      }
      return loadSelectedArtifacts(toLoad, loadLevel, reload, allowDeleted, transactionId,
         BranchManager.isArchived(branch));
   }

   private static List<Artifact> loadSelectedArtifacts(List<ArtifactToken> toLoad, LoadLevel loadLevel, LoadType reload, DeletionFlag allowDeleted, TransactionId transactionId, boolean isArchived) throws OseeCoreException {
      Set<Artifact> artifacts = new LinkedHashSet<>();
      if (transactionId.isValid()) {
         loadArtifacts(toLoad, loadLevel, transactionId, reload, allowDeleted, artifacts, isArchived);
      } else {
         loadActiveArtifacts(toLoad, artifacts, loadLevel, reload, allowDeleted, isArchived);
      }
      return new LinkedList<Artifact>(artifacts);
   }

   private static void loadActiveArtifacts(List<ArtifactToken> toLoad, Set<Artifact> artifacts, LoadLevel loadLevel, LoadType reload, DeletionFlag allowDeleted, boolean isArchived) throws OseeCoreException {
      if (!toLoad.isEmpty()) {
         int numRequested = toLoad.size();
         Iterator<ArtifactToken> iterator = toLoad.iterator();
         ConcurrentHashMap<ArtifactToken, ReentrantLock> locks = new ConcurrentHashMap<>();

         while (iterator.hasNext()) {
            ArtifactToken artifact = iterator.next();

            Artifact active = null;

            if (reload == LoadType.INCLUDE_CACHE) {
               synchronized (ArtifactCache.class) {
                  active = ArtifactCache.getActive(artifact);
               }
            }

            boolean doNotLoad = determineIfIShouldLoad(artifacts, allowDeleted, locks, artifact, active);

            if (doNotLoad) {
               iterator.remove();
            }
         }

         // load arts that are not in the cache
         try {
            loadArtifacts(toLoad, loadLevel, TransactionId.SENTINEL, reload, allowDeleted, artifacts, isArchived);
         } finally {
            // remove and unlock locks this thread created but didn't load
            if (artifacts.size() != numRequested) {
               for (ArtifactToken artifact : toLoad) {
                  removeAndUnlock(artifact);
                  locks.remove(artifact);
               }
            }
         }
         processLocks(locks, artifacts);
      }
   }

   private static void removeAndUnlock(ArtifactToken artifact) {
      ReentrantLock lock = null;
      synchronized (loadingActiveMap) {
         lock = loadingActiveMap.remove(artifact);
      }
      if (lock != null && lock.isLocked()) {
         lock.unlock();
      }
   }

   private static boolean determineIfIShouldLoad(Set<Artifact> artifacts, DeletionFlag allowDeleted, ConcurrentHashMap<ArtifactToken, ReentrantLock> locks, ArtifactToken artifact, Artifact active) {
      boolean doNotLoad = false;
      //not in the cache
      if (active == null) {
         synchronized (loadingActiveMap) {
            ReentrantLock lock = loadingActiveMap.get(artifact);
            // this thread should load the artifact
            if (lock == null) {
               lock = new ReentrantLock();
               lock.lock();
               loadingActiveMap.put(artifact, lock);
            } else if (!lock.isHeldByCurrentThread()) {
               // another thread is loading the artifact, do not load it
               locks.put(artifact, lock);
               doNotLoad = true;
            }
         }
      } else {
         // artifact is in the cache, do not load it
         if (!active.isDeleted() || active.isDeleted() && allowDeleted == DeletionFlag.INCLUDE_DELETED) {
            artifacts.add(active);
         }
         doNotLoad = true;
      }
      return doNotLoad;
   }

   private static void processLocks(ConcurrentHashMap<ArtifactToken, ReentrantLock> locks, Set<Artifact> artifacts) {
      Iterator<Entry<ArtifactToken, ReentrantLock>> iterator = locks.entrySet().iterator();
      while (iterator.hasNext()) {
         Entry<ArtifactToken, ReentrantLock> entry = iterator.next();
         ArtifactToken artifact = entry.getKey();
         ReentrantLock lock = entry.getValue();
         lock.lock();
         lock.unlock();
         Artifact active = ArtifactCache.getActive(artifact);
         if (active != null) {
            artifacts.add(active);
         }
      }
   }

   private static void loadArtifactsFromQueryId(Collection<Artifact> loadedItems, int queryId, LoadLevel loadLevel, ISearchConfirmer confirmer, int fetchSize, LoadType reload, TransactionId transactionId, DeletionFlag allowDeleted, boolean isArchived) throws OseeCoreException {
      OseeSql sqlKey;
      boolean historical = transactionId.isValid();

      if (historical && isArchived) {
         sqlKey = OseeSql.LOAD_HISTORICAL_ARCHIVED_ARTIFACTS;
      } else if (isArchived) {
         sqlKey = OseeSql.LOAD_CURRENT_ARCHIVED_ARTIFACTS;
      } else if (historical) {
         sqlKey = OseeSql.LOAD_HISTORICAL_ARTIFACTS;
      } else if (allowDeleted == DeletionFlag.INCLUDE_DELETED) {
         sqlKey = OseeSql.LOAD_CURRENT_ARTIFACTS_WITH_DELETED;
      } else {
         sqlKey = OseeSql.LOAD_CURRENT_ARTIFACTS;
      }

      JdbcStatement chStmt = ConnectionHandler.getStatement();
      String sql = null;
      try {
         sql = ServiceUtil.getSql(sqlKey);
         chStmt.runPreparedQuery(fetchSize, sql, queryId);

         ArtifactId previousArtId = ArtifactId.SENTINEL;
         BranchId previousBranchId = BranchId.SENTINEL;
         Long previousViewId = -1L;
         while (chStmt.next()) {
            ArtifactId artId = ArtifactId.valueOf(chStmt.getLong("id2"));
            BranchId branch = BranchId.valueOf(chStmt.getLong("branch_id"));
            Long viewId = chStmt.getLong("id4");

            // assumption: sql is returning rows ordered by branch_id, art_id, transaction_id in descending order
            if (previousArtId.notEqual(artId) || previousBranchId.notEqual(branch) || !previousViewId.equals(viewId)) {
               // assumption: sql is returning unwanted deleted artifacts only in the historical case
               if (!historical || allowDeleted == DeletionFlag.INCLUDE_DELETED || ModificationType.getMod(
                  chStmt.getInt("mod_type")) != ModificationType.DELETED) {
                  Artifact artifact = retrieveShallowArtifact(chStmt, reload, historical, isArchived);
                  loadedItems.add(artifact);
               }
            }
            previousArtId = artId;
            previousBranchId = branch;
            previousViewId = viewId;
         }
      } catch (OseeDataStoreException ex) {
         OseeLog.logf(Activator.class, Level.SEVERE, ex, "%s - %s", sqlKey, sql == null ? "SQL unknown" : sql);
         throw ex;
      } finally {
         chStmt.close();
      }

      if (confirmer == null || confirmer.canProceed(loadedItems.size())) {
         loadArtifactsData(queryId, loadedItems, loadLevel, reload, transactionId, allowDeleted, isArchived);
      }
   }

   /**
    * loads or reloads artifacts based on artifact ids and branch uuids
    *
    * @param artifacts
    * @param locks
    */
   private static void loadArtifacts(List<ArtifactToken> toLoad, LoadLevel loadLevel, TransactionId transactionId, LoadType reload, DeletionFlag allowDeleted, Set<Artifact> artifacts, boolean isArchived) throws OseeCoreException {
      if (toLoad != null && !toLoad.isEmpty()) {

         Id4JoinQuery joinQuery = JoinUtility.createId4JoinQuery();
         for (ArtifactToken artifact : toLoad) {
            BranchId branch = artifact.getBranch();
            joinQuery.add(branch, artifact, transactionId, branch.getViewId());
         }
         loadArtifacts(artifacts, joinQuery, loadLevel, null, reload, transactionId, allowDeleted, isArchived);
      }
   }

   private static void loadArtifacts(Collection<Artifact> loadedItems, Id4JoinQuery joinQuery, LoadLevel loadLevel, ISearchConfirmer confirmer, LoadType reload, TransactionId transactionId, DeletionFlag allowDeleted, boolean isArchived) throws OseeCoreException {
      if (!joinQuery.isEmpty()) {
         Collection<Artifact> data;
         if (loadedItems.isEmpty()) {
            data = loadedItems;
         } else {
            // Use a new list if loaded items already contains data to prevent artifact overwrites during loading
            data = new ArrayList<>(joinQuery.size());
         }
         long time = System.currentTimeMillis();
         try {
            joinQuery.store();
            loadArtifactsFromQueryId(data, joinQuery.getQueryId(), loadLevel, confirmer, joinQuery.size(), reload,
               transactionId, allowDeleted, isArchived);
         } finally {
            try {
               if (data != loadedItems) {
                  loadedItems.addAll(data);
               }
               OseeLog.logf(Activator.class, Level.FINE, "Artifact Load Time [%s] for [%d] artifacts. ",
                  Lib.getElapseString(time), loadedItems.size());
            } finally {
               joinQuery.close();
            }
         }
      }
   }

   /**
    * Determines the artIds and branchUuids of artifacts to load based on sql and queryParameters
    */
   public static List<ArtifactId> selectArtifactIds(String sql, Object[] queryParameters, int artifactCountEstimate) throws OseeCoreException {
      JdbcStatement chStmt = ConnectionHandler.getStatement();
      long time = System.currentTimeMillis();

      List<ArtifactId> toLoad = new ArrayList<>();

      try {
         chStmt.runPreparedQuery(artifactCountEstimate, sql, queryParameters);
         while (chStmt.next()) {
            toLoad.add(ArtifactId.valueOf(chStmt.getLong("art_id")));
         }
      } finally {
         chStmt.close();
      }
      OseeLog.logf(Activator.class, Level.FINE, "Artifact Selection Time [%s], [%d] artifacts selected",
         Lib.getElapseString(time), toLoad.size());
      return toLoad;
      //      processList(queryId, toLoad, artifacts, insertParameters, transactionId, reload, locks);
   }

   /**
    * Determines the artIds and branchUuids of artifacts to load based on sql and queryParameters
    */
   public static List<ArtifactToken> selectArtifacts(String sql, Object[] queryParameters, int artifactCountEstimate) throws OseeCoreException {
      long time = System.currentTimeMillis();
      List<ArtifactToken> toLoad = new LinkedList<>();

      ConnectionHandler.getJdbcClient().runQuery(
         stmt -> toLoad.add(ArtifactToken.valueOf(stmt.getLong("art_id"), BranchId.valueOf(stmt.getLong("branch_id")))),
         artifactCountEstimate, sql, queryParameters);

      OseeLog.logf(Activator.class, Level.FINE, "Artifact Selection Time [%s], [%d] artifacts selected",
         Lib.getElapseString(time), toLoad.size());
      return toLoad;
   }

   /**
    * This method is called only after the cache has been checked
    */
   private static Artifact retrieveShallowArtifact(JdbcStatement chStmt, LoadType reload, boolean historical, boolean isArchived) throws OseeCoreException {
      ArtifactId artifactId = ArtifactId.valueOf(chStmt.getLong("id2"));
      BranchId branch = BranchId.create(chStmt.getLong("branch_id"), ArtifactId.valueOf(chStmt.getLong("id4")));

      TransactionToken transactionId = TransactionToken.SENTINEL;
      ApplicabilityId appId = ApplicabilityId.valueOf(chStmt.getLong("app_id"));
      if (historical) {
         transactionId = TransactionToken.valueOf(chStmt.getLong("stripe_transaction_id"), branch);
      }

      Artifact artifact = historical ? null : ArtifactCache.getActive(artifactId, branch);
      if (artifact == null) {
         ArtifactTypeId artifactType = ArtifactTypeId.valueOf(chStmt.getLong("art_type_id"));
         ArtifactFactory factory = ArtifactTypeManager.getFactory(artifactType);

         artifact =
            factory.loadExisitingArtifact(artifactId, chStmt.getString("guid"), artifactType, chStmt.getInt("gamma_id"),
               branch, transactionId, ModificationType.getMod(chStmt.getInt("mod_type")), appId, historical);
      }

      if (reload == LoadType.RELOAD_CACHE) {
         artifact.internalSetPersistenceData(chStmt.getInt("gamma_id"), transactionId,
            ModificationType.getMod(chStmt.getInt("mod_type")), appId, historical, false);
      }
      return artifact;
   }

   static void loadArtifactData(Artifact artifact, LoadLevel loadLevel, boolean isArchived) throws OseeCoreException {
      try (Id4JoinQuery joinQuery = JoinUtility.createId4JoinQuery()) {
         joinQuery.add(artifact.getBranch(), ArtifactId.valueOf(artifact.getId()), TransactionId.SENTINEL,
            artifact.getBranch().getViewId());
         joinQuery.store();

         List<Artifact> artifacts = new ArrayList<>(1);
         artifacts.add(artifact);
         loadArtifactsData(joinQuery.getQueryId(), artifacts, loadLevel, LoadType.INCLUDE_CACHE, TransactionId.SENTINEL,
            artifact.isDeleted() ? DeletionFlag.INCLUDE_DELETED : DeletionFlag.EXCLUDE_DELETED, isArchived);
      }
   }

   private static void loadArtifactsData(int queryId, Collection<Artifact> artifacts, LoadLevel loadLevel, LoadType reload, TransactionId transactionId, DeletionFlag allowDeleted, boolean isArchived) throws OseeCoreException {
      if (reload == LoadType.RELOAD_CACHE) {
         for (Artifact artifact : artifacts) {
            artifact.prepareForReload();
         }
      }
      boolean historical = transactionId.isValid();
      CompositeKeyHashMap<ArtifactId, Id, Artifact> tempCache = new CompositeKeyHashMap<>(artifacts.size(), true);

      for (Artifact artifact : artifacts) {
         Id key2 = historical ? transactionId : artifact.getBranch();
         tempCache.put(artifact, key2, artifact);
      }

      AttributeLoader.loadAttributeData(queryId, tempCache, historical, allowDeleted, loadLevel, isArchived);
      RelationLoader.loadRelationData(queryId, artifacts, historical, loadLevel);

      if (!historical) {
         for (Artifact artifact : artifacts) {
            removeAndUnlock(artifact);
         }
      }
   }
}