/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.internal.workdef;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.client.internal.IAtsWorkItemArtifactService;
import org.eclipse.osee.ats.core.client.search.AtsArtifactQuery;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkItemArtifactProviderImpl implements IAtsWorkItemArtifactService {

   @Override
   public Artifact get(IAtsObject atsObject) throws OseeCoreException {
      if (atsObject instanceof Artifact) {
         return (Artifact) atsObject;
      }
      Artifact artifact = AtsArtifactQuery.getArtifactFromId(atsObject.getUuid());
      return artifact;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <A extends Artifact> A get(IAtsWorkItem workItem, Class<?> clazz) throws OseeCoreException {
      Artifact artifact = get(workItem);
      if (clazz.isInstance(artifact)) {
         return (A) artifact;
      }
      return null;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <A extends Artifact> List<A> get(Collection<? extends IAtsWorkItem> workItems, Class<?> clazz) throws OseeCoreException {
      List<A> arts = new ArrayList<A>();
      for (IAtsWorkItem workItem : workItems) {
         Artifact artifact = get(workItem, clazz);
         if (artifact != null) {
            arts.add((A) artifact);
         }
      }
      return arts;
   }

   @Override
   public Collection<? extends IAtsWorkItem> getWorkItems(Collection<Artifact> arts) {
      return Collections.castMatching(IAtsWorkItem.class, arts);
   }

   @Override
   public IAtsTeamWorkflow getParentTeamWorkflow(IAtsWorkItem workItem) throws OseeCoreException {
      Artifact artifact = get(workItem);
      if (artifact.isOfType(AtsArtifactTypes.TeamWorkflow)) {
         return (IAtsTeamWorkflow) artifact;
      } else if (artifact.isOfType(AtsArtifactTypes.Task)) {
         Collection<Artifact> awas = artifact.getRelatedArtifacts(AtsRelationTypes.TeamWfToTask_TeamWf);
         if (awas.isEmpty()) {
            throw new OseeStateException("Task has no parent %s", artifact.toStringWithId());
         }
         return (IAtsTeamWorkflow) awas.iterator().next();
      } else if (artifact.isOfType(AtsArtifactTypes.ReviewArtifact)) {
         Collection<Artifact> awas = artifact.getRelatedArtifacts(AtsRelationTypes.TeamWorkflowToReview_Team);
         if (!awas.isEmpty()) {
            return (IAtsTeamWorkflow) awas.iterator().next();
         }
      }
      return null;
   }

   @Override
   public IAtsTeamDefinition getTeamDefinition(IAtsWorkItem workItem) throws OseeCoreException {
      Artifact art = get(workItem);
      if (art != null) {
         if (art instanceof AbstractWorkflowArtifact) {
            AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) art;
            return awa.getParentTeamWorkflow().getTeamDefinition();
         }
      }
      return null;
   }

}
