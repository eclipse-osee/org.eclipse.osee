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
package org.eclipse.osee.ats.navigate;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsOpenOption;
import org.eclipse.osee.ats.actions.wizard.NewActionJob;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.transition.IAtsTransitionManager;
import org.eclipse.osee.ats.api.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.client.action.ActionManager;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.ChangeTypeUtil;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.core.workflow.transition.TransitionFactory;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelper;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.world.WorldXNavigateItemAction;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
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

   public AtsRemoteEventTestItem(XNavigateItem parent) {
      super(parent, "ATS Remote Event Test");
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws OseeCoreException {
      if (AtsUtil.isProductionDb()) {
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
      IAtsActionableItem sawCodeAi = (IAtsActionableItem) AtsClientService.get().getQueryService().createQuery(
         AtsArtifactTypes.ActionableItem).andName("SAW Code").getResults().getAtMostOneOrNull();
      Conditions.checkNotNull(sawCodeAi, "SAW Code AI; DBInit should be Demo DbInit");
      aias.add(sawCodeAi);
      return aias;
   }

   private void runClientTest() throws OseeCoreException {
      String title = getName() + " - Destination Client Test";
      resultData.log("Running " + title);
      NewActionJob job = null;
      job = new NewActionJob("tt", "description", ChangeType.Improvement, "1", null, false, getActionableItems(), null,
         null);
      job.setUser(true);
      job.setPriority(Job.LONG);
      job.schedule();
      try {
         job.join();
      } catch (InterruptedException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      Artifact actionArt = job.getActionArt();
      resultData.log("Created Action " + actionArt);
      TeamWorkFlowArtifact teamArt = ActionManager.getFirstTeam(actionArt);

      // Make current user assignee for convenience to developer
      IAtsChangeSet changes = AtsClientService.get().createChangeSet(getClass().getSimpleName() + " - set assignee");
      teamArt.getStateMgr().addAssignee(AtsClientService.get().getUserService().getCurrentUser());
      changes.add(teamArt);
      changes.execute();

      validateActionAtStart(actionArt);

      // Wait for destination client to start
      if (!MessageDialog.openConfirm(Displays.getActiveShell(), getName(),
         "Launch \"Destination Client - Start\" test, enter \"" + actionArt.getName().replaceFirst("tt ",
            "") + "\" and press Ok")) {
         return;
      }

      int sleepTime = 250;
      makeChanges1(teamArt);
      sleep(sleepTime);
      makeChanges2(teamArt);
      sleep(sleepTime);
      makeChanges3(teamArt);
      sleep(sleepTime);
      makeChanges4(teamArt);
      sleep(sleepTime);
      makeChanges5(teamArt);
      sleep(sleepTime);
      makeChanges6(teamArt);
      sleep(sleepTime);
      makeChanges7(teamArt);
      sleep(sleepTime);

      validateActionAtEnd(actionArt);

      // Wait for destination client to end
      if (!MessageDialog.openConfirm(Displays.getActiveShell(), getName(),
         "Launch \"Destination Client - End\" test, enter \"" + actionArt.getName().replaceFirst("tt ",
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

   private void makeChanges7(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      IAtsChangeSet changes = AtsClientService.get().createChangeSet("Remote Event Test");
      TransitionHelper helper = new TransitionHelper("Remote Event Test", Arrays.asList(teamArt),
         TeamState.Analyze.getName(), Collections.singleton(AtsClientService.get().getUserService().getCurrentUser()),
         null, changes, AtsClientService.get().getServices(), TransitionOption.None);
      IAtsTransitionManager transitionMgr = TransitionFactory.getTransitionManager(helper);
      TransitionResults results = transitionMgr.handleAllAndPersist();
      if (!results.isEmpty()) {
         throw new OseeStateException(results.toString());
      }
   }

   private void makeChanges6(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      // Make changes and transition
      AtsClientService.get().getVersionService().setTargetedVersionAndStore(teamArt, getSawBld2());
      teamArt.setSoleAttributeFromString(AtsAttributeTypes.ValidationRequired, "false");
      teamArt.persist("Remote Event Test");
   }

   private void makeChanges5(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      // Make changes and persist
      teamArt.setSoleAttributeFromString(AtsAttributeTypes.ValidationRequired, "true");
      teamArt.persist(getClass().getSimpleName());
   }

   private void makeChanges4(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      // Make changes and persist
      teamArt.deleteAttributes(AtsAttributeTypes.ValidationRequired);
      teamArt.deleteAttributes(AtsAttributeTypes.Resolution);
      teamArt.setSoleAttributeFromString(AtsAttributeTypes.Description, "description 4");
      ChangeTypeUtil.setChangeType(teamArt, ChangeType.Support);
      teamArt.setSoleAttributeFromString(AtsAttributeTypes.PriorityType, "3");
      AtsClientService.get().getVersionService().setTargetedVersionAndStore(teamArt, getSawBld3());
      teamArt.persist("Remote Event Test");
   }

   private void makeChanges3(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      // Make changes and persist
      AtsClientService.get().getVersionService().setTargetedVersionAndStore(teamArt, getSawBld2());
      teamArt.setSoleAttributeFromString(AtsAttributeTypes.ValidationRequired, "false");
      teamArt.persist(getClass().getSimpleName());
   }

   private void makeChanges2(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      // Make changes and persist
      teamArt.setSoleAttributeFromString(AtsAttributeTypes.Description, "description 3");
      teamArt.setSoleAttributeFromString(AtsAttributeTypes.ProposedResolution, "this is resolution");
      teamArt.persist(getClass().getSimpleName());
   }

   private void makeChanges1(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      // Make changes and persist
      teamArt.setSoleAttributeFromString(AtsAttributeTypes.Description, "description 2");
      ChangeTypeUtil.setChangeType(teamArt, ChangeType.Problem);
      teamArt.setSoleAttributeFromString(AtsAttributeTypes.PriorityType, "2");
      teamArt.setSoleAttributeFromString(AtsAttributeTypes.ValidationRequired, "true");
      AtsClientService.get().getVersionService().setTargetedVersionAndStore(teamArt, getSawBld1());
      teamArt.persist("Remote Event Test");
   }

   private IAtsVersion getSawBld1() throws OseeCoreException {
      return AtsClientService.get().getVersionService().getById(SAW_Bld_1);
   }

   private IAtsVersion getSawBld2() throws OseeCoreException {
      return AtsClientService.get().getVersionService().getById(SAW_Bld_2);
   }

   private IAtsVersion getSawBld3() throws OseeCoreException {
      return AtsClientService.get().getVersionService().getById(SAW_Bld_3);
   }

   private void validateActionAtStart(Artifact actionArt) throws OseeCoreException {
      resultData.log("\nValidating Start...");
      // Ensure event service is connected
      if (!OseeEventManager.isEventManagerConnected()) {
         resultData.error("Remote Event Service is not connected");
         return;
      }
      resultData.log("Remote Event Service connected");

      // Validate values
      TeamWorkFlowArtifact teamArt = ActionManager.getFirstTeam(actionArt);
      testEquals("Description", "description", teamArt.getSoleAttributeValue(AtsAttributeTypes.Description, null));
      testEquals("Change Type", ChangeType.Improvement, ChangeTypeUtil.getChangeType(teamArt));
      testEquals("Priority", "1", teamArt.getSoleAttributeValue(AtsAttributeTypes.PriorityType, null));
   }

   private void validateActionAtEnd(Artifact actionArt) throws OseeCoreException {
      resultData.log("\nValidating End...");
      // Ensure event service is connected
      if (!OseeEventManager.isEventManagerConnected()) {
         resultData.error("Remote Event Service is not connected");
         return;
      }
      resultData.log("Remote Event Service connected");

      // Validate values
      TeamWorkFlowArtifact teamArt = ActionManager.getFirstTeam(actionArt);
      testEquals("Description", "description 4", teamArt.getSoleAttributeValue(AtsAttributeTypes.Description, null));
      testEquals("Change Type", ChangeType.Support, ChangeTypeUtil.getChangeType(teamArt));
      testEquals("Priority", "3", teamArt.getSoleAttributeValue(AtsAttributeTypes.PriorityType, null));
      testEquals("Validation Required", false,
         teamArt.getSoleAttributeValue(AtsAttributeTypes.ValidationRequired, null));

      IAtsVersion verArt = AtsClientService.get().getVersionService().getTargetedVersion(teamArt);
      String expectedTargetedVersion;
      if (verArt != null) {
         expectedTargetedVersion = verArt.toString();
      } else {
         expectedTargetedVersion = "not set";
      }
      testEquals("Targeted Version", expectedTargetedVersion, "SAW_Bld_2");
      testEquals("State", TeamState.Analyze.getName(), teamArt.getStateMgr().getCurrentStateName());
   }

   private void testEquals(String name, Object expected, Object actual) {
      if (!expected.equals(actual)) {
         resultData.error(String.format("Error: [%s] - expected [%s] actual[%s]", name, expected, actual));
      } else {
         resultData.log(String.format("Valid: [%s] - expected [%s] actual[%s]", name, expected, actual));
      }
   }

   private void runDestinationTestStart(String ttNum) throws OseeCoreException {
      String title = getName() + " - Destination Client Test - Start";
      String actionTitle = "tt " + ttNum;
      resultData.log("Running " + title);

      Artifact actionArt =
         ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.Action, actionTitle, AtsUtilCore.getAtsBranch());

      if (actionArt == null) {
         resultData.error(String.format("Couldn't load Action named [%s]", actionTitle));
      } else {
         resultData.log("Loaded Action " + actionArt);
         AtsUtil.openATSAction(actionArt, AtsOpenOption.OpenOneOrPopupSelect);
      }
      validateActionAtStart(actionArt);
      XResultDataUI.report(resultData, title);
   }

   private void runDestinationTestEnd(String ttNum) throws OseeCoreException {
      String title = getName() + " - Destination Client Test - End";
      String actionTitle = "tt " + ttNum;
      resultData.log("Running " + title);

      Artifact actionArt =
         ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.Action, actionTitle, AtsUtilCore.getAtsBranch());

      if (actionArt == null) {
         resultData.error(String.format("Couldn't load Action named [%s]", actionTitle));
      } else {
         resultData.log("Loaded Action " + actionArt);
         AtsUtil.openATSAction(actionArt, AtsOpenOption.OpenOneOrPopupSelect);
      }
      validateActionAtEnd(actionArt);
      XResultDataUI.report(resultData, title);
   }

}
