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
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.util.AtsBranchManager;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.ViewWordChangeReportHandler;

/**
 * @author Donald G. Dunne
 */
public class ExportChangeReportsAction extends Action {
   private final WorldEditor worldEditor;
   private Branch branch;

   public ExportChangeReportsAction(WorldEditor worldEditor) {
      setText("Export Change Report(s)");
      setImageDescriptor(getImageDescriptor());
      this.worldEditor = worldEditor;
   }

   public Set<TeamWorkFlowArtifact> getWorkflows() {
      return worldEditor.getWorldComposite().getXViewer().getSelectedTeamWorkflowArtifacts();
   }

   @Override
   public void run() {

      try {
         export();
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   private TransactionRecord pickTransaction(IArtifact workflow) throws OseeCoreException {
      for (TransactionRecord transaction : TransactionManager.getCommittedArtifactTransactionIds(workflow)) {
         if (transaction.getBranch().equals(branch)) {
            return transaction;
         }
      }
      throw new OseeStateException("no transaction record for " + branch + " found.");
   }

   private void export() throws OseeCoreException {
      ViewWordChangeReportHandler handler = new ViewWordChangeReportHandler();
      branch = BranchManager.getBranchByGuid("NBdJRXpKwHF0bAvVHSwA");
      //      Collection<String> legacyIds =
      //            Arrays.asList("10594", "10599", "11129", "11224", "11233", "11327", "11329", "11382", "11408", "11416",
      //                  "11420", "11435", "11464", "11495", "11499", "11556", "11558", "11576", "11648", "11778");
      //      List<Artifact> workflows =
      //            ArtifactQuery.getArtifactListFromAttributeValues(AtsAttributeTypes.LegacyPCRId, legacyIds,
      //                  CoreBranches.COMMON, legacyIds.size());
      for (Artifact workflow : getWorkflows()) {
         // if (workflow.getSoleAttributeValue(ATSAttributes.TEAM_DEFINITION_GUID_ATTRIBUTE.getStoreName()).equals(
         //               "AAABIBFe5KAARwQiIYZIcA")) {
         AtsBranchManager atsBranchMgr = ((TeamWorkFlowArtifact) workflow).getBranchMgr();
         IProgressMonitor monitor = new NullProgressMonitor();
         Collection<Change> changes = null;
         if (atsBranchMgr.isCommittedBranchExists()) {
            changes = ChangeManager.getChangesPerTransaction(pickTransaction(workflow), monitor);
         } else {
            Branch branch = atsBranchMgr.getWorkingBranch();
            if (branch != null) {
               changes = ChangeManager.getChangesPerBranch(branch, monitor);
            }
         }
         if (changes != null) {
            handler.viewWordChangeReport(changes, true, workflow.getSoleAttributeValueAsString(
                  AtsAttributeTypes.LegacyPCRId, null));
         }
         //}
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
