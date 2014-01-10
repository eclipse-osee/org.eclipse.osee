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
import org.eclipse.osee.ats.api.notify.IAtsNotificationServiceProvider;
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
import org.eclipse.osee.ats.api.workflow.state.IAtsWorkStateFactory;
import org.eclipse.osee.ats.core.column.IAtsColumnUtilities;
import org.eclipse.osee.ats.core.config.IAtsConfig;
import org.eclipse.osee.ats.core.config.IAtsConfigProvider;
import org.eclipse.osee.ats.core.internal.AtsEarnedValueService;
import org.eclipse.osee.ats.core.internal.column.ev.AtsColumnUtilities;
import org.eclipse.osee.ats.core.internal.log.AtsLogFactory;
import org.eclipse.osee.ats.core.internal.state.AtsStateFactory;
import org.eclipse.osee.ats.core.internal.state.AtsWorkStateFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Donald G. Dunne
 */
public class AtsCore {

   public static final String PLUGIN_ID = "org.eclipse.osee.ats.core";
   private static IAtsColumnUtilities columnUtilities;
   private static IAtsLogFactory logFactory;
   private static IAtsStateFactory stateFactory;
   private static IAttributeResolver attrResolver;
   private static IAtsWorkDefinitionService workDefService;
   private static IAtsNotificationServiceProvider notifyServiceProvider;
   private static IAtsUserService userService;
   private static Boolean started = null;
   private static IAtsWorkItemServiceProvider workItemServiceProvider;
   private static IAtsBranchServiceProvider branchServiceProvider;
   private static IAtsReviewServiceProvider reviewServiceProvider;
   private static AtsWorkStateFactory workStateFactory;
   private static IAtsConfigProvider atsConfigProvider;

   public void setAtsConfigProvider(IAtsConfigProvider atsConfigProvider) {
      AtsCore.atsConfigProvider = atsConfigProvider;
   }

   public void setAtsUserService(IAtsUserService userService) {
      AtsCore.userService = userService;
   }

   public static void setAtsWorkDefService(IAtsWorkDefinitionService workDefService) {
      AtsCore.workDefService = workDefService;
   }

   public static void setAtsNotificationServiceProvider(IAtsNotificationServiceProvider notifyServiceProvider) {
      AtsCore.notifyServiceProvider = notifyServiceProvider;
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
      Conditions.checkNotNull(notifyServiceProvider, "IAtsNotificationService");
      Conditions.checkNotNull(branchServiceProvider, "IAtsBranchServiceProvider");
      Conditions.checkNotNull(reviewServiceProvider, "IAtsReviewServiceProvider");
      System.out.println("ATS - AtsCore started");
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

   public static IAtsNotificationService getNotifyService() throws OseeCoreException {
      checkStarted();
      return notifyServiceProvider.getNotifyService();
   }

   public static IAtsUserService getUserService() throws OseeStateException {
      checkStarted();
      return userService;
   }

   public static IAtsColumnUtilities getColumnUtilities() {
      if (columnUtilities == null) {
         columnUtilities =
            new AtsColumnUtilities(getReviewService(), getWorkItemService(),
               AtsEarnedValueService.getEarnedValueServiceProvider());
      }
      return columnUtilities;
   }

   public static IAtsStateFactory getStateFactory() {
      if (stateFactory == null) {
         stateFactory = new AtsStateFactory(attrResolver, getWorkStateFactory());
      }
      return stateFactory;
   }

   public static IAtsWorkStateFactory getWorkStateFactory() {
      if (workStateFactory == null) {
         workStateFactory = new AtsWorkStateFactory(getUserService());
      }
      return workStateFactory;
   }

   public static IAtsLogFactory getLogFactory() {
      if (logFactory == null) {
         logFactory = new AtsLogFactory();
      }
      return logFactory;
   }

   public static IAtsConfig getAtsConfig() throws OseeStateException {
      return atsConfigProvider.getAtsConfig();
   }

   public static IAtsBranchService getBranchService() throws OseeCoreException {
      return branchServiceProvider.getBranchService();
   }

   public static IAtsReviewService getReviewService() throws OseeCoreException {
      return reviewServiceProvider.getReviewService();
   }

   public static IAtsConfigProvider getAtsConfigProvider() {
      return atsConfigProvider;
   }

}
