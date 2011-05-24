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
package org.eclipse.osee.ats.core.review;

import java.util.Collection;
import org.eclipse.osee.ats.core.review.defect.DefectManager;
import org.eclipse.osee.ats.core.review.role.UserRole;
import org.eclipse.osee.ats.core.workflow.StateManager;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.IBasicUser;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.IATSStateMachineArtifact;

/**
 * @author Donald G. Dunne
 */
public class PeerToPeerReviewArtifact extends AbstractReviewArtifact implements IReviewArtifact, IATSStateMachineArtifact {

   public PeerToPeerReviewArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, IArtifactType artifactType) throws OseeCoreException {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
      defectManager = new DefectManager(this);
   }

   @Override
   public Collection<IBasicUser> getImplementers() throws OseeCoreException {
      Collection<IBasicUser> users = StateManager.getImplementersByState(this, PeerToPeerReviewState.Review);
      for (UserRole role : userRoleManager.getUserRoles()) {
         users.add(role.getUser());
      }
      return users;
   }

}
