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
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.AtsProductLineEndpointApi;
import org.eclipse.osee.ats.api.workflow.Attribute;
import org.eclipse.osee.ats.rest.internal.workitem.operations.ActionOperations;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.orcs.OrcsApi;

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
   public List<BranchToken> getBranches(String branchQueryType) {

      List<BranchToken> pleBranchList = new ArrayList<>();
      if (branchQueryType.equals("baseline")) {
         for (Branch branch : orcsApi.getQueryFactory().branchQuery().includeArchived(false).includeDeleted(
            false).andIsOfType(BranchType.BASELINE).andStateIs(BranchState.CREATED,
               BranchState.MODIFIED).getResults().getList()) {
            if (orcsApi.getQueryFactory().fromBranch(branch).andId(CoreArtifactTokens.ProductLineFolder).exists()) {
               pleBranchList.add(branch);
            }
         }
      }
      if (branchQueryType.equals("working")) {

         List<String> arbs = new ArrayList<>();
         for (ArtifactId arbIds : orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andTypeEquals(
            AtsArtifactTypes.ActionableItem).andAttributeIs(AtsAttributeTypes.WorkType, "ARB").asArtifactIds()) {
            arbs.add(arbIds.getIdString());
         }

         for (ArtifactId artifactId : orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).and(
            AtsAttributeTypes.ActionableItemReference, arbs).asArtifactIds()) {
            pleBranchList.addAll(
               orcsApi.getQueryFactory().branchQuery().includeArchived(false).includeDeleted(false).andIsOfType(
                  BranchType.WORKING).andStateIs(BranchState.CREATED, BranchState.MODIFIED).andAssociatedArtId(
                     artifactId).getResults().getList());
         }
      }
      Collections.sort(pleBranchList);

      return pleBranchList;
   }

   @Override
   public XResultData checkPlarbApproval(String id) {
      XResultData rd = new XResultData();
      IAtsWorkItem workItem = atsApi.getQueryService().getWorkItem(id);
      ActionOperations ops = new ActionOperations(null, workItem, atsApi, orcsApi);
      Attribute approval = ops.getActionAttributeValues(AtsAttributeTypes.ProductLineApprovedBy, workItem);
      if (approval.getValues().isEmpty()) {
         rd.error("Working branch has not yet been approved by PLARB");
      }
      return rd;
   }

}