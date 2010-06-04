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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Ryan D. Brooks
 * @author Robert A. Fisher
 */
public class ArtifactPersistenceManager {

   private static final String GET_GAMMAS_ARTIFACT_REVERT =
         "SELECT txs1.gamma_id, txs1.transaction_id FROM osee_txs txs1, osee_attribute attr1 WHERE txs1.gamma_id = attr1.gamma_id and txs1.branch_id = ? and attr1.art_id = ? " + //
         "UNION ALL SELECT txs2.gamma_id, txs2.transaction_id FROM osee_txs txs2, osee_relation_link rel2 WHERE txs2.gamma_id = rel2.gamma_id AND txs2.branch_id = ? AND (rel2.a_art_id = ? or rel2.b_art_id = ?) " + //
         "UNION ALL SELECT txs3.gamma_id, txs3.transaction_id FROM osee_txs txs3, osee_artifact art3 WHERE txs3.gamma_id = art3.gamma_id AND txs3.branch_id = ? AND art3.art_id = ?";

   private static final String GET_GAMMAS_RELATION_REVERT =
         "SELECT txs.gamma_id, txs.transaction_id FROM osee_txs txs, osee_relation_link rel WHERE txs.gamma_id = rel.gamma_id AND txs.branch_id = ? AND rel.rel_link_id = ?";

   private static final String GET_GAMMAS_ATTRIBUTE_REVERT =
         "SELECT txs.gamma_id, txs.transaction_id FROM osee_txs txs, osee_attribute attr WHERE txs.gamma_id = attr.gamma_id AND txs.branch_id = ? AND attr.attr_id = ?";

   private static final String ARTIFACT_SELECT =
         "SELECT art1.art_id, txs1.branch_id FROM osee_artifact art1, osee_txs txs1 WHERE art1.gamma_id = txs1.gamma_id AND txs1.tx_current = " + TxChange.CURRENT.getValue() + " AND txs1.branch_id = ? AND ";

   private static final String ARTIFACT_ID_SELECT = "SELECT art1.art_id FROM osee_artifact art1 WHERE ";

   private static final String ARTIFACT_NEW_ON_BRANCH =
         "SELECT count(1) FROM osee_artifact art, osee_txs txs WHERE art.art_id = ? and art.gamma_id = txs.gamma_id and txs.branch_id = ? and txs.transaction_id = ?";

   private static final String RELATION_NEW_ON_BRANCH =
         "SELECT count(1) FROM osee_relation_link rel, osee_txs txs WHERE rel.a_art_id = ? and rel.b_art_id = ? and rel.rel_link_type_id = ? and rel.gamma_id = txs.gamma_id and txs.branch_id = ? and txs.transaction_id = ?";

   public static CharSequence getSelectArtIdSql(ISearchPrimitive searchCriteria, List<Object> dataList, Branch branch) {
      return getSelectArtIdSql(searchCriteria, dataList, null, branch);
   }

   private static CharSequence getSelectArtIdSql(ISearchPrimitive searchCriteria, List<Object> dataList, String alias, Branch branch) {
      StringBuilder sql = new StringBuilder();

      sql.append("SELECT ");
      sql.append(searchCriteria.getArtIdColName());

      if (alias != null) {
         sql.append(" AS " + alias);
      }

      sql.append(" FROM ");
      sql.append(searchCriteria.getTableSql(dataList, branch));

      String criteriaSql = searchCriteria.getCriteriaSql(dataList, branch);
      if (criteriaSql.trim().length() != 0) {
         sql.append(" WHERE (");
         sql.append(criteriaSql);
         sql.append(")");
      }

      return sql;
   }

   public static String getIdSql(List<ISearchPrimitive> searchCriteria, boolean all, List<Object> dataList, Branch branch) {
      return getSql(searchCriteria, all, ARTIFACT_ID_SELECT, dataList, branch);
   }

   private static String getSql(List<ISearchPrimitive> searchCriteria, boolean all, String header, List<Object> dataList, Branch branch) {
      StringBuilder sql = new StringBuilder(header);

      if (all) {
         ISearchPrimitive primitive = null;
         Iterator<ISearchPrimitive> iter = searchCriteria.iterator();

         while (iter.hasNext()) {
            primitive = iter.next();
            sql.append("art1.art_id in (");
            sql.append(getSelectArtIdSql(primitive, dataList, branch));

            if (iter.hasNext()) {
               sql.append(") AND ");
            }
         }
         sql.append(")");
      } else {
         ISearchPrimitive primitive = null;
         Iterator<ISearchPrimitive> iter = searchCriteria.iterator();

         sql.append("art1.art_id IN(SELECT art_id FROM osee_artifact art1, (");

         while (iter.hasNext()) {
            primitive = iter.next();
            sql.append(getSelectArtIdSql(primitive, dataList, "desired_art_id", branch));
            if (iter.hasNext()) {
               sql.append(" UNION ALL ");
            }
         }
         sql.append(") ORD_ARTS");
         sql.append(" WHERE art_id = ORD_ARTS.desired_art_id");

         sql.append(")");
      }

      return sql.toString();
   }

   @Deprecated
   public static Collection<Artifact> getArtifacts(List<ISearchPrimitive> searchCriteria, boolean all, Branch branch, ISearchConfirmer confirmer) throws OseeCoreException {
      LinkedList<Object> queryParameters = new LinkedList<Object>();
      queryParameters.add(branch.getId());
      return ArtifactLoader.getArtifacts(getSql(searchCriteria, all, ARTIFACT_SELECT, queryParameters, branch),
            queryParameters.toArray(), 100, ArtifactLoad.FULL, false, confirmer, null, false);
   }

   /**
    * @param transaction
    *           if the transaction is null then persist is not called
    * @param overrideDeleteCheck
    *           if <b>true</b> deletes without checking preconditions
    * @param artifacts
    *           The artifacts to delete.
    */
   public static void deleteArtifact(SkynetTransaction transaction, boolean overrideDeleteCheck, final Artifact... artifacts) throws OseeCoreException {
      deleteArtifactList(transaction, overrideDeleteCheck, Arrays.asList(artifacts));
   }

   public static void deleteArtifactList(SkynetTransaction transaction, boolean overrideDeleteCheck, final List<Artifact> artifacts) throws OseeCoreException {
      if (artifacts.isEmpty()) {
         return;
      }

      if (!overrideDeleteCheck) {
         performDeleteChecks(artifacts);
      }

      bulkLoadRelatives(artifacts);

      boolean reorderRelations = true;
      for (Artifact artifact : artifacts) {
         deleteTrace(artifact, transaction, reorderRelations);
      }
   }

   private static void performDeleteChecks(List<Artifact> artifacts) throws OseeCoreException {
      // Confirm artifacts are fit to delete
      for (IArtifactCheck check : ArtifactChecks.getArtifactChecks()) {
         IStatus result = check.isDeleteable(artifacts);
         if (!result.isOK()) {
            throw new OseeStateException(result.getMessage());
         }
      }
   }

   private static void bulkLoadRelatives(List<Artifact> artifacts) throws OseeCoreException {
      Collection<Integer> artIds = new HashSet<Integer>();
      for (Artifact artifact : artifacts) {
         boolean includeDeleted = false;
         for (RelationLink link : artifact.getRelationsAll(includeDeleted)) {
            artIds.add(link.getAArtifactId());
            artIds.add(link.getBArtifactId());
         }
      }
      Branch branch = artifacts.get(0).getBranch();
      ArtifactQuery.getArtifactListFromIds(artIds, branch);
   }

   private static void deleteTrace(Artifact artifact, SkynetTransaction transaction, boolean reorderRelations) throws OseeCoreException {
      if (!artifact.isDeleted()) {
         // This must be done first since the the actual deletion of an
         // artifact clears out the link manager
         for (Artifact childArtifact : artifact.getChildren()) {
            deleteTrace(childArtifact, transaction, false);
         }
         try {
            // calling deCache here creates a race condition when the handleRelationModifiedEvent listeners fire - RS
            //          ArtifactCache.deCache(artifact);
            artifact.internalSetDeleted();
            RelationManager.deleteRelationsAll(artifact, reorderRelations);
            if (transaction != null) {
               artifact.persist(transaction);
            }
         } catch (OseeCoreException ex) {
            artifact.resetToPreviousModType();
            throw ex;
         }
      }
   }

   public static void revertAttribute(OseeConnection connection, Attribute<?> attribute) throws OseeCoreException {
      if (attribute == null) {
         return;
      }
      revertAttribute(connection, attribute.getArtifact().getBranch().getId(), attribute.getArtifact().getArtId(),
            attribute.getId());
   }

   public static void revertAttribute(OseeConnection connection, int branchId, int artId, int attributeId) throws OseeCoreException {
      TransactionRecord transId =
            TransactionManager.createNextTransactionId(connection, BranchManager.getBranch(branchId),
                  UserManager.getUser(), "");
      long totalTime = System.currentTimeMillis();
      //Get attribute Gammas
      IOseeStatement chStmt = ConnectionHandler.getStatement(connection);
      RevertAction revertAction = null;
      try {
         chStmt.runPreparedQuery(GET_GAMMAS_ATTRIBUTE_REVERT, branchId, attributeId);
         revertAction = new RevertAction(connection, chStmt, transId);
         revertAction.revertObject(totalTime, artId, "Attribute");
      } finally {
         chStmt.close();
      }
      revertAction.fixArtifactVersionForAttributeRevert(branchId, artId);
   }

   /**
    * Should NOT be used for relation types that maintain order. Not handled
    * yet
    */
   public static void revertRelationLink(OseeConnection connection, RelationLink link) throws OseeCoreException {

      // Only reverts relation links that don't span multiple branches. Need
      // to revisit if additional functionality is needed.
      if (!link.getArtifactA().getBranch().equals(link.getArtifactB().getBranch())) {
         throw new OseeArgumentException(String.format("Cannot revert Relation %d. Relation spans multiple branches",
               link.getId()));
      }
      long totalTime = System.currentTimeMillis();
      Branch branch = link.getArtifactA().getBranch();
      IOseeStatement chStmt = ConnectionHandler.getStatement(connection);
      try {
         chStmt.runPreparedQuery(GET_GAMMAS_RELATION_REVERT, branch.getId(), link.getId());
         TransactionRecord transId =
               TransactionManager.createNextTransactionId(connection, branch, UserManager.getUser(), "");
         RevertAction revertAction = new RevertAction(connection, chStmt, transId);
         revertAction.revertObject(totalTime, link.getId(), "Relation Link");
      } finally {
         chStmt.close();
      }
   }

   public static void revertArtifact(OseeConnection connection, Artifact artifact) throws OseeCoreException {
      Conditions.checkNotNull(artifact, "Artifact to revert");
      revertArtifact(connection, artifact.getBranch(), artifact.getArtId());
   }

   public static void revertArtifact(OseeConnection connection, Branch branch, int artId) throws OseeCoreException {
      long totalTime = System.currentTimeMillis();
      IOseeStatement chStmt = ConnectionHandler.getStatement(connection);
      try {
         int branchId = branch.getId();
         chStmt.runPreparedQuery(GET_GAMMAS_ARTIFACT_REVERT, branchId, artId, branchId, artId, artId, branchId, artId);
         TransactionRecord transId =
               TransactionManager.createNextTransactionId(connection, branch, UserManager.getUser(), "");
         RevertAction revertAction = new RevertAction(connection, chStmt, transId);
         revertAction.revertObject(totalTime, artId, "Artifact");
      } finally {
         chStmt.close();
      }
   }

   public static boolean isArtifactNewOnBranch(Artifact artifact) throws OseeCoreException {
      Branch branch = artifact.getBranch();
      return ConnectionHandler.runPreparedQueryFetchInt(0, ARTIFACT_NEW_ON_BRANCH, artifact.getArtId(), branch.getId(),
            branch.getBaseTransaction().getId()) == 0;
   }

   public static boolean isRelationNewOnBranch(RelationLink relation) throws OseeCoreException {
      Branch branch = relation.getABranch();
      return ConnectionHandler.runPreparedQueryFetchInt(-1, RELATION_NEW_ON_BRANCH, relation.getAArtifactId(),
            relation.getBArtifactId(), relation.getRelationType().getId(), branch.getId(),
            branch.getBaseTransaction().getId()) == 0;
   }
}