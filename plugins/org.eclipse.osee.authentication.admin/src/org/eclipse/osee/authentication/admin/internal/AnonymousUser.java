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

package org.eclipse.osee.authentication.admin.internal;

import java.util.Collections;
import org.eclipse.osee.authentication.admin.AuthenticatedUser;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class AnonymousUser implements AuthenticatedUser {

   protected static final String ANONYMOUS_NAME = "Anonymous";

   @Override
   public String getName() {
      return ANONYMOUS_NAME;
   }

   @Override
   public String getDisplayName() {
      return getName();
   }

   @Override
   public String getUserName() {
      return Strings.emptyString();
   }

   @Override
   public String getEmailAddress() {
      return Strings.emptyString();
   }

   @Override
   public boolean isActive() {
      return true;
   }

   @Override
   public Iterable<String> getRoles() {
      return Collections.emptyList();
   }

   @Override
   public boolean isAuthenticated() {
      return false;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (getName() == null ? 0 : getName().hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      AnonymousUser other = (AnonymousUser) obj;
      if (getName() == null) {
         if (other.getName() != null) {
            return false;
         }
      } else if (!getName().equals(other.getName())) {
         return false;
      }
      return true;
   }
}
