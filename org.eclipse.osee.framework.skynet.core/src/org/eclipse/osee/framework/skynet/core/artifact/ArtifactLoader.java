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
import static org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad.FULL_ATTRIBUTE;
import static org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad.FULL_FULL;
import static org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad.RELATION;
import static org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad.SHALLOW;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.skynet.core.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQueryBuilder;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeToTransactionOperation;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.change.TxChange;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;

/**
 * @author Ryan D. Brooks
 */
public final class ArtifactLoader {

   private static Artifact retrieveShallowArtifact(ResultSet rSet, Branch branch) throws SQLException {
      int artifactId = rSet.getInt("art_id");

      Artifact artifact = ArtifactCache.get(artifactId, branch);
      if (artifact != null) {
         return artifact;
      }

      ArtifactType artifactType = ArtifactTypeManager.getType(rSet.getInt("art_type_id"));
      ArtifactFactory factory = artifactType.getFactory();

      return factory.loadExisitingArtifact(artifactId, rSet.getInt("gamma_id"), rSet.getString("guid"),
            rSet.getString("human_readable_id"), artifactType.getFactoryKey(), branch, artifactType,
            rSet.getInt("transaction_id"), ModificationType.getMod(rSet.getInt("mod_type")), true);
   }

   public static boolean loadArtifacts(Collection<Artifact> artifacts, Branch branch, ArtifactLoad loadLevel, String sql, Object... queryParameters) throws SQLException {
      return loadArtifacts(artifacts, branch, loadLevel, null, sql, queryParameters);
   }

   /**
    * @param artifacts
    * @param branch
    * @param loadLevel
    * @param confirmer
    * @param sql
    * @param queryParameters
    * @return
    * @throws SQLException
    */
   public static boolean loadArtifacts(Collection<Artifact> artifacts, Branch branch, ArtifactLoad loadLevel, ISearchConfirmer confirmer, String sql, Object... queryParameters) throws SQLException {
      ConnectionHandlerStatement chStmt = null;
      int artifactsCount = artifacts.size();

      try {
         chStmt = ConnectionHandler.runPreparedQuery(sql, queryParameters);
         ResultSet rSet = chStmt.getRset();

         while (rSet.next()) {
            artifactsCount++;
            artifacts.add(retrieveShallowArtifact(rSet, branch));
         }
      } finally {
         DbUtil.close(chStmt);
      }

      if (confirmer == null || confirmer.canProceed(artifactsCount)) {
         loadArtifactsData(artifacts, branch, loadLevel);
         return true;
      }
      return false;
   }

   public static void loadArtifactData(Artifact artifact, ArtifactLoad loadLevel) throws SQLException {
      List<Artifact> artifacts = new ArrayList<Artifact>(1);
      artifacts.add(artifact);
      loadArtifactsData(artifacts, artifact.getBranch(), loadLevel);
   }

   /**
    * accepts an array of no more than 1000 artifacts sorted in ascending order
    * 
    * @param artifacts
    * @param transactionId
    * @throws SQLException
    */
   public static void loadArtifactsData(Collection<Artifact> artifacts, Branch branch, ArtifactLoad loadLevel) throws SQLException {
      if (artifacts.size() == 0) {
         return;
      }
      if (loadLevel == SHALLOW) {
         return;
      } else if (loadLevel == ATTRIBUTE) {
         loadAttributesData(artifacts, branch);
      } else if (loadLevel == RELATION) {
         loadRelationData(artifacts, branch, SHALLOW);
      } else if (loadLevel == FULL) {
         loadAttributesData(artifacts, branch);
         loadRelationData(artifacts, branch, SHALLOW);
      } else if (loadLevel == FULL_ATTRIBUTE) {
         loadAttributesData(artifacts, branch);
      } else if (loadLevel == FULL_FULL) {
         loadAttributesData(artifacts, branch);
         loadRelationData(artifacts, branch, FULL);
      }

      for (Artifact artifact : artifacts) {
         artifact.onInitializationComplete();
      }
   }

   private static void loadRelationData(Collection<Artifact> artifacts, Branch branch, ArtifactLoad otherSideLoadLevel) throws SQLException {
      List<Artifact> artifactsNeedingRelations = new ArrayList<Artifact>(artifacts.size());
      Set<Integer> artifactIdsToLoad = null;
      if (otherSideLoadLevel != SHALLOW) {
         artifactIdsToLoad = new HashSet<Integer>(artifacts.size() * 6);
      }

      for (Artifact artifact : artifacts) {
         if (!artifact.isLinksLoaded()) {
            artifactsNeedingRelations.add(artifact);
         }
      }

      int startIndex;
      int stopIndex = 0;

      while (stopIndex < artifactsNeedingRelations.size()) {
         startIndex = stopIndex;
         stopIndex = Math.min(startIndex + 1000, artifactsNeedingRelations.size());

         ConnectionHandlerStatement chStmt = null;
         try {
            List<Object> relationDataList = new ArrayList<Object>(6);
            String sql = getRelationSQL(artifactsNeedingRelations, startIndex, stopIndex, branch, relationDataList);
            chStmt = ConnectionHandler.runPreparedQuery(sql, relationDataList.toArray());

            ResultSet rSet = chStmt.getRset();
            while (rSet.next()) {
               int relationId = rSet.getInt("rel_link_id");
               int aArtifactId = rSet.getInt("a_art_id");
               int bArtifactId = rSet.getInt("b_art_id");
               RelationType relationType = RelationTypeManager.getType(rSet.getInt("rel_link_type_id"));

               RelationLink relation =
                     RelationManager.getLoadedRelation(relationType, aArtifactId, bArtifactId, branch, branch);

               if (relation == null) {
                  if (otherSideLoadLevel != SHALLOW) {
                     artifactIdsToLoad.add(aArtifactId);
                     artifactIdsToLoad.add(bArtifactId);
                  }

                  int aOrderValue = rSet.getInt("a_order_value");
                  int bOrderValue = rSet.getInt("b_order_value");
                  int gammaId = rSet.getInt("gamma_id");
                  String rationale = rSet.getString("rationale");

                  relation =
                        new RelationLink(aArtifactId, bArtifactId, branch, branch, relationType, relationId, gammaId,
                              rationale, aOrderValue, bOrderValue);

               }
               RelationManager.manageRelation(relation, RelationSide.SIDE_A);
               RelationManager.manageRelation(relation, RelationSide.SIDE_B);
            }

            for (int index = startIndex; index < stopIndex; index++) {
               artifactsNeedingRelations.get(index).setLinksLoaded();
            }
         } finally {
            DbUtil.close(chStmt);
         }
      }

      if (otherSideLoadLevel != SHALLOW) {
         // bulk load any artifacts needed for these links (this checks the cache first)
         new ArtifactQueryBuilder(artifactIdsToLoad, branch, false, otherSideLoadLevel).getArtifacts(null);
      }
   }

   private static void loadAttributesData(Collection<Artifact> artifacts, Branch branch) throws SQLException {
      List<Artifact> artifactsNeedingAttributes = new ArrayList<Artifact>(artifacts.size());
      for (Artifact artifact : artifacts) {
         if (!artifact.isAttributesLoaded()) {
            artifactsNeedingAttributes.add(artifact);
         }
      }

      int startIndex;
      int stopIndex = 0;

      while (stopIndex < artifactsNeedingAttributes.size()) {
         startIndex = stopIndex;
         stopIndex = Math.min(startIndex + 1000, artifactsNeedingAttributes.size());

         ConnectionHandlerStatement chStmt = null;
         try {
            List<Object> attributeDataList = new ArrayList<Object>(4);
            String sql = getAttributeSQL(artifactsNeedingAttributes, startIndex, stopIndex, branch, attributeDataList);
            chStmt = ConnectionHandler.runPreparedQuery(sql, attributeDataList.toArray());

            ResultSet rSet = chStmt.getRset();
            while (rSet.next()) {
               Artifact artifact = ArtifactCache.get(rSet.getInt("art_id"), branch);
               AttributeToTransactionOperation.initializeAttribute(artifact, rSet.getInt("attr_type_id"),
                     rSet.getString("value"), rSet.getString("uri"), rSet.getInt("attr_id"), rSet.getInt("gamma_id"));
            }
         } finally {
            DbUtil.close(chStmt);
         }
      }

      for (Artifact artifact : artifactsNeedingAttributes) {
         try {
            AttributeToTransactionOperation.meetMinimumAttributeCounts(artifact);
         } catch (OseeCoreException ex) {
            throw new SQLException(ex);
         }
      }
   }

   private static String getAttributeSQL(List<Artifact> artifacts, int startIndex, int stopIndex, Branch branch, List<Object> attributeDataList) {
      StringBuilder sql = new StringBuilder(10000);
      sql.append("SELECT att1.* from osee_define_attribute att1, osee_define_txs txs1, osee_define_tx_details txd1 WHERE att1.art_id");

      if (stopIndex - startIndex == 1) {
         sql.append("=?");
         attributeDataList.add(SQL3DataType.INTEGER);
         attributeDataList.add(artifacts.get(startIndex).getArtId());
      } else {
         makeArtifactIdList(artifacts, startIndex, stopIndex, sql);
      }

      sql.append(" AND att1.gamma_id = txs1.gamma_id AND txs1.tx_current=");
      sql.append(TxChange.CURRENT.ordinal());
      sql.append(" AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id=?");

      attributeDataList.add(SQL3DataType.INTEGER);
      attributeDataList.add(branch.getBranchId());

      return sql.toString();
   }

   private static String getRelationSQL(List<Artifact> artifacts, int startIndex, int stopIndex, Branch branch, List<Object> relationDataList) {
      StringBuilder sql = new StringBuilder(10000);
      sql.append("SELECT rel1.*, txs1.* FROM osee_define_rel_link rel1, osee_define_txs txs1, osee_define_tx_details txd1 WHERE (rel1.a_art_id");

      if (stopIndex - startIndex == 1) {
         sql.append("=? OR rel1.b_art_id =?");
         relationDataList.add(SQL3DataType.INTEGER);
         relationDataList.add(artifacts.get(startIndex).getArtId());
         relationDataList.add(SQL3DataType.INTEGER);
         relationDataList.add(artifacts.get(startIndex).getArtId());
      } else {
         StringBuilder artifactIdSql = new StringBuilder(8 * (stopIndex - startIndex));
         makeArtifactIdList(artifacts, startIndex, stopIndex, artifactIdSql);
         sql.append(artifactIdSql);
         sql.append(" OR rel1.b_art_id");
         sql.append(artifactIdSql);
      }

      sql.append(") AND rel1.gamma_id = txs1.gamma_id AND txs1.tx_current=");
      sql.append(TxChange.CURRENT.ordinal());
      sql.append(" AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = ?");

      relationDataList.add(SQL3DataType.INTEGER);
      relationDataList.add(branch.getBranchId());

      return sql.toString();
   }

   /**
    * The in clause is limited to 1000 values at a time
    * 
    * @param artifacts
    * @param startIndex
    * @param stopIndex
    * @param sql
    */
   private static void makeArtifactIdList(List<Artifact> artifacts, int startIndex, int stopIndex, StringBuilder sql) {
      sql.append(" IN (");
      for (int index = startIndex; index < stopIndex; index++) {
         sql.append(artifacts.get(index).getArtId());
         sql.append(',');
      }

      sql.deleteCharAt(sql.length() - 1);
      sql.append(')');
   }
}
