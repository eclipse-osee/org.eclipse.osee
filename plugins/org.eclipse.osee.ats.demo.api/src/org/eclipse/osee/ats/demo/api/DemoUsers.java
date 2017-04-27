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
package org.eclipse.osee.ats.demo.api;

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.core.data.UserToken;

/**
 * @author Donald G. Dunne
 */
public final class DemoUsers {

   // @formatter:off
   public static final UserToken Joe_Smith = UserToken.create(61106791L, "ABNRvbZxXHICYklfslwA", "Joe Smith", "", "3333", true, false, false);
   public static final UserToken Kay_Jones = UserToken.create(5896672L, "ABNRvuB8x3VARkkn3YAA", "Kay Jones", "", "4444", true, false, false);
   public static final UserToken Jason_Michael = UserToken.create(277990, "ABNRvuHWtXAdxbG3mUAA", "Jason Michael", "", "5555", true, false, false);
   public static final UserToken Alex_Kay = UserToken.create(8006939L, "ABNRvuKDIWOcPDe4X0wA", "Alex Kay", "", "6666", true, false, false);
   public static final UserToken Inactive_Steve = UserToken.create(5808093, "ABNRvuRG6jKwKnEoX4gA", "Inactive Steve", "", "7777", false, false, false);
   public static List<UserToken> values = Arrays.asList(Joe_Smith, Kay_Jones, Jason_Michael, Alex_Kay, Inactive_Steve);
   // @formatter:on

   private DemoUsers() {
      // Constants
   }

   public static List<UserToken> values() {
      return values;
   }

}
