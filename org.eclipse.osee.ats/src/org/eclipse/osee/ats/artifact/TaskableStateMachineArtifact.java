/*
 * Created on Jun 11, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.artifact;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;

/**
 * @author Donald G. Dunne
 */
public abstract class TaskableStateMachineArtifact extends StateMachineArtifact {

   /**
    * @param parentFactory
    * @param guid
    * @param humanReadableId
    * @param branch
    * @param artifactType
    */
   public TaskableStateMachineArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, ArtifactType artifactType) {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
      registerSMARelation(AtsRelation.SmaToTask_Task);
   }

   @Override
   public ActionArtifact getParentActionArtifact() throws SQLException {
      return null;
   }

   @Override
   public StateMachineArtifact getParentSMA() throws SQLException {
      return null;
   }

   @Override
   public TeamWorkFlowArtifact getParentTeamWorkflow() throws SQLException {
      return null;
   }

   @Override
   public Set<User> getPrivilegedUsers() throws SQLException {
      return null;
   }

   @Override
   public VersionArtifact getTargetedForVersion() throws SQLException {
      return null;
   }

   @Override
   public Date getWorldViewEstimatedReleaseDate() throws OseeCoreException, SQLException {
      return null;
   }

   @Override
   public Date getWorldViewReleaseDate() throws OseeCoreException, SQLException {
      return null;
   }

   @Override
   public String getWorldViewVersion() throws OseeCoreException, SQLException {
      return null;
   }

   @Override
   public boolean showTaskTab() {
      return (isTaskable() || smaMgr.isCompleted());
   }

   @Override
   public void atsDelete(Set<Artifact> deleteArts, Map<Artifact, Object> allRelated) throws OseeCoreException, SQLException {
      super.atsDelete(deleteArts, allRelated);
      for (TaskArtifact taskArt : smaMgr.getTaskMgr().getTaskArtifacts())
         taskArt.atsDelete(deleteArts, allRelated);
   }

   @Override
   public void transitioned(WorkPageDefinition fromPage, WorkPageDefinition toPage, Collection<User> toAssignees, boolean persist) throws OseeCoreException, SQLException {
      super.transitioned(fromPage, toPage, toAssignees, persist);
      for (TaskArtifact taskArt : smaMgr.getTaskMgr().getTaskArtifacts())
         taskArt.parentWorkFlowTransitioned(fromPage, toPage, toAssignees, persist);
   }

}
