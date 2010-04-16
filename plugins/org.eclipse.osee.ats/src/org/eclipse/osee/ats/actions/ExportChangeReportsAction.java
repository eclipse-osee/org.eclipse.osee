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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsBranchManager;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.change.ViewWordChangeReportHandler;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class ExportChangeReportsAction extends Action {
   private final WorldEditor worldEditor;
   private final List<TeamWorkFlowArtifact> workflows = new ArrayList<TeamWorkFlowArtifact>();
   private final boolean reverse = true;

   public ExportChangeReportsAction(WorldEditor worldEditor) {
      setText("Export Change Report(s)");
      setImageDescriptor(getImageDescriptor());
      this.worldEditor = worldEditor;
      workflows.clear();
   }

   public List<TeamWorkFlowArtifact> getWorkflows() throws OseeCoreException {
      Collection<String> legacyIds =
            Arrays.asList(new String[] {"11012", "11016", "11017", "11019",
                  "11020", "11024", "11025", "11026", "11034", "11035", "11041", "11048", "11052", "11053", "11057",
                  "11063", "11064", "11067", "11076", "11077", "11080", "11082", "11090", "11093", "11094", "11095",
                  "11096", "11097", "11098", "11100", "11101", "11102", "11103", "11104", "11105", "11106", "11107",
                  "11112", "11113", "11119", "11120", "11121", "11122", "11123", "11124", "11125", "11126", "11127",
                  "11128", "11135", "11137"
            });

      if (workflows.isEmpty()) {
         List<Artifact> artifacts =
               ArtifactQuery.getArtifactListFromAttributeValues(AtsAttributeTypes.LegacyPCRId, legacyIds,
                     CoreBranches.COMMON, legacyIds.size());
         for (Artifact artifact : artifacts) {
            if (artifact.getArtifactType().getGuid().equals("AAMFDjZ1UVAQTXHk2GgA")) {
               workflows.add((TeamWorkFlowArtifact) artifact);
            }
         }
         Collections.sort(workflows);
         if (reverse) {
            Collections.reverse(workflows);
         }
      }
      return workflows;
      //return worldEditor.getWorldComposite().getXViewer().getSelectedTeamWorkflowArtifacts();
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
      int minTransactionId = -1;
      for (TransactionRecord transaction : TransactionManager.getCommittedArtifactTransactionIds(workflow)) {
         if (minTransactionId < transaction.getId()) {
            minTransactionId = transaction.getId();
         }
      }
      if (minTransactionId == -1) {
         throw new OseeStateException("no transaction records found for " + workflow);
      }
      return TransactionManager.getTransactionId(minTransactionId);
   }

   private void export() throws OseeCoreException {
      ViewWordChangeReportHandler handler = new ViewWordChangeReportHandler();

      for (TeamWorkFlowArtifact workflow : getWorkflows()) {

         AtsBranchManager atsBranchMgr = workflow.getBranchMgr();
         IProgressMonitor monitor = new NullProgressMonitor();
         Collection<Change> changes = null;
         if (atsBranchMgr.isCommittedBranchExists()) {
            changes = ChangeManager.getChangesPerTransaction(pickTransaction(workflow), monitor);
         } else {
            Branch branch = atsBranchMgr.getWorkingBranch();
            if (atsBranchMgr.isWorkingBranchInWork() && !branch.getBranchType().isBaselineBranch()) {
               changes = ChangeManager.getChangesPerBranch(branch, monitor);
            }
         }
         if (changes != null && changes.size() < 4000) {
            handler.viewWordChangeReport(changes, true, workflow.getSoleAttributeValueAsString(
                  AtsAttributeTypes.LegacyPCRId, null));
         }
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.EXPORT_DATA);
   }

   public void updateEnablement() throws OseeCoreException {
      setEnabled(getWorkflows().size() > 0);
   }
}
