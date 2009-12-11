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
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.actions.wizard.NewActionJob;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact.DefaultTeamState;
import org.eclipse.osee.ats.editor.SMAManager.TransitionOption;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.AtsPriority.PriorityType;
import org.eclipse.osee.ats.world.WorldXNavigateItemAction;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.RemoteEventManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.ats.AtsOpenOption;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class AtsRemoteEventTestItem extends WorldXNavigateItemAction {

   XResultData resultData;

   /**
    * @param parent
    * @throws OseeArgumentException
    */
   public AtsRemoteEventTestItem(XNavigateItem parent) throws OseeArgumentException {
      super(parent, "ATS Remote Event Test");
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws OseeCoreException {
      if (AtsUtil.isProductionDb()) {
         AWorkbench.popup("ERROR", "This should not to be run on production DB");
         return;
      }
      MessageDialog dialog =
            new MessageDialog(Display.getCurrent().getActiveShell(), getName(), null,
                  getName() + "\n\nSelect Source or Destination Client", MessageDialog.QUESTION, new String[] {
                        "Source Client", "Destination Client - Start", "Destination Client - End", "Cancel"}, 2);
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
            new NewActionJob("tt", "description", ChangeType.Improvement, PriorityType.Priority_1, null, false,
                  Arrays.asList("Other"), ActionableItemArtifact.getActionableItems(Arrays.asList("ATS")), null);
      job.setUser(true);
      job.setPriority(Job.LONG);
      job.schedule();
      try {
         job.join();
      } catch (InterruptedException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
      }

      ActionArtifact actionArt = job.getActionArt();
      resultData.log("Created Action " + actionArt);
      TeamWorkFlowArtifact teamArt = actionArt.getTeamWorkFlowArtifacts().iterator().next();

      // Make current user assignee for convenience to developer
      teamArt.getSmaMgr().getStateMgr().addAssignee(UserManager.getUser());
      teamArt.persist();

      validateActionAtStart(actionArt);

      // Wait for destination client to start
      if (!MessageDialog.openConfirm(
            Display.getCurrent().getActiveShell(),
            getName(),
            "Launch \"Destination Client - Start\" test, enter \"" + actionArt.getName().replaceFirst("tt ", "") + "\" and press Ok")) {
         return;
      }

      // Make changes and persist
      SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Remote Event Test");
      teamArt.setSoleAttributeFromString(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), "description 2");
      teamArt.setSoleAttributeFromString(ATSAttributes.CHANGE_TYPE_ATTRIBUTE.getStoreName(), ChangeType.Problem.name());
      teamArt.setSoleAttributeFromString(ATSAttributes.PRIORITY_TYPE_ATTRIBUTE.getStoreName(),
            PriorityType.Priority_2.getShortName());
      teamArt.setSoleAttributeFromString(ATSAttributes.VALIDATION_REQUIRED_ATTRIBUTE.getStoreName(), "yes");
      teamArt.addRelation(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version, getVersion256());
      teamArt.persist(transaction);
      transaction.execute();

      // Make changes and persist
      teamArt.setSoleAttributeFromString(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), "description 3");
      teamArt.setSoleAttributeFromString(ATSAttributes.PROPOSED_RESOLUTION_ATTRIBUTE.getStoreName(),
            "this is resolution");
      teamArt.persist();

      // Make changes and persist
      teamArt.deleteRelation(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version, getVersion256());
      teamArt.addRelation(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version, getVersion257());
      teamArt.setSoleAttributeFromString(ATSAttributes.VALIDATION_REQUIRED_ATTRIBUTE.getStoreName(), "no");
      teamArt.persist();

      // Make changes and persist
      transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Remote Event Test");
      teamArt.deleteAttributes(ATSAttributes.VALIDATION_REQUIRED_ATTRIBUTE.getStoreName());
      teamArt.deleteAttributes(ATSAttributes.RESOLUTION_ATTRIBUTE.getStoreName());
      teamArt.setSoleAttributeFromString(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), "description 4");
      teamArt.setSoleAttributeFromString(ATSAttributes.CHANGE_TYPE_ATTRIBUTE.getStoreName(), ChangeType.Support.name());
      teamArt.setSoleAttributeFromString(ATSAttributes.PRIORITY_TYPE_ATTRIBUTE.getStoreName(),
            PriorityType.Priority_3.getShortName());
      teamArt.setRelations(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version, Collections.singleton(getVersion258()));
      teamArt.persist(transaction);
      transaction.execute();

      // Make changes and persist
      teamArt.setSoleAttributeFromString(ATSAttributes.VALIDATION_REQUIRED_ATTRIBUTE.getStoreName(), "yes");
      teamArt.persist();

      // Make changes and transition
      transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Remote Event Test");
      teamArt.setRelations(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version, Collections.singleton(getVersion257()));
      teamArt.setSoleAttributeFromString(ATSAttributes.VALIDATION_REQUIRED_ATTRIBUTE.getStoreName(), "no");
      teamArt.persist(transaction);
      transaction.execute();

      transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Remote Event Test");
      teamArt.getSmaMgr().transition(DefaultTeamState.Analyze.name(), Collections.singleton(UserManager.getUser()),
            transaction, TransitionOption.Persist);
      teamArt.persist(transaction);
      transaction.execute();

      validateActionAtEnd(actionArt);

      // Wait for destination client to end
      if (!MessageDialog.openConfirm(
            Display.getCurrent().getActiveShell(),
            getName(),
            "Launch \"Destination Client - End\" test, enter \"" + actionArt.getName().replaceFirst("tt ", "") + "\" and press Ok")) {
         return;
      }

      resultData.report(title);
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
      if (!RemoteEventManager.isConnected()) {
         resultData.logError("Remote Event Service is not connected");
         return;
      }
      resultData.log("Remote Event Service connected");

      // Validate values
      TeamWorkFlowArtifact teamArt = actionArt.getTeamWorkFlowArtifacts().iterator().next();
      testEquals("Description", "description", teamArt.getSoleAttributeValue(
            ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), null));
      testEquals("Change Type", ChangeType.Improvement.name(), teamArt.getSoleAttributeValue(
            ATSAttributes.CHANGE_TYPE_ATTRIBUTE.getStoreName(), null));
      testEquals("Priority", PriorityType.Priority_1.getShortName(), teamArt.getSoleAttributeValue(
            ATSAttributes.PRIORITY_TYPE_ATTRIBUTE.getStoreName(), null));
   }

   private void validateActionAtEnd(ActionArtifact actionArt) throws OseeCoreException {
      resultData.log("\nValidating End...");
      // Ensure event service is connected
      if (!RemoteEventManager.isConnected()) {
         resultData.logError("Remote Event Service is not connected");
         return;
      }
      resultData.log("Remote Event Service connected");

      // Validate values
      TeamWorkFlowArtifact teamArt = actionArt.getTeamWorkFlowArtifacts().iterator().next();
      testEquals("Description", "description 4", teamArt.getSoleAttributeValue(
            ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), null));
      testEquals("Change Type", ChangeType.Support.name(), teamArt.getSoleAttributeValue(
            ATSAttributes.CHANGE_TYPE_ATTRIBUTE.getStoreName(), null));
      testEquals("Priority", PriorityType.Priority_3.getShortName(), teamArt.getSoleAttributeValue(
            ATSAttributes.PRIORITY_TYPE_ATTRIBUTE.getStoreName(), null));
      testEquals("Validation Required", "false", String.valueOf(teamArt.getSoleAttributeValue(
            ATSAttributes.VALIDATION_REQUIRED_ATTRIBUTE.getStoreName(), null)));
      testEquals(
            "Targeted Version",
            (teamArt.getSmaMgr().getTargetedForVersion() != null ? teamArt.getSmaMgr().getTargetedForVersion().toString() : "not set"),
            "2.5.7");
      testEquals("State", DefaultTeamState.Analyze.name(), teamArt.getSmaMgr().getStateMgr().getCurrentStateName());
   }

   private void testEquals(String name, Object expected, Object actual) throws OseeCoreException {
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
         AtsUtil.openAtsAction(actionArt, AtsOpenOption.OpenOneOrPopupSelect);
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
         AtsUtil.openAtsAction(actionArt, AtsOpenOption.OpenOneOrPopupSelect);
      }
      validateActionAtEnd(actionArt);
      resultData.report(title);
   }

}
