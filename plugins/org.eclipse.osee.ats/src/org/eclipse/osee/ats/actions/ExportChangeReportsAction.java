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

         Collection<String> legacyIds =
            Arrays.asList(new String[] {"5812", "6126", "6127", "6156", "6162", "6243", "6282", "6283", "6284", "6285",
               "6286", "6287", "6288", "6289", "6290", "6291", "6292", "6293", "6294", "6306", "6308", "6318", "6351",
               "6352", "6355", "6358", "6424", "6484", "6574", "6579", "6583", "6599", "6601", "6603", "6665", "6666",
               "6668", "6701", "6703", "6719", "6720", "6721", "6722", "6724", "6726", "6728", "6736", "6737", "6751",
               "6752", "6759", "6783", "6786", "6787", "6788", "6799", "6803", "6810", "6812", "6813", "6816", "6818",
               "6826", "6830", "6831", "6832", "6839", "6868", "6873", "6881", "6887", "6889", "6905", "6908", "6911",
               "6914", "6933", "6937", "6945", "6969", "6994", "6998", "7011", "7025", "7032", "7041", "7063", "7088",
               "7094", "7116", "7130", "7152", "7154", "7155", "7156", "7157", "7179", "7219", "7220", "7223", "7224",
               "7225", "7227", "7228", "7229", "7230", "7231", "7232", "7233", "7234", "7235", "7236", "7237", "7238",
               "7239", "7240", "7241", "7263", "7272", "7286", "7300", "7350", "7367", "7368", "7371", "7376", "7407",
               "7444", "7481", "7484", "7485", "7486", "7489", "7491", "7492", "7493", "7496", "7497", "7498", "7499",
               "7500", "7503", "7504", "7505", "7514", "7518", "7520", "7539", "7562", "7566", "7567", "7569", "7570",
               "7571", "7572", "7573", "7576", "7577", "7579", "7580", "7581", "7582", "7583", "7584", "7604", "7605",
               "7606", "7607", "7608", "7609", "7610", "7625", "7626", "7630", "7638", "7639", "7668", "7672", "7673",
               "7674", "7675", "7684", "7698", "7700", "7705", "7708", "7710", "7713", "7714", "7720", "7722", "7727",
               "7729", "7735", "7736", "7737", "7743", "7744", "7749", "7753", "7755", "7772", "7773", "7784", "7785",
               "7789", "7805", "7806", "7812", "7829", "7842", "7853", "7867", "7877", "7884", "7894", "7896", "7899",
               "7909", "7934", "7936", "7940", "7941", "7949", "7952", "7956", "7963", "7964", "7966", "7976", "7983",
               "7985", "7986", "8000", "8028", "8036", "8049", "8103", "8104", "8112", "8143", "8148", "8153", "8156",
               "8157", "8183", "8200", "8201", "8204", "8312", "8337", "8375", "8376", "8430", "8444", "8488", "8521",
               "8529", "8541", "8562", "8631", "8642", "8662", "8707", "8760", "8797", "8956", "8958", "8992", "9033",
               "9127", "9176", "9192", "9220", "9236", "9311", "9324", "9325", "9342", "9343", "9344", "9345", "9346",
               "9347", "9400", "9417", "9418", "9420", "9422", "9442", "9445", "9463", "9482", "9484", "9485", "9488",
               "9492", "9635", "9656", "9721", "9927", "9997", "10027", "10083", "10116", "10135", "10255", "10256",
               "10267", "10280", "10343", "10351", "10409", "10477", "10484", "10513", "10558", "10589", "10609",
               "10670", "10671", "10682", "10689", "10697", "10703", "10773", "10783", "10789", "10805", "10806",
               "10807", "10808", "10809", "10810", "10844", "10856", "10863", "10903", "10913", "10920", "10928",
               "10931", "11021", "11041", "11055", "11090", "11123", "11127", "11130", "11179", "11183", "11184",
               "11187", "11204", "11238", "11258", "11278", "11288", "11321", "11345", "11349", "11355", "11372",
               "11389", "11392", "11411", "11420", "11435", "11438", "11439", "11440", "11460", "11465", "11477",
               "11478", "11479", "11484", "11488", "11565", "11568", "11571", "11574", "11582", "11622", "11649",
               "11655", "11694", "11700", "11733", "11773", "11781", "11819", "11822", "11867", "11875", "11899",
               "11915", "11919", "11925", "11943", "11956", "11967", "12054", "12069", "12074", "12075", "12081",
               "12087", "12104", "12111", "12129", "12186", "12199", "12224", "12231", "12244", "12246", "12273",
               "12292", "12303", "12307", "12315", "12319", "12330", "12334", "12355", "12356", "12360", "12379",
               "12382", "12388", "12401", "12425", "12429", "12442"});

         List<TeamWorkFlowArtifact> workflows = new ArrayList<TeamWorkFlowArtifact>();
         if (workflows.isEmpty()) {
            List<Artifact> artifacts =
               ArtifactQuery.getArtifactListFromAttributeValues(AtsAttributeTypes.LegacyPCRId, legacyIds,
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
      protected void doWork(IProgressMonitor monitor) throws Exception {
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
               String folderName = workflow.getSoleAttributeValueAsString(AtsAttributeTypes.LegacyPCRId, null);
               IOperation subOp = new WordChangeReportOperation(changes, true, folderName);
               doSubWork(subOp, monitor, 0.50);
            } else {
               monitor.worked(calculateWork(0.50));
            }
         }
         RenderingUtil.setPopupsAllowed(true);
      }
   }
}
