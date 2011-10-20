/*
 * Created on Oct 19, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.operation;

import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.core.action.ActionArtifact;
import org.eclipse.osee.ats.core.internal.Activator;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.type.AtsRelationTypes;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

public class MoveTeamWorkflowsOperation extends AbstractOperation {

   private final TeamWorkFlowArtifact destTeamWorkflow;
   private final Collection<TeamWorkFlowArtifact> sourceTeamWorkflows;
   private final String destActionTitle;

   public MoveTeamWorkflowsOperation(String operationName, TeamWorkFlowArtifact destTeamWorkflows, Collection<TeamWorkFlowArtifact> sourceTeamWorkflows, String destActionTitle) {
      super(operationName, Activator.PLUGIN_ID);
      this.destTeamWorkflow = destTeamWorkflows;
      this.sourceTeamWorkflows = sourceTeamWorkflows;
      this.destActionTitle = destActionTitle;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws OseeCoreException {
      if (sourceTeamWorkflows.isEmpty()) {
         throw new OseeArgumentException("ERROR", "Must provide Source Team Workflow(s).");
      }
      if (destTeamWorkflow == null) {
         throw new OseeArgumentException("ERROR", "Must provide Destination Team Workflow.");
      }

      SkynetTransaction transaction = new SkynetTransaction(AtsUtilCore.getAtsBranch(), getName());
      if (Strings.isValid(destActionTitle)) {
         destTeamWorkflow.getParentActionArtifact().setName(destActionTitle);
      }
      for (TeamWorkFlowArtifact teamArt : sourceTeamWorkflows) {
         ActionArtifact parentAction = teamArt.getParentActionArtifact();
         teamArt.deleteRelations(AtsRelationTypes.ActionToWorkflow_Action);
         teamArt.clearCaches();
         teamArt.addRelation(AtsRelationTypes.ActionToWorkflow_Action, destTeamWorkflow.getParentActionArtifact());
         teamArt.persist(transaction);
         boolean allDeleted = true;
         for (RelationLink link : parentAction.getRelations(AtsRelationTypes.ActionToWorkflow_Action)) {
            if (!link.isDeleted()) {
               allDeleted = false;
            }
         }
         if (allDeleted) {
            parentAction.deleteAndPersist(transaction);
         }
      }
      destTeamWorkflow.getParentActionArtifact().persist(transaction);
      transaction.execute();
   }

}
