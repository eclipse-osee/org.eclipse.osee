/*********************************************************************
 * Copyright (c) 2011 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.core.validator;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.review.IAtsPeerReviewRoleManager;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.review.UserRoleError;
import org.eclipse.osee.ats.api.util.IValueProvider;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.framework.core.widget.WidgetId;

/**
 * @author Donald G. Dunne
 */
public class AtsXUserRoleValidator extends AtsXWidgetValidator {

   @Override
   public WidgetResult validateTransition(IAtsWorkItem workItem, IValueProvider provider, WidgetDefinition widgetDef,
      StateDefinition fromStateDef, StateDefinition toStateDef, AtsApi atsApi) {
      WidgetResult result = WidgetResult.Success;
      WidgetId widgetId = widgetDef.getWidgetId();
      if (widgetId.equals(WidgetIdAts.XUserRoleViewerWidget)) {
         IAtsPeerReviewRoleManager roleMgr = ((IAtsPeerToPeerReview) workItem).getRoleManager();
         UserRoleError error = roleMgr.validateRoleTypeMinimums(fromStateDef, roleMgr);
         return error.toWidgetResult(widgetDef);
      }
      return result;
   }

}
