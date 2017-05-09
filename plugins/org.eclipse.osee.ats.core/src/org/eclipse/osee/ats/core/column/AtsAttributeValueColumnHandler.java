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
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.column.IAtsColumn;
import org.eclipse.osee.ats.api.config.AtsAttributeValueColumn;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.internal.column.ev.AtsColumnService;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Handler for dynamically configured ATS attribute columns
 *
 * @author Donald G. Dunne
 */
public class AtsAttributeValueColumnHandler implements IAtsColumn {

   private final AtsAttributeValueColumn column;
   private final IAtsServices services;

   public AtsAttributeValueColumnHandler(AtsAttributeValueColumn column, IAtsServices services) {
      this.column = column;
      this.services = services;
   }

   @Override
   public String getColumnText(IAtsObject atsObject) {
      return getColumnText(atsObject, column.getAttrTypeId(), isActionRollup(), isInheritParent(), services);
   }

   private Boolean isInheritParent() {
      Boolean inheritParent = column.isInheritParent();
      return inheritParent == null ? false : inheritParent;
   }

   private Boolean isActionRollup() {
      Boolean actionRollup = column.isActionRollup();
      return actionRollup == null ? false : actionRollup;
   }

   public static String getColumnText(IAtsObject atsObject, long attrTypeId, boolean isActionRollup, boolean isInheritParent, IAtsServices services) {
      try {
         if (services.getStoreService().isDeleted(atsObject)) {
            return "<deleted>";
         }
         if (atsObject instanceof IAtsWorkItem) {
            IAtsWorkItem workItem = (IAtsWorkItem) atsObject;
            AttributeTypeId attributeType = AttributeTypeId.valueOf(attrTypeId);
            String result =
               services.getAttributeResolver().getAttributesToStringUniqueList(workItem, attributeType, ";");
            if (Strings.isValid(result)) {
               return result;
            } else if (isInheritParent && !workItem.isTeamWorkflow() && workItem.getParentTeamWorkflow() != null) {
               result = Collections.toString("; ", services.getAttributeResolver().getAttributesToStringUniqueList(
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
               String str = getColumnText(team, attrTypeId, isActionRollup, isInheritParent, services);
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

   public static String getColumnText(IAtsObject atsObject, AttributeTypeId attributeType, boolean isActionRollup, boolean inheritParent, IAtsServices services) {
      return getColumnText(atsObject, attributeType.getId(), isActionRollup, inheritParent, services);
   }

   @Override
   public String toString() {
      return "AtsAttributeValueColumnHandler [attrType=" + column.getAttrTypeName() + "]";
   }

}
