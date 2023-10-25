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

import java.util.List;
import java.util.Map;
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
      validateWorkDefReferences(rd);
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

   private void validateWorkDefReferences(XResultData rd) {
      rd.log("\n\nValidating (tx_current==1) Work Definition Reference attrs...");
      List<Map<String, String>> query = atsApi.getQueryService().query( //
         "SELECT DISTINCT(attr.value) FROM osee_attribute attr, osee_txs txs WHERE \n" + //
            "txs.branch_id = 570 and attr.gamma_id = txs.gamma_id \n" + //
            "AND txs.tx_current = 1 AND attr.ATTR_TYPE_ID = " + //
            AtsAttributeTypes.WorkflowDefinitionReference.getIdString());
      for (Map<String, String> entry : query) {
         String idStr = entry.values().iterator().next();
         Long id = Long.valueOf(idStr);
         WorkDefinition workDef = atsApi.getWorkDefinitionService().getWorkDefinition(id);
         if (workDef == null) {
            rd.errorf("Work Def Id = %s - InValid\n", idStr);
         } else {
            rd.logf("Work Def Id = %s - Valid\n", idStr);
         }
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
