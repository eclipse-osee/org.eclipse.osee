/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.ats.rest.internal.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.AtsProductLineEndpointApi;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.Attribute;
import org.eclipse.osee.ats.rest.internal.workitem.operations.ActionOperations;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchCategoryToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchSelected;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreBranchCategoryTokens;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.search.BranchQuery;

/**
 * @author Audrey Denk
 */
public final class AtsProductLineEndpointImpl implements AtsProductLineEndpointApi {

   private final OrcsApi orcsApi;
   private final AtsApi atsApi;

   public AtsProductLineEndpointImpl(AtsApi atsApi, OrcsApi orcsApi) {
      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
   }

   @Override
   public List<BranchToken> getBranches(BranchType type, String workType, BranchCategoryToken category,
      BranchCategoryToken excludeCategory, String filter, long pageNum, long pageSize) {
      BranchQuery query = getBranchQuery(type, workType, category, excludeCategory, filter, pageNum, pageSize);
      return query.getResultsAsId().getList();
   }

   private BranchQuery getBranchQuery(BranchType type, String workType, BranchCategoryToken category,
      BranchCategoryToken excludeCategory, String filter, long pageNum, long pageSize) {
      List<Pair<ArtifactTypeToken, AttributeTypeToken>> artAttrPairs = new ArrayList<>();
      if (!WorkType.valueOfOrNone(workType).equals(WorkType.None)) { //check for valid workType
         artAttrPairs.add(new Pair<ArtifactTypeToken, AttributeTypeToken>(AtsArtifactTypes.TeamDefinition,
            AtsAttributeTypes.WorkType));
         artAttrPairs.add(new Pair<ArtifactTypeToken, AttributeTypeToken>(AtsArtifactTypes.TeamWorkflow,
            AtsAttributeTypes.TeamDefinitionReference));
      }
      BranchQuery query = orcsApi.getQueryFactory().branchQuery();
      if (category.isValid()) {
         query = query.andIsOfCategory(category);
      }
      if (excludeCategory.isValid()) {
         query = query.andIsNotOfCategory(excludeCategory);
      }
      query = query.includeArchived(false).includeDeleted(false);
      query = type.getId() > -1 ? query.andIsOfType(type) : query; //BranchType.WORKING = 0 :(
      query = query.orderByName();
      query = !filter.equals("") ? query.andNameLike(filter) : query;
      query = pageNum > 0L && pageSize > 0L ? query.isOnPage(pageNum, pageSize) : query;
      query = query.andStateIs(BranchState.MODIFIED, BranchState.CREATED);
      query = type.equals(BranchType.WORKING) && !WorkType.valueOfOrNone(workType).equals(
         WorkType.None) ? query.mapAssocArtIdToRelatedAttributes(workType, CoreBranches.COMMON, artAttrPairs) : query;
      return query;
   }

   private List<Branch> getBranchesWithDetails(BranchType type, String workType, BranchCategoryToken category,
      String filter, long pageNum, long pageSize) {
      List<Pair<ArtifactTypeToken, AttributeTypeToken>> artAttrPairs = new ArrayList<>();
      if (!WorkType.valueOfOrNone(workType).equals(WorkType.None)) { //check for valid workType
         artAttrPairs.add(new Pair<ArtifactTypeToken, AttributeTypeToken>(AtsArtifactTypes.TeamDefinition,
            AtsAttributeTypes.WorkType));
         artAttrPairs.add(new Pair<ArtifactTypeToken, AttributeTypeToken>(AtsArtifactTypes.TeamWorkflow,
            AtsAttributeTypes.TeamDefinitionReference));
      }
      BranchQuery query = orcsApi.getQueryFactory().branchQuery();
      query = category.isValid() ? query.andIsOfCategory(category) : query;
      query = query.andIsNotOfCategory(CoreBranchCategoryTokens.PR);
      query = query.includeArchived(false).includeDeleted(false);
      query = type.getId() > -1 ? query.andIsOfType(type) : query;
      query = query.orderByName();
      query = !filter.equals("") ? query.andNameLike(filter) : query;
      query = pageNum > 0L && pageSize > 0L ? query.isOnPage(pageNum, pageSize) : query;
      query = query.andStateIs(BranchState.MODIFIED, BranchState.CREATED);
      query = type.equals(BranchType.WORKING) && !WorkType.valueOfOrNone(workType).equals(
         WorkType.None) ? query.mapAssocArtIdToRelatedAttributes(workType, CoreBranches.COMMON, artAttrPairs) : query;
      return query.getResults().getList();

   }

   @Override
   public List<BranchSelected> getPeerReviewWorkingBranchListAll(BranchType type, String workType,
      BranchCategoryToken category, BranchId peerReviewBranchId, String filter, long pageNum, long pageSize) {
      List<BranchSelected> peerReviewBranchList = new ArrayList<>();
      List<TransactionReadable> txs = new ArrayList<>();
      Branch prBranch =
         orcsApi.getQueryFactory().branchQuery().andId(peerReviewBranchId).getResults().getAtMostOneOrDefault(
            Branch.SENTINEL);

      if (prBranch.isValid()) {
         txs = orcsApi.getQueryFactory().transactionQuery().andBranch(peerReviewBranchId).getResults().getList();
      }
      List<ArtifactId> commitArtIds = txs.stream().filter(
         a -> a.getCommitArt().getId() > 0 && a.getCommitArt().getId() > prBranch.getBaselineTx().getId()).map(
            b -> b.getCommitArt()).collect(Collectors.toList());
      for (Branch branch : getBranchesWithDetails(type, workType, category, filter, pageNum, pageSize)) {
         if (commitArtIds.contains(branch.getAssociatedArtifact())) {
            peerReviewBranchList.add(new BranchSelected(branch, true));
         } else {
            if (branch.getBranchState().isModified()) {
               peerReviewBranchList.add(new BranchSelected(branch, false));
            }
         }
      }
      if (commitArtIds.size() > 0) {
         List<Branch> committedBranches =
            orcsApi.getQueryFactory().branchQuery().andAssociatedArtIds(commitArtIds).getResults().getList();
         for (Branch committedBranch : committedBranches) {
            if (committedBranch.isValid() && peerReviewBranchList.stream().noneMatch(
               branch -> branch.getBranch().equals(committedBranch))) {
               peerReviewBranchList.add(new BranchSelected(committedBranch, true, true));
            }
         }
      }

      peerReviewBranchList.sort(new Comparator<BranchSelected>() {
         @Override
         public int compare(BranchSelected o1, BranchSelected o2) {
            Integer tw1 = -1;
            Integer tw2 = -1;
            if (o1.getBranch().getName().startsWith("TW")) {
               tw1 = Integer.parseInt(o1.getBranch().getName().split(" ")[0].replace("TW", ""));
            }
            if (o2.getBranch().getName().startsWith("TW")) {
               tw2 = Integer.parseInt(o2.getBranch().getName().split(" ")[0].replace("TW", ""));
            }
            if (tw1 > -1 && tw2 > -1) {
               return tw1.compareTo(tw2);
            }
            return o1.getBranch().getName().compareTo(o2.getBranch().getName());
         }
      });
      return peerReviewBranchList;
   }

   @Override
   public XResultData checkPlarbApproval(String id) {
      XResultData rd = new XResultData();
      IAtsWorkItem workItem = atsApi.getQueryService().getWorkItem(id);
      ActionOperations ops = new ActionOperations(workItem, atsApi, orcsApi);
      Attribute approval = ops.getActionAttributeValues(AtsAttributeTypes.ProductLineApprovedBy, workItem);
      if (approval.getValues().isEmpty()) {
         rd.error("Working branch has not yet been approved by PLARB");
      }
      return rd;
   }

   @Override
   public XResultData setPlarbApproval(String id) {
      XResultData rd = new XResultData();
      IAtsWorkItem workItem = atsApi.getQueryService().getWorkItem(id);
      IAtsChangeSet changes = atsApi.createChangeSet("Set Plarb approval user");
      Date resultDate = new Date(System.currentTimeMillis());

      UserId account = orcsApi.userService().getUser();
      if (account.isInvalid()) {
         rd.error("Account Id not passed properly.  See Admin for help.");
      }
      changes.setSoleAttributeValue(workItem, AtsAttributeTypes.ProductLineApprovedBy, account.getIdString());
      changes.setSoleAttributeValue(workItem, AtsAttributeTypes.ProductLineApprovedDate, resultDate);
      changes.execute();
      return rd;
   }

   @Override
   public int getBranchCount(BranchType type, String workType, BranchCategoryToken category,
      BranchCategoryToken excludeCategory, String filter) {
      BranchQuery query = getBranchQuery(type, workType, category, excludeCategory, filter, 0, 0);
      return query.getCount();
   }

}