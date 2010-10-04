/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsBranchManager;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;

/**
 * @author Ryan D. Brooks
 */
public class ExportChangeReportOperation extends AbstractOperation {
   private final Collection<TeamWorkFlowArtifact> workflows;

   public ExportChangeReportOperation(Collection<TeamWorkFlowArtifact> workflows) {
      super("Exporting Change Report(s)", AtsPlugin.PLUGIN_ID);
      this.workflows = workflows;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws OseeCoreException {
      RenderingUtil.setPopupsAllowed(false);

      for (Artifact workflow : workflows) {
         Collection<Change> changes = computeChanges(workflow, monitor);
         if (!changes.isEmpty() && changes.size() < 4000) {
            String legacyPcrId = workflow.getSoleAttributeValueAsString(AtsAttributeTypes.LegacyPcrId, null);
            generateDiffReport(changes, legacyPcrId, monitor);
         }
         monitor.worked(calculateWork(0.50));
      }
      RenderingUtil.setPopupsAllowed(true);
   }

   private Collection<Change> computeChanges(Artifact workflow, IProgressMonitor monitor) throws OseeCoreException {
      AtsBranchManager atsBranchMgr = ((TeamWorkFlowArtifact) workflow).getBranchMgr();

      List<Change> changes = new ArrayList<Change>();
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

         Iterator<Change> iterator = changes.iterator();
         while (iterator.hasNext()) {
            if (!(iterator.next() instanceof ArtifactChange)) {
               iterator.remove();
            }
         }

         Collections.sort(changes);
      }
      return changes;
   }

   private TransactionRecord pickTransaction(IArtifact workflow) throws OseeCoreException {
      int minTransactionId = -1;
      for (TransactionRecord transaction : TransactionManager.getCommittedArtifactTransactionIds(workflow)) {
         if (minTransactionId < transaction.getId() && transaction.getBranch().getArchiveState().isUnArchived()) {
            minTransactionId = transaction.getId();
         }
      }
      if (minTransactionId == -1) {
         throw new OseeStateException("no transaction records found for [%s]", workflow);
      }
      return TransactionManager.getTransactionId(minTransactionId);
   }

   private void generateDiffReport(Collection<Change> changes, String legacyPcrId, IProgressMonitor monitor) throws OseeCoreException {
      VariableMap variableMap =
         new VariableMap(IRenderer.NO_DISPLAY, true, "diffReportFolderName", legacyPcrId, IRenderer.FILE_NAME_OPTION,
            legacyPcrId);
      Collection<ArtifactDelta> compareArtifacts = ChangeManager.getCompareArtifacts(changes);

      WordTemplateRenderer renderer = new WordTemplateRenderer();
      renderer.setOptions(variableMap);
      renderer.getComparator().compareArtifacts(monitor, PresentationType.DIFF, compareArtifacts);
   }
}