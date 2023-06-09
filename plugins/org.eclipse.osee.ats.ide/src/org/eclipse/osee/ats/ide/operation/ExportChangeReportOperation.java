/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.ide.operation;

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
import org.eclipse.osee.ats.api.util.ExportChangeReportUtil;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.model.change.CompareData;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.publishing.RendererMap;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.change.ArtifactChange;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.compare.CompareDataCollector;

/**
 * @author Ryan D. Brooks
 */
public final class ExportChangeReportOperation extends AbstractOperation {
   private final List<IAtsTeamWorkflow> workflows;
   private final Appendable resultFolder;
   private final boolean reverse;
   private final boolean writeChangeReports;
   private final ArtifactTypeToken[] DISALLOW_TYPES = {CoreArtifactTypes.ImplementationDetailsMsWord};
   private final String overrideDataRightsClassification;
   boolean debug = false;

   public ExportChangeReportOperation(List<IAtsTeamWorkflow> workflows, boolean reverse, boolean writeChangeReports, String overrideDataRightsClassification, Appendable resultFolder, OperationLogger logger) {
      super("Exporting Change Report(s)", Activator.PLUGIN_ID, logger);
      this.workflows = workflows;
      this.reverse = reverse;
      this.writeChangeReports = writeChangeReports;
      this.overrideDataRightsClassification = overrideDataRightsClassification;
      this.resultFolder = resultFolder;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) {
      logf("Starting %s, processing %d workflows.", getClass().getSimpleName(), workflows.size());

      sortWorkflows();
      Set<String> skippedTypes = new HashSet<>();

      CompareDataCollector collector = new CompareDataCollector() {

         @Override
         public void onCompare(CompareData data) {
            String filePath = data.getOutputPath();
            String modifiedPath = filePath.substring(0, filePath.lastIndexOf(File.separator));
            try {
               if (resultFolder.toString().isEmpty()) {
                  resultFolder.append(modifiedPath);
               }
            } catch (IOException ex) {
               OseeCoreException.wrapAndThrow(ex);
            }
         }
      };

      for (IAtsTeamWorkflow workflow : workflows) {
         Set<ArtifactId> artIds = new HashSet<>();
         Collection<Change> changes = computeChanges(workflow, monitor, artIds);
         if (!changes.isEmpty() && changes.size() < 4000) {
            logf("Exporting: %s -- %s", workflow.toString(), workflow.getAtsId());
            String id = workflow.getAtsId();
            String prefix = "/" + id;
            if (writeChangeReports) {

               Collection<ArtifactDelta> artifactDeltas = ChangeManager.getCompareArtifacts(changes);

               // only allow SoftwareRequirementsFolder for HLR
               Iterator<ArtifactDelta> it = artifactDeltas.iterator();
               while (it.hasNext()) {
                  ArtifactDelta next = it.next();
                  Artifact endArtifact = next.getEndArtifact();
                  if (!endArtifact.isOfType(ExportChangeReportUtil.ARTIFACT_ALLOW_TYPES)) {
                     it.remove();
                     artIds.remove(ArtifactId.create(endArtifact));
                     if (debug) {
                        logf("skipping: [" + endArtifact.getName().replaceAll("%",
                           "%%") + "] type: [" + endArtifact.getArtifactTypeName() + "] branch: [" + endArtifact.getBranch().getIdString() + "] artId: [" + endArtifact.getIdString() + "]");
                     }
                     skippedTypes.add(endArtifact.getArtifactTypeName());
                  }
               }
               if (artifactDeltas.isEmpty()) {
                  logf("Nothing exported for Workflow[%s]", id);
                  continue;
               }

               //@formatter:off
               var rendererOptions =
                  RendererMap.of
                     (
                       RendererOption.NO_DISPLAY,           true,
                       RendererOption.SKIP_DIALOGS,         true,
                       RendererOption.OVERRIDE_DATA_RIGHTS, overrideDataRightsClassification
                     );
               //@formatter:on

               RendererManager.diff(collector, artifactDeltas, prefix, rendererOptions);
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
               OseeCoreException.wrapAndThrow(ex);
            }
         }
         monitor.worked(calculateWork(0.50));
      }

      logf("-------- skipped types --------- ");
      for (String skipped : skippedTypes) {
         logf(skipped);
      }
   }

   private void sortWorkflows() {
      Collections.sort(workflows, new Comparator<IAtsTeamWorkflow>() {
         @Override
         public int compare(IAtsTeamWorkflow workflow1, IAtsTeamWorkflow workflow2) {
            try {
               String atsId1 = AtsApiService.get().getAttributeResolver().getSoleAttributeValue(workflow1,
                  AtsAttributeTypes.AtsId, "");
               String atsId2 = AtsApiService.get().getAttributeResolver().getSoleAttributeValue(workflow2,
                  AtsAttributeTypes.AtsId, "");

               int compare = atsId1.compareTo(atsId2);
               return reverse ? -1 * compare : compare;
            } catch (OseeCoreException ex) {
               return -1;
            }
         }
      });
   }

   private Collection<Change> computeChanges(IAtsTeamWorkflow teamWf, IProgressMonitor monitor,
      Set<ArtifactId> artIds) {

      List<Change> changes = new ArrayList<>();
      IOperation operation = null;
      if (AtsApiService.get().getBranchService().isCommittedBranchExists(teamWf)) {
         operation = ChangeManager.comparedToPreviousTx(pickTransaction(teamWf), changes);
      } else {
         BranchId workingBranch = AtsApiService.get().getBranchService().getWorkingBranch(teamWf);
         if (workingBranch != null && !BranchManager.getType(workingBranch).isBaselineBranch()) {
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

   private TransactionToken pickTransaction(IAtsTeamWorkflow workflow) {
      TransactionToken minTransactionId = TransactionToken.SENTINEL;
      for (TransactionToken transaction : TransactionManager.getCommittedArtifactTransactionIds(
         AtsApiService.get().getQueryServiceIde().getArtifact(workflow))) {
         if (minTransactionId.isOlderThan(transaction) && !BranchManager.isArchived(transaction.getBranch())) {
            minTransactionId = transaction;
         }
      }
      if (!minTransactionId.isValid()) {
         throw new OseeStateException("no transaction records found for [%s]", workflow);
      }
      return minTransactionId;
   }

}