/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.ats.core.column;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.column.model.AtsCoreCodeColumn;

/**
 * @author Donald G. Dunne
 */
public class WorkDefinitionColumn extends AtsCoreCodeColumn {

   public WorkDefinitionColumn(AtsApi atsApi) {
      super(AtsColumnTokensDefault.WorkDefinitionColumn, atsApi);
   }

   @Override
   public String getText(IAtsObject atsObject) {
      if (atsObject instanceof IAtsWorkItem) {
         WorkDefinition workDef = atsApi.getWorkDefinitionService().getWorkDefinition((IAtsWorkItem) atsObject);
         if (workDef != null) {
            return workDef.getName();
         }
      }
      return "";
   }

}
