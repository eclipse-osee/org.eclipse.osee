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
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * Pack and unpack userIds denoted by <userid><userid> string
 *
 * @author Donald G. Dunne
 */
public class UsersByIds {

   public Pattern userPattern = Pattern.compile("<(.*?)>");

   public String getStorageString(Collection<AtsUser> users) {
      StringBuffer sb = new StringBuffer();
      for (AtsUser u : users) {
         sb.append("<" + u.getUserId() + ">");
      }
      return sb.toString();
   }

   public List<AtsUser> getUsers(String sorageString, IAtsUserService userService) {
      List<AtsUser> users = new ArrayList<>();
      Matcher m = userPattern.matcher(sorageString);
      while (m.find()) {
         String userId = m.group(1);
         if (!Strings.isValid(userId)) {
            throw new IllegalArgumentException("Blank userId specified.");
         }
         try {
            AtsUser u = userService.getUserByUserId(m.group(1));
            users.add(u);
         } catch (Exception ex) {
            OseeLog.log(UsersByIds.class, Level.SEVERE, ex);
         }
      }
      return users;
   }

}
