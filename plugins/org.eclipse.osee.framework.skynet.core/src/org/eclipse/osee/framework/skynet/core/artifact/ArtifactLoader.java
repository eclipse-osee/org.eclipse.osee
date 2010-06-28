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

import static org.eclipse.osee.framework.skynet.core.artifact.DeletionFlag.EXCLUDE_DELETED;
import static org.eclipse.osee.framework.skynet.core.artifact.DeletionFlag.INCLUDE_DELETED;
import static org.eclipse.osee.framework.skynet.core.artifact.LoadType.INCLUDE_CACHE;
import static org.eclipse.osee.framework.skynet.core.artifact.LoadType.RELOAD_CACHE;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.database.core.OseeSql;
import org.eclipse.osee.framework.database.core.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Ryan D. Brooks
 */
public final class ArtifactLoader {

   private static final String INSERT_JOIN_ARTIFACT =
         "INSERT INTO osee_join_artifact (query_id, insert_time, art_id, branch_id, transaction_id) VALUES (?, ?, ?, ?, ?)";

   private static final String DELETE_FROM_JOIN_ARTIFACT = "DELETE FROM osee_join_artifact WHERE query_id = ?";

   private static final boolean DEBUG =
         "TRUE".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.osee.framework.skynet.core/debug/Loading"));

   /**
    * (re)loads the artifacts selected by sql and then returns them in a list
    */
   public static List<Artifact> getArtifacts(String sql, Object[] queryParameters, int artifactCountEstimate, ArtifactLoad loadLevel, LoadType reload, ISearchConfirmer confirmer, TransactionRecord transactionId, DeletionFlag allowDeleted) throws OseeCoreException {
      int queryId = getNewQueryId();
      CompositeKeyHashMap<Integer, Integer, Object[]> insertParameters =
            new CompositeKeyHashMap<Integer, Integer, Object[]>(artifactCountEstimate, false);
      selectArtifacts(queryId, insertParameters, sql, queryParameters, artifactCountEstimate, transactionId);
      boolean historical = transactionId != null;
      List<Artifact> artifacts =
            loadArtifacts(queryId, loadLevel, confirmer, new ArrayList<Object[]>(insertParameters.values()), reload,
                  historical, allowDeleted);
      return artifacts;
   }

   public static List<Artifact> loadArtifactsFromQueryId(int queryId, ArtifactLoad loadLevel, ISearchConfirmer confirmer, int fetchSize, LoadType reload, boolean historical, DeletionFlag allowDeleted) throws OseeCoreException {
      List<Artifact> loadedItems = new ArrayList<Artifact>(fetchSize);
      loadArtifactsFromQueryId(loadedItems, queryId, loadLevel, confirmer, fetchSize, reload, historical, allowDeleted);
      return loadedItems;
   }

   private static void loadArtifactsFromQueryId(Collection<Artifact> loadedItems, int queryId, ArtifactLoad loadLevel, ISearchConfirmer confirmer, int fetchSize, LoadType reload, boolean historical, DeletionFlag allowDeleted) throws OseeCoreException {
      try {
         OseeSql sqlKey;
         if (historical) {
            sqlKey = OseeSql.LOAD_HISTORICAL_ARTIFACTS;
         } else if (allowDeleted == INCLUDE_DELETED) {
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
                  if (!historical || allowDeleted == INCLUDE_DELETED || ModificationType.getMod(chStmt.getInt("mod_type")) != ModificationType.DELETED) {
                     loadedItems.add(retrieveShallowArtifact(chStmt, reload, historical));
                  }
               }
               previousArtId = artId;
               previousBranchId = branchId;
            }
         } catch (OseeDataStoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, String.format("%s - %s", sqlKey,
                  sql == null ? "SQL unknown" : sql), ex);
            throw ex;
         } finally {
            chStmt.close();
         }

         if (confirmer == null || confirmer.canProceed(loadedItems.size())) {
            loadArtifactsData(queryId, loadedItems, loadLevel, reload, historical, allowDeleted);

            for (Artifact artifact : loadedItems) {
               ArtifactCache.cacheByStaticId(artifact);
            }
         }
      } finally {
         clearQuery(queryId);
      }
   }

   public static List<Artifact> loadArtifacts(Collection<Integer> artIds, IOseeBranch branch, ArtifactLoad loadLevel, LoadType reload) throws OseeCoreException {
      return loadArtifacts(artIds, branch, loadLevel, null, reload);
   }

   /**
    * loads or reloads artifacts based on artifact ids and branch ids
    */
   public static List<Artifact> loadArtifacts(Collection<Integer> artIds, IOseeBranch branch, ArtifactLoad loadLevel, TransactionRecord transactionId, LoadType reload) throws OseeCoreException {
      ArrayList<Artifact> artifacts = new ArrayList<Artifact>();

      if (artIds != null && !artIds.isEmpty()) {
         int queryId = ArtifactLoader.getNewQueryId();
         Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();
         boolean historical = transactionId != null;

         List<Object[]> insertParameters = new LinkedList<Object[]>();

         for (int artId : org.eclipse.osee.framework.jdk.core.util.Collections.unique(artIds)) {
            insertParameters.add(new Object[] {queryId, insertTime, artId, BranchManager.getBranchId(branch),
                  historical ? transactionId.getId() : SQL3DataType.INTEGER});
         }

         for (Artifact artifact : loadArtifacts(queryId, loadLevel, null, insertParameters, reload, historical,
               INCLUDE_DELETED)) {
            artifacts.add(artifact);
         }
      }
      return artifacts;
   }

   /**
    * loads or reloads artifacts based on artifact ids and branch ids in the insertParameters
    */
   public static List<Artifact> loadArtifacts(int queryId, ArtifactLoad loadLevel, ISearchConfirmer confirmer, List<Object[]> insertParameters, LoadType reload, boolean historical, DeletionFlag allowDeleted) throws OseeCoreException {
      List<Artifact> loadedItems = new ArrayList<Artifact>(insertParameters.size());
      loadArtifacts(loadedItems, queryId, loadLevel, confirmer, insertParameters, reload, historical, allowDeleted);
      return loadedItems;
   }

   public static void loadArtifacts(Collection<Artifact> loadedItems, int queryId, ArtifactLoad loadLevel, ISearchConfirmer confirmer, List<Object[]> insertParameters, LoadType reload, boolean historical, DeletionFlag allowDeleted) throws OseeCoreException {
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
            loadArtifactsFromQueryId(data, queryId, loadLevel, confirmer, insertParameters.size(), reload, historical,
                  allowDeleted);
         } finally {
            if (data != loadedItems) {
               loadedItems.addAll(data);
            }
            OseeLog.log(Activator.class, Level.FINE, String.format("Artifact Load Time [%s] for [%d] artifacts. ",
                  Lib.getElapseString(time), loadedItems.size()), new Exception("Artifact Load Time"));
            clearQuery(queryId);
         }
      }
   }

   /**
    * must be call in a try block with a finally clause which calls clearQuery()
    */
   public static int insertIntoArtifactJoin(OseeConnection connection, List<Object[]> insertParameters) throws OseeDataStoreException {
      return ConnectionHandler.runBatchUpdate(connection, INSERT_JOIN_ARTIFACT, insertParameters);
   }

   /**
    * must be call in a try block with a finally clause which calls clearQuery()
    */
   public static int insertIntoArtifactJoin(List<Object[]> insertParameters) throws OseeDataStoreException {
      return insertIntoArtifactJoin(null, insertParameters);
   }

   /**
    * should only be used in tandem with with selectArtifacts()
    * 
    * @param queryId value gotten from call to getNewQueryId and used in populating the insert parameters for
    *           selectArtifacts
    */
   public static void clearQuery(int queryId) throws OseeDataStoreException {
      ConnectionHandler.runPreparedUpdate(DELETE_FROM_JOIN_ARTIFACT, queryId);
   }

   /**
    * should only be used in tandem with with selectArtifacts()
    * 
    * @param queryId value gotten from call to getNewQueryId and used in populating the insert parameters for
    *           selectArtifacts
    */
   public static void clearQuery(OseeConnection connection, int queryId) throws OseeDataStoreException {
      if (connection != null) {
         ConnectionHandler.runPreparedUpdate(connection, DELETE_FROM_JOIN_ARTIFACT, queryId);
      } else {
         ConnectionHandler.runPreparedUpdate(DELETE_FROM_JOIN_ARTIFACT, queryId);
      }
   }

   /**
    * @param insertParameters will be populated by this method
    */
   public static void selectArtifacts(int queryId, CompositeKeyHashMap<Integer, Integer, Object[]> insertParameters, String sql, Object[] queryParameters, int artifactCountEstimate, TransactionRecord transactionId) throws OseeDataStoreException {
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      long time = System.currentTimeMillis();

      try {
         chStmt.runPreparedQuery(artifactCountEstimate, sql, queryParameters);
         Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();

         if (DEBUG) {
            System.out.println("ArtifactLoader: Found the following Artifacts");
         }
         while (chStmt.next()) {
            int artId = chStmt.getInt("art_id");
            int branchId = chStmt.getInt("branch_id");
            if (DEBUG) {
               System.out.println(String.format("  ArtifactID = %d , BranchID = %d", artId, branchId));
            }
            Object transactionParameter = transactionId == null ? SQL3DataType.INTEGER : transactionId.getId();
            insertParameters.put(artId, branchId, new Object[] {queryId, insertTime, artId, branchId,
                  transactionParameter});
         }
      } finally {
         chStmt.close();
      }
      OseeLog.log(Activator.class, Level.FINE, String.format("Artifact Selection Time [%s], [%d] artifacts selected",
            Lib.getElapseString(time), insertParameters.size()), new Exception("Artifact Selection Time"));
   }

   private static Artifact retrieveShallowArtifact(IOseeStatement chStmt, LoadType reload, boolean historical) throws OseeCoreException {
      int artifactId = chStmt.getInt("art_id");
      Branch branch = BranchManager.getBranch(chStmt.getInt("branch_id"));
      Artifact artifact;
      int transactionId = Artifact.TRANSACTION_SENTINEL;
      if (historical) {
         int stripeTransactionNumber = chStmt.getInt("stripe_transaction_id");
         transactionId = stripeTransactionNumber;
         artifact = ArtifactCache.getHistorical(artifactId, stripeTransactionNumber);
      } else {
         artifact = ArtifactCache.getActive(artifactId, branch);
      }

      if (artifact == null) {
         ArtifactType artifactType = ArtifactTypeManager.getType(chStmt.getInt("art_type_id"));
         ArtifactFactory factory = ArtifactTypeManager.getFactory(artifactType);

         artifact =
               factory.loadExisitingArtifact(artifactId, chStmt.getString("guid"),
                     chStmt.getString("human_readable_id"), artifactType, chStmt.getInt("gamma_id"), branch,
                     transactionId, ModificationType.getMod(chStmt.getInt("mod_type")), historical);

      } else if (reload == RELOAD_CACHE) {
         artifact.internalSetPersistenceData(chStmt.getInt("gamma_id"), transactionId,
               ModificationType.getMod(chStmt.getInt("mod_type")), historical);
      }
      return artifact;
   }

   static void loadArtifactData(Artifact artifact, ArtifactLoad loadLevel) throws OseeCoreException {
      int queryId = getNewQueryId();
      Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();

      try {
         ConnectionHandler.runPreparedUpdate(INSERT_JOIN_ARTIFACT, queryId, insertTime, artifact.getArtId(),
               artifact.getBranch().getId(), SQL3DataType.INTEGER);

         List<Artifact> artifacts = new ArrayList<Artifact>(1);
         artifacts.add(artifact);
         loadArtifactsData(queryId, artifacts, loadLevel, INCLUDE_CACHE, false,
               artifact.isDeleted() ? INCLUDE_DELETED : EXCLUDE_DELETED);
      } finally {
         clearQuery(queryId);
      }
   }

   private static void loadArtifactsData(int queryId, Collection<Artifact> artifacts, ArtifactLoad loadLevel, LoadType reload, boolean historical, DeletionFlag allowDeleted) throws OseeCoreException {
      if (reload == RELOAD_CACHE) {
         for (Artifact artifact : artifacts) {
            artifact.prepareForReload();
         }
      }

      AttributeLoader.loadAttributeData(queryId, artifacts, historical, allowDeleted, loadLevel);
      RelationLoader.loadRelationData(queryId, artifacts, historical, loadLevel);

      for (Artifact artifact : artifacts) {
         artifact.onInitializationComplete();
         if (reload == RELOAD_CACHE) {
            OseeEventManager.kickArtifactModifiedEvent(ArtifactLoader.class, ArtifactModType.Reverted, artifact);
         }
      }
   }

   public static int getNewQueryId() {
      return new Random().nextInt(Integer.MAX_VALUE);
   }
}