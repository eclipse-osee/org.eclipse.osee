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

package org.eclipse.osee.ats.actions.wizard;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.ats.util.AtsPriority.PriorityType;
import org.eclipse.osee.framework.jdk.core.util.AFile;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.ui.plugin.util.OseeData;
import org.eclipse.osee.framework.ui.skynet.ats.AtsOpenOption;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Donald G. Dunne
 */
public class NewActionJob extends Job {
   private boolean saveIt = false;
   private String identifyStateDescription = null;
   private String title;
   private final String desc;
   private final ChangeType changeType;
   private final PriorityType priority;
   private final Date needByDate;
   private final boolean validationRequired;
   private ActionArtifact actionArt;
   private final Set<ActionableItemArtifact> actionableItems;
   private final Collection<String> userComms;
   private static int ttNum;
   private final NewActionWizard wizard;

   public NewActionJob(String title, String desc, ChangeType changeType, PriorityType priority, Date needByDate, boolean validationRequired, Collection<String> userComms, Set<ActionableItemArtifact> actionableItems, NewActionWizard wizard) {
      super("Creating New Action");
      this.title = title;
      this.desc = desc;
      this.changeType = changeType;
      this.priority = priority;
      this.needByDate = needByDate;
      this.validationRequired = validationRequired;
      this.userComms = userComms;
      this.actionableItems = actionableItems;
      this.wizard = wizard;
   }

   public IStatus run(final IProgressMonitor monitor) {
      try {
         AbstractSkynetTxTemplate newActionTx =
               new AbstractSkynetTxTemplate(BranchPersistenceManager.getInstance().getAtsBranch()) {

                  @Override
                  protected void handleTxWork() throws Exception {
                     if (title.equals("tt")) title += " " + getAtsDeveloperTTNum();
                     actionArt =
                           createAction(monitor, title, desc, changeType, priority, userComms, validationRequired,
                                 needByDate, actionableItems);

                     if (wizard != null) wizard.notifyAtsWizardItemExtensions(actionArt);

                     monitor.subTask("Persisting");
                  }
               };
         newActionTx.execute();

         // Because this is a job, it will automatically kill any popups that are created during.
         // Thus, if multiple teams were selected to create, don't popup on openAction or dialog
         // will exception out when it is killed at the end of this job.
         AtsLib.openAtsAction(actionArt, AtsOpenOption.OpenAll);
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
         return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, ex.getMessage(), ex);
      } finally {
         monitor.done();
      }
      return Status.OK_STATUS;
   }

   /**
    * The development of ATS requires quite a few Actions to be created. To facilitate this, getTTNum will retrieve a
    * persistent number from the filesystem so each action has a different name. By entering "tt" in the title, new
    * action wizard will be prepopulated with selections and the action name will be created as "tt <number in
    * atsNumFilename>".
    * 
    * @return number
    */
   public static int getAtsDeveloperTTNum() {
      File numFile = OseeData.getFile("atsDevNum.txt");
      if (numFile.exists() && ttNum == 0) {
         try {
            ttNum = new Integer(AFile.readFile(numFile).replaceAll("\\s", ""));
         } catch (NumberFormatException ex) {
         } catch (NullPointerException ex) {
         }
      }
      ttNum++;
      AFile.writeFile(numFile, ttNum + "");
      return ttNum;
   }

   public static ActionArtifact createAction(IProgressMonitor monitor, String title, String desc, ChangeType changeType, PriorityType priority, Collection<String> userComms, boolean validationRequired, Date needByDate, Collection<ActionableItemArtifact> actionableItems) throws Exception {
      // if "tt" is title, this is an action created for development. To
      // make it easier, all fields are automatically filled in for ATS developer

      if (monitor != null) monitor.subTask("Creating Action");
      ActionArtifact actionArt =
            (ActionArtifact) ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor(
                  ActionArtifact.ARTIFACT_NAME).makeNewArtifact(BranchPersistenceManager.getInstance().getAtsBranch());
      ActionArtifact.setArtifactIdentifyData(actionArt, title, desc, changeType, priority, userComms,
            validationRequired, needByDate);

      // Relate Action to ActionableItems (by guid)
      for (ActionableItemArtifact aia : actionableItems)
         actionArt.getActionableItemsDam().addActionableItem(aia);

      // Retrieve Team Definitions corresponding to selected Actionable Items
      if (monitor != null) monitor.subTask("Creating WorkFlows");
      Set<TeamDefinitionArtifact> teams =
            TeamDefinitionArtifact.getImpactedTeamDefs(actionArt.getActionableItemsDam().getActionableItems());
      if (teams == null || teams.size() == 0) {
         StringBuffer sb = new StringBuffer();
         for (ActionableItemArtifact aia : actionableItems)
            sb.append("Selected AI \"" + aia + "\" " + aia.getHumanReadableId() + "\n");
         throw new IllegalArgumentException(
               "No teams returned for Action's selected Actionable Items\n" + sb.toString());
      }

      // Create team workflow artifacts
      for (TeamDefinitionArtifact teamDef : teams) {
         actionArt.createTeamWorkflow(teamDef, actionableItems, teamDef.getLeads(actionableItems));
      }
      actionArt.persist(true);
      return actionArt;

   }

   /**
    * @return Returns the identifyStateDescription.
    */
   public String getIdentifyStateDescription() {
      return identifyStateDescription;
   }

   /**
    * @param identifyStateDescription The identifyStateDescription to set.
    */
   public void setIdentifyStateDescription(String identifyStateDescription) {
      this.identifyStateDescription = identifyStateDescription;
   }

   /**
    * @return Returns the saveIt.
    */
   public boolean isSaveIt() {
      return saveIt;
   }

   /**
    * @param saveIt The saveIt to set.
    */
   public void setSaveIt(boolean saveIt) {
      this.saveIt = saveIt;
   }

}
