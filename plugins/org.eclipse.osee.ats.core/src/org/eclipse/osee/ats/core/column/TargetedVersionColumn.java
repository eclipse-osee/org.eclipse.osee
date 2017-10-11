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
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.config.Versions;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class TargetedVersionColumn extends AbstractServicesColumn {

   public TargetedVersionColumn(AtsApi atsApi) {
      super(atsApi);
   }

   @Override
   public String getText(IAtsObject atsObject) throws Exception {
      String result = "";
      if (atsObject instanceof IAtsAction) {
         IAtsAction action = (IAtsAction) atsObject;
         Set<String> strs = new HashSet<>();
         for (IAtsTeamWorkflow team : action.getTeamWorkflows()) {
            String str = Versions.getTargetedVersionStr(team, atsApi.getVersionService());
            if (Strings.isValid(str)) {
               strs.add(str);
            }
         }
         result = Collections.toString(";", strs);
      } else if (atsObject instanceof IAtsWorkItem) {
         result = Versions.getTargetedVersionStr((IAtsWorkItem) atsObject, atsApi.getVersionService());
      }
      return result;
   }

}
