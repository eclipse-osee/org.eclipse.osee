/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.ev.IAtsEarnedValueService;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.api.insertion.IAtsInsertionActivity;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.model.WorkPackage;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.logger.Log;

/**
 * @author Donald G. Dunne
 */
public abstract class AtsAbstractEarnedValueImpl implements IAtsEarnedValueService {

   protected final Log logger;
   protected final IAtsServices services;

   public AtsAbstractEarnedValueImpl(Log logger, IAtsServices services) {
      this.logger = logger;
      this.services = services;
   }

   @Override
   public ArtifactId getWorkPackageId(IAtsWorkItem workItem) {
      ArtifactId artifact = services.getArtifact(workItem);
      Conditions.checkNotNull(artifact, "workItem", "Can't Find Work Package matching %s", workItem.toStringWithId());
      return services.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.WorkPackageReference,
         ArtifactId.SENTINEL);
   }

   @Override
   public IAtsWorkPackage getWorkPackage(IAtsWorkItem workItem) {
      ArtifactId workPackageId = getWorkPackageId(workItem);
      ArtifactToken workPkgArt = services.getArtifact(workPackageId);
      return new WorkPackage(logger, workPkgArt, services);
   }

   @Override
   public Collection<IAtsWorkPackage> getWorkPackageOptions(IAtsObject object) {
      List<IAtsWorkPackage> workPackageOptions = new ArrayList<>();
      getWorkPackageOptions(object, workPackageOptions);
      return workPackageOptions;
   }

   public Collection<IAtsWorkPackage> getWorkPackageOptions(IAtsObject object, List<IAtsWorkPackage> workPackageOptions) {
      // Config objects get work package options from related work package artifacts
      if (object instanceof IAtsConfigObject) {
         IAtsConfigObject configObj = (IAtsConfigObject) object;
         ArtifactId artifact = services.getArtifact(configObj);
         if (artifact != null) {
            for (ArtifactToken workPackageArt : services.getRelationResolver().getRelated(artifact,
               AtsRelationTypes.WorkPackage_WorkPackage)) {
               workPackageOptions.add(new WorkPackage(logger, workPackageArt, services));
            }
         }
      }
      // Team Wf get work package options of Ais and Team Definition
      else if (object instanceof IAtsTeamWorkflow) {
         IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) object;
         getWorkPackageOptions(teamWf.getTeamDefinition(), workPackageOptions);
         for (IAtsActionableItem ai : teamWf.getActionableItems()) {
            getWorkPackageOptions(ai, workPackageOptions);
         }
      }
      // Children work items inherit the work packages options of their parent team workflow
      else if (object instanceof IAtsWorkItem) {
         IAtsWorkItem workItem = (IAtsWorkItem) object;
         // Work Items related to Team Wf get their options from Team Wf
         IAtsTeamWorkflow teamWf = workItem.getParentTeamWorkflow();
         if (teamWf != null) {
            getWorkPackageOptions(teamWf, workPackageOptions);
         }
         // Stand-alone reviews get their options from related AIs and Team Defs
         else if (workItem instanceof IAtsAbstractReview) {
            IAtsAbstractReview review = (IAtsAbstractReview) workItem;
            for (IAtsActionableItem ai : review.getActionableItems()) {
               getWorkPackageOptions(ai, workPackageOptions);
               if (ai.getTeamDefinition() != null) {
                  getWorkPackageOptions(ai.getTeamDefinition(), workPackageOptions);
               }
            }
         }
      }
      return workPackageOptions;
   }

   @Override
   public IAtsWorkPackage getWorkPackage(ArtifactToken artifact) {
      return new WorkPackage(logger, artifact, services);
   }

   @Override
   public Collection<IAtsWorkPackage> getWorkPackages(IAtsInsertionActivity insertionActivity) {
      List<IAtsWorkPackage> workPackages = new ArrayList<>();
      for (ArtifactToken artifact : services.getRelationResolver().getRelated(
         services.getArtifact(insertionActivity.getId()),
         AtsRelationTypes.InsertionActivityToWorkPackage_WorkPackage)) {
         workPackages.add(new WorkPackage(logger, artifact, services));
      }
      return workPackages;
   }

   @Override
   public double getEstimatedHoursFromArtifact(IAtsWorkItem workItem) {
      return services.getAttributeResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.EstimatedHours, 0.0);
   }

   @Override
   public double getEstimatedHoursFromTasks(IAtsWorkItem workItem, IStateToken relatedToState) {
      if (!(workItem instanceof IAtsTeamWorkflow)) {
         return 0;
      }
      return getEstimatedHoursFromTasks((workItem), relatedToState);
   }

   /**
    * Return Estimated Hours for all tasks
    */
   @Override
   public double getEstimatedHoursFromTasks(IAtsWorkItem workItem) {
      if (!(workItem instanceof IAtsTeamWorkflow)) {
         return 0;
      }
      double hours = 0;
      for (IAtsTask task : services.getTaskService().getTask(workItem)) {
         hours += getEstimatedHoursFromArtifact(task);
      }
      return hours;
   }

   @Override
   public double getEstimatedHoursFromReviews(IAtsWorkItem workItem) {
      double hours = 0;
      if (workItem.isTeamWorkflow()) {
         for (IAtsAbstractReview review : services.getReviewService().getReviews((IAtsTeamWorkflow) workItem)) {
            hours += getEstimatedHoursFromArtifact(review);
         }
      }
      return hours;
   }

   @Override
   public double getEstimatedHoursFromReviews(IAtsWorkItem workItem, IStateToken relatedToState) {
      double hours = 0;
      if (workItem.isTeamWorkflow()) {
         IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) workItem;
         for (IAtsAbstractReview review : services.getReviewService().getReviews(teamWf)) {
            if (review.getRelatedToState().equals(relatedToState.getName())) {
               hours += getEstimatedHoursFromArtifact(review);
            }
         }
      }
      return hours;
   }

   @Override
   public double getEstimatedHoursTotal(IAtsWorkItem workItem, IStateToken relatedToState) {
      return getEstimatedHoursFromArtifact(workItem) + getEstimatedHoursFromTasks(workItem,
         relatedToState) + getEstimatedHoursFromReviews(workItem, relatedToState);
   }

   @Override
   public double getEstimatedHoursTotal(IAtsWorkItem workItem) {
      return getEstimatedHoursFromArtifact(workItem) + getEstimatedHoursFromTasks(
         workItem) + getEstimatedHoursFromReviews(workItem);
   }

   @Override
   public void setWorkPackage(IAtsWorkPackage workPackage, IAtsWorkItem workItem, IAtsChangeSet changes) {
      changes.setSoleAttributeValue(workItem, AtsAttributeTypes.WorkPackageReference, workPackage);
   }
}