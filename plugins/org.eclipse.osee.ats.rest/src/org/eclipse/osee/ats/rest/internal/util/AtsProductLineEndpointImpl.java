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
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.AtsProductLineEndpointApi;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.Attribute;
import org.eclipse.osee.ats.rest.internal.workitem.operations.ActionOperations;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchCategoryToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.orcs.OrcsApi;
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
   public List<BranchToken> getBranches(BranchType type, String workType, BranchCategoryToken category, String filter,
      long pageNum, long pageSize) {
      List<Pair<ArtifactTypeToken, AttributeTypeToken>> artAttrPairs = new ArrayList<>();
      if (!WorkType.valueOfOrNone(workType).equals(WorkType.None)) { //check for valid workType
         artAttrPairs.add(new Pair<ArtifactTypeToken, AttributeTypeToken>(AtsArtifactTypes.TeamDefinition,
            AtsAttributeTypes.WorkType));
         artAttrPairs.add(new Pair<ArtifactTypeToken, AttributeTypeToken>(AtsArtifactTypes.TeamWorkflow,
            AtsAttributeTypes.TeamDefinitionReference));
      }
      BranchQuery query = orcsApi.getQueryFactory().branchQuery();
      query = category.isValid() ? query.andIsOfCategory(category) : query;
      query = query.includeArchived(false).includeDeleted(false);
      query = type.getId() > -1 ? query.andIsOfType(type) : query; //BranchType.WORKING = 0 :(
      query = query.orderByName();
      query = !filter.equals("") ? query.andNameLike(filter) : query;
      query = pageNum > 0L && pageSize > 0L ? query.isOnPage(pageNum, pageSize) : query;
      query = query.andStateIs(BranchState.MODIFIED, BranchState.CREATED);
      query = type.equals(BranchType.WORKING) && !WorkType.valueOfOrNone(workType).equals(
         WorkType.None) ? query.mapAssocArtIdToRelatedAttributes(workType, CoreBranches.COMMON, artAttrPairs) : query;
      return query.getResultsAsId().getList();

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
   public int getBranchCount(BranchType type, String workType, BranchCategoryToken category, String filter) {
      List<Pair<ArtifactTypeToken, AttributeTypeToken>> artAttrPairs = new ArrayList<>();
      if (!WorkType.valueOfOrNone(workType).equals(WorkType.None)) { //check for valid workType
         artAttrPairs.add(new Pair<ArtifactTypeToken, AttributeTypeToken>(AtsArtifactTypes.TeamDefinition,
            AtsAttributeTypes.WorkType));
         artAttrPairs.add(new Pair<ArtifactTypeToken, AttributeTypeToken>(AtsArtifactTypes.TeamWorkflow,
            AtsAttributeTypes.TeamDefinitionReference));
      }
      BranchQuery query = orcsApi.getQueryFactory().branchQuery();
      query = category.isValid() ? query.andIsOfCategory(category) : query;
      query = query.includeArchived(false).includeDeleted(false);
      query = type.getId() > -1 ? query.andIsOfType(type) : query; //BranchType.WORKING = 0 :(
      query = type.equals(BranchType.WORKING) && artAttrPairs.size() > 0 ? query.andStateIs(BranchState.MODIFIED,
         BranchState.CREATED) : query;
      query = query.orderByName();
      query = !filter.equals("") ? query.andNameLike(filter) : query;
      query = type.equals(BranchType.WORKING) && !WorkType.valueOfOrNone(workType).equals(
         WorkType.None) ? query.mapAssocArtIdToRelatedAttributes(workType, CoreBranches.COMMON, artAttrPairs) : query;
      return query.getCount();
   }
}