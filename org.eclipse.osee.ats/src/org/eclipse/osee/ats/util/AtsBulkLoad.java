/*
 * Created on Sep 3, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.util;

import java.sql.SQLException;
import java.util.Collection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;

/**
 * Convenience methods to bulk load ATS objects based on currently held objects
 * 
 * @author Donald G. Dunne
 */
public class AtsBulkLoad {

   public static void loadFromActions(Collection<? extends Artifact> actions) throws SQLException {
      RelationManager.getRelatedArtifacts(actions, 4, AtsRelation.SmaToTask_Task,
            AtsRelation.ActionToWorkflow_WorkFlow,
            AtsRelation.TeamWorkflowToReview_Review);
   }

   public static void loadFromTeamWorkflows(Collection<? extends Artifact> teams) throws SQLException {
      RelationManager.getRelatedArtifacts(teams, 3, AtsRelation.SmaToTask_Task,
            AtsRelation.TeamWorkflowToReview_Team, AtsRelation.ActionToWorkflow_Action);
   }
}
