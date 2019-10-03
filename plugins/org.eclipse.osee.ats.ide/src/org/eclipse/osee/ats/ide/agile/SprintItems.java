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
package org.eclipse.osee.ats.ide.agile;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * Used to validate workflows for use in sprints / backlogs
 *
 * @author Donald G. Dunne
 */
public class SprintItems {

   private final Collection<? extends Artifact> workItemArts;
   private boolean commonSelectedSprint;
   private boolean noBacklogDetected;
   private boolean multipleBacklogsDetected;
   private Artifact commonBacklog;
   private Set<ArtifactToken> multipleSprints;

   public SprintItems(final Collection<? extends Artifact> workItemArts) {
      this.workItemArts = workItemArts;
      validate();
   }

   public void validate() {
      commonBacklog = null;
      multipleSprints = new HashSet<>();
      commonSelectedSprint = false;
      noBacklogDetected = false;
      multipleBacklogsDetected = false;
      for (Artifact workItemArt : workItemArts) {
         if (workItemArt instanceof IAtsWorkItem) {
            IAtsWorkItem workItem = (IAtsWorkItem) workItemArt;
            try {
               ArtifactId backlogArt = AtsClientService.get().getRelationResolver().getRelatedOrNull(workItem,
                  AtsRelationTypes.AgileBacklog_AgileBacklog);
               Artifact relatedBacklogArt = AtsClientService.get().getQueryServiceClient().getArtifact(backlogArt);
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

            multipleSprints.addAll(AtsClientService.get().getRelationResolver().getRelated(workItem,
               AtsRelationTypes.AgileSprintToItem_AgileSprint));
            commonSelectedSprint = multipleSprints.size() <= 1;
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
         backlog = AtsClientService.get().getWorkItemService().getAgileBacklog(commonBacklog);
      }
      return backlog;
   }

   public Set<IAgileSprint> getMultipleSprints() {
      Set<IAgileSprint> sprints = new HashSet<>();
      for (ArtifactToken art : multipleSprints) {
         sprints.add(AtsClientService.get().getWorkItemService().getAgileSprint(art));
      }
      return sprints;
   }

}
