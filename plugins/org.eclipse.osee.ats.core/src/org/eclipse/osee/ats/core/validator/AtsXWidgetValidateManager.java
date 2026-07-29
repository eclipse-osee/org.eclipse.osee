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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.IValueProvider;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.WidgetStatus;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WidgetDef;
import org.eclipse.osee.ats.api.workflow.transition.IAtsXWidgetValidator;
import org.eclipse.osee.ats.api.workflow.transition.IAtsXWidgetValidatorProvider;
import org.eclipse.osee.ats.core.util.ArtifactValueProvider;

/**
 * @author Donald G. Dunne
 */
public class AtsXWidgetValidateManager {

   private static List<IAtsXWidgetValidatorProvider> providers = new LinkedList<>();

   public AtsXWidgetValidateManager() {
      // for jax-rs
   }

   public AtsXWidgetValidateManager(boolean clearCache) {
      if (clearCache) {
         providers.clear();
      }
   }

   public void addWidgetValidatorProvider(IAtsXWidgetValidatorProvider provider) {
      getProviders().add(provider);
   }

   public void removeWidgetValidatorProvider(IAtsXWidgetValidatorProvider provider) {
      getProviders().remove(provider);
   }

   public static List<WidgetResult> validateTransition(IAtsWorkItem workItem, List<WidgetResult> results,
      IValueProvider valueProvider, WidgetDef widgetDef, StateDefinition fromStateDef,
      StateDefinition toStateDef, AtsApi atsApi) {
      List<IAtsXWidgetValidatorProvider> providers = getProviders();
      for (IAtsXWidgetValidatorProvider provider : providers) {
         for (IAtsXWidgetValidator validator : provider.getValidators()) {
            try {
               WidgetResult status = validator.validateTransition(workItem, valueProvider, widgetDef, fromStateDef,
                  toStateDef, atsApi);

               if (status != null) {
                  if (!status.isSuccess()) {
                     results.add(status);
                  }
               }
            } catch (Exception ex) {
               results.add(new WidgetResult(WidgetStatus.Exception, ex,
                  String.format("Exception retrieving validation for widget [%s] Exception [%s]",
                     validator.getClass().getSimpleName(), ex.getLocalizedMessage()),
                  ex));
            }
         }
      }
      return results;
   }

   static synchronized List<IAtsXWidgetValidatorProvider> getProviders() {
      return providers;
   }

   public static Collection<WidgetResult> validateTransition(IAtsWorkItem workItem, StateDefinition toStateDef,
      AtsApi atsApi) {
      List<WidgetResult> results = new ArrayList<>();
      List<WidgetDef> widgetItems =
         atsApi.getWorkDefinitionService().getWidgetsFromLayoutItems(workItem.getStateDefinition());
      List<WidgetDef> headerWidgetItems = atsApi.getWorkDefinitionService().getWidgetsFromLayoutItems(
         workItem.getStateDefinition(), workItem.getWorkDefinition().getHeaderDef().getLayoutItems());
      if (!headerWidgetItems.isEmpty()) {
         for (WidgetDef item : headerWidgetItems) {
            widgetItems.add(item);
         }
      }
      for (WidgetDef widgetDef : widgetItems) {
         ArtifactValueProvider provider = new ArtifactValueProvider(workItem.getStoreObject(), widgetDef, atsApi);
         AtsXWidgetValidateManager.validateTransition(workItem, results, provider, widgetDef,
            workItem.getStateDefinition(), toStateDef, atsApi);
      }
      return results;
   }

}
