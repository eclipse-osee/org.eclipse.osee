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

import org.eclipse.osee.ats.api.review.IAtsReviewService;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.util.IAtsUtilService;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogFactory;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateFactory;
import org.eclipse.osee.ats.api.workflow.state.IAtsWorkStateFactory;
import org.eclipse.osee.ats.core.column.IAtsColumnUtilities;
import org.eclipse.osee.ats.core.internal.AtsEarnedValueService;
import org.eclipse.osee.ats.core.internal.column.ev.AtsColumnUtilities;
import org.eclipse.osee.ats.core.internal.log.AtsLogFactory;
import org.eclipse.osee.ats.core.internal.state.AtsStateFactory;
import org.eclipse.osee.ats.core.internal.state.AtsWorkStateFactory;
import org.eclipse.osee.ats.core.internal.util.AtsUtilService;

/**
 * @author Donald G. Dunne
 */
public final class AtsCoreFactory {

   private AtsCoreFactory() {
      //
   }

   public static IAtsStateFactory newStateFactory(IAttributeResolver attrResolver, IAtsUserService userService) {
      return new AtsStateFactory(attrResolver, new AtsWorkStateFactory(userService));
   }

   public static IAtsLogFactory newLogFactory() {
      return new AtsLogFactory();
   }

   public static IAtsColumnUtilities getColumnUtilities(IAtsReviewService reviewService, IAtsWorkItemService workItemService) {
      return new AtsColumnUtilities(reviewService, workItemService,
         AtsEarnedValueService.getEarnedValueServiceProvider());
   }

   public static IAtsWorkStateFactory getWorkStateFactory(IAtsUserService userService) {
      return new AtsWorkStateFactory(userService);
   }

   public static IAtsLogFactory getLogFactory() {
      return new AtsLogFactory();
   }

   public static IAtsUtilService getUtilService(IAttributeResolver attrResolver) {
      return new AtsUtilService(attrResolver);
   }
}
