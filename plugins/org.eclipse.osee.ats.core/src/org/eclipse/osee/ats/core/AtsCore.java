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
package org.eclipse.osee.ats.core;

import org.eclipse.osee.ats.api.notify.IAtsNotificationService;
import org.eclipse.osee.ats.api.review.IAtsReviewService;
import org.eclipse.osee.ats.api.review.IAtsReviewServiceProvider;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionService;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workflow.IAtsBranchService;
import org.eclipse.osee.ats.api.workflow.IAtsBranchServiceProvider;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemServiceProvider;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogFactory;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateFactory;
import org.eclipse.osee.ats.core.column.IAtsColumnUtilities;
import org.eclipse.osee.ats.core.config.IAtsConfig;
import org.eclipse.osee.ats.core.internal.AtsConfigUtility;
import org.eclipse.osee.ats.core.internal.AtsEarnedValueService;
import org.eclipse.osee.ats.core.internal.column.ev.AtsColumnUtilities;
import org.eclipse.osee.ats.core.internal.log.AtsLogFactory;
import org.eclipse.osee.ats.core.internal.state.AtsStateFactory;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;

/**
 * @author Donald G. Dunne
 */
public class AtsCore {

   private static IAtsColumnUtilities columnUtilities;
   private static IAtsLogFactory logFactory;
   private static IAtsStateFactory stateFactory;
   private static IAttributeResolver attrResolver;
   private static IAtsWorkDefinitionService workDefService;
   private static IAtsNotificationService notifyService;
   private static IAtsUserService userService;
   private static Boolean started = null;
   private static IAtsWorkItemServiceProvider workItemServiceProvider;
   private static IAtsBranchServiceProvider branchServiceProvider;
   private static IAtsReviewServiceProvider reviewServiceProvider;

   public void setAtsUserService(IAtsUserService userService) {
      AtsCore.userService = userService;
   }

   public static void setAtsWorkDefService(IAtsWorkDefinitionService workDefService) {
      AtsCore.workDefService = workDefService;
   }

   public static void setAtsNotificationService(IAtsNotificationService notifyService) {
      AtsCore.notifyService = notifyService;
   }

   public static void setAtsWorkItemServiceProvider(IAtsWorkItemServiceProvider workItemServiceProvider) {
      AtsCore.workItemServiceProvider = workItemServiceProvider;
   }

   public static void setAtsAttributeResolver(IAttributeResolver attrResolver) {
      AtsCore.attrResolver = attrResolver;
   }

   public static void setAtsBranchServiceProvider(IAtsBranchServiceProvider branchServiceProvider) {
      AtsCore.branchServiceProvider = branchServiceProvider;
   }

   public static void setAtsReviewServiceProvider(IAtsReviewServiceProvider reviewServiceProvider) {
      AtsCore.reviewServiceProvider = reviewServiceProvider;
   }

   public void start() throws OseeCoreException {
      Conditions.checkNotNull(userService, "IAtsUserService");
      Conditions.checkNotNull(workDefService, "IAtsWorkDefinitionService");
      Conditions.checkNotNull(workItemServiceProvider, "IAtsWorkItemServiceProvider");
      Conditions.checkNotNull(attrResolver, "IAttributeResolver");
      Conditions.checkNotNull(notifyService, "IAtsNotificationService");
      Conditions.checkNotNull(branchServiceProvider, "IAtsBranchServiceProvider");
      Conditions.checkNotNull(reviewServiceProvider, "IAtsReviewServiceProvider");
      started = true;
   }

   public static IAttributeResolver getAttrResolver() throws OseeStateException {
      checkStarted();
      return attrResolver;
   }

   private static void checkStarted() throws OseeStateException {
      if (started == null) {
         throw new OseeStateException("AtsCore did not start");
      }
   }

   public static IAtsWorkItemService getWorkItemService() throws OseeStateException {
      checkStarted();
      return workItemServiceProvider.getWorkItemService();
   }

   public static IAtsWorkDefinitionService getWorkDefService() throws OseeStateException {
      checkStarted();
      return workDefService;
   }

   public static IAtsNotificationService getNotifyService() throws OseeStateException {
      checkStarted();
      return notifyService;
   }

   public static IAtsUserService getUserService() throws OseeStateException {
      checkStarted();
      return userService;
   }

   public static IAtsColumnUtilities getColumnUtilities() {
      if (columnUtilities == null) {
         columnUtilities = new AtsColumnUtilities(AtsEarnedValueService.getEarnedValueServiceProvider());
      }
      return columnUtilities;
   }

   public static IAtsStateFactory getStateFactory() {
      if (stateFactory == null) {
         stateFactory = new AtsStateFactory(attrResolver);
      }
      return stateFactory;
   }

   public static IAtsLogFactory getLogFactory() {
      if (logFactory == null) {
         logFactory = new AtsLogFactory();
      }
      return logFactory;
   }

   public static IAtsConfig getAtsConfig() throws OseeStateException {
      return AtsConfigUtility.getAtsConfigProvider().getAtsConfig();
   }

   public static IAtsBranchService getBranchService() throws OseeCoreException {
      return branchServiceProvider.getBranchService();
   }

   public static IAtsReviewService getReviewService() throws OseeCoreException {
      return reviewServiceProvider.getReviewService();
   }

}
