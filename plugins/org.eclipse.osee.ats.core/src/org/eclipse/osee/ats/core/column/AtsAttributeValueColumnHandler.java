/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.column;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.column.IAtsColumn;
import org.eclipse.osee.ats.api.config.AtsAttributeValueColumn;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.internal.column.ev.AtsColumnService;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Handler for dynamically configured ATS attribute columns
 *
 * @author Donald G. Dunne
 */
public class AtsAttributeValueColumnHandler implements IAtsColumn {

   private final AtsAttributeValueColumn column;
   private final AtsApi atsApi;

   public AtsAttributeValueColumnHandler(AtsAttributeValueColumn column, AtsApi atsApi) {
      this.column = column;
      this.atsApi = atsApi;
   }

   @Override
   public String getColumnText(IAtsObject atsObject) {
      return getColumnText(atsObject, column.getAttrTypeId(), isActionRollup(), isInheritParent(), atsApi);
   }

   private Boolean isInheritParent() {
      Boolean inheritParent = column.isInheritParent();
      return inheritParent == null ? false : inheritParent;
   }

   private Boolean isActionRollup() {
      Boolean actionRollup = column.isActionRollup();
      return actionRollup == null ? false : actionRollup;
   }

   public static String getColumnText(IAtsObject atsObject, long attrTypeId, boolean isActionRollup, boolean isInheritParent, AtsApi atsApi) {
      try {
         if (atsApi.getStoreService().isDeleted(atsObject)) {
            return "<deleted>";
         }
         if (atsObject instanceof IAtsWorkItem) {
            IAtsWorkItem workItem = (IAtsWorkItem) atsObject;

            AttributeTypeToken attributeType = atsApi.getStoreService().getAttributeType(attrTypeId);
            String result = atsApi.getAttributeResolver().getAttributesToStringUniqueList(workItem, attributeType, ";");
            if (Strings.isValid(result)) {
               return result;
            } else if (isInheritParent && !workItem.isTeamWorkflow() && workItem.getParentTeamWorkflow() != null) {
               result = Collections.toString("; ", atsApi.getAttributeResolver().getAttributesToStringUniqueList(
                  workItem.getParentTeamWorkflow(), attributeType, ";"));
               if (Strings.isValid(result)) {
                  return result;
               }
            }
         }
         if (atsObject instanceof IAtsAction && isActionRollup) {
            Collection<IAtsTeamWorkflow> teams = ((IAtsAction) atsObject).getTeamWorkflows();
            Set<String> strs = new HashSet<>();
            strs.add(atsObject.getName());
            for (IAtsTeamWorkflow team : teams) {
               String str = getColumnText(team, attrTypeId, isActionRollup, isInheritParent, atsApi);
               if (Strings.isValid(str)) {
                  strs.add(str);
               }
            }
            return Collections.toString("; ", strs);
         }
      } catch (Exception ex) {
         return AtsColumnService.CELL_ERROR_PREFIX + " - " + ex.getLocalizedMessage();
      }
      return "";
   }

   public static String getColumnText(IAtsObject atsObject, AttributeTypeToken attributeType, boolean isActionRollup, boolean inheritParent, AtsApi atsApi) {
      return getColumnText(atsObject, attributeType.getId(), isActionRollup, inheritParent, atsApi);
   }

   @Override
   public String toString() {
      return "AtsAttributeValueColumnHandler [attrType=" + column.getAttrTypeName() + "]";
   }
}