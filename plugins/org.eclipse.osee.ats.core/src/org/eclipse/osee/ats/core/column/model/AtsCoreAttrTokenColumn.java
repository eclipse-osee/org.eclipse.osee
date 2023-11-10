/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.core.column.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.column.AtsCoreAttrTokColumnToken;
import org.eclipse.osee.ats.api.column.AtsCoreColumn;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.internal.column.AtsColumnService;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.DisplayHint;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class AtsCoreAttrTokenColumn extends AtsCoreColumn {

   private final AtsCoreAttrTokColumnToken columnToken;
   private final String source;

   public AtsCoreAttrTokenColumn(AtsCoreAttrTokColumnToken columnToken, String source, AtsApi atsApi) {
      super(columnToken, atsApi);
      this.columnToken = columnToken;
      this.source = source;
   }

   public AtsCoreAttrTokenColumn(AttributeTypeToken attrType, String source, AtsApi atsApi) {
      this(new AtsCoreAttrTokColumnToken(attrType), source, atsApi);
   }

   @Override
   public String getColumnText(IAtsObject atsObject) {
      return getColumnText(atsObject, columnToken.getAttrTypeId(), isActionRollup(), isInheritParent(), atsApi);
   }

   private Boolean isInheritParent() {
      Boolean inheritParent = columnToken.isInheritParent();
      return inheritParent == null ? false : inheritParent;
   }

   private Boolean isActionRollup() {
      Boolean actionRollup = columnToken.isActionRollup();
      return actionRollup == null ? false : actionRollup;
   }

   public String getColumnText(IAtsObject atsObject, long attrTypeId, boolean isActionRollup, boolean isInheritParent,
      AtsApi atsApi) {
      try {
         if (atsApi.getStoreService().isDeleted(atsObject)) {
            return "<deleted>";
         }
         if (atsObject instanceof IAtsWorkItem) {
            IAtsWorkItem workItem = (IAtsWorkItem) atsObject;

            AttributeTypeToken attributeType = atsApi.tokenService().getAttributeType(attrTypeId);

            if (attributeType.getDisplayHints().contains(DisplayHint.YesNoBoolean)) {
               if (atsApi.getAttributeResolver().isAttributeTypeValid((IAtsWorkItem) atsObject, attributeType)) {
                  Boolean set = atsApi.getAttributeResolver().getSoleAttributeValue(atsObject, attributeType, null);
                  if (set == null) {
                     return "";
                  } else if (set) {
                     return "Yes";
                  } else {
                     return "No";
                  }
               }
            }

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

   public String getColumnText(IAtsObject atsObject, AttributeTypeToken attributeType, boolean isActionRollup,
      boolean inheritParent, AtsApi atsApi) {
      return getColumnText(atsObject, attributeType.getId(), isActionRollup, inheritParent, atsApi);
   }

   @Override
   public String toString() {
      return "AtsCoreAttrTokenColumn [attrType=" + columnToken.getAttrTypeName() + "]";
   }

   @Override
   public AtsCoreAttrTokColumnToken getColumnToken() {
      return columnToken;
   }

   @Override
   public String getSource() {
      return getClass().getSimpleName() + " - " + source;
   }

}