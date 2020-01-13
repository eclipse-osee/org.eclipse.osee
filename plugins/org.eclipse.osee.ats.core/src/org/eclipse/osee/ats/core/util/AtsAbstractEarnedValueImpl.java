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
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.ev.IAtsEarnedValueService;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.api.insertion.IAtsInsertionActivity;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.util.AtsUtil;
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
   protected final AtsApi atsApi;

   public AtsAbstractEarnedValueImpl(Log logger, AtsApi atsApi) {
      this.logger = logger;
      this.atsApi = atsApi;
   }

   @Override
   public IAtsWorkPackage getWorkPackageById(ArtifactId workPackageId) {
      IAtsWorkPackage workPackage = null;
      if (workPackageId instanceof IAtsWorkPackage) {
         workPackage = (IAtsWorkPackage) workPackageId;
      } else {
         ArtifactToken art = atsApi.getQueryService().getArtifact(workPackageId);
         if (art.isOfType(AtsArtifactTypes.WorkPackage)) {
            workPackage = new WorkPackage(atsApi.getLogger(), atsApi, art);
         }
      }
      return workPackage;
   }

   @Override
   public ArtifactId getWorkPackageId(IAtsWorkItem workItem) {
      ArtifactId artifact = atsApi.getQueryService().getArtifact(workItem);
      Conditions.checkNotNull(artifact, "workItem", "Can't Find Work Package matching %s", workItem.toStringWithId());
      return atsApi.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.WorkPackageReference,
         ArtifactId.SENTINEL);
   }

   @Override
   public IAtsWorkPackage getWorkPackage(IAtsWorkItem workItem) {
      ArtifactId workPackageId = getWorkPackageId(workItem);
      if (workPackageId.isValid()) {
         ArtifactToken workPkgArt = atsApi.getQueryService().getArtifact(workPackageId);
         return new WorkPackage(logger, atsApi, workPkgArt);
      }
      return null;
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
         ArtifactId artifact = atsApi.getQueryService().getArtifact(configObj);
         if (artifact != null) {
            for (ArtifactToken workPackageArt : atsApi.getRelationResolver().getRelated(artifact,
               AtsRelationTypes.WorkPackage_WorkPackage)) {
               workPackageOptions.add(new WorkPackage(logger, atsApi, workPackageArt));
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
   public IAtsWorkPackage getWorkPackage(ArtifactId artifact) {
      ArtifactToken realArt = atsApi.getQueryService().getArtifact(artifact);
      return new WorkPackage(logger, atsApi, realArt);
   }

   @Override
   public Collection<IAtsWorkPackage> getWorkPackages(IAtsInsertionActivity insertionActivity) {
      List<IAtsWorkPackage> workPackages = new ArrayList<>();
      for (ArtifactToken artifact : atsApi.getRelationResolver().getRelated(
         atsApi.getQueryService().getArtifact(insertionActivity.getId()),
         AtsRelationTypes.InsertionActivityToWorkPackage_WorkPackage)) {
         workPackages.add(new WorkPackage(logger, atsApi, artifact));
      }
      return workPackages;
   }

   @Override
   public double getEstimatedHoursFromArtifact(IAtsWorkItem workItem) {
      return atsApi.getAttributeResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.EstimatedHours, 0.0);
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
      for (IAtsTask task : atsApi.getTaskService().getTask(workItem)) {
         hours += getEstimatedHoursFromArtifact(task);
      }
      return hours;
   }

   @Override
   public double getEstimatedHoursFromReviews(IAtsWorkItem workItem) {
      double hours = 0;
      if (workItem.isTeamWorkflow()) {
         for (IAtsAbstractReview review : atsApi.getReviewService().getReviews((IAtsTeamWorkflow) workItem)) {
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
         for (IAtsAbstractReview review : atsApi.getReviewService().getReviews(teamWf)) {
            if (review.getRelatedToState().equals(relatedToState.getName())) {
               hours += getEstimatedHoursFromArtifact(review);
            }
         }
      }
      return hours;
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

   @Override
   public double getRemainHoursFromArtifact(IAtsWorkItem workItem) {
      if (workItem.isCompletedOrCancelled()) {
         return 0;
      }
      double est = atsApi.getAttributeResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.EstimatedHours, 0.0);
      if (est == 0) {
         return getEstimatedHoursFromArtifact(workItem);
      }
      return est - est * PercentCompleteTotalUtil.getPercentCompleteTotal(workItem, atsApi) / 100.0;
   }

   @Override
   public double getRemainHoursTotal(IAtsWorkItem workItem) {
      return getRemainHoursFromArtifact(workItem) + getRemainFromTasks(workItem) + getRemainFromReviews(workItem);
   }

   /**
    * Return Remain Hours for all tasks
    */
   @Override
   public double getRemainFromTasks(IAtsWorkItem workItem) {
      if (!workItem.isTeamWorkflow()) {
         return 0;
      }
      double hours = 0;
      for (IAtsTask task : atsApi.getTaskService().getTasks((IAtsTeamWorkflow) workItem)) {
         hours += getRemainHoursFromArtifact(task);
      }
      return hours;
   }

   @Override
   public double getRemainFromReviews(IAtsWorkItem workItem) {
      if (workItem.isTeamWorkflow()) {
         return atsApi.getEarnedValueService().getEstimatedHoursFromReviews(workItem);
      }
      return 0;
   }

   @Override
   public double getManHrsPerDayPreference() {
      return AtsUtil.DEFAULT_HOURS_PER_WORK_DAY;
   }

   /**
    * Return Total Percent Complete / # Tasks for "Related to State" stateName
    *
    * @param relatedToState state name of parent workflow's state
    */
   @Override
   public int getPercentCompleteFromTasks(IAtsWorkItem workItem, IStateToken relatedToState) {
      int spent = 0, result = 0;
      if (workItem.isTeamWorkflow()) {
         Collection<IAtsTask> tasks = atsApi.getTaskService().getTasks((IAtsTeamWorkflow) workItem, relatedToState);
         for (IAtsTask task : tasks) {
            spent += PercentCompleteTotalUtil.getPercentCompleteTotal(task, atsApi);
         }
         if (spent > 0) {
            result = spent / tasks.size();
         }
      }
      return result;
   }

   @Override
   public int getPercentCompleteFromTasks(IAtsWorkItem workItem) {
      int spent = 0, result = 0;
      if (workItem.isTeamWorkflow()) {
         Collection<IAtsTask> tasks = atsApi.getTaskService().getTasks((IAtsTeamWorkflow) workItem);
         for (IAtsTask task : tasks) {
            spent += PercentCompleteTotalUtil.getPercentCompleteTotal(task, atsApi);
         }
         if (spent > 0) {
            result = spent / tasks.size();
         }
      }
      return result;
   }

   /**
    * Return Total Percent Complete / # Reviews for "Related to State" stateName
    *
    * @param relatedToState state name of parent workflow's state
    */
   @Override
   public int getPercentCompleteFromReviews(IAtsWorkItem workItem, IStateToken relatedToState) {
      int spent = 0;
      if (workItem.isTeamWorkflow()) {
         Collection<IAtsAbstractReview> reviews =
            atsApi.getReviewService().getReviews((IAtsTeamWorkflow) workItem, relatedToState);
         for (IAtsAbstractReview review : reviews) {
            spent += PercentCompleteTotalUtil.getPercentCompleteTotal(review, atsApi);
         }
         if (spent == 0) {
            return 0;
         }
         spent = spent / reviews.size();
      }
      return spent;
   }

}
