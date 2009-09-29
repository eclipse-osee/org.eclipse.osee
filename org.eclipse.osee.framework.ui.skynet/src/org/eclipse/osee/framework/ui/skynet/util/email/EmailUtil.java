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
package org.eclipse.osee.framework.ui.skynet.util.email;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

/**
 * @author Donald G. Dunne
 */
public class EmailUtil {

   private static Pattern p = Pattern.compile(".+@.+\\.[a-z]+");

   public static boolean isEmailValid(String email) {
      return p.matcher(email).matches();
   }

   public static boolean isEmailValid(User user) throws OseeCoreException {
      return isEmailValid(user.getEmail());
   }

   public static Collection<User> getValidEmailUsers(Collection<User> users) throws OseeCoreException {
      Set<User> validUsers = new HashSet<User>();
      for (User user : users) {
         try {
            if (isEmailValid(user)) {
               validUsers.add(user);
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE, ex);
         }
      }
      return validUsers;
   }
}
