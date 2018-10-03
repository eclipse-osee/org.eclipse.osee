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
package org.eclipse.osee.ats.core.column;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;

/**
 * @author Donald G. Dunne
 */
public class ParentTitleColumn extends AbstractServicesColumn {

   public ParentTitleColumn(AtsApi atsApi) {
      super(atsApi);
   }

   @Override
   String getText(IAtsObject atsObject) throws Exception {
      String result = null;
      if (atsObject instanceof IAtsWorkItem && (!(atsObject instanceof IAtsTeamWorkflow))) {
         IAtsTeamWorkflow parentTeam = ((IAtsWorkItem) atsObject).getParentTeamWorkflow();
         if (parentTeam != null) {
            result = parentTeam.getName();
         }
      }
      return result;
   }

}
