/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.core.workdef.operations;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.LayoutItem;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class ValidateWorkDefinitionsOperation {

   private final AtsApi atsApi;

   public ValidateWorkDefinitionsOperation(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   public XResultData run() {
      XResultData results = new XResultData();
      for (WorkDefinition workDef : atsApi.getWorkDefinitionService().getAllWorkDefinitions()) {
         results.logf("Validating workDef [%s]\n", workDef.getName());
         results.logf("--- Validating header\n");
         for (LayoutItem layoutItem : workDef.getHeaderDef().getLayoutItems()) {
            validateLayoutItem(layoutItem, results);
         }
         for (StateDefinition stateDef : workDef.getStates()) {
            results.logf("--- Validating state [%s]\n", stateDef.getName());
            for (LayoutItem layoutItem : stateDef.getLayoutItems()) {
               validateLayoutItem(layoutItem, results);
            }
         }
      }
      return results;
   }

   private void validateLayoutItem(LayoutItem layoutItem, XResultData results) {
      if (layoutItem instanceof WidgetDefinition) {
         WidgetDefinition widgetDef = (WidgetDefinition) layoutItem;
         AttributeTypeToken attrTypeTok = widgetDef.getAttributeType();
         if (attrTypeTok != null) {
            AttributeTypeToken attrType = atsApi.tokenService().getAttributeType(attrTypeTok.getId());
            if (attrType == null) {
               results.errorf("------ Type %s for widget %s is not valid.\n", attrTypeTok.toStringWithId(),
                  widgetDef.toStringWithId());
            } else {
               results.logf("------ Type %s for widget %s\n",
                  (attrTypeTok.isValid() ? attrTypeTok.toStringWithId() : "N/A"), widgetDef.toStringWithId());
            }
         }
      } else if (layoutItem instanceof CompositeLayoutItem) {
         CompositeLayoutItem comp = (CompositeLayoutItem) layoutItem;
         for (LayoutItem compLayoutItem : comp.getLayoutItems()) {
            validateLayoutItem(compLayoutItem, results);
         }
      }
   }

}
