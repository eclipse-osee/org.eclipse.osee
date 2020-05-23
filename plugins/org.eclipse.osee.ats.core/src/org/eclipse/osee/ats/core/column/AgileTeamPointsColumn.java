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
import org.eclipse.osee.ats.api.IAtsWorkItem;

/**
 * Display Points as either "ats.Points" or "ats.Points Numeric" as configured on Agile Team artifact
 *
 * @author Donald G. Dunne
 */
public class AgileTeamPointsColumn extends AbstractServicesColumn {

   public AgileTeamPointsColumn(AtsApi atsApi) {
      super(atsApi);
   }

   @Override
   public String getText(IAtsObject atsObject) throws Exception {
      String result = "";
      if (atsObject instanceof IAtsWorkItem) {
         result = atsApi.getAgileService().getAgileTeamPointsStr((IAtsWorkItem) atsObject);
      }
      return result;
   }

}
