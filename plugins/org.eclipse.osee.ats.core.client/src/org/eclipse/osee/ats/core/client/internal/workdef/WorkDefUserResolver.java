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
package org.eclipse.osee.ats.core.client.internal.workdef;

import java.util.logging.Level;
import org.eclipse.osee.ats.api.workdef.IUserResolver;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;

/**
 * @author Donald G. Dunne
 */
public class WorkDefUserResolver implements IUserResolver {

   public WorkDefUserResolver() {
   }

   @Override
   public boolean isUserIdValid(String userId) {
      boolean result = false;
      try {
         result = UserManager.getUserByUserId(userId) != null;
      } catch (OseeCoreException ex) {
         // Do nothing
      }
      return result;
   }

   @Override
   public boolean isUserNameValid(String name) {
      boolean result = false;
      try {
         result = UserManager.getUserByName(name) != null;
      } catch (OseeCoreException ex) {
         // Do Nothing
      }
      return result;
   }

   @Override
   public String getUserIdByName(String name) {
      String userId = null;
      try {
         User user = UserManager.getUserByName(name);
         if (user != null) {
            userId = user.getUserId();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return userId;
   }
}
