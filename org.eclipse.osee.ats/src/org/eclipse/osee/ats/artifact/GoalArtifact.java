/*
 * Created on Jul 3, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.artifact;

import java.util.Date;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;

/**
 * @author Donald G. Dunne
 */
public class GoalArtifact extends StateMachineArtifact {

   public static String ARTIFACT_NAME = "Goal";

   public static enum GoalState {
      InWork, Completed, Cancelled
   };

   /**
    * @param parentFactory
    * @param guid
    * @param humanReadableId
    * @param branch
    * @param artifactType
    * @throws OseeDataStoreException
    */
   public GoalArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, ArtifactType artifactType) throws OseeDataStoreException {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
      registerAtsWorldRelation(AtsRelation.Goal_Member);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.artifact.StateMachineArtifact#getParentActionArtifact()
    */
   @Override
   public ActionArtifact getParentActionArtifact() throws OseeCoreException {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.artifact.StateMachineArtifact#getParentSMA()
    */
   @Override
   public StateMachineArtifact getParentSMA() throws OseeCoreException {
      List<Artifact> parents = getRelatedArtifacts(AtsRelation.Goal_Goal);
      if (parents.size() == 0) return null;
      if (parents.size() == 1) return (StateMachineArtifact) parents.iterator().next();
      System.err.println("Two parent goals, what do here?");
      return (StateMachineArtifact) parents.iterator().next();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.artifact.StateMachineArtifact#getParentTeamWorkflow()
    */
   @Override
   public TeamWorkFlowArtifact getParentTeamWorkflow() throws OseeCoreException {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.artifact.StateMachineArtifact#getPrivilegedUsers()
    */
   @Override
   public Set<User> getPrivilegedUsers() throws OseeCoreException {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.artifact.StateMachineArtifact#isTaskable()
    */
   @Override
   public boolean isTaskable() throws OseeCoreException {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.artifact.StateMachineArtifact#getWorldViewReleaseDate()
    */
   @Override
   public Date getWorldViewReleaseDate() throws OseeCoreException {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.artifact.StateMachineArtifact#getWorldViewTargetedVersion()
    */
   @Override
   public VersionArtifact getWorldViewTargetedVersion() throws OseeCoreException {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldViewArtifact#getWorldViewParentID()
    */
   @Override
   public String getWorldViewParentID() throws OseeCoreException {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.hyper.IHyperArtifact#getHyperTargetVersion()
    */
   @Override
   public String getHyperTargetVersion() {
      return null;
   }

}
