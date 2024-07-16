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

import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.column.model.AtsCoreCodeColumn;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class TargetedVersionColumn extends AtsCoreCodeColumn {

   public TargetedVersionColumn(AtsApi atsApi) {
      super(AtsColumnTokensDefault.TargetedVersionColumn, atsApi);
   }

   @Override
   public String getText(IAtsObject atsObject) throws Exception {
      String result = "";
      if (atsObject instanceof IAtsAction) {
         IAtsAction action = (IAtsAction) atsObject;
         Set<String> strs = new HashSet<>();
         for (IAtsTeamWorkflow team : action.getTeamWorkflows()) {
            String str = atsApi.getVersionService().getTargetedVersionStr(team, atsApi.getVersionService());
            if (Strings.isValid(str)) {
               strs.add(str);
            }
         }
         result = Collections.toString(";", strs);
      } else if (atsObject instanceof IAtsWorkItem) {
         result =
            atsApi.getVersionService().getTargetedVersionStr((IAtsWorkItem) atsObject, atsApi.getVersionService());
      }
      return result;
   }

}
