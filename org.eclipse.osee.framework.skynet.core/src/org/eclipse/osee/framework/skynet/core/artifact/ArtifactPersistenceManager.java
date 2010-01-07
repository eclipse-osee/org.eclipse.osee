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

import static org.eclipse.osee.framework.skynet.core.artifact.search.SkynetDatabase.ARTIFACT_TABLE;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.artifact.search.SkynetDatabase;
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
         "SELECT txs1.gamma_id, txd1.tx_type, txs1.transaction_id  FROM osee_tx_details txd1, osee_txs  txs1, osee_attribute atr1 where txd1.transaction_id = txs1.transaction_id and txs1.gamma_id = atr1.gamma_id and txd1.branch_id = ? and atr1.art_id = ? UNION ALL SELECT txs2.gamma_id, txd2.tx_type, txs2.transaction_id FROM osee_tx_details txd2, osee_txs txs2, osee_relation_link rel2 where txd2.transaction_id = txs2.transaction_id and txs2.gamma_id = rel2.gamma_id and txd2.branch_id = ? and (rel2.a_art_id = ? or rel2.b_art_id = ?) UNION ALL SELECT txs3.gamma_id, txd3.tx_type, txs3.transaction_id FROM osee_tx_details txd3, osee_txs txs3, osee_artifact_version art3 where txd3.transaction_id = txs3.transaction_id and txs3.gamma_id = art3.gamma_id and txd3.branch_id = ? and art3.art_id = ?";

   private static final String GET_GAMMAS_RELATION_REVERT =
         "SELECT txs2.gamma_id, txd2.tx_type, txs2.transaction_id FROM osee_tx_details txd2, osee_txs txs2, osee_relation_link rel2 where txd2.transaction_id = txs2.transaction_id and txs2.gamma_id = rel2.gamma_id and txd2.branch_id = ? and rel2.rel_link_id = ?";

   private static final String GET_GAMMAS_ATTRIBUTE_REVERT =
         "SELECT txs2.gamma_id, txd2.tx_type, txs2.transaction_id FROM osee_tx_details txd2, osee_txs txs2, osee_attribute atr2 where txd2.transaction_id = txs2.transaction_id and txs2.gamma_id = atr2.gamma_id and txd2.branch_id = ? and atr2.attr_id = ?";

   private static final String ARTIFACT_SELECT =
         "SELECT osee_artifact.art_id, txd1.branch_id FROM osee_artifact, osee_artifact_version arv1, osee_txs txs1, osee_tx_details txd1 WHERE " + ARTIFACT_TABLE.column("art_id") + "=arv1.art_id AND arv1.gamma_id=txs1.gamma_id AND txs1.tx_current=" + TxChange.CURRENT.getValue() + " AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id=? AND ";

   private static final String ARTIFACT_ID_SELECT =
         "SELECT " + SkynetDatabase.ARTIFACT_TABLE.columns("art_id") + " FROM " + SkynetDatabase.ARTIFACT_TABLE + " WHERE ";

   private static final String ARTIFACT_NEW_ON_BRANCH =
         "select txd.tx_type from osee_artifact_version arv, osee_txs txs, osee_tx_details txd WHERE arv.art_id = ? and arv.gamma_id = txs.gamma_id and txs.branch_id = ? and txs.transaction_id = txd.transaction_id and txd.tx_type = 1";

   private static final String RELATION_NEW_ON_BRANCH =
         "select txd.tx_type from osee_relation_link rel, osee_txs txs, osee_tx_details txd WHERE rel.a_art_id = ? and rel.b_art_id = ? and rel.rel_link_type_id = ? and rel.gamma_id = txs.gamma_id and txs.branch_id = ? and txs.transaction_id = txd.transaction_id and txd.tx_type = 1";

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
            sql.append(SkynetDatabase.ARTIFACT_TABLE.column("art_id") + " in (");
            sql.append(getSelectArtIdSql(primitive, dataList, branch));

            if (iter.hasNext()) {
               sql.append(") AND ");
            }
         }
         sql.append(")");
      } else {
         ISearchPrimitive primitive = null;
         Iterator<ISearchPrimitive> iter = searchCriteria.iterator();

         sql.append(SkynetDatabase.ARTIFACT_TABLE.column("art_id") + " IN(SELECT art_id FROM " + ARTIFACT_TABLE + ", (");

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
    * @param transaction if the transaction is null then persist is not called otherwise
    * @param overrideDeleteCheck if <b>true</b> deletes without checking preconditions
    * @param artifacts The artifacts to delete.
    */
   public static void deleteArtifact(SkynetTransaction transaction, boolean overrideDeleteCheck, final Artifact... artifacts) throws OseeCoreException {
      if (artifacts.length == 0) {
         return;
      }

      if (!overrideDeleteCheck) {
         // Confirm artifacts are fit to delete
         for (IArtifactCheck check : ArtifactChecks.getArtifactChecks()) {
            IStatus result = check.isDeleteable(Arrays.asList(artifacts));
            if (!result.isOK()) {
               throw new OseeStateException(result.getMessage());
            }
         }
      }
      //Bulk Load Artifacts
      Collection<Integer> artIds = new LinkedList<Integer>();
      for (Artifact artifact : artifacts) {
         for (RelationLink link : artifact.getRelationsAll(false)) {
            if (link.getRelationType().isOrdered()) {
               artIds.add(artifact.getArtId() == link.getAArtifactId() ? link.getBArtifactId() : link.getAArtifactId());
            }
         }
      }
      Branch branch = artifacts[0].getBranch();
      ArtifactQuery.getArtifactListFromIds(artIds, branch);

      for (Artifact artifact : artifacts) {
         deleteTrace(artifact, transaction, true);
      }
   }

   /**
    * @param artifact
    * @param builder
    * @param reorderReloations
    * @throws Exception
    */
   private static void deleteTrace(Artifact artifact, SkynetTransaction transaction, boolean reorderRelations) throws OseeCoreException {
      if (!artifact.isDeleted()) {
         // This must be done first since the the actual deletion of an artifact clears out the link manager
         for (Artifact childArtifact : artifact.getChildren()) {
            deleteTrace(childArtifact, transaction, false);
         }
         try {
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
            attribute.getAttrId());
   }

   public static void revertAttribute(OseeConnection connection, int branchId, int artId, int attributeId) throws OseeCoreException {
      TransactionRecord transId =
            TransactionManager.createNextTransactionId(BranchManager.getBranch(branchId), UserManager.getUser(), "");
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
    * Should NOT be used for relation types that maintain order. Not handled yet
    */
   public static void revertRelationLink(OseeConnection connection, RelationLink link) throws OseeCoreException {
      //Only reverts relation links that don't span multiple branches.  Need to revisit if additional functionality is needed.
      if (!link.getArtifactA().getBranch().equals(link.getArtifactB().getBranch())) {
         throw new OseeArgumentException(String.format("Can not revert Relation %d. Relation spans multiple branches",
               link.getRelationId()));
      }
      revertRelationLink(connection, link.getArtifactA().getBranch().getId(), link.getRelationId(),
            link.getArtifactA().getArtId(), link.getArtifactB().getArtId());
   }

   private static void revertRelationLink(OseeConnection connection, int branchId, int relLinkId, int aArtId, int bArtId) throws BranchDoesNotExist, OseeCoreException {
      long time = System.currentTimeMillis();
      long totalTime = time;
      IOseeStatement chStmt = ConnectionHandler.getStatement(connection);

      TransactionRecord transId =
            TransactionManager.createNextTransactionId(BranchManager.getBranch(branchId), UserManager.getUser(), "");

      try {
         chStmt.runPreparedQuery(GET_GAMMAS_RELATION_REVERT, branchId, relLinkId);
         new RevertAction(connection, chStmt, transId).revertObject(totalTime, relLinkId, "Relation Link");
      } finally {
         chStmt.close();
      }
   }

   public static void revertArtifact(OseeConnection connection, Artifact artifact) throws OseeCoreException {
      if (artifact == null) {
         return;
      }
      revertArtifact(connection, artifact.getBranch().getId(), artifact.getArtId());
   }

   public static void revertArtifact(OseeConnection connection, int branchId, int artId) throws OseeCoreException {
      TransactionRecord transId =
            TransactionManager.createNextTransactionId(BranchManager.getBranch(branchId), UserManager.getUser(), "");
      long totalTime = System.currentTimeMillis();
      //Get attribute Gammas
      IOseeStatement chStmt = ConnectionHandler.getStatement(connection);
      try {
         chStmt.runPreparedQuery(GET_GAMMAS_ARTIFACT_REVERT, branchId, artId, branchId, artId, artId, branchId, artId);
         new RevertAction(connection, chStmt, transId).revertObject(totalTime, artId, "Artifact");
      } finally {
         chStmt.close();
      }
   }

   public static boolean isArtifactNewOnBranch(Artifact artifact) throws OseeDataStoreException {
      return ConnectionHandler.runPreparedQueryFetchInt(-1, ARTIFACT_NEW_ON_BRANCH, artifact.getBranch().getId(),
            artifact.getArtId()) == -1;
   }

   public static boolean isRelationNewOnBranch(RelationLink relation) throws OseeDataStoreException {
      return ConnectionHandler.runPreparedQueryFetchInt(-1, RELATION_NEW_ON_BRANCH, relation.getAArtifactId(),
            relation.getBArtifactId(), relation.getRelationType().getId(), relation.getABranch().getId()) == -1;
   }
}