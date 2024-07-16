/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.db.internal.search.handlers;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAllBranches;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAllTxs;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactGuids;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactIds;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactTxComment;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactType;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAssociatedArtId;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeKeywords;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeRaw;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeTypeExists;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeTypeNotExists;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAuthorIds;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchAncestorOf;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchArchived;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchCategory;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchChildOf;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchIds;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchName;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchState;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranchType;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaCommitIds;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaDateRange;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaDateWithOperator;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaMergeBranchFor;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaPagination;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelatedRecursive;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelatedTo;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelationTypeExists;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelationTypeFollow;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelationTypeNotExists;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelationTypeSideExists;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelationTypeSideNotExists;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTxBranchIds;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTxComment;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTxGetHead;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTxGetPrior;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTxIdWithOperator;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTxIdWithTwoOperators;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTxIds;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTxType;
import org.eclipse.osee.orcs.db.internal.search.tagger.TagProcessor;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandlerFactory;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandlerFactoryImpl;

/**
 * @author Roberto E. Escobar
 */
public final class SqlHandlerFactoryUtil {

   private SqlHandlerFactoryUtil() {
      // Static Utility
   }

   public static SqlHandlerFactory createArtifactSqlHandlerFactory(TagProcessor tagProcessor) {
      Map<Class<? extends Criteria>, Class<? extends SqlHandler<?>>> handleMap = new HashMap<>();
      addArtifactHandlers(handleMap);
      return new SqlHandlerFactoryImpl(tagProcessor, handleMap);
   }

   public static SqlHandlerFactory createBranchSqlHandlerFactory(Log logger) {
      Map<Class<? extends Criteria>, Class<? extends SqlHandler<?>>> handleMap = new HashMap<>();
      addBranchHandlers(handleMap);
      return new SqlHandlerFactoryImpl(null, handleMap);
   }

   public static SqlHandlerFactory createTxSqlHandlerFactory(Log logger) {
      Map<Class<? extends Criteria>, Class<? extends SqlHandler<?>>> handleMap = new HashMap<>();
      addTxHandlers(handleMap);
      return new SqlHandlerFactoryImpl(null, handleMap);
   }

   private static void addArtifactHandlers(Map<Class<? extends Criteria>, Class<? extends SqlHandler<?>>> handleMap) {
      handleMap.put(CriteriaArtifactGuids.class, ArtifactGuidSqlHandler.class);
      handleMap.put(CriteriaArtifactIds.class, ArtifactIdsSqlHandler.class);
      handleMap.put(CriteriaArtifactType.class, ArtifactTypeSqlHandler.class);
      handleMap.put(CriteriaArtifactTxComment.class, ArtifactTxCommentSqlHandler.class);
      handleMap.put(CriteriaRelatedTo.class, RelatedToSqlHandler.class);
      handleMap.put(CriteriaRelatedRecursive.class, RelatedRecursiveHandler.class);
      handleMap.put(CriteriaRelationTypeExists.class, RelationTypeExistsSqlHandler.class);
      handleMap.put(CriteriaRelationTypeSideExists.class, RelationTypeSideExistsSqlHandler.class);
      handleMap.put(CriteriaRelationTypeNotExists.class, RelationTypeNotExistsSqlHandler.class);
      handleMap.put(CriteriaRelationTypeSideNotExists.class, RelationTypeSideNotExistsSqlHandler.class);
      handleMap.put(CriteriaAttributeTypeExists.class, AttributeTypeExistsSqlHandler.class);
      handleMap.put(CriteriaAttributeTypeNotExists.class, AttributeTypeNotExistsSqlHandler.class);
      handleMap.put(CriteriaAttributeRaw.class, AttributeRawSqlHandler.class);
      handleMap.put(CriteriaAttributeKeywords.class, AttributeTokenSqlHandler.class);
      handleMap.put(CriteriaRelationTypeFollow.class, RelationTypeFollowSqlHandler.class);
      handleMap.put(CriteriaPagination.class, PaginationSqlHandler.class);
   }

   private static void addBranchHandlers(Map<Class<? extends Criteria>, Class<? extends SqlHandler<?>>> handleMap) {
      handleMap.put(CriteriaBranchArchived.class, BranchArchivedSqlHandler.class);
      handleMap.put(CriteriaBranchIds.class, BranchIdsSqlHandler.class);
      handleMap.put(CriteriaBranchName.class, BranchNameSqlHandler.class);
      handleMap.put(CriteriaBranchState.class, BranchStateSqlHandler.class);
      handleMap.put(CriteriaBranchType.class, BranchTypesSqlHandler.class);
      handleMap.put(CriteriaBranchChildOf.class, BranchChildOfSqlHandler.class);
      handleMap.put(CriteriaBranchAncestorOf.class, BranchAncestorOfSqlHandler.class);
      handleMap.put(CriteriaMergeBranchFor.class, MergeBranchForSqlHandler.class);
      handleMap.put(CriteriaAssociatedArtId.class, AssociatedArtIdSqlHandler.class);
      handleMap.put(CriteriaBranchCategory.class, BranchCategorySqlHandler.class);
      handleMap.put(CriteriaAllBranches.class, AllBranchesSqlHandler.class);
   }

   private static void addTxHandlers(Map<Class<? extends Criteria>, Class<? extends SqlHandler<?>>> handleMap) {
      handleMap.put(CriteriaTxIds.class, TxIdsSqlHandler.class);
      handleMap.put(CriteriaTxBranchIds.class, TxBranchIdsSqlHandler.class);
      handleMap.put(CriteriaTxType.class, TxTypesSqlHandler.class);
      handleMap.put(CriteriaTxComment.class, TxCommentSqlHandler.class);
      handleMap.put(CriteriaAllTxs.class, AllTxsSqlHandler.class);
      handleMap.put(CriteriaTxIdWithOperator.class, TxIdWithOperatorSqlHandler.class);
      handleMap.put(CriteriaTxIdWithTwoOperators.class, TxIdWithTwoOperatorsSqlHandler.class);
      handleMap.put(CriteriaDateWithOperator.class, TxDateWithOperatorSqlHandler.class);
      handleMap.put(CriteriaDateRange.class, TxDateRangeSqlHandler.class);
      handleMap.put(CriteriaAuthorIds.class, TxAuthorIdsSqlHandler.class);
      handleMap.put(CriteriaCommitIds.class, TxCommitArtIdSqlHandler.class);
      handleMap.put(CriteriaTxGetHead.class, TxGetHeadSqlHandler.class);
      handleMap.put(CriteriaTxGetPrior.class, TxGetPriorSqlHandler.class);
   }
}