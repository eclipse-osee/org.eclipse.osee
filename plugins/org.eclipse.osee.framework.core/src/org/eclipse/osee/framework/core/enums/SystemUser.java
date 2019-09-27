/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.enums;

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.jdk.core.type.Id;

/**
 * @author Donald G. Dunne
 */
public final class SystemUser {

   // @formatter:off
   public static final UserToken OseeSystem = UserToken.create(11, "OSEE System", "", "99999999", false, CoreUserGroups.OseeAdmin, CoreUserGroups.OseeAccessAdmin);
   public static final UserToken Anonymous = UserToken.create(1896, "Anonymous", "", "99999998", false);
   public static final UserToken BootStrap = UserToken.create(2184322, "Boot Strap", "bootstrap@osee.org", "bootstrap", true);
   public static final UserToken UnAssigned = UserToken.create(33429, "UnAssigned", "", "99999997", true);
   public static final List<UserToken> values = Arrays.asList(OseeSystem, Anonymous, BootStrap, UnAssigned);
   // @formatter:on

   private SystemUser() {
      // Constants
   }

   public static List<UserToken> values() {
      return values;
   }

   public static boolean isSystemUser(Id user) {
      return values().contains(user);
   }
}