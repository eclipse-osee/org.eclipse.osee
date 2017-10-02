/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core;

import org.eclipse.osee.framework.plugin.core.IWorkbenchUser;
import org.eclipse.osee.framework.plugin.core.IWorkbenchUserService;

/**
 * @author Roberto E. Escobar
 */
public class WorkbenchUserService implements IWorkbenchUserService {

   @Override
   public IWorkbenchUser getUser() {
      return new WorkbenchUserAdaptor(UserManager.getUser());
   }

   private static final class WorkbenchUserAdaptor implements IWorkbenchUser {

      private final User user;

      public WorkbenchUserAdaptor(User user) {
         this.user = user;
      }

      @Override
      public String getEmail() {
         return user.getEmail();
      }

      @Override
      public String getName() {
         return user.getName();
      }

      @Override
      public String getUserID() {
         return user.getUserId();
      }

      @Override
      public boolean isActive() {
         return user.isActive();
      }
   }
}
