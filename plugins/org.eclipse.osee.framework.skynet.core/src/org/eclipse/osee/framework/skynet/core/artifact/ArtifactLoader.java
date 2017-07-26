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
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
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
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
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

   private static final CompositeKeyHashMap<Integer, Long, ReentrantLock> loadingActiveMap =
      new CompositeKeyHashMap<Integer, Long, ReentrantLock>(1000, true);

   /**
    * (re)loads the artifacts selected by sql and then returns them in a list
    */
   public static List<Artifact> getArtifacts(String sql, Object[] queryParameters, int artifactCountEstimate, LoadLevel loadLevel, LoadType reload, ISearchConfirmer confirmer, TransactionRecord transactionId, DeletionFlag allowDeleted, boolean isArchived) throws OseeCoreException {
      List<Pair<ArtifactId, BranchId>> toLoad = selectArtifacts(sql, queryParameters, artifactCountEstimate);
      List<Artifact> artifacts =
         loadSelectedArtifacts(toLoad, loadLevel, reload, allowDeleted, transactionId, isArchived);

      if (confirmer != null) {
         confirmer.canProceed(artifacts.size());
      }
      return new LinkedList<Artifact>(artifacts);
   }

   public static List<Artifact> loadArtifactIds(Collection<Integer> artIds, BranchId branch, LoadLevel loadLevel, LoadType reload, DeletionFlag allowDeleted) {
      return loadArtifactIds(artIds, branch, loadLevel, reload, allowDeleted, TransactionId.SENTINEL);
   }

   public static List<Artifact> loadArtifactIds(Collection<Integer> artIds, BranchId branch, LoadLevel loadLevel, LoadType reload, DeletionFlag allowDeleted, TransactionId transactionId) {
      List<ArtifactId> artifacts = new ArrayList<>();
      for (Integer artId : artIds) {
         artifacts.add(ArtifactId.valueOf(artId));
      }
      return loadArtifacts(artifacts, branch, loadLevel, reload, allowDeleted, transactionId);
   }

   public static List<Artifact> loadArtifacts(Collection<? extends ArtifactId> artIds, BranchId branch, LoadLevel loadLevel, LoadType reload, DeletionFlag allowDeleted) {
      return loadArtifacts(artIds, branch, loadLevel, reload, allowDeleted, TransactionId.SENTINEL);
   }

   public static List<Artifact> loadArtifacts(Collection<? extends ArtifactId> artIds, BranchId branch, LoadLevel loadLevel, LoadType reload, DeletionFlag allowDeleted, TransactionId transactionId) {
      List<Pair<ArtifactId, BranchId>> toLoad = new LinkedList<>();
      for (ArtifactId artId : new HashSet<>(artIds)) {
         toLoad.add(new Pair<ArtifactId, BranchId>(artId, branch));
      }
      return loadSelectedArtifacts(toLoad, loadLevel, reload, allowDeleted, transactionId,
         BranchManager.isArchived(branch));
   }

   private static List<Artifact> loadSelectedArtifacts(List<Pair<ArtifactId, BranchId>> toLoad, LoadLevel loadLevel, LoadType reload, DeletionFlag allowDeleted, TransactionId transactionId, boolean isArchived) throws OseeCoreException {
      Set<Artifact> artifacts = new LinkedHashSet<>();
      if (transactionId.isValid()) {
         loadArtifacts(toLoad, loadLevel, transactionId, reload, allowDeleted, artifacts, isArchived);
      } else {
         loadActiveArtifacts(toLoad, artifacts, loadLevel, reload, allowDeleted, isArchived);
      }
      return new LinkedList<Artifact>(artifacts);
   }

   private static void loadActiveArtifacts(List<Pair<ArtifactId, BranchId>> toLoad, Set<Artifact> artifacts, LoadLevel loadLevel, LoadType reload, DeletionFlag allowDeleted, boolean isArchived) throws OseeCoreException {
      if (!toLoad.isEmpty()) {
         int numRequested = toLoad.size();
         Iterator<Pair<ArtifactId, BranchId>> iterator = toLoad.iterator();
         CompositeKeyHashMap<Integer, Long, ReentrantLock> locks =
            new CompositeKeyHashMap<Integer, Long, ReentrantLock>();

         while (iterator.hasNext()) {
            Pair<ArtifactId, BranchId> next = iterator.next();
            ArtifactId artId = next.getFirst();
            BranchId branch = next.getSecond();

            Artifact active = null;

            if (reload == LoadType.INCLUDE_CACHE) {
               synchronized (ArtifactCache.class) {
                  active = ArtifactCache.getActive(artId, branch);
               }
            }

            boolean doNotLoad =
               determineIfIShouldLoad(artifacts, allowDeleted, locks, artId.getId().intValue(), branch.getId(), active);

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
               for (Pair<ArtifactId, BranchId> loadPair : toLoad) {
                  Integer artId = loadPair.getFirst().getId().intValue();
                  Long branchUuid = loadPair.getSecond().getId();
                  removeAndUnlock(artId, branchUuid);
                  locks.removeAndGet(artId, branchUuid);
               }
            }
         }
         processLocks(locks, artifacts);
      }
   }

   private static void removeAndUnlock(Integer artId, Long branchUuid) {
      ReentrantLock lock = null;
      synchronized (loadingActiveMap) {
         lock = loadingActiveMap.removeAndGet(artId, branchUuid);
      }
      if (lock != null && lock.isLocked()) {
         lock.unlock();
      }
   }

   private static boolean determineIfIShouldLoad(Set<Artifact> artifacts, DeletionFlag allowDeleted, CompositeKeyHashMap<Integer, Long, ReentrantLock> locks, Integer artId, Long branchUuid, Artifact active) {
      boolean doNotLoad = false;
      //not in the cache
      if (active == null) {
         synchronized (loadingActiveMap) {
            ReentrantLock lock = loadingActiveMap.get(artId, branchUuid);
            // this thread should load the artifact
            if (lock == null) {
               lock = new ReentrantLock();
               lock.lock();
               loadingActiveMap.put(artId, branchUuid, lock);
            } else if (!lock.isHeldByCurrentThread()) {
               // another thread is loading the artifact, do not load it
               locks.put(artId, branchUuid, lock);
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

   private static void processLocks(CompositeKeyHashMap<Integer, Long, ReentrantLock> locks, Set<Artifact> artifacts) {
      Iterator<Entry<Pair<Integer, Long>, ReentrantLock>> iterator = locks.entrySet().iterator();
      while (iterator.hasNext()) {
         Entry<Pair<Integer, Long>, ReentrantLock> entry = iterator.next();
         ArtifactId artId = ArtifactId.valueOf(entry.getKey().getFirst());
         BranchId branch = BranchId.valueOf(entry.getKey().getSecond());
         entry.getValue().lock();
         entry.getValue().unlock();
         Artifact active = ArtifactCache.getActive(artId, branch);
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

         int previousArtId = -1;
         long previousBranchId = -1;
         Long previousViewId = -1L;
         while (chStmt.next()) {
            int artId = chStmt.getInt("id2");
            long branchUuid = chStmt.getLong("branch_id");
            Long viewId = chStmt.getLong("id4");

            // assumption: sql is returning rows ordered by branch_id, art_id, transaction_id in descending order
            if (previousArtId != artId || previousBranchId != branchUuid || !previousViewId.equals(viewId)) {
               // assumption: sql is returning unwanted deleted artifacts only in the historical case
               if (!historical || allowDeleted == DeletionFlag.INCLUDE_DELETED || ModificationType.getMod(
                  chStmt.getInt("mod_type")) != ModificationType.DELETED) {
                  Artifact artifact = retrieveShallowArtifact(chStmt, reload, historical, isArchived);
                  loadedItems.add(artifact);
               }
            }
            previousArtId = artId;
            previousBranchId = branchUuid;
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
   private static void loadArtifacts(List<Pair<ArtifactId, BranchId>> toLoad, LoadLevel loadLevel, TransactionId transactionId, LoadType reload, DeletionFlag allowDeleted, Set<Artifact> artifacts, boolean isArchived) throws OseeCoreException {
      if (toLoad != null && !toLoad.isEmpty()) {

         Id4JoinQuery joinQuery = JoinUtility.createId4JoinQuery();
         for (Pair<ArtifactId, BranchId> pair : toLoad) {
            joinQuery.add(pair.getSecond(), pair.getFirst(), transactionId, pair.getSecond().getViewId());
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
            int artId = chStmt.getInt("art_id");
            toLoad.add(ArtifactId.valueOf(artId));
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
   public static List<Pair<ArtifactId, BranchId>> selectArtifacts(String sql, Object[] queryParameters, int artifactCountEstimate) throws OseeCoreException {
      JdbcStatement chStmt = ConnectionHandler.getStatement();
      long time = System.currentTimeMillis();

      List<Pair<ArtifactId, BranchId>> toLoad = new LinkedList<>();

      try {
         chStmt.runPreparedQuery(artifactCountEstimate, sql, queryParameters);
         while (chStmt.next()) {
            int artId = chStmt.getInt("art_id");
            long branchUuid = chStmt.getLong("branch_id");
            toLoad.add(new Pair<>(ArtifactId.valueOf(artId), BranchId.valueOf(branchUuid)));
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
      long key2;

      CompositeKeyHashMap<Integer, Long, Artifact> tempCache =
         new CompositeKeyHashMap<Integer, Long, Artifact>(artifacts.size(), true);

      for (Artifact artifact : artifacts) {
         key2 = historical ? transactionId.getId() : artifact.getBranchId();
         tempCache.put(artifact.getArtId(), key2, artifact);
      }

      AttributeLoader.loadAttributeData(queryId, tempCache, historical, allowDeleted, loadLevel, isArchived);
      RelationLoader.loadRelationData(queryId, artifacts, historical, loadLevel);

      if (!historical) {
         for (Artifact artifact : artifacts) {
            key2 = artifact.getBranchId();
            removeAndUnlock(artifact.getArtId(), key2);
         }
      }
   }

}