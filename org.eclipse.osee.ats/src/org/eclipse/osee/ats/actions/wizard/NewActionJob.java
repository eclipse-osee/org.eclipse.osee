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

import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.util.ActionManager;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.AtsPriority.PriorityType;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.ats.AtsOpenOption;
import org.eclipse.osee.framework.ui.skynet.notify.OseeNotificationManager;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;

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

   @Override
   public IStatus run(final IProgressMonitor monitor) {
      try {
         SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Create New Action");
         if (title.equals("tt")) {
            title += " " + AtsUtil.getAtsDeveloperIncrementingNum();
         }
         actionArt =
               ActionManager.createAction(monitor, title, desc, changeType, priority, validationRequired, needByDate,
                     actionableItems, transaction);

         if (wizard != null) {
            wizard.notifyAtsWizardItemExtensions(actionArt, transaction);
         }

         monitor.subTask("Persisting");
         transaction.execute();

         // Because this is a job, it will automatically kill any popups that are created during.
         // Thus, if multiple teams were selected to create, don't popup on openAction or dialog
         // will exception out when it is killed at the end of this job.
         AtsUtil.openAtsAction(actionArt, AtsOpenOption.OpenAll);
         OseeNotificationManager.sendNotifications();
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, ex.getMessage(), ex);
      } finally {
         monitor.done();
      }
      return Status.OK_STATUS;
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

   /**
    * @return the actionArt
    */
   public ActionArtifact getActionArt() {
      return actionArt;
   }

}
