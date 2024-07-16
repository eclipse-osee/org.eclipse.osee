/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.core.internal.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.workflow.WorkState;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.api.workflow.state.IAtsWorkStateFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkStateFactory implements IAtsWorkStateFactory {

   public Pattern storagePattern = Pattern.compile("^(.*?);(.*?);(.*?);(.*?)$");
   public Pattern userPattern = Pattern.compile("<(.*?)>");
   private final IAtsUserService userService;

   public AtsWorkStateFactory(IAtsUserService userService) {
      this.userService = userService;
   }

   @Override
   public String toStoreStr(IAtsStateManager stateMgr, String stateName) {
      StringBuffer sb = new StringBuffer(stateName);
      sb.append(";");
      sb.append(getStorageString(stateMgr.getAssignees(stateName)));
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

   @Override
   public WorkState fromStoreStr(String storeStr) {
      WorkState state = WorkState.create("Unknown");
      if (Strings.isValid(storeStr)) {
         Matcher m = storagePattern.matcher(storeStr);
         if (m.find()) {
            state.setName(m.group(1));
            if (!m.group(3).equals("")) {
               state.setHoursSpent(Float.valueOf(m.group(3)).doubleValue());
            }
            if (!m.group(4).equals("")) {
               state.setPercentComplete(Integer.valueOf(m.group(4)).intValue());
            }
            String userStr = m.group(2);
            List<AtsUser> users = getUsers(userStr);
            state.setAssignees(users);
         } else {
            throw new OseeArgumentException("Can't unpack state data [%s]", storeStr);
         }
      }
      return state;
   }

   @Override
   public String getStorageString(Collection<AtsUser> users) {
      StringBuffer sb = new StringBuffer();
      for (AtsUser u : users) {
         sb.append("<" + u.getUserId() + ">");
      }
      return sb.toString();
   }

   @Override
   public List<AtsUser> getUsers(String sorageString) {
      List<AtsUser> users = new ArrayList<>();
      Matcher m = userPattern.matcher(sorageString);
      while (m.find()) {
         String userId = m.group(1);
         if (!Strings.isValid(userId)) {
            throw new IllegalArgumentException("Blank userId specified.");
         }
         try {
            String uId = m.group(1);
            AtsUser u = userService.getUserByUserId(uId);
            Conditions.checkNotNull(u, "userById " + uId);
            users.add(u);
         } catch (Exception ex) {
            OseeLog.log(AtsWorkStateFactory.class, Level.SEVERE, ex);
         }
      }
      return users;
   }

}
