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

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import static org.eclipse.osee.framework.skynet.core.artifact.LoadType.INCLUDE_CACHE;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * @author Ryan D. Brooks
 * @author Robert A. Fisher
 */
public class ArtifactPersistenceManager {
   private static final String ARTIFACT_SELECT =
      "SELECT art1.art_id, txs1.branch_id FROM osee_artifact art1, osee_txs txs1 WHERE art1.gamma_id = txs1.gamma_id AND txs1.tx_current = " + TxChange.CURRENT.getValue() + " AND txs1.branch_id = ? AND ";

   private static final String ARTIFACT_ID_SELECT = "SELECT art1.art_id FROM osee_artifact art1 WHERE ";

   private static final String ARTIFACT_NEW_ON_BRANCH =
      "SELECT count(1) FROM osee_artifact art, osee_txs txs WHERE art.art_id = ? and art.gamma_id = txs.gamma_id and txs.branch_id = ? and txs.transaction_id = ?";

   private static final String RELATION_NEW_ON_BRANCH =
      "SELECT count(1) FROM osee_relation_link rel, osee_txs txs WHERE rel.a_art_id = ? and rel.b_art_id = ? and rel.rel_link_type_id = ? and rel.gamma_id = txs.gamma_id and txs.branch_id = ? and txs.transaction_id = ?";

   public static CharSequence getSelectArtIdSql(ISearchPrimitive searchCriteria, List<Object> dataList, IOseeBranch branch) throws OseeCoreException {
      return getSelectArtIdSql(searchCriteria, dataList, null, branch);
   }

   private static CharSequence getSelectArtIdSql(ISearchPrimitive searchCriteria, List<Object> dataList, String alias, IOseeBranch branch) throws OseeCoreException {
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

   public static String getIdSql(List<ISearchPrimitive> searchCriteria, boolean all, List<Object> dataList, IOseeBranch branch) throws OseeCoreException {
      return getSql(searchCriteria, all, ARTIFACT_ID_SELECT, dataList, branch);
   }

   private static String getSql(List<ISearchPrimitive> searchCriteria, boolean all, String header, List<Object> dataList, IOseeBranch branch) throws OseeCoreException {
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
   public static Collection<Artifact> getArtifacts(List<ISearchPrimitive> searchCriteria, boolean all, IOseeBranch branch, ISearchConfirmer confirmer) throws OseeCoreException {
      LinkedList<Object> queryParameters = new LinkedList<Object>();
      queryParameters.add(BranchManager.getBranchId(branch));
      return ArtifactLoader.getArtifacts(getSql(searchCriteria, all, ARTIFACT_SELECT, queryParameters, branch),
         queryParameters.toArray(), 100, LoadLevel.ALL, INCLUDE_CACHE, confirmer, null, EXCLUDE_DELETED);
   }

   /**
    * @param transaction if the transaction is null then persist is not called
    * @param overrideDeleteCheck if <b>true</b> deletes without checking preconditions
    * @param artifacts The artifacts to delete.
    */
   public static void deleteArtifact(SkynetTransaction transaction, boolean overrideDeleteCheck, final Artifact... artifacts) throws OseeCoreException {
      deleteArtifactCollection(transaction, overrideDeleteCheck, Arrays.asList(artifacts));
   }

   public static void deleteArtifactCollection(SkynetTransaction transaction, boolean overrideDeleteCheck, final Collection<Artifact> artifacts) throws OseeCoreException {
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

   private static void performDeleteChecks(Collection<Artifact> artifacts) throws OseeCoreException {
      // Confirm artifacts are fit to delete
      for (IArtifactCheck check : ArtifactChecks.getArtifactChecks()) {
         IStatus result = check.isDeleteable(artifacts);
         if (!result.isOK()) {
            throw new OseeStateException(result.getMessage());
         }
      }
   }

   private static void bulkLoadRelatives(Collection<Artifact> artifacts) throws OseeCoreException {
      Collection<Integer> artIds = new HashSet<Integer>();
      for (Artifact artifact : artifacts) {
         for (RelationLink link : artifact.getRelationsAll(DeletionFlag.EXCLUDE_DELETED)) {
            artIds.add(link.getAArtifactId());
            artIds.add(link.getBArtifactId());
         }
      }
      IOseeBranch branch = artifacts.iterator().next().getBranch();
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
            RelationManager.deleteRelationsAll(artifact, reorderRelations, transaction);
            if (transaction != null) {
               artifact.persist(transaction);
            }
         } catch (OseeCoreException ex) {
            artifact.resetToPreviousModType();
            throw ex;
         }
      }
   }

   public static boolean isArtifactNewOnBranch(Artifact artifact) throws OseeCoreException {
      Branch branch = BranchManager.getBranch(artifact.getBranch());
      return ConnectionHandler.runPreparedQueryFetchInt(0, ARTIFACT_NEW_ON_BRANCH, artifact.getArtId(), branch.getId(),
         branch.getBaseTransaction().getId()) == 0;
   }

   public static boolean isRelationNewOnBranch(RelationLink relation) throws OseeCoreException {
      Branch branch = BranchManager.getBranch(relation.getBranch());
      return ConnectionHandler.runPreparedQueryFetchInt(-1, RELATION_NEW_ON_BRANCH, relation.getAArtifactId(),
         relation.getBArtifactId(), relation.getRelationType().getId(), branch.getId(),
         branch.getBaseTransaction().getId()) == 0;
   }
}