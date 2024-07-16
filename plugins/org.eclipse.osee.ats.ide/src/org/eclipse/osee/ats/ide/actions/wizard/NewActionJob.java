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

package org.eclipse.osee.ats.ide.actions.wizard;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.INewActionListener;
import org.eclipse.osee.ats.ide.AtsOpenOption;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsEditors;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.ats.ide.world.WorldEditorSimpleProvider;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class NewActionJob extends Job {
   private String title;
   private final String desc;
   private final ChangeType changeType;
   private final String priority;
   private final Date needByDate;
   private final boolean validationRequired;
   private final Set<IAtsActionableItem> actionableItems;
   private final Collection<INewActionListener> newActionListeners;
   private boolean openOnComplete = true;
   private ActionResult result;

   public NewActionJob(String title, String desc, ChangeType changeType, String priority, Date needByDate, boolean validationRequired, Set<IAtsActionableItem> actionableItems, Collection<INewActionListener> newActionListeners) {
      super("Creating New Action");
      this.title = title;
      this.desc = desc;
      this.changeType = changeType;
      this.priority = priority;
      this.needByDate = needByDate;
      this.validationRequired = validationRequired;
      this.actionableItems = actionableItems;
      this.newActionListeners = newActionListeners;
   }

   @Override
   public IStatus run(final IProgressMonitor monitor) {
      try {
         if (actionableItems.isEmpty()) {
            throw new OseeArgumentException("Actionable Items can not be empty for New Action");
         }
         if ("tt".equals(title)) {
            title += " " + AtsApiService.get().getRandomNum();
         }
         IAtsChangeSet changes = AtsApiService.get().createChangeSet("Create New Action");
         result =
            AtsApiService.get().getActionService().createAction(AtsApiService.get().getUserService().getCurrentUser(),
               title, desc, changeType, priority, validationRequired, needByDate, actionableItems, new Date(),
               AtsApiService.get().getUserService().getCurrentUser(),
               newActionListeners == null ? Collections.emptyList() : newActionListeners, changes);

         if (monitor != null) {
            monitor.subTask("Persisting");
         }
         changes.execute();

         if (openOnComplete) {
            // Because this is a job, it will automatically kill any popups that are created during.
            // Thus, if multiple teams were selected to create, don't popup on openAction or dialog
            // will exception out when it is killed at the end of this job.
            if (result.getTeamWfs().size() == 1) {
               AtsEditors.openATSAction(result.getFirstTeam().getStoreObject(), AtsOpenOption.OpenAll);
            } else {
               WorldEditor.open(new WorldEditorSimpleProvider("Workflows", result.getTeamWfArts()));
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1, ex.getMessage(), ex);
      } finally {
         if (monitor != null) {
            monitor.done();
         }
      }
      return Status.OK_STATUS;
   }

   public ActionResult getResults() {
      return result;
   }

   public boolean isOpenOnComplete() {
      return openOnComplete;
   }

   public void setOpenOnComplete(boolean openOnComplete) {
      this.openOnComplete = openOnComplete;
   }

}
