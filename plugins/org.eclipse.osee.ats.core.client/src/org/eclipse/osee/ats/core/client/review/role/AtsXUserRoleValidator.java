/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.review.role;

import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.review.IAtsPeerReviewRoleManager;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.util.IValueProvider;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.core.validator.AtsXWidgetValidator;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class AtsXUserRoleValidator extends AtsXWidgetValidator {

   public static String WIDGET_NAME = "XUserRoleViewer";

   @Override
   public WidgetResult validateTransition(IAtsWorkItem workItem, IValueProvider provider, IAtsWidgetDefinition widgetDef, IAtsStateDefinition fromStateDef, IAtsStateDefinition toStateDef, IAtsServices services)  {
      WidgetResult result = WidgetResult.Valid;
      if (WIDGET_NAME.equals(widgetDef.getXWidgetName())) {
         // ReviewDefectValidation converted to provider IValueProvider
         IAtsPeerReviewRoleManager mgr = ((IAtsPeerToPeerReview) workItem).getRoleManager();
         UserRoleError error = UserRoleValidator.isValid(mgr, fromStateDef, toStateDef);
         return error.toWidgetResult(widgetDef);
      }
      return result;
   }

}
