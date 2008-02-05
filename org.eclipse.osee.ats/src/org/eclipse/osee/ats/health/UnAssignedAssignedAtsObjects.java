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

package org.eclipse.osee.ats.health;

import java.sql.SQLException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.util.widgets.SMAState;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.skynet.core.user.UserEnum;
import org.eclipse.osee.framework.skynet.core.util.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class UnAssignedAssignedAtsObjects extends XNavigateItemAction {

   boolean fixIt = false;
   XResultData rd = new XResultData(AtsPlugin.getLogger());

   /**
    * @param parent
    */
   public UnAssignedAssignedAtsObjects(XNavigateItem parent) {
      super(parent, "Report UnAssigned AND Assigned / NoOne Assigned (partial fix available)");
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.navigate.ActionNavigateItem#run()
    */
   @Override
   public void run() throws SQLException {
      if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), getName(), getName())) return;
      Jobs.startJob(new Report(getName()), true);
   }

   public class Report extends Job {

      public Report(String name) {
         super(name);
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
       */
      @Override
      protected IStatus run(IProgressMonitor monitor) {
         IStatus toReturn = Status.CANCEL_STATUS;
         try {
            if (fixIt) {
               AbstractSkynetTxTemplate txWrapper =
                     new AbstractSkynetTxTemplate(BranchPersistenceManager.getInstance().getAtsBranch()) {
                        @Override
                        protected void handleTxWork() throws Exception {
                           getUnassignedAtsObjectHelper();
                        }
                     };
               txWrapper.execute();

            } else {
               getUnassignedAtsObjectHelper();
            }
            toReturn = Status.OK_STATUS;
         } catch (Exception ex) {
            OSEELog.logException(AtsPlugin.class, ex, false);
            toReturn = new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, ex.getMessage(), ex);
         } finally {
            monitor.done();
         }
         return toReturn;
      }
   }

   private void getUnassignedAtsObjectHelper() throws Exception {
      User unAssignedUser = SkynetAuthentication.getInstance().getUser(UserEnum.UnAssigned);
      User noOneUser = SkynetAuthentication.getInstance().getUser(UserEnum.NoOne);
      final XResultData rd = new XResultData(AtsPlugin.getLogger());

      for (Artifact art : StateMachineArtifact.getAllSMATypeArtifacts()) {
         StateMachineArtifact sma = (StateMachineArtifact) art;
         SMAManager smaMgr = new SMAManager(sma);
         if (smaMgr.getAssignees().size() > 1) {
            if (smaMgr.getAssignees().contains(unAssignedUser)) {
               rd.logError(art.getHumanReadableId() + " is unassigned and assigned => " + Artifacts.commaArts(smaMgr.getAssignees()));
               if (fixIt) {
                  SMAState state = smaMgr.getSMAState();
                  state.removeAssignee(unAssignedUser);
                  smaMgr.getCurrentStateDam().setState(state);
               }
            }
         }
         for (SMAState state : smaMgr.getStateDam().getStates()) {
            if (state.getAssignees().size() > 1 && state.getAssignees().contains(unAssignedUser)) {
               rd.logError(art.getHumanReadableId() + " state " + state.getName() + " is unassigned and assigned => " + Artifacts.commaArts(state.getAssignees()));
               if (fixIt) {
                  state.removeAssignee(unAssignedUser);
                  smaMgr.getStateDam().setState(state);
               }
            }
         }
         if (smaMgr.getAssignees().contains(noOneUser)) {
            rd.logError(art.getHumanReadableId() + " is assigned to NoOne; invalid assignment - MANUAL FIX REQUIRED");
         }
         if (sma.isDirty()) {
            sma.persistAttributes();
         }
         rd.report(getName());
      }
   }

}
