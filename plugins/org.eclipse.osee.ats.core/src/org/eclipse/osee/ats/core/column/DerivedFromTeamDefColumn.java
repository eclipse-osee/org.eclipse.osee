/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Donald G. Dunne
 */
public class DerivedFromTeamDefColumn extends AbstractDerivedFromColumn {

   public DerivedFromTeamDefColumn(AtsApi atsApi) {
      super(atsApi);
   }

   @Override
   String getText(IAtsObject atsObject) throws Exception {
      return getDerivedFromTeamDef(atsObject, atsApi);
   }

   public static String getDerivedFromTeamDef(Object element, AtsApi atsApi) {
      ArtifactToken derivedFrom = getDerivedFrom(element, atsApi);
      if (derivedFrom.isValid()) {
         IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItem(derivedFrom);
         if (workItem != null) {
            IAtsTeamWorkflow teamWf = workItem.getParentTeamWorkflow();
            return teamWf.getTeamDefinition().getName();
         }
      }
      return "";
   }

}
