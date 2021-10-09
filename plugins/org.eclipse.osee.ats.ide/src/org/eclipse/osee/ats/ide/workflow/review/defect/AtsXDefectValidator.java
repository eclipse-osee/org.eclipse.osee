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

package org.eclipse.osee.ats.ide.workflow.review.defect;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.IValueProvider;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.ats.core.review.ReviewDefectError;
import org.eclipse.osee.ats.core.review.ReviewDefectManager;
import org.eclipse.osee.ats.core.validator.AtsXWidgetValidator;

/**
 * @author Donald G. Dunne
 */
public class AtsXDefectValidator extends AtsXWidgetValidator {

   public static String WIDGET_NAME = "XDefectViewer";

   @Override
   public WidgetResult validateTransition(IAtsWorkItem workItem, IValueProvider provider, WidgetDefinition widgetDef, IAtsStateDefinition fromStateDef, IAtsStateDefinition toStateDef, AtsApi atsServices) {
      WidgetResult result = WidgetResult.Success;
      if (WIDGET_NAME.equals(widgetDef.getXWidgetName())) {
         ReviewDefectManager mgr = new ReviewDefectManager(provider);
         ReviewDefectError error = ReviewDefectValidator.isValid(mgr.getDefectItems());
         return error.toWidgetResult(widgetDef);
      }
      return result;
   }
}
