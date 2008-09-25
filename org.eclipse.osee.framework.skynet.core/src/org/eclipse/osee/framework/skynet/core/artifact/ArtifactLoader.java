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

import static org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad.ATTRIBUTE;
import static org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad.FULL;
import static org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad.RELATION;
import static org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad.SHALLOW;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeToTransactionOperation;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.change.TxChange;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

/**
 * @author Ryan D. Brooks
 */
public final class ArtifactLoader {
   private static final String SELECT_RELATIONS =
         "SELECT rel_link_id, a_art_id, b_art_id, rel_link_type_id, a_order, b_order, rel1.gamma_id, rationale, al1.branch_id FROM osee_join_artifact al1, osee_define_rel_link rel1, osee_define_txs txs1, osee_define_tx_details txd1 WHERE al1.query_id = ? AND (al1.art_id = rel1.a_art_id OR al1.art_id = rel1.b_art_id) AND rel1.gamma_id = txs1.gamma_id AND txs1.tx_current=" + TxChange.CURRENT.getValue() + " AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = al1.branch_id";

   private static final String SELECT_CURRENT_ATTRIBUTES_PREFIX =
         "SELECT att1.art_id, att1.attr_id, att1.value, att1.gamma_id, att1.attr_type_id, att1.uri, al1.branch_id, txs1.mod_type FROM osee_join_artifact al1, osee_define_attribute att1, osee_define_txs txs1, osee_define_tx_details txd1 WHERE al1.query_id = ? AND al1.art_id = att1.art_id AND att1.gamma_id = txs1.gamma_id AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = al1.branch_id AND txs1.tx_current ";
   private static final String SELECT_CURRENT_ATTRIBUTES =
         SELECT_CURRENT_ATTRIBUTES_PREFIX + "= " + TxChange.CURRENT.getValue() + " order by al1.branch_id, al1.art_id";

   private static final String SELECT_CURRENT_ATTRIBUTES_WITH_DELETED =
         SELECT_CURRENT_ATTRIBUTES_PREFIX + "IN (" + TxChange.CURRENT.getValue() + ", " + TxChange.ARTIFACT_DELETED.getValue() + ") order by al1.branch_id, al1.art_id";

   private static final String SELECT_HISTORICAL_ATTRIBUTES =
         "SELECT att1.art_id, att1.attr_id, att1.value, att1.gamma_id, att1.attr_type_id, att1.uri, al1.branch_id, txs1.mod_type, txd1.transaction_id, al1.transaction_id as stripe_transaction_id FROM osee_join_artifact al1, osee_define_attribute att1, osee_define_txs txs1, osee_define_tx_details txd1 WHERE al1.query_id = ? AND al1.art_id = att1.art_id AND att1.gamma_id = txs1.gamma_id AND txs1.transaction_id <= al1.transaction_id AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = al1.branch_id order by txd1.branch_id, att1.art_id, att1.attr_id, txd1.transaction_id desc";

   private static final String SELECT_CURRENT_ARTIFACTS_PREFIX =
         "SELECT al1.art_id, txs1.gamma_id, mod_type, txd1.*, art_type_id, guid, human_readable_id FROM osee_join_artifact al1, osee_define_artifact art1, osee_define_artifact_version arv1, osee_define_txs txs1, osee_define_tx_details txd1 WHERE al1.query_id = ? AND al1.art_id = art1.art_id AND art1.art_id = arv1.art_id AND arv1.gamma_id = txs1.gamma_id AND txd1.branch_id = al1.branch_id AND txd1.transaction_id = txs1.transaction_id AND txs1.tx_current ";

   private static final String SELECT_CURRENT_ARTIFACTS =
         SELECT_CURRENT_ARTIFACTS_PREFIX + "= " + TxChange.CURRENT.getValue();

   private static final String SELECT_CURRENT_ARTIFACTS_WITH_DELETED =
         SELECT_CURRENT_ARTIFACTS_PREFIX + "in (" + TxChange.CURRENT.getValue() + ", " + TxChange.DELETED.getValue() + ")";

   private static final String SELECT_HISTORICAL_ARTIFACTS =
         "SELECT al1.art_id, txs1.gamma_id, mod_type, txd1.*, art_type_id, guid, human_readable_id, al1.transaction_id as stripe_transaction_id FROM osee_join_artifact al1, osee_define_artifact art1, osee_define_artifact_version arv1, osee_define_txs txs1, osee_define_tx_details txd1 WHERE al1.query_id = ? AND al1.art_id = art1.art_id AND art1.art_id = arv1.art_id AND arv1.gamma_id = txs1.gamma_id AND txs1.transaction_id <= al1.transaction_id AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = al1.branch_id order by al1.branch_id, art1.art_id, txs1.transaction_id desc";

   private static final String INSERT_JOIN_ARTIFACT =
         "INSERT INTO osee_join_artifact (query_id, insert_time, art_id, branch_id, transaction_id) VALUES (?, ?, ?, ?, ?)";

   private static final String DELETE_FROM_JOIN_ARTIFACT = "DELETE FROM osee_join_artifact WHERE query_id = ?";

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
    * @return
    * @throws SQLException
    */
   public static List<Artifact> getArtifacts(String sql, Object[] queryParameters, int artifactCountEstimate, ArtifactLoad loadLevel, boolean reload, ISearchConfirmer confirmer, TransactionId transactionId, boolean allowDeleted) throws SQLException {
      int queryId = getNewQueryId();
      CompositeKeyHashMap<Integer, Integer, Object[]> insertParameters =
            new CompositeKeyHashMap<Integer, Integer, Object[]>(artifactCountEstimate);
      selectArtifacts(queryId, insertParameters, sql, queryParameters, artifactCountEstimate, transactionId);
      List<Artifact> artifacts =
            loadArtifacts(queryId, loadLevel, confirmer, insertParameters.values(), reload, transactionId != null,
                  allowDeleted);
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
    * @param allowDeleted TODO
    * @param historical TODO
    * @return
    * @throws SQLException
    */
   public static List<Artifact> getArtifacts(String sql, Object[] queryParameters, int artifactCountEstimate, ArtifactLoad loadLevel, boolean reload, TransactionId transactionId, boolean allowDeleted) throws SQLException {
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
    * @param allowDeleted TODO
    * @return
    * @throws SQLException
    */
   public static List<Artifact> loadArtifactsFromQueryId(int queryId, ArtifactLoad loadLevel, ISearchConfirmer confirmer, int fetchSize, boolean reload, boolean historical, boolean allowDeleted) throws SQLException {
      List<Artifact> artifacts = new ArrayList<Artifact>(fetchSize);
      try {
         ConnectionHandlerStatement chStmt = null;
         try {
            if (historical) {
               chStmt = ConnectionHandler.runPreparedQuery(fetchSize, SELECT_HISTORICAL_ARTIFACTS, queryId);
            } else {
               String sql = allowDeleted ? SELECT_CURRENT_ARTIFACTS_WITH_DELETED : SELECT_CURRENT_ARTIFACTS;
               chStmt = ConnectionHandler.runPreparedQuery(fetchSize, sql, queryId);
            }

            int previousArtId = -1;
            int previousBranchId = -1;
            ResultSet rSet = chStmt.getRset();
            while (chStmt.next()) {
               int artId = rSet.getInt("art_id");
               int branchId = rSet.getInt("branch_id");
               if (!historical || (previousArtId != artId || previousBranchId != branchId)) {
                  artifacts.add(retrieveShallowArtifact(rSet, reload, historical));
               }
               previousArtId = artId;
               previousBranchId = branchId;
            }
         } finally {
            DbUtil.close(chStmt);
         }

         if (confirmer == null || confirmer.canProceed(artifacts.size())) {
            loadArtifactsData(queryId, artifacts, loadLevel, reload, historical, allowDeleted);
         }
      } catch (OseeCoreException ex) {
         throw new SQLException(ex);
      } finally {
         try {
            clearQuery(queryId);
         } catch (OseeDataStoreException ex) {
            throw new SQLException(ex);
         }
      }
      return artifacts;
   }

   /**
    * loads or reloads artifacts based on artifact ids and branch ids in the insertParameters
    * 
    * @param queryId
    * @param loadLevel
    * @param confirmer
    * @param insertParameters
    * @param reload
    * @param historical TODO
    * @param allowDeleted TODO
    * @return
    * @throws SQLException
    */
   public static List<Artifact> loadArtifacts(int queryId, ArtifactLoad loadLevel, ISearchConfirmer confirmer, Collection<Object[]> insertParameters, boolean reload, boolean historical, boolean allowDeleted) throws SQLException {
      List<Artifact> artifacts = Collections.emptyList();
      if (insertParameters.size() > 0) {
         long time = System.currentTimeMillis();
         try {
            selectArtifacts(insertParameters);
            artifacts =
                  loadArtifactsFromQueryId(queryId, loadLevel, confirmer, insertParameters.size(), reload, historical,
                        allowDeleted);
         } finally {
            OseeLog.log(SkynetActivator.class, Level.FINE, String.format(
                  "Artifact Load Time [%s] for [%d] artifacts. ", Lib.getElapseString(time), artifacts.size()),
                  new Exception("Artifact Load Time"));
            try {
               clearQuery(queryId);
            } catch (OseeDataStoreException ex) {
               throw new SQLException(ex);
            }
         }
      }
      return artifacts;
   }

   /**
    * must be call in a try block with a finally clause which calls clearQuery()
    * 
    * @param insertParameters
    * @throws SQLException
    */
   public static int selectArtifacts(Collection<Object[]> insertParameters) throws SQLException {
      return ConnectionHandler.runPreparedUpdateBatch(INSERT_JOIN_ARTIFACT, insertParameters);
   }

   /**
    * should only be used in tandem with with selectArtifacts()
    * 
    * @param queryId value gotten from call to getNewQueryId and used in populating the insert parameters for
    *           selectArtifacts
    * @throws SQLException
    */
   public static void clearQuery(int queryId) throws OseeDataStoreException {
      clearQuery(queryId, null);
   }

   /**
    * should only be used in tandem with with selectArtifacts()
    * 
    * @param queryId value gotten from call to getNewQueryId and used in populating the insert parameters for
    *           selectArtifacts
    * @throws SQLException
    */
   public static void clearQuery(int queryId, Connection connection) throws OseeDataStoreException {
      try {
         if (connection != null) {
            ConnectionHandler.runPreparedUpdate(connection, DELETE_FROM_JOIN_ARTIFACT, queryId);
         } else {
            ConnectionHandler.runPreparedUpdateReturnCount(DELETE_FROM_JOIN_ARTIFACT, queryId);
         }
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   /**
    * @param queryId
    * @param insertParameters will be populated by this method
    * @param sql
    * @param queryParameters
    * @param artifactCountEstimate
    * @throws SQLException
    */
   public static void selectArtifacts(int queryId, CompositeKeyHashMap<Integer, Integer, Object[]> insertParameters, String sql, Object[] queryParameters, int artifactCountEstimate, TransactionId transactionId) throws SQLException {
      ConnectionHandlerStatement chStmt = null;
      long time = System.currentTimeMillis();

      try {
         chStmt = ConnectionHandler.runPreparedQuery(artifactCountEstimate, sql, queryParameters);
         ResultSet rSet = chStmt.getRset();
         Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();

         while (rSet.next()) {
            int artId = rSet.getInt("art_id");
            int branchId = rSet.getInt("branch_id");
            Object transactionParameter =
                  transactionId == null ? SQL3DataType.INTEGER : transactionId.getTransactionNumber();
            insertParameters.put(artId, branchId, new Object[] {queryId, insertTime, artId, branchId,
                  transactionParameter});
         }
      } finally {
         DbUtil.close(chStmt);
      }
      OseeLog.log(SkynetActivator.class, Level.FINE,
            String.format("Artifact Selection Time [%s], [%d] artifacts selected", Lib.getElapseString(time),
                  insertParameters.size()), new Exception("Artifact Selection Time"));
   }

   private static Artifact retrieveShallowArtifact(ResultSet rSet, boolean reload, boolean historical) throws OseeCoreException, SQLException {
      int artifactId = rSet.getInt("art_id");
      Branch branch = BranchPersistenceManager.getBranch(rSet.getInt("branch_id"));
      TransactionId transactionId = TransactionIdManager.getTransactionId(rSet);
      Artifact artifact;

      if (historical) {
         int stripeTransactionNumber = rSet.getInt("stripe_transaction_id");
         if (stripeTransactionNumber != transactionId.getTransactionNumber()) {
            transactionId = TransactionIdManager.getTransactionId(stripeTransactionNumber);
         }
         artifact = ArtifactCache.getHistorical(artifactId, transactionId.getTransactionNumber());
      } else {
         artifact = ArtifactCache.getActive(artifactId, branch);
      }

      if (artifact == null) {
         ArtifactType artifactType = ArtifactTypeManager.getType(rSet.getInt("art_type_id"));
         ArtifactFactory factory = artifactType.getFactory();

         artifact =
               factory.loadExisitingArtifact(artifactId, rSet.getString("guid"), rSet.getString("human_readable_id"),
                     artifactType, rSet.getInt("gamma_id"), transactionId,
                     ModificationType.getMod(rSet.getInt("mod_type")), historical);

      } else if (reload) {
         artifact.internalSetPersistenceData(rSet.getInt("gamma_id"), transactionId,
               ModificationType.getMod(rSet.getInt("mod_type")), historical);
      }
      return artifact;
   }

   static void loadArtifactData(Artifact artifact, ArtifactLoad loadLevel) throws OseeCoreException {
      int queryId = getNewQueryId();
      Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();

      try {
         ConnectionHandler.runPreparedUpdate(INSERT_JOIN_ARTIFACT, queryId, insertTime, artifact.getArtId(),
               artifact.getBranch().getBranchId(), SQL3DataType.INTEGER);

         List<Artifact> artifacts = new ArrayList<Artifact>(1);
         artifacts.add(artifact);
         loadArtifactsData(queryId, artifacts, loadLevel, false, false, artifact.isDeleted());
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
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

      if (loadLevel == SHALLOW) {
         return;
      } else if (loadLevel == ATTRIBUTE) {
         loadAttributeData(queryId, artifacts, historical, allowDeleted);
      } else if (loadLevel == RELATION) {
         loadRelationData(queryId, artifacts, historical);
      } else if (loadLevel == FULL) {
         loadAttributeData(queryId, artifacts, historical, allowDeleted);
         loadRelationData(queryId, artifacts, historical);
      }

      for (Artifact artifact : artifacts) {
         artifact.onInitializationComplete();
         if (reload) {
            OseeEventManager.kickArtifactModifiedEvent(ArtifactLoader.class, ArtifactModType.Reverted, artifact);
         }
      }
   }

   private static void loadRelationData(int queryId, Collection<Artifact> artifacts, boolean historical) throws OseeCoreException {
      if (historical) {
         return; // TODO: someday we might have a use for historical relations, but not now
      }
      ConnectionHandlerStatement chStmt = null;
      try {
         chStmt = ConnectionHandler.runPreparedQuery(artifacts.size() * 8, SELECT_RELATIONS, queryId);
         ResultSet rSet = chStmt.getRset();
         while (rSet.next()) {
            int relationId = rSet.getInt("rel_link_id");
            int aArtifactId = rSet.getInt("a_art_id");
            int bArtifactId = rSet.getInt("b_art_id");
            Branch aBranch = BranchPersistenceManager.getBranch(rSet.getInt("branch_id"));
            Branch bBranch = aBranch; // TODO these branch ids need to come from the relation link table
            RelationType relationType = RelationTypeManager.getType(rSet.getInt("rel_link_type_id"));

            RelationLink relation =
                  RelationManager.getLoadedRelation(relationType, aArtifactId, bArtifactId, aBranch, bBranch);

            if (relation == null) {
               int aOrderValue = rSet.getInt("a_order");
               int bOrderValue = rSet.getInt("b_order");
               int gammaId = rSet.getInt("gamma_id");
               String rationale = rSet.getString("rationale");

               relation =
                     new RelationLink(aArtifactId, bArtifactId, aBranch, bBranch, relationType, relationId, gammaId,
                           rationale, aOrderValue, bOrderValue);

            }
            RelationManager.manageRelation(relation, RelationSide.SIDE_A);
            RelationManager.manageRelation(relation, RelationSide.SIDE_B);
         }
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      } finally {
         DbUtil.close(chStmt);
      }
      Map<Integer, RelationLink> sideB = new HashMap<Integer, RelationLink>();
      Map<Integer, RelationLink> sideA = new HashMap<Integer, RelationLink>();
      for (Artifact artifact : artifacts) {
         artifact.setLinksLoaded();
         RelationManager.sortRelations(artifact, sideA, sideB);
      }
   }

   private static void loadAttributeData(int queryId, Collection<Artifact> artifacts, boolean historical, boolean allowDeletedArtifacts) throws OseeDataStoreException, ArtifactDoesNotExist {
      ConnectionHandlerStatement chStmt = null;
      try {
         if (historical) {
            chStmt = ConnectionHandler.runPreparedQuery(artifacts.size() * 8, SELECT_HISTORICAL_ATTRIBUTES, queryId);
         } else {
            String sql = allowDeletedArtifacts ? SELECT_CURRENT_ATTRIBUTES_WITH_DELETED : SELECT_CURRENT_ATTRIBUTES;
            chStmt = ConnectionHandler.runPreparedQuery(artifacts.size() * 8, sql, queryId);
         }

         ResultSet rSet = chStmt.getRset();
         Artifact artifact = null;
         int previousArtifactId = -1;
         int previousBranchId = -1;
         int previousAttrId = -1;

         while (rSet.next()) {
            int artifactId = rSet.getInt("art_id");
            int branchId = rSet.getInt("branch_id");
            int attrId = rSet.getInt("attr_id");

            // if a different artifact than the previous iteration
            if (branchId != previousBranchId || artifactId != previousArtifactId) {
               if (artifact != null) { // exclude the first pass because there is no previous artifact
                  // meet minimum attributes for the previous artifact since its existing attributes have already been loaded
                  AttributeToTransactionOperation.meetMinimumAttributeCounts(artifact, false);
                  ArtifactCache.cachePostAttributeLoad(artifact);
               }

               if (historical) {
                  artifact = ArtifactCache.getHistorical(artifactId, rSet.getInt("stripe_transaction_id"));
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

            // if a different attribute than the previous iteration and its attribute had not already been loaded and this attribute is not deleted
            if (attrId != previousAttrId && artifact != null && rSet.getInt("mod_type") != ModificationType.DELETED.getValue()) {
               AttributeToTransactionOperation.initializeAttribute(artifact, rSet.getInt("attr_type_id"), attrId,
                     rSet.getInt("gamma_id"), rSet.getString("value"), rSet.getString("uri"));
            }
            previousArtifactId = artifactId;
            previousBranchId = branchId;
            previousAttrId = attrId;
         }
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      } finally {
         DbUtil.close(chStmt);
      }
   }

   public static int getNewQueryId() {
      return (int) (Math.random() * Integer.MAX_VALUE);
   }
}