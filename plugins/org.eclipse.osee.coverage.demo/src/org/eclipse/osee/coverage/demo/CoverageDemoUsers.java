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
package org.eclipse.osee.coverage.demo;

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.core.data.IUserToken;
import org.eclipse.osee.framework.core.data.TokenFactory;

/**
 * @author Donald G. Dunne
 */
public final class CoverageDemoUsers {

   // @formatter:off
   public static final IUserToken Joe_Smith = TokenFactory.createUserToken(61106791L, "ABNRvbZxXHICYklfslwA", "Joe Smith", "", "3333", false, false, false);
   public static final IUserToken Kay_Jones = TokenFactory.createUserToken(5896672L, "ABNRvuB8x3VARkkn3YAA", "Kay Jones", "", "4444", false, false, false);
   public static final IUserToken Jason_Michael = TokenFactory.createUserToken(277990, "ABNRvuHWtXAdxbG3mUAA", "Jason Michael", "", "5555", true, false, false);
   public static final IUserToken Alex_Kay = TokenFactory.createUserToken(8006939L, "ABNRvuKDIWOcPDe4X0wA", "Alex Kay", "", "6666", true, false, false);
   public static final IUserToken Inactive_Steve = TokenFactory.createUserToken(5808093, "ABNRvuRG6jKwKnEoX4gA", "Inactive Steve", "", "7777", false, false, false);
   public static List<IUserToken> values = Arrays.asList(Joe_Smith, Kay_Jones, Jason_Michael, Alex_Kay, Inactive_Steve);
   // @formatter:on

   private CoverageDemoUsers() {
      // Constants
   }

   public static List<IUserToken> values() {
      return values;
   }

}
