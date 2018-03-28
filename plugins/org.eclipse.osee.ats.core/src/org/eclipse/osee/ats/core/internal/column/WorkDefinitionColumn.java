/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.internal.column;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.column.IAtsColumn;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;

/**
 * @author Donald G. Dunne
 */
public class WorkDefinitionColumn implements IAtsColumn {

   private final AtsApi atsApi;

   public WorkDefinitionColumn(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public String getColumnText(IAtsObject atsObject) {
      if (atsObject instanceof IAtsWorkItem) {
         IAtsWorkDefinition workDef = atsApi.getWorkDefinitionService().getWorkDefinition((IAtsWorkItem) atsObject);
         if (workDef != null) {
            return workDef.getName();
         }
      }
      return "";
   }

}
