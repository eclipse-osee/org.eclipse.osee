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
package org.eclipse.osee.ats.actions;

import java.util.Collection;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.util.AtsBranchManager;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.ViewWordChangeReportHandler;

/**
 * @author Donald G. Dunne
 */
public class ExportChangeReportsAction extends Action {
   private final WorldEditor worldEditor;

   public ExportChangeReportsAction(WorldEditor worldEditor) {
      setText("Export Change Reports(s)");
      setImageDescriptor(getImageDescriptor());
      this.worldEditor = worldEditor;
   }

   public Set<TeamWorkFlowArtifact> getWorkflows() {
      return worldEditor.getWorldComposite().getXViewer().getSelectedTeamWorkflowArtifacts();
   }

   @Override
   public void run() {
      ViewWordChangeReportHandler handler = new ViewWordChangeReportHandler();
      try {
         for (TeamWorkFlowArtifact teamArt : getWorkflows()) {
            AtsBranchManager atsBranchMgr = teamArt.getSmaMgr().getBranchMgr();
            IProgressMonitor monitor = new NullProgressMonitor();
            Collection<Change> changes = null;
            if (atsBranchMgr.isCommittedBranchExists()) {
               TransactionRecord transaction = atsBranchMgr.getTransactionIdOrPopupChoose("Show Change Report", false);
               changes = ChangeManager.getChangesPerTransaction(transaction, monitor);
            } else {
               Branch branch = atsBranchMgr.getWorkingBranch();
               if (branch != null) {
                  changes = ChangeManager.getChangesPerBranch(branch, monitor);
               }
            }
            if (changes != null) {
               handler.viewWordChangeReport(changes);
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.EXPORT_DATA);
   }

   public void updateEnablement() {
      setEnabled(getWorkflows().size() > 0);
   }
}
