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
                  "11128", "11135", "11137", "11138", "11139", "11140", "11141", "11142", "11144", "11145", "11148",
                  "11149", "11150", "11153", "11161", "11163", "11172", "11188", "11191", "11201", "11203", "11206",
                  "11207", "11210", "11212", "11219", "11220", "11225", "11226", "11228", "11229", "11241", "11245",
                  "11246", "11248", "11249", "11250", "11251", "11253", "11256", "11260", "11262", "11268", "11269",
                  "11270", "11272", "11273", "11277", "11278", "11281", "11286", "11288", "11290", "11296", "11297",
                  "11298", "11306", "11307", "11313", "11315", "11317", "11320", "11322", "11324", "11330", "11331",
                  "11333", "11340", "11341", "11342", "11344", "11346", "11348", "11350", "11351", "11354", "11355",
                  "11358", "11359", "11360", "11361", "11366", "11367", "11368", "11370", "11371", "11377", "11378",
                  "11379", "11381", "11384", "11385", "11388", "11389", "11390", "11391", "11392", "11393", "11394",
                  "11395", "11396", "11397", "11398", "11399", "11400", "11404", "11405", "11406", "11410", "11411",
                  "11413", "11417", "11421", "11422", "11424", "11426", "11427", "11428", "11429", "11430", "11431",
                  "11432", "11433", "11434", "11438", "11439", "11440", "11441", "11442", "11443", "11444", "11445",
                  "11446", "11447", "11448", "11449", "11450", "11453", "11454", "11457", "11459", "11460", "11463",
                  "11465", "11466", "11467", "11470", "11471", "11472", "11473", "11474", "11480", "11481", "11482",
                  "11483", "11489", "11490", "11491", "11492", "11494", "11497", "11503", "11506", "11507", "11512",
                  "11513", "11516", "11520", "11522", "11523", "11531", "11533", "11534", "11543", "11544", "11545",
                  "11549", "11561", "11567"
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
