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
   private final boolean reverse = false;

   public ExportChangeReportsAction(WorldEditor worldEditor) {
      setText("Export Change Report(s)");
      setImageDescriptor(getImageDescriptor());
      this.worldEditor = worldEditor;
      workflows.clear();
   }

   public List<TeamWorkFlowArtifact> getWorkflows() throws OseeCoreException {
      Collection<String> legacyIds =
            Arrays.asList(new String[] {"10792", "10794", "10796", "10800", "10801", "10802", "10803", "10804",
                  "10842", "10847", "10854", "10865", "10868", "10871", "10872", "10873", "10899", "10900", "10901",
                  "10902", "10903", "10904", "10905", "10907", "10908", "10916", "10917", "10918", "10921",
                  "10923", "10924", "10927", "10930", "10943", "10945", "10946", "10949", "10953", "10954", "10972",
                  "10973", "10974", "10975", "10976", "10978", "10979", "10980", "10982", "10983", "10984", "10985",
                  "10986", "10990", "11004", "11006", "11008", "11009", "11010", "11011", "11012", "11016", "11017",
                  "11019", "11020", "11024", "11025", "11026", "11034", "11035", "11041", "11048", "11052", "11053",
                  "11057", "11063", "11064", "11067", "11076", "11077", "11080", "11082", "11090", "11093", "11094",
                  "11095", "11096", "11097", "11098", "11100", "11101", "11102", "11103", "11104", "11105", "11106",
                  "11107", "11112", "11113", "11119", "11120", "11121", "11122", "11123", "11124", "11125", "11126",
                  "11127", "11128", "11135", "11137", "11138", "11139", "11140", "11141", "11142", "11144", "11145",
                  "11148", "11149", "11150", "11153", "11161", "11163", "11172", "11188", "11191", "11201", "11203",
                  "11206", "11207", "11210", "11212", "11219", "11220", "11225", "11226", "11228", "11229", "11241",
                  "11245", "11246", "11248", "11249", "11250", "11251", "11253", "11256", "11260", "11262", "11268",
                  "11269", "11270", "11272", "11273", "11277", "11278", "11281", "11286", "11288", "11290", "11296",
                  "11297", "11298", "11306", "11307", "11313", "11315", "11317", "11320", "11322", "11324", "11330",
                  "11331", "11333", "11340", "11341", "11342", "11344", "11346", "11348", "11350", "11351", "11354",
                  "11355", "11358", "11359", "11360", "11361", "11366", "11367", "11368", "11370", "11371", "11377",
                  "11378", "11379", "11381", "11384", "11385", "11388", "11389", "11390", "11391", "11392", "11393",
                  "11394", "11395", "11396", "11397", "11398", "11399", "11400", "11404", "11405", "11406", "11410",
                  "11411", "11413", "11417", "11421", "11422", "11424", "11426", "11427", "11428", "11429", "11430",
                  "11431", "11432", "11433", "11434", "11438", "11439", "11440", "11441", "11442", "11443", "11444",
                  "11445", "11446", "11447", "11448", "11449", "11450", "11453", "11454", "11457", "11459", "11460",
                  "11463", "11465", "11466", "11467", "11470", "11471", "11472", "11473", "11474", "11480", "11481",
                  "11482", "11483", "11489", "11490", "11491", "11492", "11494", "11497", "11503", "11506", "11507",
                  "11512", "11513", "11516", "11520", "11522", "11523", "11531", "11533", "11534", "11543", "11544",
                  "11545", "11549", "11561", "11567", "11569", "11571", "11574", "11577", "11578", "11579", "11586",
                  "11587", "11588", "11591", "11592", "11593", "11594", "11602", "11603", "11605", "11609", "11617",
                  "11647", "11649", "11684", "11707", "11713", "11715", "11717", "11721", "11722", "11726", "11729",
                  "11730", "11731", "11732", "11733", "11734", "11735", "11736", "11739", "11740", "11747", "11748",
                  "11749", "11750", "11770", "11775", "11784", "11785", "11788", "11789", "11790", "11791", "11792",
                  "11793", "11794", "11795", "11796", "11797", "11798", "11802", "11803", "11804", "11805", "11806",
                  "11807", "11808", "11812", "11813", "11814", "11819", "11822", "11825", "11826", "11827", "11838",
                  "11840", "11841", "11842", "11843", "11844", "11845", "11848", "11849", "11852", "11855", "11858",
                  "11859", "11860", "11861", "11862", "11866", "11868", "11870", "11871", "11876", "11877", "11884",
                  "11886", "11889", "11892", "11893", "11895", "11902", "11903", "11904", "11906", "11913", "11925",
                  "11929", "11931", "11940", "11944", "11945", "11946", "11951", "11960", "11963", "11964", "11967",
                  "11969", "11978", "11983", "11986", "11990", "11991", "11995", "11997", "12001", "12002", "12003",
                  "12006", "12008", "12014", "12015", "12024", "12025", "12027", "12029", "12030", "12031", "12035",
                  "12036", "12037", "12043", "12045", "12050", "12053", "12055", "12057", "12058", "12062", "12064",
                  "12066", "12067", "12070", "12072", "12083", "12090", "12093", "12095", "12096", "12099", "12105",
                  "12107", "12108", "12109", "12110", "12116", "12127", "12130", "12131", "12138", "12139", "12140",
                  "12144", "12146", "12152", "12163", "12174", "12183", "12189", "12194"});

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
            TransactionRecord transactionRecord = pickTransaction(workflow);
            if (!transactionRecord.getBranch().getBranchType().isBaselineBranch()) {
               changes = ChangeManager.getChangesPerTransaction(transactionRecord, monitor);
            }
         } else {
            Branch branch = atsBranchMgr.getWorkingBranch();
            if (!branch.getBranchType().isBaselineBranch()) {
               if (atsBranchMgr.isWorkingBranchInWork()) {
                  changes = ChangeManager.getChangesPerBranch(branch, monitor);
               }
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
