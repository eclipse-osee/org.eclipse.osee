/*
 * Created on Jan 3, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.xnavigate;

import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.ui.skynet.blam.BlamWorkflow;
import org.eclipse.osee.framework.ui.skynet.blam.WorkflowEditor;
import org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation;

/**
 * @author Donald G. Dunne
 */
public class XNavigateItemBlam extends XNavigateItem {
   private final BlamOperation blamOperation;

   /**
    * @param parent
    * @param name
    */
   public XNavigateItemBlam(XNavigateItem parent, BlamOperation blamOperation) {
      super(parent, blamOperation.getClass().getSimpleName());
      this.blamOperation = blamOperation;
   }

   @Override
   public void run() throws SQLException {
      BlamWorkflow workflow;
      try {
         workflow =
               (BlamWorkflow) ArtifactPersistenceManager.getInstance().getArtifactFromTypeName(
                     BlamWorkflow.ARTIFACT_NAME, getName(), BranchPersistenceManager.getInstance().getCommonBranch());
      } catch (Exception ex) {
         workflow = BlamWorkflow.createBlamWorkflow(blamOperation);
         workflow.setDescriptiveName(getName());
         workflow.persist(true);
      }
      workflow.setSoleOperation(blamOperation);

      WorkflowEditor.editArtifact(workflow);
   }
}