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

package org.eclipse.osee.ats.core.internal.column;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.column.AtsColumn;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;


/**
 * @author Donald G. Dunne
 */
public class WorkDefinitionColumn implements AtsColumn {

   private final AtsApi atsApi;

   public WorkDefinitionColumn(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public String getColumnText(IAtsObject atsObject) {
      if (atsObject instanceof IAtsWorkItem) {
         WorkDefinition workDef = atsApi.getWorkDefinitionService().getWorkDefinition((IAtsWorkItem) atsObject);
         if (workDef != null) {
            return workDef.getName();
         }
      }
      return "";
   }

}
