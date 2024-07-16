/*********************************************************************
 * Copyright (c) 2019 Boeing
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
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.column.model.AtsCoreCodeColumn;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Jeremy A. Midvidy
 */
public class FoundInVersionColumn extends AtsCoreCodeColumn {

   public FoundInVersionColumn(AtsApi atsApi) {
      super(AtsColumnTokensDefault.FoundInVersionColumn, atsApi);
   }

   @Override
   public String getText(IAtsObject atsObject) throws Exception {
      String result = "";
      if (atsObject instanceof IAtsAction) {
         IAtsAction action = (IAtsAction) atsObject;
         Set<String> strs = new HashSet<>();
         for (IAtsTeamWorkflow team : action.getTeamWorkflows()) {
            String str = "";
            IAtsVersion ver = atsApi.getVersionService().getFoundInVersion(team);
            if (ver != null) {
               str = ver.toString();
            }
            if (Strings.isValid(str)) {
               strs.add(str);
            }
         }
         result = Collections.toString(";", strs);
      } else if (atsObject instanceof IAtsWorkItem) {
         IAtsWorkItem workItem = (IAtsWorkItem) atsObject;
         IAtsTeamWorkflow teamWf = workItem.getParentTeamWorkflow();
         if (teamWf != null && teamWf.isValid()) {
            IAtsVersion ver = atsApi.getVersionService().getFoundInVersion(teamWf);
            if (ver != null) {
               result = ver.toString();
            }
         }
      }
      return result;
   }
}
