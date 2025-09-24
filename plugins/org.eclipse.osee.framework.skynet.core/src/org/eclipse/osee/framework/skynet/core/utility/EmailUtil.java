/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.skynet.core.utility;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.data.OseeUser;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Donald G. Dunne
 */
public class EmailUtil {

   private static Pattern addressPattern = Pattern.compile(".+?@.+?\\.[a-z]+");

   public static boolean isEmailValid(String email) {
      if (Strings.isInvalid(email)) {
         return false;
      }
      return addressPattern.matcher(email).matches();
   }

   public static boolean isEmailValid(OseeUser user) {
      return isEmailValid(user.getEmail());
   }

   public static Collection<OseeUser> getValidEmailUsers(Collection<OseeUser> users) {
      Set<OseeUser> validUsers = new HashSet<>();
      for (OseeUser user : users) {
         try {
            if (isEmailValid(user)) {
               validUsers.add(user);
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return validUsers;
   }

   public static Collection<OseeUser> getActiveEmailUsers(Collection<OseeUser> users) {
      Set<OseeUser> activeUsers = new HashSet<>();
      for (OseeUser user : users) {
         try {
            if (user.isActive()) {
               activeUsers.add(user);
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return activeUsers;
   }

}
