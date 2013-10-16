/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.workflow.state;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.core.model.impl.WorkStateImpl;
import org.eclipse.osee.ats.core.users.UsersByIds;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkStateFactory {

   public static Pattern storagePattern = Pattern.compile("^(.*?);(.*?);(.*?);(.*?)$");

   public static String toXml(IAtsStateManager stateMgr, String stateName) throws OseeCoreException {
      StringBuffer sb = new StringBuffer(stateName);
      sb.append(";");
      sb.append(UsersByIds.getStorageString(stateMgr.getAssignees(stateName)));
      sb.append(";");
      double hoursSpent = stateMgr.getHoursSpent(stateName);
      if (hoursSpent > 0) {
         sb.append(stateMgr.getHoursSpentStr(stateName));
      }
      sb.append(";");
      int percentComplete = stateMgr.getPercentComplete(stateName);
      if (percentComplete > 0) {
         sb.append(percentComplete);
      }
      return sb.toString();
   }

   public static WorkStateImpl getFromXml(String xml) throws OseeCoreException {
      WorkStateImpl state = new WorkStateImpl("Unknown");
      if (Strings.isValid(xml)) {
         Matcher m = storagePattern.matcher(xml);
         if (m.find()) {
            state.setName(m.group(1));
            if (!m.group(3).equals("")) {
               state.setHoursSpent(new Float(m.group(3)).doubleValue());
            }
            if (!m.group(4).equals("")) {
               state.setPercentComplete(Integer.valueOf(m.group(4)).intValue());
            }
            state.setAssignees(UsersByIds.getUsers(m.group(2)));
         } else {
            throw new OseeArgumentException("Can't unpack state data [%s]", xml);
         }
      }
      return state;
   }

}
