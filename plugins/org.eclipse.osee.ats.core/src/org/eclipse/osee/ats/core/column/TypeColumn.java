/*********************************************************************
 * Copyright (c) 2016 Boeing
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
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.column.model.AtsCoreCodeColumn;

/**
 * @author Donald G. Dunne
 */
public class TypeColumn extends AtsCoreCodeColumn {

   public TypeColumn(AtsApi atsApi) {
      super(AtsColumnTokensDefault.TypeColumn, atsApi);
   }

   @Override
   public String getText(IAtsObject atsObject) {
      if (!atsApi.getStoreService().isDeleted(atsObject)) {
         if (atsObject instanceof IAtsTeamWorkflow) {
            return ((IAtsTeamWorkflow) atsObject).getTeamDefinition().getName() + " Workflow";
         } else if (atsApi.getAgileService().isBacklog(atsObject)) {
            return "Backlog";
         } else {
            return atsApi.getStoreService().getArtifactType(atsObject).getName();
         }
      }
      return "(Deleted)";
   }
}