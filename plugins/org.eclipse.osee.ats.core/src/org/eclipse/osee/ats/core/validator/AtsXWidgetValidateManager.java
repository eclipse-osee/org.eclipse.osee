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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.IValueProvider;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.WidgetStatus;
import org.eclipse.osee.ats.api.workflow.transition.IAtsXWidgetValidator;
import org.eclipse.osee.ats.api.workflow.transition.IAtsXWidgetValidatorProvider;
import org.eclipse.osee.ats.core.util.ArtifactValueProvider;

/**
 * @author Donald G. Dunne
 */
public class AtsXWidgetValidateManager {

   private static List<IAtsXWidgetValidatorProvider> providers;

   public void addWidgetValidatorProvider(IAtsXWidgetValidatorProvider provider) {
      getProviders().add(provider);
   }

   public void removeWidgetValidatorProvider(IAtsXWidgetValidatorProvider provider) {
      getProviders().remove(provider);
   }

   public static List<WidgetResult> validateTransition(IAtsWorkItem workItem, List<WidgetResult> results, IValueProvider valueProvider, IAtsWidgetDefinition widgetDef, IAtsStateDefinition fromStateDef, IAtsStateDefinition toStateDef, AtsApi atsServices) {
      for (IAtsXWidgetValidatorProvider provider : getProviders()) {
         for (IAtsXWidgetValidator validator : provider.getValidators()) {
            try {
               WidgetResult status = validator.validateTransition(workItem, valueProvider, widgetDef, fromStateDef,
                  toStateDef, atsServices);
               if (!status.isValid()) {
                  results.add(status);
               }
            } catch (Exception ex) {
               results.add(new WidgetResult(WidgetStatus.Exception, widgetDef, ex,
                  String.format("Exception retrieving validation for widget [%s] Exception [%s]",
                     validator.getClass().getSimpleName(), ex.getLocalizedMessage()),
                  ex));
            }
         }
      }
      return results;
   }

   static {
      providers = new LinkedList<>();
      providers.add(new AtsCoreXWidgetValidatorProvider());
   }

   static synchronized List<IAtsXWidgetValidatorProvider> getProviders() {
      return providers;
   }

   public static Collection<WidgetResult> validateTransition(IAtsWorkItem workItem, IAtsStateDefinition toStateDef, AtsApi atsApi) {
      List<WidgetResult> results = new ArrayList<>();
      List<IAtsWidgetDefinition> widgetItems =
         atsApi.getWorkDefinitionService().getWidgetsFromLayoutItems(workItem.getStateDefinition());
      List<IAtsWidgetDefinition> headerWidgetItems = atsApi.getWorkDefinitionService().getWidgetsFromLayoutItems(
         workItem.getStateDefinition(), workItem.getWorkDefinition().getHeaderDef().getLayoutItems());
      if (!headerWidgetItems.isEmpty()) {
         for (IAtsWidgetDefinition item : headerWidgetItems) {
            widgetItems.add(item);
         }
      }
      for (IAtsWidgetDefinition widgetDef : widgetItems) {
         ArtifactValueProvider provider = new ArtifactValueProvider(workItem.getStoreObject(), widgetDef, atsApi);
         AtsXWidgetValidateManager.validateTransition(workItem, results, provider, widgetDef,
            workItem.getStateDefinition(), toStateDef, atsApi);
      }
      return results;
   }

}
