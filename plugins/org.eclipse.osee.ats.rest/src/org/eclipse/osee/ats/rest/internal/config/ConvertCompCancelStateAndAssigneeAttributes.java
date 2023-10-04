/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.ats.rest.internal.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IAtsDatabaseConversion;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.rest.AtsApiServer;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime;

/**
 * See getDescription() below
 *
 * @author Donald G Dunne
 */
public class ConvertCompCancelStateAndAssigneeAttributes implements IAtsDatabaseConversion {

   private final AtsApiServer atsApiServer;
   private final String TITLE = "Convert Completed and Cancelled State Attrs";

   public ConvertCompCancelStateAndAssigneeAttributes(AtsApiServer atsApiServer) {
      this.atsApiServer = atsApiServer;
   }

   @Override
   public void run(XResultData rd, boolean reportOnly, AtsApi atsApi) {
      ElapsedTime time = new ElapsedTime(TITLE + " - Loading", false);
      Set<ArtifactId> uniqueArtIds = new HashSet<>();
      uniqueArtIds.addAll(atsApiServer.getOrcsApi() //
         .getQueryFactory() //
         .fromBranch(atsApi.getAtsBranch()) //
         .andIsOfType(AtsArtifactTypes.AbstractWorkflowArtifact) //
         .and(AtsAttributeTypes.CurrentStateType, Arrays.asList(StateType.Cancelled.name())) //
         .andNotExists(AtsAttributeTypes.CurrentStateName, "Cancelled") //
         .asArtifactIds());
      List<ArtifactId> artIds = new ArrayList<>();
      artIds.addAll(uniqueArtIds);
      List<Collection<ArtifactId>> artIdLists = Collections.subDivide(artIds, 500);
      time.end();

      int x = 1;
      for (Collection<ArtifactId> artIdList : artIdLists) {
         convert(rd, reportOnly, atsApi, artIdList, x++, artIdLists.size());
         // sleep so don't overload database
         try {
            Thread.sleep(1000);
         } catch (InterruptedException ex) {
            // dono
         }
      }
   }

   private void convert(XResultData rd, boolean reportOnly, AtsApi atsApi, Collection<ArtifactId> artIdList, int x,
      int size) {
      IAtsChangeSet changes = null;
      System.err.println(String.format("Processing art set %s/%s", x, size));
      rd.logf("Processing art set %s/%s\n\n", x, size);
      if (!reportOnly) {
         AtsUser systemUser = atsApi.getUserService().getUserById(AtsCoreUsers.SYSTEM_USER);
         changes = atsApi.createChangeSet(TITLE, systemUser);
      }
      Collection<ArtifactToken> allArtifacts = atsApi.getQueryService().getArtifacts(artIdList, atsApi.getAtsBranch());
      for (ArtifactToken artifact : allArtifacts) {
         IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItem(artifact);
         String currStateFull =
            atsApi.getAttributeResolver().getSoleAttributeValueAsString(workItem, AtsAttributeTypes.CurrentState, "");
         String currStateName = currStateFull.replaceFirst(";.*$", "");

         if (workItem.isCompletedOrCancelled()) {
            String currStateNameAttrVal = workItem.getCurrentStateName();
            if (!currStateName.equals(currStateNameAttrVal)) {
               rd.logf("--- Set CurrentStateName to [%s] for %s\n", currStateName, workItem.toStringWithId());
               if (!reportOnly) {
                  changes.setSoleAttributeValue(workItem, AtsAttributeTypes.CurrentStateName, currStateName);
               }
            }

            Collection<String> assignees = atsApi.getAttributeResolver().getAttributesToStringList(workItem,
               AtsAttributeTypes.CurrentStateAssignee);
            if (!assignees.isEmpty()) {
               rd.logf("--- Delete Assignees [%s] for %s\n\n", assignees, workItem.toStringWithId());
               if (!reportOnly) {
                  changes.deleteAttributes(workItem, AtsAttributeTypes.CurrentStateAssignee);
               }
            }
         }
      }
      if (!reportOnly && !changes.isEmpty()) {
         TransactionToken tx = changes.executeIfNeeded();
         rd.logf("Transaction %s\n", tx.getIdString());
      }
      rd.log("Complete");
   }

   @Override
   public String getDescription() {
      StringBuffer data = new StringBuffer();
      data.append(
         "Updates ATS completed/cancelled workflows to set ats.Current State Name and ats.Current State Assignee(required)\n\n");
      data.append("NOTE: Will need to bump server memory to 8GB.\n\n");
      data.append("Necessary for upgrade from 0.26.11\n\n");
      data.append("This will create new attrs as needed.\n" //
         + "Can be run multiple times without corruption.\n" //
         + "Should be run periodically on 0.26.11\n");
      return data.toString();
   }

   @Override
   public String getName() {
      return "Convert Comp Cancel StateAndAssigneeAttributes";
   }
}