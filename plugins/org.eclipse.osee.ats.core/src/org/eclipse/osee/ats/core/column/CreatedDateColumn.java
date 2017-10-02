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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;

/**
 * @author Donald G. Dunne
 */
public class CreatedDateColumn extends AbstractServicesColumn {

   public CreatedDateColumn(IAtsServices services) {
      super(services);
   }

   @Override
   public String getText(IAtsObject atsObject) throws Exception {
      return getDateStr(atsObject);
   }

   public static Date getDate(Object object) {
      Date result = null;
      if (object instanceof IAtsAction) {
         IAtsAction action = (IAtsAction) object;
         if (!action.getTeamWorkflows().isEmpty()) {
            result = getDate(action.getTeamWorkflows().iterator().next());
         }
      } else if (object instanceof IAtsWorkItem) {
         result = ((IAtsWorkItem) object).getCreatedDate();
      }
      return result;
   }

   public static String getDateStr(Object object) {
      Set<String> strs = new HashSet<>();
      if (object instanceof IAtsAction) {
         IAtsAction action = (IAtsAction) object;
         for (IAtsTeamWorkflow team : action.getTeamWorkflows()) {
            Date date = getDate(team);
            if (date == null) {
               strs.add("");
            } else {
               strs.add(DateUtil.getMMDDYYHHMM(getDate(team)));
            }
         }
         return Collections.toString(";", strs);

      }
      return DateUtil.getMMDDYYHHMM(getDate(object));
   }

}
