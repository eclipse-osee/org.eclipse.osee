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

/**
 * @author Donald G. Dunne
 */
public final class DemoUsers {

   // @formatter:off
   public static final UserToken Joe_Smith = UserToken.create(61106791, "Joe Smith", "joe@boeing.com", "3333", true, false, false);
   public static final UserToken Kay_Jones = UserToken.create(5896672, "Kay Jones", "kay@boeing.com", "4444", true, false, false);
   public static final UserToken Jason_Michael = UserToken.create(277990, "Jason Michael", "jason@boeing.com", "5555", true, true, false);
   public static final UserToken Alex_Kay = UserToken.create(8006939, "Alex Kay", "", "6666", true, false, false);
   public static final UserToken Inactive_Steve = UserToken.create(5808093, "Inactive Steve", "insactiveSteve@boeing.com", "7777", false, false, false);
   public static List<UserToken> values = Arrays.asList(Joe_Smith, Kay_Jones, Jason_Michael, Alex_Kay, Inactive_Steve);

   public static final String Joe_Smith_And_Kay_Jones = DemoUsers.Joe_Smith.getName() + "; " + DemoUsers.Kay_Jones.getName();
   public static final String Kay_Jones_And_Joe_Smith = DemoUsers.Kay_Jones.getName() + "; " + DemoUsers.Joe_Smith.getName();
   // @formatter:on

   private DemoUsers() {
      // Constants
   }

   public static List<UserToken> values() {
      return values;
   }
}