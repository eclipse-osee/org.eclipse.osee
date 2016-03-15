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

import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.internal.column.ev.AtsColumnService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * Return current list of assignees sorted if in Working state or string of implementors surrounded by ()
 *
 * @author Donald G. Dunne
 */
public class StateColumn implements IAtsColumn {

   public static StateColumn instance = new StateColumn();

   @Override
   public String getColumnText(IAtsObject atsObject) {
      String result = "";
      try {
         if (atsObject instanceof IAtsWorkItem) {
            return ((IAtsWorkItem) atsObject).getStateMgr().getCurrentStateName();
         } else if (atsObject instanceof IAtsAction) {
            Set<String> strs = new HashSet<>();
            for (IAtsTeamWorkflow team : ((IAtsAction) atsObject).getTeamWorkflows()) {
               strs.add(team.getStateMgr().getCurrentStateName());
            }
            return org.eclipse.osee.framework.jdk.core.util.Collections.toString(";", strs);
         }
      } catch (OseeCoreException ex) {
         return AtsColumnService.CELL_ERROR_PREFIX + " - " + ex.getLocalizedMessage();
      }
      return result;
   }
}
