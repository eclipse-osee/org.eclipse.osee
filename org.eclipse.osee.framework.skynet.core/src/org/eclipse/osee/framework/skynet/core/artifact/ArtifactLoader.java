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

import static org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad.SHALLOW;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.RelationType;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.database.core.OseeSql;
import org.eclipse.osee.framework.database.core.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

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
    * 
    * @param sql
    * @param queryParameters
    * @param artifactCountEstimate
    * @param loadLevel
    * @param reload
    * @param confirmer
    * @param transactionId
    * @param allowDeleted
    * @return list of artifacts resulting for the sql query
    * @throws OseeCoreException
    */
   public static List<Artifact> getArtifacts(String sql, Object[] queryParameters, int artifactCountEstimate, ArtifactLoad loadLevel, boolean reload, ISearchConfirmer confirmer, TransactionRecord transactionId, boolean allowDeleted) throws OseeCoreException {
      int queryId = getNewQueryId();
      CompositeKeyHashMap<Integer, Integer, Object[]> insertParameters =
            new CompositeKeyHashMap<Integer, Integer, Object[]>(artifactCountEstimate, false);
      selectArtifacts(queryId, insertParameters, sql, queryParameters, artifactCountEstimate, transactionId);
      List<Artifact> artifacts =
            loadArtifacts(queryId, loadLevel, confirmer, new ArrayList<Object[]>(insertParameters.values()), reload,
                  transactionId != null, allowDeleted);
      return artifacts;
   }

   /**
    * (re)loads the artifacts selected by sql and then returns them in a list
    * 
    * @param sql
    * @param queryParameters
    * @param artifactCountEstimate
    * @param loadLevel
    * @param reload
    * @param allowDeleted allow the inclusion of deleted artifacts in the results
    * @param historical
    * @return list of artifacts resulting for the sql query
    * @throws OseeCoreException
    */
   public static List<Artifact> getArtifacts(String sql, Object[] queryParameters, int artifactCountEstimate, ArtifactLoad loadLevel, boolean reload, TransactionRecord transactionId, boolean allowDeleted) throws OseeCoreException {
      return getArtifacts(sql, queryParameters, artifactCountEstimate, loadLevel, reload, null, transactionId,
            allowDeleted);
   }

   /**
    * @param queryId
    * @param loadLevel
    * @param confirmer used to prompt user whether to proceed if certain conditions are met
    * @param fetchSize
    * @param reload
    * @param historical
    * @param allowDeleted allow the inclusion of deleted artifacts in the results
    * @throws OseeCoreException
    */
   public static List<Artifact> loadArtifactsFromQueryId(int queryId, ArtifactLoad loadLevel, ISearchConfirmer confirmer, int fetchSize, boolean reload, boolean historical, boolean allowDeleted) throws OseeCoreException {
      List<Artifact> artifacts = new ArrayList<Artifact>(fetchSize);
      try {
         IOseeStatement chStmt = ConnectionHandler.getStatement();
         try {
            String sql;
            if (historical) {
               sql = ClientSessionManager.getSql(OseeSql.LOAD_HISTORICAL_ARTIFACTS);
            } else {
               sql =
                     allowDeleted ? ClientSessionManager.getSql(OseeSql.LOAD_CURRENT_ARTIFACTS_WITH_DELETED) : ClientSessionManager.getSql(OseeSql.LOAD_CURRENT_ARTIFACTS);
            }
            chStmt.runPreparedQuery(fetchSize, sql, queryId);

            int previousArtId = -1;
            int previousBranchId = -1;
            while (chStmt.next()) {
               int artId = chStmt.getInt("art_id");
               int branchId = chStmt.getInt("branch_id");
               // assumption: sql is returning rows ordered by branch_id, art_id, transaction_id in descending order
               if (previousArtId != artId || previousBranchId != branchId) {
                  // assumption: sql is returning unwanted deleted artifacts only in the historical case
                  if (!(historical && !allowDeleted && ModificationType.getMod(chStmt.getInt("mod_type")) == ModificationType.DELETED)) {
                     artifacts.add(retrieveShallowArtifact(chStmt, reload, historical));
                  }
               }
               previousArtId = artId;
               previousBranchId = branchId;
            }
         } finally {
            chStmt.close();
         }

         if (confirmer == null || confirmer.canProceed(artifacts.size())) {
            loadArtifactsData(queryId, artifacts, loadLevel, reload, historical, allowDeleted);

            for (Artifact artifact : artifacts) {
               ArtifactCache.cacheByStaticId(artifact);
            }
         }
      } finally {
         clearQuery(queryId);
      }
      return artifacts;
   }

   /**
    * loads or reloads artifacts based on artifact ids and branch ids
    * 
    * @param artIds
    * @param branch
    * @param loadLevel
    * @return list of the loaded artifacts
    * @throws OseeCoreException
    */
   public static List<Artifact> loadArtifacts(Collection<Integer> artIds, IOseeBranch branch, ArtifactLoad loadLevel, boolean reload) throws OseeCoreException {
      return loadArtifacts(artIds, branch, loadLevel, null, reload);
   }

   /**
    * loads or reloads artifacts based on artifact ids and branch ids
    * 
    * @param artIds
    * @param branch
    * @param loadLevel
    * @param transactionId
    * @return list of the loaded artifacts
    * @throws OseeCoreException
    */
   public static List<Artifact> loadArtifacts(Collection<Integer> artIds, IOseeBranch branch, ArtifactLoad loadLevel, TransactionRecord transactionId, boolean reload) throws OseeCoreException {
      ArrayList<Artifact> artifacts = new ArrayList<Artifact>();

      if (!artIds.isEmpty()) {
         int queryId = ArtifactLoader.getNewQueryId();
         Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();
         boolean historical = transactionId != null;

         List<Object[]> insertParameters = new LinkedList<Object[]>();

         for (int artId : org.eclipse.osee.framework.jdk.core.util.Collections.unique(artIds)) {
            insertParameters.add(new Object[] {queryId, insertTime, artId, BranchManager.getBranchId(branch),
                  historical ? transactionId.getId() : SQL3DataType.INTEGER});
         }

         for (Artifact artifact : loadArtifacts(queryId, loadLevel, null, insertParameters, reload, historical, true)) {
            artifacts.add(artifact);
         }
      }
      return artifacts;
   }

   /**
    * loads or reloads artifacts based on artifact ids and branch ids in the insertParameters
    */
   public static List<Artifact> loadArtifacts(int queryId, ArtifactLoad loadLevel, ISearchConfirmer confirmer, List<Object[]> insertParameters, boolean reload, boolean historical, boolean allowDeleted) throws OseeCoreException {

      List<Artifact> artifacts = Collections.emptyList();
      if (insertParameters.size() > 0) {
         long time = System.currentTimeMillis();
         try {
            insertIntoArtifactJoin(insertParameters);
            artifacts =
                  loadArtifactsFromQueryId(queryId, loadLevel, confirmer, insertParameters.size(), reload, historical,
                        allowDeleted);
         } finally {
            OseeLog.log(Activator.class, Level.FINE, String.format("Artifact Load Time [%s] for [%d] artifacts. ",
                  Lib.getElapseString(time), artifacts.size()), new Exception("Artifact Load Time"));
            clearQuery(queryId);
         }
      }
      return artifacts;
   }

   /**
    * must be call in a try block with a finally clause which calls clearQuery()
    * 
    * @param insertParameters
    * @throws OseeDataStoreException
    */
   public static int insertIntoArtifactJoin(OseeConnection connection, List<Object[]> insertParameters) throws OseeDataStoreException {
      return ConnectionHandler.runBatchUpdate(connection, INSERT_JOIN_ARTIFACT, insertParameters);
   }

   /**
    * must be call in a try block with a finally clause which calls clearQuery()
    * 
    * @param insertParameters
    * @throws OseeDataStoreException
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
    * @param queryId
    * @param insertParameters will be populated by this method
    * @param sql
    * @param queryParameters
    * @param artifactCountEstimate
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

   private static Artifact retrieveShallowArtifact(IOseeStatement chStmt, boolean reload, boolean historical) throws OseeCoreException {
      int artifactId = chStmt.getInt("art_id");
      Branch branch = BranchManager.getBranch(chStmt.getInt("branch_id"));
      TransactionRecord transactionId = TransactionManager.getTransactionId(chStmt.getInt("transaction_id"));
      Artifact artifact;

      if (historical) {
         int stripeTransactionNumber = chStmt.getInt("stripe_transaction_id");
         if (stripeTransactionNumber != transactionId.getId()) {
            transactionId = TransactionManager.getTransactionId(stripeTransactionNumber);
         }
         artifact = ArtifactCache.getHistorical(artifactId, transactionId.getId());
      } else {
         artifact = ArtifactCache.getActive(artifactId, branch);
      }

      if (artifact == null) {
         ArtifactType artifactType = ArtifactTypeManager.getType(chStmt.getInt("art_type_id"));
         ArtifactFactory factory = ArtifactTypeManager.getFactory(artifactType);

         artifact =
               factory.loadExisitingArtifact(artifactId, chStmt.getString("guid"),
                     chStmt.getString("human_readable_id"), artifactType, chStmt.getInt("gamma_id"), transactionId,
                     ModificationType.getMod(chStmt.getInt("mod_type")), historical);

      } else if (reload) {
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
         loadArtifactsData(queryId, artifacts, loadLevel, false, false, artifact.isDeleted());
      } finally {
         clearQuery(queryId);
      }
   }

   private static void loadArtifactsData(int queryId, Collection<Artifact> artifacts, ArtifactLoad loadLevel, boolean reload, boolean historical, boolean allowDeleted) throws OseeCoreException {
      if (reload) {
         for (Artifact artifact : artifacts) {
            artifact.prepareForReload();
         }
      }

      loadAttributeData(queryId, artifacts, historical, allowDeleted, loadLevel);
      loadRelationData(queryId, artifacts, historical, loadLevel);

      for (Artifact artifact : artifacts) {
         artifact.onInitializationComplete();
         if (reload) {
            OseeEventManager.kickArtifactModifiedEvent(ArtifactLoader.class, ArtifactModType.Reverted, artifact);
         }
      }
   }

   private static void loadRelationData(int queryId, Collection<Artifact> artifacts, boolean historical, ArtifactLoad loadLevel) throws OseeCoreException {
      if (loadLevel == SHALLOW || loadLevel == ArtifactLoad.ATTRIBUTE) {
         return;
      }

      if (historical) {
         return; // TODO: someday we might have a use for historical relations, but not now
      }
      IOseeStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(artifacts.size() * 8, ClientSessionManager.getSql(OseeSql.LOAD_RELATIONS), queryId);
         while (chStmt.next()) {
            int relationId = chStmt.getInt("rel_link_id");
            int aArtifactId = chStmt.getInt("a_art_id");
            int bArtifactId = chStmt.getInt("b_art_id");
            Branch aBranch = BranchManager.getBranch(chStmt.getInt("branch_id"));
            Branch bBranch = aBranch; // TODO these branch ids need to come from the relation link table
            RelationType relationType = RelationTypeManager.getType(chStmt.getInt("rel_link_type_id"));

            int gammaId = chStmt.getInt("gamma_id");
            String rationale = chStmt.getString("rationale");

            RelationLink.getOrCreate(aArtifactId, bArtifactId, aBranch, bBranch, relationType, relationId, gammaId,
                  rationale, ModificationType.getMod(chStmt.getInt("mod_type")));
         }
      } finally {
         chStmt.close();
      }
      for (Artifact artifact : artifacts) {
         artifact.setLinksLoaded(true);
      }
   }

   private static void loadAttributeData(int queryId, Collection<Artifact> artifacts, boolean historical, boolean allowDeletedArtifacts, ArtifactLoad loadLevel) throws OseeCoreException {
      if (loadLevel == SHALLOW || loadLevel == ArtifactLoad.RELATION) {
         return;
      }

      IOseeStatement chStmt = ConnectionHandler.getStatement();
      try {
         if (historical) {
            chStmt.runPreparedQuery(artifacts.size() * 8,
                  ClientSessionManager.getSql(OseeSql.LOAD_HISTORICAL_ATTRIBUTES), queryId);
         } else {
            String sql;

            if (loadLevel == ArtifactLoad.ALL_CURRENT) {
               sql = ClientSessionManager.getSql(OseeSql.LOAD_ALL_CURRENT_ATTRIBUTES);
            } else {
               sql =
                     allowDeletedArtifacts ? ClientSessionManager.getSql(OseeSql.LOAD_CURRENT_ATTRIBUTES_WITH_DELETED) : ClientSessionManager.getSql(OseeSql.LOAD_CURRENT_ATTRIBUTES);
            }

            chStmt.runPreparedQuery(artifacts.size() * 8, sql, queryId);
         }

         Artifact artifact = null;
         int previousArtifactId = -1;
         int previousBranchId = -1;
         int previousAttrId = -1;
         int previousGammaId = -1;
         int previousModType = -1;

         while (chStmt.next()) {
            int artifactId = chStmt.getInt("art_id");
            int branchId = chStmt.getInt("branch_id");
            int attrId = chStmt.getInt("attr_id");
            int gammaId = chStmt.getInt("gamma_id");
            int modType = chStmt.getInt("mod_type");

            // if a different artifact than the previous iteration
            if (branchId != previousBranchId || artifactId != previousArtifactId) {
               if (artifact != null) { // exclude the first pass because there is no previous artifact
                  // meet minimum attributes for the previous artifact since its existing attributes have already been loaded
                  artifact.meetMinimumAttributeCounts(false);
                  ArtifactCache.cachePostAttributeLoad(artifact);
               }

               if (historical) {
                  artifact = ArtifactCache.getHistorical(artifactId, chStmt.getInt("stripe_transaction_id"));
               } else {
                  artifact = ArtifactCache.getActive(artifactId, branchId);
               }
               if (artifact == null) {
                  //TODO just masking a DB issue, we should probably really have an error here - throw new ArtifactDoesNotExist("Can not find aritfactId: " + artifactId + " on branch " + branchId);
                  OseeLog.log(ArtifactLoader.class, Level.WARNING, String.format(
                        "Orphaned attribute for artifact id[%d] branch[%d]", artifactId, branchId));
               } else if (artifact.isAttributesLoaded()) {
                  artifact = null;
               }
            }

            // if we get more than one version from the same attribute on the same artifact on the same branch
            if (attrId == previousAttrId && branchId == previousBranchId && artifactId == previousArtifactId) {
               if (historical) {
                  // Okay to skip on historical loading... because the most recent transaction is used first due to sorting on the query
               } else {
                  OseeLog.log(
                        ArtifactLoader.class,
                        Level.WARNING,
                        String.format(
                              "multiple attribute version for attribute id [%d] artifact id[%d] branch[%d] previousGammaId[%s] currentGammaId[%s] previousModType[%s] currentModType[%s]",
                              attrId, artifactId, branchId, previousGammaId, gammaId, previousModType, modType));
               }
            } else if (artifact != null) { //artifact will have been set to null if artifact.isAttributesLoaded() returned true
               artifact.internalInitializeAttribute(AttributeTypeManager.getType(chStmt.getInt("attr_type_id")),
                     attrId, gammaId, ModificationType.getMod(modType), false, chStmt.getString("value"),
                     chStmt.getString("uri"));
            }

            previousArtifactId = artifactId;
            previousBranchId = branchId;
            previousAttrId = attrId;
            previousGammaId = gammaId;
            previousModType = modType;
         }
      } finally {
         chStmt.close();
      }
   }

   public static int getNewQueryId() {
      return new Random().nextInt(Integer.MAX_VALUE);
   }
}