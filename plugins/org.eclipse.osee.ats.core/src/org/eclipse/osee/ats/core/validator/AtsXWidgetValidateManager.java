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
package org.eclipse.osee.ats.core.validator;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.WidgetStatus;

/**
 * @author Donald G. Dunne
 */
public class AtsXWidgetValidateManager {
   public static AtsXWidgetValidateManager instance = new AtsXWidgetValidateManager();
   private final List<AtsXWidgetValidatorProvider> providers = new LinkedList<>();

   protected AtsXWidgetValidateManager() {
   }

   public void validateTransition(List<WidgetResult> results, IValueProvider valueProvider, IAtsWidgetDefinition widgetDef, IAtsStateDefinition fromStateDef, IAtsStateDefinition toStateDef, IAtsServices atsServices) {
      for (AtsXWidgetValidatorProvider provider : providers) {
         for (IAtsXWidgetValidator validator : provider.getValidators()) {
            try {
               WidgetResult status =
                  validator.validateTransition(valueProvider, widgetDef, fromStateDef, toStateDef, atsServices);
               if (!status.isValid()) {
                  results.add(status);
               }
            } catch (Exception ex) {
               results.add(new WidgetResult(WidgetStatus.Exception, widgetDef, ex,
                  String.format("Exception retriving validation for widget [%s] Exception [%s]",
                     validator.getClass().getSimpleName(), ex.getLocalizedMessage()),
                  ex));
               return;
            }
         }
      }
   }

   public void add(AtsXWidgetValidatorProvider provider) {
      providers.add(provider);
   }

}
