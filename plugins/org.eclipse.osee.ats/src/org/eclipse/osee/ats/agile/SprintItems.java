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
package org.eclipse.osee.ats.agile;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * Used to validate workflows for use in sprints / backlogs
 *
 * @author Donald G. Dunne
 */
public class SprintItems {

   private final Collection<? extends Artifact> awas;
   private boolean commonSelectedSprint;
   private boolean noBacklogDetected;
   private boolean multipleBacklogsDetected;
   private Artifact commonBacklog;
   private Set<Artifact> multipleSprints;

   public SprintItems(final Collection<? extends Artifact> awas) {
      this.awas = awas;
      validate();
   }

   public void validate() {
      commonBacklog = null;
      multipleSprints = new HashSet<>();
      commonSelectedSprint = true;
      noBacklogDetected = false;
      multipleBacklogsDetected = false;
      for (Artifact artifact : awas) {
         if (artifact instanceof AbstractWorkflowArtifact) {
            AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) artifact;
            try {
               Artifact relatedBacklogArt =
                  (Artifact) AtsClientService.get().getAgileService().getRelatedBacklogArt(awa);
               if (relatedBacklogArt == null) {
                  noBacklogDetected = true;
               } else if (commonBacklog == null) {
                  commonBacklog = relatedBacklogArt;
               } else if (commonBacklog.notEqual(relatedBacklogArt)) {
                  multipleBacklogsDetected = true;
               }
            } catch (ArtifactDoesNotExist ex) {
               // do nothing
               noBacklogDetected = true;
            }

            if (commonSelectedSprint) {
               try {
                  Artifact sprintArt = awa.getRelatedArtifact(AtsRelationTypes.AgileSprintToItem_Sprint);
                  multipleSprints.add(sprintArt);
               } catch (ArtifactDoesNotExist ex) {
                  // do nothing
                  commonSelectedSprint = false;
               }
            }
         }
      }
   }

   /**
    * @return true if all items have same sprint selected
    */
   public boolean isCommonSelectedSprint() {
      return commonSelectedSprint;
   }

   /**
    * @return true if any item is not part of a backlog
    */
   public boolean isNoBacklogDetected() {
      return noBacklogDetected;
   }

   /**
    * @return true if items belong to more than 1 backlog
    */
   public boolean isMultipleBacklogsDetected() {
      return multipleBacklogsDetected;
   }

   public IAgileBacklog getCommonBacklog() {
      IAgileBacklog backlog = null;
      if (commonBacklog != null) {
         backlog = AtsClientService.get().getWorkItemFactory().getAgileBacklog(commonBacklog);
      }
      return backlog;
   }

   public Set<IAgileSprint> getMultipleSprints() {
      Set<IAgileSprint> sprints = new HashSet<>();
      for (Artifact art : multipleSprints) {
         sprints.add(AtsClientService.get().getWorkItemFactory().getAgileSprint(art));
      }
      return sprints;
   }

}
