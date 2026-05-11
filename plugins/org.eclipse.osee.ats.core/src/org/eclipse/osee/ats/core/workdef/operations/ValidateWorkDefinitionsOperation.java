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
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.LayoutItem;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.framework.core.data.ArtifactToken;
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
      XResultData rd = new XResultData();
      validateWidgetsAndAttrs(rd);
      XResultData rpt = atsApi.getServerEndpoints().getTestEp().validateWorkDefReferences();
      rd.merge(rpt);
      validateNoMissingWorkDefRefs(rd);
      return rd;
   }

   private void validateNoMissingWorkDefRefs(XResultData rd) {
      rd.log("\n\nValidating Missing Work Definition Reference attrs...");
      for (ArtifactToken art : atsApi.getQueryService().createQuery(WorkItemType.TeamWorkflow).andNotExists(
         AtsAttributeTypes.WorkflowDefinitionReference).getResultArtifacts().getList()) {
         rd.errorf("Work Def Ref NOT exists for Id = %s\n", art.getIdString());
      }
   }

   private void validateWidgetsAndAttrs(XResultData rd) {
      rd.log("Validating Work Definition widgets and widget attrs...");
      for (WorkDefinition workDef : atsApi.getWorkDefinitionService().getAllWorkDefinitions()) {
         rd.logf("Validating workDef [%s]\n", workDef.getName());
         rd.logf("--- Validating header\n");
         for (LayoutItem layoutItem : workDef.getHeaderDef().getLayoutItems()) {
            validateLayoutItem(layoutItem, rd);
         }
         for (StateDefinition stateDef : workDef.getStates()) {
            rd.logf("--- Validating state [%s]\n", stateDef.getName());
            for (LayoutItem layoutItem : stateDef.getLayoutItems()) {
               validateLayoutItem(layoutItem, rd);
            }
         }
      }
   }

   private void validateLayoutItem(LayoutItem layoutItem, XResultData rd) {
      if (layoutItem instanceof WidgetDefinition) {
         WidgetDefinition widgetDef = (WidgetDefinition) layoutItem;
         AttributeTypeToken attrTypeTok = widgetDef.getAttributeType();
         if (attrTypeTok != null) {
            AttributeTypeToken attrType = atsApi.tokenService().getAttributeType(attrTypeTok.getId());
            if (attrType == null) {
               rd.errorf("------ Type %s for widget %s is not valid.\n", attrTypeTok.toStringWithId(),
                  widgetDef.toStringWithId());
            } else {
               rd.logf("------ Type %s for widget %s\n", (attrTypeTok.isValid() ? attrTypeTok.toStringWithId() : "N/A"),
                  widgetDef.toStringWithId());
            }
         }
      } else if (layoutItem instanceof CompositeLayoutItem) {
         CompositeLayoutItem comp = (CompositeLayoutItem) layoutItem;
         for (LayoutItem compLayoutItem : comp.getLayoutItems()) {
            validateLayoutItem(compLayoutItem, rd);
         }
      }
   }

}
