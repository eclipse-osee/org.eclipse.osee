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

   @Override
   public ActionArtifact getParentActionArtifact() throws OseeCoreException {
      return null;
   }

   @Override
   public StateMachineArtifact getParentSMA() throws OseeCoreException {
      List<Artifact> parents = getRelatedArtifacts(AtsRelation.Goal_Goal);
      if (parents.size() == 0) {
         return null;
      }
      if (parents.size() == 1) {
         return (StateMachineArtifact) parents.iterator().next();
      }
      System.err.println("Two parent goals, what do here?");
      return (StateMachineArtifact) parents.iterator().next();
   }

   @Override
   public TeamWorkFlowArtifact getParentTeamWorkflow() throws OseeCoreException {
      return null;
   }

   @Override
   public Set<User> getPrivilegedUsers() throws OseeCoreException {
      return null;
   }

   @Override
   public boolean isTaskable() throws OseeCoreException {
      return false;
   }

   @Override
   public Date getWorldViewReleaseDate() throws OseeCoreException {
      return null;
   }

   @Override
   public VersionArtifact getWorldViewTargetedVersion() throws OseeCoreException {
      return null;
   }

   @Override
   public String getWorldViewParentID() throws OseeCoreException {
      return null;
   }

   @Override
   public String getHyperTargetVersion() {
      return null;
   }

}
