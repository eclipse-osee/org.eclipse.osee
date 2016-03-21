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
import org.eclipse.osee.ats.api.config.AtsAttributeValueColumn;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.internal.column.ev.AtsColumnService;
import org.eclipse.osee.framework.core.data.IAttributeType;
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
      return getColumnText(atsObject, column.getAttrTypeId(), column.isActionRollup(), services);
   }

   public static String getColumnText(IAtsObject atsObject, long attrTypeId, boolean isActionRollup, IAtsServices services) {
      try {
         if (services.getStoreService().isDeleted(atsObject)) {
            return "<deleted>";
         }
         if (atsObject instanceof IAtsWorkItem) {
            return services.getAttributeResolver().getAttributesToStringUniqueList(atsObject,
               services.getStoreService().getAttributeType(attrTypeId), ";");
         }
         if (isActionRollup && (atsObject instanceof IAtsAction)) {
            Collection<IAtsTeamWorkflow> teams = ((IAtsAction) atsObject).getTeamWorkflows();
            Set<String> strs = new HashSet<>();
            strs.add(atsObject.getName());
            for (IAtsTeamWorkflow team : teams) {
               String str = getColumnText(team, attrTypeId, isActionRollup, services);
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

   public static String getColumnText(IAtsObject atsObject, IAttributeType attributeType, boolean isActionRollup, IAtsServices services) {
      return getColumnText(atsObject, attributeType.getGuid(), isActionRollup, services);
   }

}
