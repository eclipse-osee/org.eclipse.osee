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
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IAtsDatabaseConversion;
import org.eclipse.osee.ats.api.util.IAtsOperationCache;
import org.eclipse.osee.ats.api.util.health.HealthCheckResults;
import org.eclipse.osee.ats.rest.internal.AtsApiServerImpl;
import org.eclipse.osee.ats.rest.internal.util.AtsOperationCache;
import org.eclipse.osee.ats.rest.internal.util.health.AtsHealthCheckOperation.TestCurrentState;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * See getDescription() below
 *
 * @author Donald G Dunne
 */
public class ConvertToStateAndAssigneeAttributes implements IAtsDatabaseConversion {

   private final AtsApiServerImpl atsApiServer;
   private final boolean debug = false;
   private final String TITLE = "Convert Current State Attrs";

   public ConvertToStateAndAssigneeAttributes(AtsApiServerImpl atsApiServer) {
      this.atsApiServer = atsApiServer;
   }

   @Override
   public void run(XResultData rd, boolean reportOnly, AtsApi atsApi) {
      rd.errorf("TODO: This convert should not be used without fix/update");
      if (true) {
         return;
      }
      OrcsApi orcsApi = atsApiServer.getOrcsApi();
      ElapsedTime time = new ElapsedTime(TITLE + " - Loading", true);
      List<ArtifactId> artIds =
         atsApiServer.getOrcsApi().getQueryFactory().fromBranch(atsApi.getAtsBranch()).andIsOfType(
            AtsArtifactTypes.AbstractWorkflowArtifact).andNotExists(AtsAttributeTypes.CurrentStateName).asArtifactIds();
      List<Collection<ArtifactId>> artIdLists = Collections.subDivide(artIds, 1000);
      time.end();

      TestCurrentState testCurrentState = new TestCurrentState();
      TestAssignees testAssignees = new TestAssignees();
      AtsOperationCache cache = new AtsOperationCache(atsApi, debug);

      HealthCheckResults checks = new HealthCheckResults();
      IAtsChangeSet changes = null;
      int x = 1;
      for (Collection<ArtifactId> artIdList : artIdLists) {
         // Wait a minute to keep from overloading the database
         try {
            Thread.sleep(60000);
         } catch (InterruptedException ex) {
            // Do nothing
         }
         OseeLog.log(ConvertToStateAndAssigneeAttributes.class, Level.INFO,
            String.format("Processing art set %s/%s", x++, artIdLists.size()));
         if (!reportOnly) {
            AtsUser systemUser = atsApi.getUserService().getUserById(AtsCoreUsers.SYSTEM_USER);
            changes = atsApi.createChangeSet(TITLE, systemUser);
         }
         Collection<ArtifactToken> allArtifacts =
            atsApi.getQueryService().getArtifacts(artIdList, atsApi.getAtsBranch());
         for (ArtifactToken artifact : allArtifacts) {
            IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItem(artifact);
            testCurrentState.check(artifact, workItem, checks, atsApi, changes, cache);
            testAssignees.check(artifact, workItem, checks, atsApi, changes, cache);
         }
         if (!reportOnly && !changes.isEmpty()) {
            TransactionToken tx = changes.execute();
            rd.logf("Transaction %s", tx.getIdString());
         }
      }
      rd.log("Complete");
   }

   // This must be fixed to extract user ids from currentState before using
   private static class TestAssignees {

      public boolean check(ArtifactToken artifact, IAtsWorkItem workItem, HealthCheckResults results, AtsApi atsApi,
         IAtsChangeSet changes, IAtsOperationCache cache) {
         String currentStateValue =
            atsApi.getAttributeResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.CurrentState, "unknown");
         List<Long> userArtIds = new ArrayList<>();
         for (AtsUser user : workItem.getStateMgr().getAssignees(currentStateValue)) {
            userArtIds.add(user.getArtifactId().getId());
         }
         List<Long> currUserArtIds = new ArrayList<>();
         for (String id : atsApi.getAttributeResolver().getAttributesToStringList(workItem,
            AtsAttributeTypes.CurrentStateAssignee)) {
            if (Strings.isNumeric(id)) {
               currUserArtIds.add(Long.valueOf(id));
            } else {
               results.log(artifact, "TestAssignees",
                  String.format("Error: Invalid Current State Assigne [%s] for %s", id, workItem.toStringWithId()));
            }
         }
         if (Collections.setComplement(currUserArtIds, userArtIds).size() > 0 || Collections.setComplement(userArtIds,
            currUserArtIds).size() > 0) {
            results.log(artifact, "TestAssignees", String.format(
               "Info: Updated Current State Assignees attr to [%s] for %s", userArtIds, workItem.toStringWithId()));
            if (changes != null) {
               changes.setAttributeValues(workItem, AtsAttributeTypes.CurrentStateAssignee,
                  Collections.castAll(userArtIds));
            }
         }
         return true;
      }
   }
   @Override
   public String getDescription() {
      StringBuffer data = new StringBuffer();
      data.append("Updates ATS workflows to new ats.Current State Name and ats.Assignee(required)\n\n");
      data.append("Necessary for upgrade from 0.26.9\n\n");
      data.append("This will create new attrs as needed.\n" //
         + "Can be run multiple times without corruption.\n" //
         + "Should be run periodically on 0.26.9\n");
      return data.toString();
   }

   @Override
   public String getName() {
      return "Convert to ats.Current State Name and Assignees";
   }
}