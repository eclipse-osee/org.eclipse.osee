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

/**
 * @author Donald G. Dunne
 */
public class PrIdColumn extends AbstractServicesColumn {

   public PrIdColumn(AtsApi atsApi) {
      super(atsApi);
   }

   @Override
   public String getText(IAtsObject atsObject) throws Exception {
      if (atsObject instanceof IAtsWorkItem) {
         IAtsTeamWorkflow teamWf = ((IAtsWorkItem) atsObject).getParentTeamWorkflow();
         String atsId = teamWf.getAtsId();
         if (atsId.startsWith("PR")) {
            return teamWf.getAtsId();
         }
         for (IAtsTeamWorkflow sibling : atsApi.getWorkItemService().getSiblings(teamWf)) {
            String siblingId = sibling.getAtsId();
            if (siblingId.startsWith("PR")) {
               return siblingId;
            }
         }
      }
      return "";
   }
}
