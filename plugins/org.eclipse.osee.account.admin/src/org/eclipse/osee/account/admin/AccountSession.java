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

package org.eclipse.osee.account.admin;

import java.util.Date;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Roberto E. Escobar
 */
public interface AccountSession {

   Long getAccountId();

   String getSessionToken();

   Date getCreatedOn();

   Date getLastAccessedOn();

   String getAccessedFrom();

   String getAccessDetails();

   AccountSession SENTINEL = createSentinel();

   public static AccountSession createSentinel() {
      final class AccountSessionSentinel extends NamedIdBase implements AccountSession {

         @Override
         public Long getAccountId() {
            return null;
         }

         @Override
         public String getSessionToken() {
            return null;
         }

         @Override
         public Date getCreatedOn() {
            return null;
         }

         @Override
         public Date getLastAccessedOn() {
            return null;
         }

         @Override
         public String getAccessedFrom() {
            return null;
         }

         @Override
         public String getAccessDetails() {
            return null;
         }

      }
      return new AccountSessionSentinel();
   }

}
