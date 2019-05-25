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
package org.eclipse.osee.account.admin;

import org.eclipse.osee.account.rest.model.AccountWebPreferences;
import org.eclipse.osee.framework.jdk.core.type.NamedId;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Roberto E. Escobar
 */
public interface Account extends NamedId {

   boolean isActive();

   Account SENTINEL = createSentinel();

   String getEmail();

   String getUserName();

   AccountPreferences getPreferences();

   AccountWebPreferences getWebPreferences();

   public static Account createSentinel() {
      final class AccountSentinel extends NamedIdBase implements Account {

         @Override
         public boolean isActive() {
            return false;
         }

         @Override
         public String getEmail() {
            return null;
         }

         @Override
         public String getUserName() {
            return null;
         }

         @Override
         public AccountPreferences getPreferences() {
            return null;
         }

         @Override
         public AccountWebPreferences getWebPreferences() {
            return null;
         }
      }
      return new AccountSentinel();
   }
}
