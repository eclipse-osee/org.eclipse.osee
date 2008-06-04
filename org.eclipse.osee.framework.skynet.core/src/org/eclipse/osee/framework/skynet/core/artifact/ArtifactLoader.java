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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeToTransactionOperation;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.change.TxChange;
import org.eclipse.osee.framework.skynet.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;

/**
 * @author Ryan D. Brooks
 */
public final class ArtifactLoader {

   private static final String LOADER_INSERT = "INSERT INTO osee_artifact_loader (query_id, art_id, branch_id) ";

   private static final String SELECT_RELATIONS =
         "SELECT rel_link_id, a_art_id, b_art_id, rel_link_type_id, a_order_value, b_order_value, rel1.gamma_id, rationale, al1.branch_id FROM osee_artifact_loader al1, osee_define_rel_link rel1, osee_define_txs txs1, osee_define_tx_details txd1 WHERE al1.query_id = ? AND (al1.art_id = rel1.a_art_id OR al1.art_id = rel1.b_art_id) AND rel1.gamma_id = txs1.gamma_id AND txs1.tx_current=" + TxChange.CURRENT.getValue() + " AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = al1.branch_id";

   private static final String SELECT_ATTRIBUTES =
         "SELECT att1.art_id, att1.attr_id, att1.value, att1.gamma_id, att1.attr_type_id, att1.uri, al1.branch_id FROM osee_artifact_loader al1, osee_define_attribute att1, osee_define_txs txs1, osee_define_tx_details txd1 WHERE al1.query_id = ? AND al1.art_id = att1.art_id AND att1.gamma_id = txs1.gamma_id AND txs1.tx_current=" + TxChange.CURRENT.getValue() + " AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = al1.branch_id order by al1.branch_id, al1.art_id";

   private static final String SELECT_ARTIFACTS =
         "SELECT al1.art_id, txs1.gamma_id, txs1.transaction_id, txd1.branch_id, art_type_id, guid, human_readable_id, mod_type FROM osee_artifact_loader al1, osee_define_artifact art1, osee_define_artifact_version arv1, osee_define_txs txs1, osee_define_tx_details txd1 WHERE al1.query_id = ? AND al1.art_id = art1.art_id AND art1.art_id = arv1.art_id AND arv1.gamma_id = txs1.gamma_id AND txd1.branch_id = al1.branch_id AND txd1.transaction_id = txs1.transaction_id AND txs1.tx_current in (" + TxChange.CURRENT.getValue() + ", " + TxChange.DELETED.getValue() + ")";

   private static final String INSERT_INTO_LOADER =
         "INSERT INTO osee_artifact_loader (query_id, art_id, branch_id) VALUES (?, ?, ?)";

   private static final String DELETE_FROM_LOADER = "DELETE FROM osee_artifact_loader WHERE query_id = ?";

   private static Artifact retrieveShallowArtifact(ResultSet rSet, boolean reload) throws SQLException {
      int artifactId = rSet.getInt("art_id");
      Branch branch = BranchPersistenceManager.getInstance().getBranch(rSet.getInt("branch_id"));

      Artifact artifact = ArtifactCache.getActive(artifactId, branch);
      if (artifact == null) {
         ArtifactType artifactType = ArtifactTypeManager.getType(rSet.getInt("art_type_id"));
         ArtifactFactory factory = artifactType.getFactory();

         artifact =
               factory.loadExisitingArtifact(artifactId, rSet.getInt("gamma_id"), rSet.getString("guid"),
                     rSet.getString("human_readable_id"), artifactType.getFactoryKey(), branch, artifactType,
                     rSet.getInt("transaction_id"), ModificationType.getMod(rSet.getInt("mod_type")), true);
      } else if (reload) {
         artifact.initPersistenceData(rSet.getInt("gamma_id"), rSet.getInt("transaction_id"),
               ModificationType.getMod(rSet.getInt("mod_type")), true);
      }
      return artifact;
   }

   public static int selectArtifacts(int queryId, String sql, Object[] queryParameters) throws SQLException {
      return ConnectionHandler.runPreparedUpdateReturnCount(LOADER_INSERT + sql, queryParameters);
   }

   public static List<Artifact> loadArtifacts(int queryId, ArtifactLoad loadLevel, ISearchConfirmer confirmer, int artifactCount, boolean reload) throws SQLException {
      if (artifactCount > 0) {
         List<Artifact> artifacts = new ArrayList<Artifact>(artifactCount);
         ConnectionHandlerStatement chStmt = null;

         try {
            chStmt = ConnectionHandler.runPreparedQuery(artifactCount, SELECT_ARTIFACTS, SQL3DataType.INTEGER, queryId);
            ResultSet rSet = chStmt.getRset();

            while (rSet.next()) {
               artifacts.add(retrieveShallowArtifact(rSet, reload));
            }
         } finally {
            DbUtil.close(chStmt);
         }

         if (confirmer == null || confirmer.canProceed(artifactCount)) {
            loadArtifactsData(queryId, artifacts, loadLevel, reload);
         }
         return artifacts;
      }
      return Collections.emptyList();
   }

   static void loadArtifactData(Artifact artifact, ArtifactLoad loadLevel) throws SQLException {
      int queryId = getNewQueryId();
      ConnectionHandler.runPreparedUpdateReturnCount(INSERT_INTO_LOADER, SQL3DataType.INTEGER, queryId,
            SQL3DataType.INTEGER, artifact.getArtId(), SQL3DataType.INTEGER, artifact.getBranch().getBranchId());
      List<Artifact> artifacts = new ArrayList<Artifact>(1);
      artifacts.add(artifact);
      loadArtifactsData(queryId, artifacts, loadLevel, false);
   }

   public static void clearQuery(int queryId) throws SQLException {
      ConnectionHandler.runPreparedUpdateReturnCount(DELETE_FROM_LOADER, SQL3DataType.INTEGER, queryId);
   }

   private static void loadArtifactsData(int queryId, Collection<Artifact> artifacts, ArtifactLoad loadLevel, boolean reload) throws SQLException {
      if (reload) {
         for (Artifact artifact : artifacts) {
            artifact.prepareForReload();
         }
      }

      if (loadLevel == SHALLOW) {
         return;
      } else if (loadLevel == ATTRIBUTE) {
         loadAttributeData(queryId, artifacts);
      } else if (loadLevel == RELATION) {
         loadRelationData(queryId, artifacts);
      } else if (loadLevel == FULL) {
         loadAttributeData(queryId, artifacts);
         loadRelationData(queryId, artifacts);
      }

      for (Artifact artifact : artifacts) {
         artifact.onInitializationComplete();
      }
   }

   private static void loadRelationData(int queryId, Collection<Artifact> artifacts) throws SQLException {
      ConnectionHandlerStatement chStmt = null;
      try {
         chStmt =
               ConnectionHandler.runPreparedQuery(artifacts.size() * 8, SELECT_RELATIONS, SQL3DataType.INTEGER, queryId);
         ResultSet rSet = chStmt.getRset();
         while (rSet.next()) {
            int relationId = rSet.getInt("rel_link_id");
            int aArtifactId = rSet.getInt("a_art_id");
            int bArtifactId = rSet.getInt("b_art_id");
            Branch aBranch = BranchPersistenceManager.getInstance().getBranch(rSet.getInt("branch_id"));
            Branch bBranch = aBranch; // TODO these branch ids need to come from the relation link table
            RelationType relationType = RelationTypeManager.getType(rSet.getInt("rel_link_type_id"));

            RelationLink relation =
                  RelationManager.getLoadedRelation(relationType, aArtifactId, bArtifactId, aBranch, bBranch);

            if (relation == null) {
               int aOrderValue = rSet.getInt("a_order_value");
               int bOrderValue = rSet.getInt("b_order_value");
               int gammaId = rSet.getInt("gamma_id");
               String rationale = rSet.getString("rationale");

               relation =
                     new RelationLink(aArtifactId, bArtifactId, aBranch, bBranch, relationType, relationId, gammaId,
                           rationale, aOrderValue, bOrderValue);

            }
            RelationManager.manageRelation(relation, RelationSide.SIDE_A);
            RelationManager.manageRelation(relation, RelationSide.SIDE_B);
         }
      } finally {
         DbUtil.close(chStmt);
      }
      Map<Integer, RelationLink> sideB = new HashMap<Integer, RelationLink>();
      Map<Integer, RelationLink> sideA = new HashMap<Integer, RelationLink>();
      for (Artifact artifact : artifacts) {
         artifact.setLinksLoaded();
         artifact.sortRelations(sideA, sideB);
      }
   }

   private static void loadAttributeData(int queryId, Collection<Artifact> artifacts) throws SQLException {
      ConnectionHandlerStatement chStmt = null;
      try {
         chStmt =
               ConnectionHandler.runPreparedQuery(artifacts.size() * 8, SELECT_ATTRIBUTES, SQL3DataType.INTEGER,
                     queryId);
         ResultSet rSet = chStmt.getRset();
         Artifact artifact = null;
         int previousArtifactId = -1;
         int previousBranchId = -1;
         while (rSet.next()) {
            int artifactId = rSet.getInt("art_id");
            int branchId = rSet.getInt("branch_id");
            if (artifactId != previousArtifactId || branchId != previousBranchId) {
               if (artifact != null) { // exclude the first pass because there is no previous artifact
                  try {
                     AttributeToTransactionOperation.meetMinimumAttributeCounts(artifact);
                  } catch (OseeDataStoreException ex) {
                     throw new SQLException(ex);
                  }
               }
               previousArtifactId = artifactId;
               previousBranchId = branchId;

               artifact = ArtifactCache.getActive(artifactId, branchId);
               if (artifact != null && artifact.isAttributesLoaded()) {
                  artifact = null;
               }
            }
            if (artifact == null) {
               continue;
            }
            AttributeToTransactionOperation.initializeAttribute(artifact, rSet.getInt("attr_type_id"),
                  rSet.getString("value"), rSet.getString("uri"), rSet.getInt("attr_id"), rSet.getInt("gamma_id"));
         }
      } finally {
         DbUtil.close(chStmt);
      }
   }

   public static int getNewQueryId() {
      return (int) (Math.random() * Integer.MAX_VALUE);
   }
}
