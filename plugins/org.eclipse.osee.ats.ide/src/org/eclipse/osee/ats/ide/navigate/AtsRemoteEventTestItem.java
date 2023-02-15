/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.navigate;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.column.ChangeTypeColumn;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelper;
import org.eclipse.osee.ats.ide.AtsOpenOption;
import org.eclipse.osee.ats.ide.actions.wizard.NewActionJob;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsEditors;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.ide.world.WorldXNavigateItemAction;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * Test that runs off a Demo DBInit database via AtsClient_Integration_Test. ActiveMQ must be running and two clients
 * launched with that active mq configured.
 *
 * @author Donald G. Dunne
 */
public class AtsRemoteEventTestItem extends WorldXNavigateItemAction {

   XResultData resultData;
   ArtifactToken SAW_Bld_1 = ArtifactToken.valueOf(2749182, "SAW_Bld_1", COMMON, AtsArtifactTypes.Version);
   ArtifactToken SAW_Bld_2 = ArtifactToken.valueOf(7632957, "SAW_Bld_2", COMMON, AtsArtifactTypes.Version);
   ArtifactToken SAW_Bld_3 = ArtifactToken.valueOf(577781, "SAW_Bld_3", COMMON, AtsArtifactTypes.Version);

   public AtsRemoteEventTestItem() {
      super("ATS Remote Event Test", XNavigateItem.TOP_ADMIN);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      if (AtsApiService.get().getStoreService().isProductionDb()) {
         AWorkbench.popup("ERROR", "This should not to be run on production DB");
         return;
      }
      MessageDialog dialog = new MessageDialog(Displays.getActiveShell(), getName(), null,
         getName() + "\n\nSelect Source or Destination Client", MessageDialog.QUESTION,
         new String[] {"Source Client", "Destination Client - Start", "Destination Client - End", "Cancel"}, 2);
      int result = dialog.open();
      resultData = new XResultData();
      if (result == 0) {
         runClientTest();
      } else if (result == 1) {
         EntryDialog diag = new EntryDialog(getName(), "Enter tt number of Source Client created Action");
         if (diag.open() == 0) {
            runDestinationTestStart(diag.getEntry());
         }
      } else if (result == 2) {
         EntryDialog diag = new EntryDialog(getName(), "Enter tt number of Source Client created Action");
         if (diag.open() == 0) {
            runDestinationTestEnd(diag.getEntry());
         }
      }
   }

   private static Set<IAtsActionableItem> getActionableItems() {
      Set<IAtsActionableItem> aias = new HashSet<>();
      IAtsActionableItem sawCodeAi = (IAtsActionableItem) AtsApiService.get().getQueryService().createQuery(
         AtsArtifactTypes.ActionableItem).andName("SAW Code").getConfigObjectResultSet().getAtMostOneOrDefault(
            IAtsActionableItem.SENTINEL);
      Conditions.assertNotSentinel(sawCodeAi, "SAW Code AI; DBInit should be Demo DbInit");
      aias.add(sawCodeAi);
      return aias;
   }

   private void runClientTest() {
      String title = getName() + " - Destination Client Test";
      resultData.log("Running " + title);
      NewActionJob job = null;
      job =
         new NewActionJob("tt", "description", ChangeTypes.Improvement, "1", null, false, getActionableItems(), null);
      job.setUser(true);
      job.setPriority(Job.LONG);
      job.schedule();
      try {
         job.join();
      } catch (InterruptedException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      IAtsAction action = job.getResults().getAction();
      resultData.log("Created Action " + action.toStringWithId());
      IAtsTeamWorkflow teamWf = (TeamWorkFlowArtifact) job.getResults().getTeamWfs().iterator().next().getStoreObject();

      // Make current user assignee for convenience to developer
      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName() + " - set assignee");
      teamWf.getStateMgr().addAssignee(AtsApiService.get().getUserService().getCurrentUser());
      changes.add(teamWf);
      changes.execute();

      validateActionAtStart(teamWf);

      // Wait for destination client to start
      if (!MessageDialog.openConfirm(Displays.getActiveShell(), getName(),
         "Launch \"Destination Client - Start\" test, enter \"" + action.getName().replaceFirst("tt ",
            "") + "\" and press Ok")) {
         return;
      }

      int sleepTime = 250;
      makeChanges1(teamWf);
      sleep(sleepTime);
      makeChanges2(teamWf);
      sleep(sleepTime);
      makeChanges3(teamWf);
      sleep(sleepTime);
      makeChanges4(teamWf);
      sleep(sleepTime);
      makeChanges5(teamWf);
      sleep(sleepTime);
      makeChanges6(teamWf);
      sleep(sleepTime);
      makeChanges7(teamWf);
      sleep(sleepTime);

      validateActionAtEnd(teamWf);

      // Wait for destination client to end
      if (!MessageDialog.openConfirm(Displays.getActiveShell(), getName(),
         "Launch \"Destination Client - End\" test, enter \"" + action.getName().replaceFirst("tt ",
            "") + "\" and press Ok")) {
         return;
      }

      XResultDataUI.report(resultData, title);
   }

   public static void sleep(long milliseconds) {
      try {
         System.out.println("Sleeping " + milliseconds);
         Thread.sleep(milliseconds);
         System.out.println("Awake");
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private void makeChanges7(IAtsTeamWorkflow teamWf) {
      TransitionHelper helper = new TransitionHelper("Remote Event Test", Arrays.asList(teamWf),
         TeamState.Analyze.getName(), Collections.singleton(AtsApiService.get().getUserService().getCurrentUser()),
         null, null, AtsApiService.get(), TransitionOption.None);
      TransitionResults results = AtsApiService.get().getWorkItemServiceIde().transition(helper);
      if (!results.isEmpty()) {
         throw new OseeStateException(results.toString());
      }
   }

   private void makeChanges6(IAtsTeamWorkflow teamWf) {
      // Make changes and transition
      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName() + " Changes6");
      AtsApiService.get().getVersionService().setTargetedVersion(teamWf, getSawBld2(), changes);
      changes.setSoleAttributeFromString(teamWf, AtsAttributeTypes.ValidationRequired, "false");
      changes.execute();
   }

   private void makeChanges5(IAtsTeamWorkflow teamWf) {
      // Make changes and persist
      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName() + " Changes5");
      changes.setSoleAttributeFromString(teamWf, AtsAttributeTypes.ValidationRequired, "true");
      changes.execute();
   }

   private void makeChanges4(IAtsTeamWorkflow teamWf) {
      // Make changes and persist
      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName() + " Changes4");
      changes.deleteAttributes(teamWf, AtsAttributeTypes.ValidationRequired);
      changes.deleteAttributes(teamWf, AtsAttributeTypes.Resolution);
      changes.setSoleAttributeFromString(teamWf, AtsAttributeTypes.Description, "description 4");
      ChangeTypeColumn.setChangeType(teamWf, ChangeTypes.Support, changes);
      changes.setSoleAttributeFromString(teamWf, AtsAttributeTypes.Priority, "3");
      AtsApiService.get().getVersionService().setTargetedVersion(teamWf, getSawBld3(), changes);
      changes.execute();
   }

   private void makeChanges3(IAtsTeamWorkflow teamWf) {
      // Make changes and persist
      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName() + " Changes3");
      AtsApiService.get().getVersionService().setTargetedVersion(teamWf, getSawBld2(), changes);
      changes.setSoleAttributeFromString(teamWf, AtsAttributeTypes.ValidationRequired, "false");
      changes.execute();
   }

   private void makeChanges2(IAtsTeamWorkflow teamWf) {
      // Make changes and persist
      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName() + " Changes2");
      changes.setSoleAttributeFromString(teamWf, AtsAttributeTypes.Description, "description 3");
      changes.setSoleAttributeFromString(teamWf, AtsAttributeTypes.ProposedResolution, "this is resolution");
      changes.execute();
   }

   private void makeChanges1(IAtsTeamWorkflow teamWf) {
      // Make changes and persist
      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName() + " Changes1");
      changes.setSoleAttributeFromString(teamWf, AtsAttributeTypes.Description, "description 2");
      changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.ChangeType, ChangeTypes.Problem.name());
      changes.setSoleAttributeFromString(teamWf, AtsAttributeTypes.Priority, "2");
      changes.setSoleAttributeFromString(teamWf, AtsAttributeTypes.ValidationRequired, "true");
      AtsApiService.get().getVersionService().setTargetedVersion(teamWf, getSawBld1(), changes);
      changes.execute();
   }

   private IAtsVersion getSawBld1() {
      return AtsApiService.get().getVersionService().getVersionById(SAW_Bld_1);
   }

   private IAtsVersion getSawBld2() {
      return AtsApiService.get().getVersionService().getVersionById(SAW_Bld_2);
   }

   private IAtsVersion getSawBld3() {
      return AtsApiService.get().getVersionService().getVersionById(SAW_Bld_3);
   }

   private void validateActionAtStart(IAtsTeamWorkflow teamWf) {
      resultData.log("\nValidating Start...");
      // Ensure event accessControlService is connected
      if (!OseeEventManager.isEventManagerConnected()) {
         resultData.error("Remote Event Service is not connected");
         return;
      }
      resultData.log("Remote Event Service connected");

      // Validate values
      testEquals("Description", "description",
         AtsApiService.get().getAttributeResolver().getSoleAttributeValue(teamWf, AtsAttributeTypes.Description, null));
      testEquals("Change Type", ChangeTypes.Improvement, ChangeTypeColumn.getChangeType(teamWf, AtsApiService.get()));
      testEquals("Priority", "1",
         AtsApiService.get().getAttributeResolver().getSoleAttributeValue(teamWf, AtsAttributeTypes.Priority, null));
   }

   private void validateActionAtEnd(IAtsTeamWorkflow teamWf) {
      resultData.log("\nValidating End...");
      // Ensure event accessControlService is connected
      if (!OseeEventManager.isEventManagerConnected()) {
         resultData.error("Remote Event Service is not connected");
         return;
      }
      resultData.log("Remote Event Service connected");

      // Validate values
      testEquals("Description", "description 4",
         AtsApiService.get().getAttributeResolver().getSoleAttributeValue(teamWf, AtsAttributeTypes.Description, null));
      testEquals("Change Type", ChangeTypes.Support, ChangeTypeColumn.getChangeType(teamWf, AtsApiService.get()));
      testEquals("Priority", "3",
         AtsApiService.get().getAttributeResolver().getSoleAttributeValue(teamWf, AtsAttributeTypes.Priority, null));
      testEquals("Validation Required", false, AtsApiService.get().getAttributeResolver().getSoleAttributeValue(teamWf,
         AtsAttributeTypes.ValidationRequired, null));

      IAtsVersion verArt = AtsApiService.get().getVersionService().getTargetedVersion(teamWf);
      String expectedTargetedVersion;
      if (verArt != null) {
         expectedTargetedVersion = verArt.toString();
      } else {
         expectedTargetedVersion = "not set";
      }
      testEquals("Targeted Version", expectedTargetedVersion, "SAW_Bld_2");
      testEquals("State", TeamState.Analyze.getName(), teamWf.getCurrentStateName());
   }

   private void testEquals(String name, Object expected, Object actual) {
      if (!expected.equals(actual)) {
         resultData.error(String.format("Error: [%s] - expected [%s] actual[%s]", name, expected, actual));
      } else {
         resultData.log(String.format("Valid: [%s] - expected [%s] actual[%s]", name, expected, actual));
      }
   }

   private void runDestinationTestStart(String ttNum) {
      String title = getName() + " - Destination Client Test - Start";
      String actionTitle = "tt " + ttNum;
      resultData.log("Running " + title);

      IAtsTeamWorkflow teamWf =
         AtsApiService.get().getQueryService().createQuery(WorkItemType.TeamWorkflow).andName(actionTitle).getItems(
            IAtsTeamWorkflow.class).iterator().next();

      resultData.log("Loaded TeamWf " + teamWf);
      AtsEditors.openATSAction(teamWf.getParentAction().getStoreObject(), AtsOpenOption.OpenOneOrPopupSelect);
      validateActionAtStart(teamWf);
      XResultDataUI.report(resultData, title);
   }

   private void runDestinationTestEnd(String ttNum) {
      String title = getName() + " - Destination Client Test - End";
      String actionTitle = "tt " + ttNum;
      resultData.log("Running " + title);

      IAtsTeamWorkflow teamWf =
         AtsApiService.get().getQueryService().createQuery(WorkItemType.TeamWorkflow).andName(actionTitle).getItems(
            IAtsTeamWorkflow.class).iterator().next();

      resultData.log("Loaded TeamWf " + teamWf);
      AtsEditors.openATSAction(teamWf.getParentAction().getStoreObject(), AtsOpenOption.OpenOneOrPopupSelect);
      validateActionAtEnd(teamWf);
      XResultDataUI.report(resultData, title);
   }

}
