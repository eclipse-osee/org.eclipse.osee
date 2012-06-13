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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.database.core.OseeSql;
import org.eclipse.osee.framework.database.core.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Ryan D. Brooks
 */
public final class ArtifactLoader {

   private static final String INSERT_JOIN_ARTIFACT =
      "INSERT INTO osee_join_artifact (query_id, insert_time, art_id, branch_id, transaction_id) VALUES (?, ?, ?, ?, ?)";
   private static final String DELETE_FROM_JOIN_ARTIFACT = "DELETE FROM osee_join_artifact WHERE query_id = ?";

   private static final CompositeKeyHashMap<Integer, Integer, ReentrantLock> loadingActiveMap =
      new CompositeKeyHashMap<Integer, Integer, ReentrantLock>(1000, true);
   private static final Random queryRandom = new Random();

   /**
    * (re)loads the artifacts selected by sql and then returns them in a list
    */
   public static List<Artifact> getArtifacts(String sql, Object[] queryParameters, int artifactCountEstimate, LoadLevel loadLevel, LoadType reload, ISearchConfirmer confirmer, TransactionRecord transactionId, DeletionFlag allowDeleted) throws OseeCoreException {
      List<Artifact> artifacts = new ArrayList<Artifact>(artifactCountEstimate);
      int queryId = getNewQueryId();
      CompositeKeyHashMap<Integer, Integer, Object[]> insertParameters =
         new CompositeKeyHashMap<Integer, Integer, Object[]>(artifactCountEstimate, true);
      selectArtifacts(artifacts, queryId, insertParameters, sql, queryParameters, artifactCountEstimate, transactionId,
         reload);

      if (!insertParameters.isEmpty()) {
         artifacts.addAll(loadArtifacts(queryId, loadLevel, confirmer,
            new ArrayList<Object[]>(insertParameters.values()), reload, transactionId, allowDeleted));
      } else if (confirmer != null) {
         confirmer.canProceed(artifacts.size());
      }
      return artifacts;
   }

   private static void loadArtifactsFromQueryId(Collection<Artifact> loadedItems, int queryId, LoadLevel loadLevel, ISearchConfirmer confirmer, int fetchSize, LoadType reload, TransactionRecord transactionId, DeletionFlag allowDeleted) throws OseeCoreException {
      try {
         OseeSql sqlKey;
         boolean historical = transactionId != null;
         if (historical) {
            sqlKey = OseeSql.LOAD_HISTORICAL_ARTIFACTS;
         } else if (allowDeleted == DeletionFlag.INCLUDE_DELETED) {
            sqlKey = OseeSql.LOAD_CURRENT_ARTIFACTS_WITH_DELETED;
         } else {
            sqlKey = OseeSql.LOAD_CURRENT_ARTIFACTS;
         }

         IOseeStatement chStmt = ConnectionHandler.getStatement();

         String sql = null;
         try {
            sql = ClientSessionManager.getSql(sqlKey);
            chStmt.runPreparedQuery(fetchSize, sql, queryId);

            int previousArtId = -1;
            int previousBranchId = -1;
            while (chStmt.next()) {
               int artId = chStmt.getInt("art_id");
               int branchId = chStmt.getInt("branch_id");
               // assumption: sql is returning rows ordered by branch_id, art_id, transaction_id in descending order
               if (previousArtId != artId || previousBranchId != branchId) {
                  // assumption: sql is returning unwanted deleted artifacts only in the historical case
                  if (!historical || allowDeleted == DeletionFlag.INCLUDE_DELETED || ModificationType.getMod(chStmt.getInt("mod_type")) != ModificationType.DELETED) {
                     Artifact shallowArtifact = retrieveShallowArtifact(chStmt, reload, historical);
                     loadedItems.add(shallowArtifact);
                  }
               }
               previousArtId = artId;
               previousBranchId = branchId;
            }
         } catch (OseeDataStoreException ex) {
            OseeLog.logf(Activator.class, Level.SEVERE, ex, "%s - %s", sqlKey, sql == null ? "SQL unknown" : sql);
            throw ex;
         } finally {
            chStmt.close();
         }

         if (confirmer == null || confirmer.canProceed(loadedItems.size())) {
            loadArtifactsData(queryId, loadedItems, loadLevel, reload, transactionId, allowDeleted);
         }
      } finally {
         clearQuery(queryId);
      }
   }

   public static List<Artifact> loadArtifacts(Collection<Integer> artIds, IOseeBranch branch, LoadLevel loadLevel, LoadType reload, DeletionFlag allowDeleted) throws OseeCoreException {
      return loadArtifacts(artIds, branch, loadLevel, null, reload, allowDeleted);
   }

   private synchronized static void checkArtifactCache(ArrayList<Artifact> artifacts, Collection<Integer> artIds, TransactionRecord transactionId, IOseeBranch branch, DeletionFlag allowDeleted) throws OseeCoreException {
      Iterator<Integer> iterator = artIds.iterator();
      while (iterator.hasNext()) {
         Integer artId = iterator.next();
         Artifact artifact = getArtifactFromCache(artId, transactionId, branch);

         if (artifact != null) {
            if (allowDeleted == DeletionFlag.EXCLUDE_DELETED && artifact.isDeleted()) {
               continue;
            }
            iterator.remove();
            artifacts.add(artifact);
         }
      }
   }

   /**
    * loads or reloads artifacts based on artifact ids and branch ids
    */
   private static List<Artifact> loadArtifacts(Collection<Integer> artIds, IOseeBranch branch, LoadLevel loadLevel, TransactionRecord transactionId, LoadType reload, DeletionFlag allowDeleted) throws OseeCoreException {
      ArrayList<Artifact> artifacts = new ArrayList<Artifact>();

      checkArtifactCache(artifacts, artIds, transactionId, branch, allowDeleted);
      if (artIds != null && !artIds.isEmpty()) {
         int queryId = ArtifactLoader.getNewQueryId();
         Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();
         boolean historical = transactionId != null;

         List<Object[]> insertParameters = new LinkedList<Object[]>();

         for (int artId : org.eclipse.osee.framework.jdk.core.util.Collections.unique(artIds)) {
            insertParameters.add(new Object[] {
               queryId,
               insertTime,
               artId,
               BranchManager.getBranchId(branch),
               historical ? transactionId.getId() : SQL3DataType.INTEGER});
         }

         for (Artifact artifact : loadArtifacts(queryId, loadLevel, null, insertParameters, reload, transactionId,
            allowDeleted)) {
            artifacts.add(artifact);
         }
      }
      return artifacts;
   }

   /**
    * loads or reloads artifacts based on artifact ids and branch ids in the insertParameters
    */
   private static List<Artifact> loadArtifacts(int queryId, LoadLevel loadLevel, ISearchConfirmer confirmer, List<Object[]> insertParameters, LoadType reload, TransactionRecord transactionId, DeletionFlag allowDeleted) throws OseeCoreException {
      List<Artifact> loadedItems = new ArrayList<Artifact>(insertParameters.size());
      loadArtifacts(loadedItems, queryId, loadLevel, confirmer, insertParameters, reload, transactionId, allowDeleted);
      return loadedItems;
   }

   private static void loadArtifacts(Collection<Artifact> loadedItems, int queryId, LoadLevel loadLevel, ISearchConfirmer confirmer, List<Object[]> insertParameters, LoadType reload, TransactionRecord transactionId, DeletionFlag allowDeleted) throws OseeCoreException {
      if (!insertParameters.isEmpty()) {
         Collection<Artifact> data;
         if (loadedItems.isEmpty()) {
            data = loadedItems;
         } else {
            // Use a new list if loaded items already contains data to prevent artifact overwrites during loading
            data = new ArrayList<Artifact>(insertParameters.size());
         }
         long time = System.currentTimeMillis();
         try {
            insertIntoArtifactJoin(insertParameters);
            loadArtifactsFromQueryId(data, queryId, loadLevel, confirmer, insertParameters.size(), reload,
               transactionId, allowDeleted);
         } finally {
            if (data != loadedItems) {
               loadedItems.addAll(data);
            }
            OseeLog.logf(Activator.class, Level.FINE, new Exception("Artifact Load Time"),
               "Artifact Load Time [%s] for [%d] artifacts. ", Lib.getElapseString(time), loadedItems.size());
            clearQuery(queryId);
         }
      }
   }

   /**
    * must be call in a try block with a finally clause which calls clearQuery()
    */
   public static int insertIntoArtifactJoin(OseeConnection connection, List<Object[]> insertParameters) throws OseeCoreException {
      return ConnectionHandler.runBatchUpdate(connection, INSERT_JOIN_ARTIFACT, insertParameters);
   }

   /**
    * must be call in a try block with a finally clause which calls clearQuery()
    */
   public static int insertIntoArtifactJoin(List<Object[]> insertParameters) throws OseeCoreException {
      return insertIntoArtifactJoin(null, insertParameters);
   }

   /**
    * should only be used in tandem with with selectArtifacts()
    * 
    * @param queryId value gotten from call to getNewQueryId and used in populating the insert parameters for
    * selectArtifacts
    */
   public static void clearQuery(int queryId) throws OseeCoreException {
      ConnectionHandler.runPreparedUpdate(DELETE_FROM_JOIN_ARTIFACT, queryId);
   }

   /**
    * should only be used in tandem with with selectArtifacts()
    * 
    * @param queryId value gotten from call to getNewQueryId and used in populating the insert parameters for
    * selectArtifacts
    */
   public static void clearQuery(OseeConnection connection, int queryId) throws OseeCoreException {
      if (connection != null) {
         ConnectionHandler.runPreparedUpdate(connection, DELETE_FROM_JOIN_ARTIFACT, queryId);
      } else {
         ConnectionHandler.runPreparedUpdate(DELETE_FROM_JOIN_ARTIFACT, queryId);
      }
   }

   /**
    * Populates artifacts with any artifact already in cache and populates insertParameters with necessary data to load
    * the rest.
    * 
    * @param reload will attempt to use cache if INCLUDE_CACHE
    * @param insertParameters populated by this method
    */
   private static void selectArtifacts(List<Artifact> artifacts, int queryId, CompositeKeyHashMap<Integer, Integer, Object[]> insertParameters, String sql, Object[] queryParameters, int artifactCountEstimate, TransactionRecord transactionId, LoadType reload) throws OseeCoreException {
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      long time = System.currentTimeMillis();

      List<Pair<Integer, Integer>> toLoad = new LinkedList<Pair<Integer, Integer>>();

      try {
         chStmt.runPreparedQuery(artifactCountEstimate, sql, queryParameters);
         while (chStmt.next()) {
            int artId = chStmt.getInt("art_id");
            int branchId = chStmt.getInt("branch_id");
            toLoad.add(new Pair<Integer, Integer>(branchId, artId));
         }
      } finally {
         chStmt.close();
      }
      processList(queryId, toLoad, artifacts, insertParameters, transactionId, reload);
      OseeLog.logf(Activator.class, Level.FINE, new Exception("Artifact Selection Time"),
         "Artifact Selection Time [%s], [%d] artifacts selected", Lib.getElapseString(time), insertParameters.size());
   }

   private synchronized static void processList(int queryId, List<Pair<Integer, Integer>> toLoad, List<Artifact> artifacts, CompositeKeyHashMap<Integer, Integer, Object[]> insertParameters, TransactionRecord transactionId, LoadType reload) throws OseeCoreException {
      Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();
      for (Pair<Integer, Integer> pair : toLoad) {
         int branchId = pair.getFirst();
         int artId = pair.getSecond();

         Artifact artifact = getArtifactFromCache(artId, transactionId, BranchManager.getBranch(branchId));
         if (artifact != null && reload == LoadType.INCLUDE_CACHE) {
            artifacts.add(artifact);
         } else {
            Object transactionParameter = transactionId == null ? SQL3DataType.INTEGER : transactionId.getId();
            insertParameters.put(artId, branchId, new Object[] {
               queryId,
               insertTime,
               artId,
               branchId,
               transactionParameter});
         }
      }
   }

   /**
    * This method is called only after the cache has been checked
    */
   private static Artifact retrieveShallowArtifact(IOseeStatement chStmt, LoadType reload, boolean historical) throws OseeCoreException {
      int artifactId = chStmt.getInt("art_id");
      Branch branch = BranchManager.getBranch(chStmt.getInt("branch_id"));
      int transactionId = Artifact.TRANSACTION_SENTINEL;
      if (historical) {
         int stripeTransactionNumber = chStmt.getInt("stripe_transaction_id");
         transactionId = stripeTransactionNumber;
      }

      Artifact artifact = historical ? null : ArtifactCache.getActive(artifactId, branch);
      if (artifact == null) {
         IArtifactType artifactType = ArtifactTypeManager.getType(chStmt.getInt("art_type_id"));
         ArtifactFactory factory = ArtifactTypeManager.getFactory(artifactType);

         artifact =
            factory.loadExisitingArtifact(artifactId, chStmt.getString("guid"), chStmt.getString("human_readable_id"),
               artifactType, chStmt.getInt("gamma_id"), branch, transactionId,
               ModificationType.getMod(chStmt.getInt("mod_type")), historical);
      }

      if (reload == LoadType.RELOAD_CACHE) {
         artifact.internalSetPersistenceData(chStmt.getInt("gamma_id"), transactionId,
            ModificationType.getMod(chStmt.getInt("mod_type")), historical);
      }
      return artifact;
   }

   @SuppressWarnings("unchecked")
   static void loadArtifactData(Artifact artifact, LoadLevel loadLevel) throws OseeCoreException {
      int queryId = getNewQueryId();
      Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();

      try {
         ConnectionHandler.runPreparedUpdate(INSERT_JOIN_ARTIFACT, queryId, insertTime, artifact.getArtId(),
            artifact.getFullBranch().getId(), SQL3DataType.INTEGER);

         List<Artifact> artifacts = new ArrayList<Artifact>(1);
         artifacts.add(artifact);
         loadArtifactsData(queryId, artifacts, loadLevel, LoadType.INCLUDE_CACHE, null,
            artifact.isDeleted() ? DeletionFlag.INCLUDE_DELETED : DeletionFlag.EXCLUDE_DELETED);
      } finally {
         clearQuery(queryId);
      }
   }

   private static Artifact getArtifactFromCache(Integer artId, TransactionRecord transactionId, IOseeBranch branch) throws OseeCoreException {
      boolean historical = transactionId != null;
      int key2 = historical ? transactionId.getId() : BranchManager.getBranchId(branch);

      Artifact cached = null;

      if (!historical) {
         ReentrantLock lock = loadingActiveMap.get(artId, key2);
         if (lock != null) {
            lock.lock();
         }

         cached = ArtifactCache.getActive(artId, key2);

         if (cached == null) {
            if (lock == null) {
               lock = new ReentrantLock();
               loadingActiveMap.put(artId, key2, lock);
            }
            lock.lock();
         }
      }
      return cached;
   }

   private static void loadArtifactsData(int queryId, Collection<Artifact> artifacts, LoadLevel loadLevel, LoadType reload, TransactionRecord transactionId, DeletionFlag allowDeleted) throws OseeCoreException {

      if (reload == LoadType.RELOAD_CACHE) {
         for (Artifact artifact : artifacts) {
            artifact.prepareForReload();
         }
      }
      boolean historical = transactionId != null;
      int key2;

      CompositeKeyHashMap<Integer, Integer, Artifact> tempCache =
         new CompositeKeyHashMap<Integer, Integer, Artifact>(artifacts.size(), true);

      for (Artifact artifact : artifacts) {
         key2 = historical ? transactionId.getId() : BranchManager.getBranchId(artifact.getBranch());
         tempCache.put(artifact.getArtId(), key2, artifact);
      }

      AttributeLoader.loadAttributeData(queryId, tempCache, historical, allowDeleted, loadLevel);
      RelationLoader.loadRelationData(queryId, artifacts, historical, loadLevel);

      if (!historical) {
         for (Artifact artifact : artifacts) {
            key2 = BranchManager.getBranchId(artifact.getBranch());

            ReentrantLock lock = null;
            synchronized (loadingActiveMap) {
               lock = loadingActiveMap.remove(artifact.getArtId(), key2);
            }
            if (lock != null) {
               if (lock.isLocked()) {
                  lock.unlock();
               }
            }
         }
      }
   }

   public static int getNewQueryId() {
      return queryRandom.nextInt(Integer.MAX_VALUE);
   }
}