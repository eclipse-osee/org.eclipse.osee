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

import static org.eclipse.osee.framework.ui.skynet.render.IRenderer.NO_DISPLAY;
import static org.eclipse.osee.framework.ui.skynet.render.IRenderer.SKIP_DIALOGS;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.plugin.core.util.OseeData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.change.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.compare.CompareData;
import org.eclipse.osee.framework.ui.skynet.render.compare.CompareDataCollector;

/**
 * @author Ryan D. Brooks
 */
public final class ExportChangeReportOperation extends AbstractOperation {
   private final List<TeamWorkFlowArtifact> workflows;
   private final Appendable resultFolder;
   private final boolean reverse;
   private final boolean writeChangeReports;

   public ExportChangeReportOperation(List<TeamWorkFlowArtifact> workflows, boolean reverse, boolean writeChangeReports, Appendable resultFolder) {
      super("Exporting Change Report(s)", Activator.PLUGIN_ID);
      this.workflows = workflows;
      this.reverse = reverse;
      this.writeChangeReports = writeChangeReports;
      this.resultFolder = resultFolder;
   }

   public ExportChangeReportOperation(List<TeamWorkFlowArtifact> workflows, boolean reverse) {
      this(workflows, reverse, true, new StringBuilder());
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws OseeCoreException {
      logf("%d workflows.", workflows.size());

      sortWorkflows();

      CompareDataCollector collector = new CompareDataCollector() {

         @Override
         public void onCompare(CompareData data) throws OseeCoreException {
            String filePath = data.getOutputPath();
            String modifiedPath = filePath.substring(0, filePath.lastIndexOf(File.separator));
            try {
               if (resultFolder.toString().isEmpty()) {
                  resultFolder.append(modifiedPath);
               }
            } catch (IOException ex) {
               OseeExceptions.wrapAndThrow(ex);
            }
         }
      };

      for (TeamWorkFlowArtifact workflow : workflows) {
         Set<Integer> artIds = new HashSet<Integer>();
         Collection<Change> changes = computeChanges(workflow, monitor, artIds);
         if (!changes.isEmpty() && changes.size() < 4000) {
            String id = workflow.getSoleAttributeValueAsString(AtsAttributeTypes.LegacyPcrId, workflow.getAtsId());
            String prefix = "/" + id;
            if (writeChangeReports) {

               Collection<ArtifactDelta> artifactDeltas = ChangeManager.getCompareArtifacts(changes);
               RendererManager.diff(collector, artifactDeltas, prefix, NO_DISPLAY, true, SKIP_DIALOGS, true);
            }
            String artIdsAsString = org.eclipse.osee.framework.jdk.core.util.Collections.toString(",", artIds);
            try {
               if (resultFolder.toString().isEmpty()) {
                  IFolder folder = OseeData.getFolder("ids");
                  File idsFolder = new File(folder.getLocationURI());
                  resultFolder.append(idsFolder.getAbsolutePath());
               }
               Lib.writeStringToFile(artIdsAsString, new File(resultFolder + prefix + "_ids.txt"));
            } catch (IOException ex) {
               OseeExceptions.wrapAndThrow(ex);
            }
         }
         monitor.worked(calculateWork(0.50));
      }
   }

   private void sortWorkflows() {
      Collections.sort(workflows, new Comparator<TeamWorkFlowArtifact>() {
         @Override
         public int compare(TeamWorkFlowArtifact workflow1, TeamWorkFlowArtifact workflow2) {
            try {
               String legacyId1 = workflow1.getSoleAttributeValue(AtsAttributeTypes.LegacyPcrId, "");
               String legacyId2 = workflow2.getSoleAttributeValue(AtsAttributeTypes.LegacyPcrId, "");

               int compare = legacyId1.compareTo(legacyId2);
               return reverse ? -1 * compare : compare;
            } catch (OseeCoreException ex) {
               return -1;
            }
         }
      });
   }

   private Collection<Change> computeChanges(Artifact workflow, IProgressMonitor monitor, Set<Integer> artIds) throws OseeCoreException {
      TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) workflow;

      List<Change> changes = new ArrayList<Change>();
      IOperation operation = null;
      if (AtsClientService.get().getBranchService().isCommittedBranchExists(teamArt)) {
         operation = ChangeManager.comparedToPreviousTx(pickTransaction(workflow), changes);
      } else {
         IOseeBranch workingBranch = AtsClientService.get().getBranchService().getWorkingBranch(teamArt);
         if (workingBranch != null && !BranchManager.getBranchType(workingBranch).isBaselineBranch()) {
            operation = ChangeManager.comparedToParent(workingBranch, changes);
         }
      }
      if (operation != null) {
         doSubWork(operation, monitor, 0.50);

         Iterator<Change> iterator = changes.iterator();
         while (iterator.hasNext()) {
            Change change = iterator.next();
            if (!(change instanceof ArtifactChange)) {
               iterator.remove();
            } else {
               artIds.add(change.getArtId());
            }
         }

         Collections.sort(changes);
      }
      return changes;
   }

   private TransactionRecord pickTransaction(IArtifact workflow) throws OseeCoreException {
      int minTransactionId = -1;
      for (TransactionRecord transaction : TransactionManager.getCommittedArtifactTransactionIds(workflow)) {
         if (minTransactionId < transaction.getId() && transaction.getFullBranch().getArchiveState().isUnArchived()) {
            minTransactionId = transaction.getId();
         }
      }
      if (minTransactionId == -1) {
         throw new OseeStateException("no transaction records found for [%s]", workflow);
      }
      return TransactionManager.getTransactionId(minTransactionId);
   }

}