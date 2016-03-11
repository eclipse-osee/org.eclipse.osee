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
package org.eclipse.osee.ats.core.internal.column.ev;

import org.eclipse.osee.ats.api.ev.IAtsEarnedValueServiceProvider;
import org.eclipse.osee.ats.api.review.IAtsReviewService;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.ats.core.column.IActivityIdUtility;
import org.eclipse.osee.ats.core.column.IAtsColumnService;
import org.eclipse.osee.ats.core.column.IAtsColumn;
import org.eclipse.osee.ats.core.internal.column.TeamColumn;

/**
 * @author Donald G. Dunne
 */
public class AtsColumnService implements IAtsColumnService {

   private final IAtsEarnedValueServiceProvider earnedValueServiceProvider;
   private WorkPackageIdColumn workPackageIdUtility;
   private ActivityIdColumn activityIdUtility;
   private WorkPackageNameColumn workPackageNameUtility;
   private WorkPackageTypeColumn workPackageTypeUtility;
   private WorkPackageProgramColumn workPackageProgramUtility;
   private IAtsColumn workPackageGuidUtility;
   private TeamColumn teamColumnUtility;
   private final IAtsWorkItemService workItemService;
   private final IAtsReviewService reviewService;
   public static final String CELL_ERROR_PREFIX = "!Error";

   public AtsColumnService(IAtsReviewService reviewService, IAtsWorkItemService workItemService, IAtsEarnedValueServiceProvider earnedValueServiceProvider) {
      this.reviewService = reviewService;
      this.workItemService = workItemService;
      this.earnedValueServiceProvider = earnedValueServiceProvider;
   }

   @Override
   public IAtsColumn getTeamColumn() {
      if (teamColumnUtility == null) {
         teamColumnUtility = new TeamColumn(workItemService, reviewService);
      }
      return teamColumnUtility;
   }

   @Override
   public IActivityIdUtility getActivityIdColumn() {
      if (activityIdUtility == null) {
         activityIdUtility = new ActivityIdColumn(earnedValueServiceProvider);
      }
      return activityIdUtility;
   }

   @Override
   public IAtsColumn getWorkPackageNameColumn() {
      if (workPackageNameUtility == null) {
         workPackageNameUtility = new WorkPackageNameColumn(earnedValueServiceProvider);
      }
      return workPackageNameUtility;
   }

   @Override
   public IAtsColumn getWorkPackageIdColumn() {
      if (workPackageIdUtility == null) {
         workPackageIdUtility = new WorkPackageIdColumn(earnedValueServiceProvider);
      }
      return workPackageIdUtility;
   }

   @Override
   public IAtsColumn getWorkPackageTypeColumn() {
      if (workPackageTypeUtility == null) {
         workPackageTypeUtility = new WorkPackageTypeColumn(earnedValueServiceProvider);
      }
      return workPackageTypeUtility;
   }

   @Override
   public IAtsColumn getWorkPackageProgramColumn() {
      if (workPackageProgramUtility == null) {
         workPackageProgramUtility = new WorkPackageProgramColumn(earnedValueServiceProvider);
      }
      return workPackageProgramUtility;
   }

   @Override
   public IAtsColumn getWorkPackageGuidColumn() {
      if (workPackageGuidUtility == null) {
         workPackageGuidUtility = new WorkPackageGuidColumn(earnedValueServiceProvider);
      }
      return workPackageGuidUtility;
   }

}