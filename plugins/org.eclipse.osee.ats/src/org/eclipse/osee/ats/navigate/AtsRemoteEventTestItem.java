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

import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Level;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsOpenOption;
import org.eclipse.osee.ats.actions.wizard.NewActionJob;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.field.ChangeTypeColumn;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.DefaultTeamState;
import org.eclipse.osee.ats.util.TransitionOption;
import org.eclipse.osee.ats.world.WorldXNavigateItemAction;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class AtsRemoteEventTestItem extends WorldXNavigateItemAction {

   XResultData resultData;

   public AtsRemoteEventTestItem(XNavigateItem parent) {
      super(parent, "ATS Remote Event Test");
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws OseeCoreException {
      if (AtsUtil.isProductionDb()) {
         AWorkbench.popup("ERROR", "This should not to be run on production DB");
         return;
      }
      MessageDialog dialog =
         new MessageDialog(Displays.getActiveShell(), getName(), null,
            getName() + "\n\nSelect Source or Destination Client", MessageDialog.QUESTION, new String[] {
               "Source Client",
               "Destination Client - Start",
               "Destination Client - End",
               "Cancel"}, 2);
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

   private void runClientTest() throws OseeCoreException {
      String title = getName() + " - Destination Client Test";
      resultData.log("Running " + title);
      NewActionJob job = null;
      job =
         new NewActionJob("tt", "description", ChangeType.Improvement, "1", null, false,
            ActionableItemArtifact.getActionableItems(Arrays.asList("ATS")), null);
      job.setUser(true);
      job.setPriority(Job.LONG);
      job.schedule();
      try {
         job.join();
      } catch (InterruptedException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }

      ActionArtifact actionArt = job.getActionArt();
      resultData.log("Created Action " + actionArt);
      TeamWorkFlowArtifact teamArt = actionArt.getTeamWorkFlowArtifacts().iterator().next();

      // Make current user assignee for convenience to developer
      teamArt.getStateMgr().addAssignee(UserManager.getUser());
      teamArt.persist();

      validateActionAtStart(actionArt);

      // Wait for destination client to start
      if (!MessageDialog.openConfirm(
         Displays.getActiveShell(),
         getName(),
         "Launch \"Destination Client - Start\" test, enter \"" + actionArt.getName().replaceFirst("tt ", "") + "\" and press Ok")) {
         return;
      }

      makeChanges1(teamArt);
      makeChanges2(teamArt);
      makeChanges3(teamArt);
      makeChanges4(teamArt);
      makeChanges5(teamArt);
      makeChanges6(teamArt);
      makeChanges7(teamArt);

      validateActionAtEnd(actionArt);

      // Wait for destination client to end
      if (!MessageDialog.openConfirm(
         Displays.getActiveShell(),
         getName(),
         "Launch \"Destination Client - End\" test, enter \"" + actionArt.getName().replaceFirst("tt ", "") + "\" and press Ok")) {
         return;
      }

      resultData.report(title);
   }

   private void makeChanges7(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Remote Event Test");
      teamArt.transition(DefaultTeamState.Analyze.name(), Collections.singleton(UserManager.getUser()), transaction,
         TransitionOption.Persist);
      teamArt.persist(transaction);
      transaction.execute();
   }

   private void makeChanges6(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      // Make changes and transition
      SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Remote Event Test");
      teamArt.setRelations(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version,
         Collections.singleton(getVersion257()));
      teamArt.setSoleAttributeFromString(AtsAttributeTypes.ValidationRequired, "no");
      teamArt.persist(transaction);
      transaction.execute();
   }

   private void makeChanges5(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      // Make changes and persist
      teamArt.setSoleAttributeFromString(AtsAttributeTypes.ValidationRequired, "yes");
      teamArt.persist();
   }

   private void makeChanges4(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      // Make changes and persist
      SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Remote Event Test");
      teamArt.deleteAttributes(AtsAttributeTypes.ValidationRequired);
      teamArt.deleteAttributes(AtsAttributeTypes.Resolution);
      teamArt.setSoleAttributeFromString(AtsAttributeTypes.Description, "description 4");
      ChangeTypeColumn.setChangeType(teamArt, ChangeType.Support);
      teamArt.setSoleAttributeFromString(AtsAttributeTypes.PriorityTypeAttribute, "3");
      teamArt.setRelations(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version,
         Collections.singleton(getVersion258()));
      teamArt.persist(transaction);
      transaction.execute();
   }

   private void makeChanges3(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      // Make changes and persist
      teamArt.deleteRelation(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version, getVersion256());
      teamArt.addRelation(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version, getVersion257());
      teamArt.setSoleAttributeFromString(AtsAttributeTypes.ValidationRequired, "no");
      teamArt.persist();
   }

   private void makeChanges2(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      // Make changes and persist
      teamArt.setSoleAttributeFromString(AtsAttributeTypes.Description, "description 3");
      teamArt.setSoleAttributeFromString(AtsAttributeTypes.ProposedResolution, "this is resolution");
      teamArt.persist();
   }

   private void makeChanges1(TeamWorkFlowArtifact teamArt) throws OseeCoreException {
      // Make changes and persist
      SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Remote Event Test");
      teamArt.setSoleAttributeFromString(AtsAttributeTypes.Description, "description 2");
      ChangeTypeColumn.setChangeType(teamArt, ChangeType.Problem);
      teamArt.setSoleAttributeFromString(AtsAttributeTypes.PriorityTypeAttribute, "2");
      teamArt.setSoleAttributeFromString(AtsAttributeTypes.ValidationRequired, "yes");
      teamArt.addRelation(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version, getVersion256());
      teamArt.persist(transaction);
      transaction.execute();
   }

   private VersionArtifact getVersion256() throws OseeCoreException {
      return (VersionArtifact) ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.Version, "2.5.6",
         AtsUtil.getAtsBranch());
   }

   private VersionArtifact getVersion257() throws OseeCoreException {
      return (VersionArtifact) ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.Version, "2.5.7",
         AtsUtil.getAtsBranch());
   }

   private VersionArtifact getVersion258() throws OseeCoreException {
      return (VersionArtifact) ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.Version, "2.5.8",
         AtsUtil.getAtsBranch());
   }

   private void validateActionAtStart(ActionArtifact actionArt) throws OseeCoreException {
      resultData.log("\nValidating Start...");
      // Ensure event service is connected
      if (!OseeEventManager.isEventManagerConnected()) {
         resultData.logError("Remote Event Service is not connected");
         return;
      }
      resultData.log("Remote Event Service connected");

      // Validate values
      TeamWorkFlowArtifact teamArt = actionArt.getTeamWorkFlowArtifacts().iterator().next();
      testEquals("Description", "description", teamArt.getSoleAttributeValue(AtsAttributeTypes.Description, null));
      testEquals("Change Type", ChangeType.Improvement, AtsAttributeTypes.getChangeType(teamArt));
      testEquals("Priority", "1", teamArt.getSoleAttributeValue(AtsAttributeTypes.PriorityTypeAttribute, null));
   }

   private void validateActionAtEnd(ActionArtifact actionArt) throws OseeCoreException {
      resultData.log("\nValidating End...");
      // Ensure event service is connected
      if (!OseeEventManager.isEventManagerConnected()) {
         resultData.logError("Remote Event Service is not connected");
         return;
      }
      resultData.log("Remote Event Service connected");

      // Validate values
      TeamWorkFlowArtifact teamArt = actionArt.getTeamWorkFlowArtifacts().iterator().next();
      testEquals("Description", "description 4", teamArt.getSoleAttributeValue(AtsAttributeTypes.Description, null));
      testEquals("Change Type", ChangeType.Support, AtsAttributeTypes.getChangeType(teamArt));
      testEquals("Priority", "3", teamArt.getSoleAttributeValue(AtsAttributeTypes.PriorityTypeAttribute, null));
      testEquals("Validation Required", "false",
         String.valueOf(teamArt.getSoleAttributeValue(AtsAttributeTypes.ValidationRequired, null)));

      VersionArtifact verArt = teamArt.getTargetedForVersion();
      String expectedTargetedVersion;
      if (verArt != null) {
         expectedTargetedVersion = verArt.toString();
      } else {
         expectedTargetedVersion = "not set";
      }
      testEquals("Targeted Version", expectedTargetedVersion, "2.5.7");
      testEquals("State", DefaultTeamState.Analyze.name(), teamArt.getStateMgr().getCurrentStateName());
   }

   private void testEquals(String name, Object expected, Object actual) {
      if (!expected.equals(actual)) {
         resultData.logError(String.format("Error: [%s] - expected [%s] actual[%s]", name, expected, actual));
      } else {
         resultData.log(String.format("Valid: [%s] - expected [%s] actual[%s]", name, expected, actual));
      }
   }

   private void runDestinationTestStart(String ttNum) throws OseeCoreException {
      String title = getName() + " - Destination Client Test - Start";
      String actionTitle = "tt " + ttNum;
      resultData.log("Running " + title);

      ActionArtifact actionArt =
         (ActionArtifact) ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.Action, actionTitle,
            AtsUtil.getAtsBranch());

      if (actionArt == null) {
         resultData.logError(String.format("Couldn't load Action named [%s]", actionTitle));
      } else {
         resultData.log("Loaded Action " + actionArt);
         AtsUtil.openATSAction(actionArt, AtsOpenOption.OpenOneOrPopupSelect);
      }
      validateActionAtStart(actionArt);
      resultData.report(title);
   }

   private void runDestinationTestEnd(String ttNum) throws OseeCoreException {
      String title = getName() + " - Destination Client Test - End";
      String actionTitle = "tt " + ttNum;
      resultData.log("Running " + title);

      ActionArtifact actionArt =
         (ActionArtifact) ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.Action, actionTitle,
            AtsUtil.getAtsBranch());

      if (actionArt == null) {
         resultData.logError(String.format("Couldn't load Action named [%s]", actionTitle));
      } else {
         resultData.log("Loaded Action " + actionArt);
         AtsUtil.openATSAction(actionArt, AtsOpenOption.OpenOneOrPopupSelect);
      }
      validateActionAtEnd(actionArt);
      resultData.report(title);
   }

}
