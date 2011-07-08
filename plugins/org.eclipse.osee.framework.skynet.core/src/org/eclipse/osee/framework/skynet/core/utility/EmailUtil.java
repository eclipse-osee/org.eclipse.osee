/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.utility;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicUser;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Donald G. Dunne
 */
public class EmailUtil {

   private static Pattern addressPattern = Pattern.compile(".+?@.+?\\.[a-z]+");

   public static boolean isEmailValid(String email) {
      return addressPattern.matcher(email).matches();
   }

   public static boolean isEmailValid(IBasicUser user) throws OseeCoreException {
      return isEmailValid(UserManager.getUser(user).getEmail());
   }

   public static Collection<IBasicUser> getValidEmailUsers(Collection<IBasicUser> users) {
      Set<IBasicUser> validUsers = new HashSet<IBasicUser>();
      for (IBasicUser user : users) {
         try {
            if (isEmailValid(UserManager.getUser(user))) {
               validUsers.add(user);
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return validUsers;
   }

   public static Collection<IBasicUser> getActiveEmailUsers(Collection<IBasicUser> users) {
      Set<IBasicUser> activeUsers = new HashSet<IBasicUser>();
      for (IBasicUser user : users) {
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
