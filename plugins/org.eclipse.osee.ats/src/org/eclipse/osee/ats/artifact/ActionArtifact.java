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

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.ats.world.IWorldViewArtifact;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;

/**
 * @author Donald G. Dunne
 */
public class ActionArtifact extends AbstractAtsArtifact implements IWorldViewArtifact {

   public ActionArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, IArtifactType artifactType) throws OseeCoreException {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
   }

   public Collection<TeamWorkFlowArtifact> getTeamWorkFlowArtifacts() throws OseeCoreException {
      return getRelatedArtifactsUnSorted(AtsRelationTypes.ActionToWorkflow_WorkFlow, TeamWorkFlowArtifact.class);
   }

   @Override
   public void atsDelete(Set<Artifact> deleteArts, Map<Artifact, Object> allRelated) throws OseeCoreException {
      super.atsDelete(deleteArts, allRelated);
      // Delete all products
      for (TeamWorkFlowArtifact art : getRelatedArtifacts(AtsRelationTypes.ActionToWorkflow_WorkFlow,
         TeamWorkFlowArtifact.class)) {
         art.atsDelete(deleteArts, allRelated);
      }
   }

   public int getWorldViewStatePercentComplete() throws OseeCoreException {
      if (getTeamWorkFlowArtifacts().size() == 1) {
         return getTeamWorkFlowArtifacts().iterator().next().getWorldViewStatePercentComplete();
      } else {
         double percent = 0;
         int items = 0;
         for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
            if (!team.isCancelled()) {
               percent += team.getWorldViewStatePercentComplete();
               items++;
            }
         }
         if (items > 0) {
            Double rollPercent = percent / items;
            return rollPercent.intValue();
         }
      }
      return 0;
   }

   @Override
   public Artifact getParentAtsArtifact() {
      return null;
   }

   @Override
   public double getWorldViewHoursSpentState() throws OseeCoreException {
      double hours = 0;
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (!team.isCancelled()) {
            hours += team.getWorldViewHoursSpentState();
         }
      }
      return hours;
   }

   @Override
   public double getWorldViewHoursSpentStateReview() throws OseeCoreException {
      double hours = 0;
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (!team.isCancelled()) {
            hours += team.getWorldViewHoursSpentStateReview();
         }
      }
      return hours;
   }

   @Override
   public double getWorldViewHoursSpentStateTask() throws OseeCoreException {
      double hours = 0;
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (!team.isCancelled()) {
            hours += team.getWorldViewHoursSpentStateTask();
         }
      }
      return hours;
   }

   @Override
   public double getWorldViewHoursSpentStateTotal() throws OseeCoreException {
      double hours = 0;
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (!team.isCancelled()) {
            hours += team.getWorldViewHoursSpentStateTotal();
         }
      }
      return hours;
   }

   @Override
   public double getWorldViewHoursSpentTotal() throws OseeCoreException {
      double hours = 0;
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (!team.isCancelled()) {
            hours += team.getWorldViewHoursSpentTotal();
         }
      }
      return hours;
   }

   @Override
   public int getWorldViewPercentCompleteState() throws OseeCoreException {
      double percent = 0;
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (!team.isCancelled()) {
            percent += team.getWorldViewPercentCompleteState();
         }
      }
      if (percent == 0) {
         return 0;
      }
      Double rollPercent = percent / getTeamWorkFlowArtifacts().size();
      return rollPercent.intValue();
   }

   @Override
   public int getWorldViewPercentCompleteStateReview() throws OseeCoreException {
      double percent = 0;
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (!team.isCancelled()) {
            percent += team.getWorldViewPercentCompleteStateReview();
         }
      }
      if (percent == 0) {
         return 0;
      }
      Double rollPercent = percent / getTeamWorkFlowArtifacts().size();
      return rollPercent.intValue();
   }

   @Override
   public int getWorldViewPercentCompleteStateTask() throws OseeCoreException {
      double percent = 0;
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (!team.isCancelled()) {
            percent += team.getWorldViewPercentCompleteStateTask();
         }
      }
      if (percent == 0) {
         return 0;
      }
      Double rollPercent = percent / getTeamWorkFlowArtifacts().size();
      return rollPercent.intValue();
   }

   @Override
   public int getWorldViewPercentCompleteTotal() throws OseeCoreException {
      double percent = 0;
      for (TeamWorkFlowArtifact team : getTeamWorkFlowArtifacts()) {
         if (!team.isCancelled()) {
            percent += team.getWorldViewPercentCompleteTotal();
         }
      }
      if (percent == 0) {
         return 0;
      }
      Double rollPercent = percent / getTeamWorkFlowArtifacts().size();
      return rollPercent.intValue();
   }

}
