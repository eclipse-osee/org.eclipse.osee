/*********************************************************************
 * Copyright (c) 2010 Boeing
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
