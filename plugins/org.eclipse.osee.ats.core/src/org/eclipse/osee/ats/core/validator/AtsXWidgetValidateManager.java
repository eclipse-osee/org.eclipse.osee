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
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.IValueProvider;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.ats.api.workdef.WidgetStatus;
import org.eclipse.osee.ats.api.workflow.transition.IAtsXWidgetValidator;
import org.eclipse.osee.ats.api.workflow.transition.IAtsXWidgetValidatorProvider;
import org.eclipse.osee.ats.core.util.ArtifactValueProvider;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;

/**
 * @author Donald G. Dunne
 */
public class AtsXWidgetValidateManager {

   private static final List<IAtsXWidgetValidatorProvider> providers = new LinkedList<>();
   private static IAtsServices services;

   public void setAtsServices(IAtsServices services) {
      AtsXWidgetValidateManager.services = services;
   }

   public void addWidgetValidatorProvider(IAtsXWidgetValidatorProvider provider) {
      System.err.println("Adding provider " + provider);
      providers.add(provider);
   }

   public static List<WidgetResult> validateTransition(List<WidgetResult> results, IValueProvider valueProvider, IAtsWidgetDefinition widgetDef, IAtsStateDefinition fromStateDef, IAtsStateDefinition toStateDef, IAtsServices atsServices) {
      for (IAtsXWidgetValidatorProvider provider : providers) {
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
            }
         }
      }
      return results;
   }

   public static Collection<WidgetResult> validateTransition(IAtsWorkItem workItem, IAtsStateDefinition toStateDef) {
      List<WidgetResult> results = new ArrayList<>();
      if (services == null) {
         throw new OseeStateException("ATS Services can not be null");
      }
      for (IAtsWidgetDefinition widgetDef : services.getWorkDefinitionAdmin().getWidgetsFromLayoutItems(
         workItem.getStateDefinition())) {
         ArtifactValueProvider provider = new ArtifactValueProvider(workItem.getStoreObject(), widgetDef, services);
         AtsXWidgetValidateManager.validateTransition(results, provider, widgetDef, workItem.getStateDefinition(),
            toStateDef, services);
      }
      return results;
   }

   public void add(IAtsXWidgetValidatorProvider provider) {
      providers.add(provider);
   }

   public void remove(IAtsXWidgetValidatorProvider provider) {
      providers.remove(provider);
   }

}
