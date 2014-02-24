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
package org.eclipse.osee.ats.core.workflow.transition;

import org.eclipse.osee.ats.api.review.IAtsReviewService;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionService;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.ats.api.workflow.transition.IAtsTransitionManager;
import org.eclipse.osee.ats.api.workflow.transition.ITransitionHelper;
import org.eclipse.osee.ats.core.AtsCore;

/**
 * @author Donald G. Dunne
 */
public class TransitionFactory {

   public static IAtsTransitionManager getTransitionManager(ITransitionHelper helper) {
      return getTransitionManager(helper, AtsCore.getUserService(), AtsCore.getReviewService(),
         AtsCore.getWorkItemService(), AtsCore.getWorkDefService(), AtsCore.getAttrResolver());
   }

   public static IAtsTransitionManager getTransitionManager(ITransitionHelper helper, IAtsUserService userService, IAtsReviewService reviewService, IAtsWorkItemService workItemService, IAtsWorkDefinitionService workDefService, IAttributeResolver attrResolver) {
      return new TransitionManager(helper, AtsCore.getUserService(), AtsCore.getReviewService(),
         AtsCore.getWorkItemService(), AtsCore.getWorkDefService(), AtsCore.getAttrResolver());
   }

}
