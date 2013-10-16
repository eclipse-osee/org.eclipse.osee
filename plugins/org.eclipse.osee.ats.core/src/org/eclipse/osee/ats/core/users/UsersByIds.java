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
package org.eclipse.osee.ats.core.users;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.core.AtsCore;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class UsersByIds {

   public static Pattern userPattern = Pattern.compile("<(.*?)>");

   public static String getStorageString(Collection<IAtsUser> users) throws OseeCoreException {
      StringBuffer sb = new StringBuffer();
      for (IAtsUser u : users) {
         sb.append("<" + u.getUserId() + ">");
      }
      return sb.toString();
   }

   public static List<IAtsUser> getUsers(String sorageString) {
      List<IAtsUser> users = new ArrayList<IAtsUser>();
      Matcher m = userPattern.matcher(sorageString);
      while (m.find()) {
         String userId = m.group(1);
         if (!Strings.isValid(userId)) {
            throw new IllegalArgumentException("Blank userId specified.");
         }
         try {
            IAtsUser u = AtsCore.getUserService().getUserById(m.group(1));
            users.add(u);
         } catch (Exception ex) {
            OseeLog.log(AtsCore.class, Level.SEVERE, ex);
         }
      }
      return users;
   }

}
