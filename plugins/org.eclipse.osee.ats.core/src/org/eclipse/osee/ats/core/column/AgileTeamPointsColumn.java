/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.column;

import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.IAtsWorkItem;

/**
 * Display Points as either "ats.Points" or "ats.Points Numeric" as configured on Agile Team artifact
 *
 * @author Donald G. Dunne
 */
public class AgileTeamPointsColumn extends AbstractServicesColumn {

   public AgileTeamPointsColumn(IAtsServices services) {
      super(services);
   }

   @Override
   public String getText(IAtsObject atsObject) throws Exception {
      String result = "";
      if (atsObject instanceof IAtsWorkItem) {
         result = services.getAgileService().getAgileTeamPointsStr((IAtsWorkItem) atsObject);
      }
      return result;
   }

}
