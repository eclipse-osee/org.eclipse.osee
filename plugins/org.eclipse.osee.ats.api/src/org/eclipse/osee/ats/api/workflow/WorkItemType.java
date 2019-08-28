/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workflow;

import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;

/**
 * @author Donald G. Dunne
 */
public enum WorkItemType {

   WorkItem("Work Item", AtsArtifactTypes.AbstractWorkflowArtifact),
   TeamWorkflow("Team Workflow", AtsArtifactTypes.TeamWorkflow),
   Task("Task", AtsArtifactTypes.Task),
   Review("Review", AtsArtifactTypes.AbstractReview),
   Goal("Goal", AtsArtifactTypes.Goal),
   PeerReview("Peer Review", AtsArtifactTypes.PeerToPeerReview),
   DecisionReview("Decision Review", AtsArtifactTypes.DecisionReview),
   AgileSprint("Agile Sprint", AtsArtifactTypes.AgileSprint),
   AgileBacklog("Agile Backlog", AtsArtifactTypes.AgileBacklog);

   private final String displayName;
   private final ArtifactTypeToken artifactType;

   private WorkItemType(String displayName, ArtifactTypeToken artifactType) {
      this.displayName = displayName;
      this.artifactType = artifactType;
   }

   public String getDisplayName() {
      return displayName;
   }

   public ArtifactTypeToken getArtifactType() {
      return artifactType;
   }
}
