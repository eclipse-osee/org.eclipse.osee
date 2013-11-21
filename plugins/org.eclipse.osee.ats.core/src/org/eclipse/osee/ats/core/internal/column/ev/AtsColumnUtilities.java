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
import org.eclipse.osee.ats.core.column.IAtsColumnUtilities;
import org.eclipse.osee.ats.core.column.IAtsColumnUtility;
import org.eclipse.osee.ats.core.internal.column.TeamColumnUtility;

/**
 * @author Donald G. Dunne
 */
public class AtsColumnUtilities implements IAtsColumnUtilities {

   private final IAtsEarnedValueServiceProvider earnedValueServiceProvider;
   private WorkPackageIdUtility workPackageIdUtility;
   private ActivityIdUtility activityIdUtility;
   private WorkPackageNameUtility workPackageNameUtility;
   private WorkPackageTypeUtility workPackageTypeUtility;
   private WorkPackageProgramUtility workPackageProgramUtility;
   private IAtsColumnUtility workPackageGuidUtility;
   private TeamColumnUtility teamColumnUtility;
   private final IAtsWorkItemService workItemService;
   private final IAtsReviewService reviewService;
   public static final String CELL_ERROR_PREFIX = "!Error";

   public AtsColumnUtilities(IAtsReviewService reviewService, IAtsWorkItemService workItemService, IAtsEarnedValueServiceProvider earnedValueServiceProvider) {
      this.reviewService = reviewService;
      this.workItemService = workItemService;
      this.earnedValueServiceProvider = earnedValueServiceProvider;
   }

   @Override
   public IAtsColumnUtility getTeamUtility() {
      if (teamColumnUtility == null) {
         teamColumnUtility = new TeamColumnUtility(workItemService, reviewService);
      }
      return teamColumnUtility;
   }

   @Override
   public IActivityIdUtility getActivityIdUtility() {
      if (activityIdUtility == null) {
         activityIdUtility = new ActivityIdUtility(earnedValueServiceProvider);
      }
      return activityIdUtility;
   }

   @Override
   public IAtsColumnUtility getWorkPackageNameUtility() {
      if (workPackageNameUtility == null) {
         workPackageNameUtility = new WorkPackageNameUtility(earnedValueServiceProvider);
      }
      return workPackageNameUtility;
   }

   @Override
   public IAtsColumnUtility getWorkPackageIdUtility() {
      if (workPackageIdUtility == null) {
         workPackageIdUtility = new WorkPackageIdUtility(earnedValueServiceProvider);
      }
      return workPackageIdUtility;
   }

   @Override
   public IAtsColumnUtility getWorkPackageTypeUtility() {
      if (workPackageTypeUtility == null) {
         workPackageTypeUtility = new WorkPackageTypeUtility(earnedValueServiceProvider);
      }
      return workPackageTypeUtility;
   }

   @Override
   public IAtsColumnUtility getWorkPackageProgramUtility() {
      if (workPackageProgramUtility == null) {
         workPackageProgramUtility = new WorkPackageProgramUtility(earnedValueServiceProvider);
      }
      return workPackageProgramUtility;
   }

   @Override
   public IAtsColumnUtility getWorkPackageGuidUtility() {
      if (workPackageGuidUtility == null) {
         workPackageGuidUtility = new WorkPackageGuidUtility(earnedValueServiceProvider);
      }
      return workPackageGuidUtility;
   }

}