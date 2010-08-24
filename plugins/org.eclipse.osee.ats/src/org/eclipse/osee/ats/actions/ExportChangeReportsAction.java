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
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
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
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.framework.ui.skynet.render.word.WordChangeReportOperation;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class ExportChangeReportsAction extends Action {
   private final WorldEditor worldEditor;
   private final boolean reverse = false;

   public ExportChangeReportsAction(WorldEditor worldEditor) {
      setText("Export Change Report(s)");
      setImageDescriptor(getImageDescriptor());
      this.worldEditor = worldEditor;
   }

   @SuppressWarnings("unused")
   public Collection<TeamWorkFlowArtifact> getWorkflows() throws OseeCoreException {
      if (true) {
         Collection<String> dontCreate = Arrays.asList(new String[] {});
         Collection<String> legacyIds = Arrays.asList(new String[] {"12442"});

         List<TeamWorkFlowArtifact> workflows = new ArrayList<TeamWorkFlowArtifact>();
         if (workflows.isEmpty()) {
            List<Artifact> artifacts =
               ArtifactQuery.getArtifactListFromAttributeValues(AtsAttributeTypes.LegacyPcrId, legacyIds,
                  CoreBranches.COMMON, legacyIds.size());
            for (Artifact artifact : artifacts) {
               if (artifact.getArtifactType().getGuid().equals("AAMFDjZ1UVAQTXHk2GgA")) {
                  TeamWorkFlowArtifact teamWorkflow = (TeamWorkFlowArtifact) artifact;
                  String legacyId = teamWorkflow.getWorldViewLegacyPCR();
                  if (!dontCreate.contains(legacyId)) {
                     workflows.add(teamWorkflow);
                  }
               }
            }
            Collections.sort(workflows, new Comparator<TeamWorkFlowArtifact>() {
               @Override
               public int compare(TeamWorkFlowArtifact workflow1, TeamWorkFlowArtifact workflow2) {
                  try {
                     int compare = workflow1.getWorldViewLegacyPCR().compareTo(workflow2.getWorldViewLegacyPCR());
                     return reverse ? -1 * compare : compare;
                  } catch (OseeCoreException ex) {
                     return -1;
                  }
               }
            });
         }
         return workflows;
      }

      return worldEditor.getWorldComposite().getXViewer().getSelectedTeamWorkflowArtifacts();
   }

   @Override
   public void run() {
      try {
         IOperation operation = new ExportChangesOperation(getWorkflows());
         Operations.executeAsJob(operation, true);
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex.toString(), ex);
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.EXPORT_DATA);
   }

   public void updateEnablement() throws OseeCoreException {
      setEnabled(!getWorkflows().isEmpty());
   }

   private static final class ExportChangesOperation extends AbstractOperation {
      private final Collection<TeamWorkFlowArtifact> workflows;

      public ExportChangesOperation(Collection<TeamWorkFlowArtifact> workflows) {
         super("Exporting Change Report(s)", AtsPlugin.PLUGIN_ID);
         this.workflows = workflows;
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

      @Override
      protected void doWork(IProgressMonitor monitor) throws OseeCoreException {
         RenderingUtil.setPopupsAllowed(false);

         for (Artifact workflow : workflows) {
            AtsBranchManager atsBranchMgr = ((TeamWorkFlowArtifact) workflow).getBranchMgr();

            Collection<Change> changes = new ArrayList<Change>();
            IOperation operation = null;
            if (atsBranchMgr.isCommittedBranchExists()) {
               operation = ChangeManager.comparedToPreviousTx(pickTransaction(workflow), changes);
            } else {
               Branch workingBranch = atsBranchMgr.getWorkingBranch();
               if (workingBranch != null && !workingBranch.getBranchType().isBaselineBranch()) {
                  operation = ChangeManager.comparedToParent(workingBranch, changes);
               }
            }
            if (operation != null) {
               doSubWork(operation, monitor, 0.50);
            }
            if (!changes.isEmpty() && changes.size() < 4000) {
               String folderName = workflow.getSoleAttributeValueAsString(AtsAttributeTypes.LegacyPcrId, null);
               IOperation subOp = new WordChangeReportOperation(changes, folderName);
               doSubWork(subOp, monitor, 0.50);
            } else {
               monitor.worked(calculateWork(0.50));
            }
         }
         RenderingUtil.setPopupsAllowed(true);
      }
   }
}
