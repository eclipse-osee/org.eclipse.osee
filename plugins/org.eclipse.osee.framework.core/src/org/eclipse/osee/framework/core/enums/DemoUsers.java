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

import static org.eclipse.osee.framework.core.enums.CoreArtifactTokens.OseeAccessAdmin;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTokens.OseeAdmin;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.core.data.UserToken;

/**
 * @author Donald G. Dunne
 */
public final class DemoUsers {

   // @formatter:off
   public static final UserToken Joe_Smith = UserToken.create(61106791, "Joe Smith", "joe@boeing.com", "3333", true, false);
   public static final UserToken Kay_Jones = UserToken.create(5896672, "Kay Jones", "kay@boeing.com", "4444", true, false);
   public static final UserToken Jason_Michael = UserToken.create(277990, "Jason Michael", "jason@boeing.com", "5555", true, false, OseeAdmin, OseeAccessAdmin);
   public static final UserToken Alex_Kay = UserToken.create(8006939, "Alex Kay", "", "6666", true, false);
   public static final UserToken Kay_Jason = UserToken.create(1402067349, "Kay Jason", "kayj@boeing.com", "7777", true, false);
   public static final UserToken Steven_Kohn = UserToken.create(1668581959, "Steven Kohn", "stevenk@boeing.com", "8888", true, false);
   public static final UserToken John_Stevens = UserToken.create(1643660349, "John Stevens", "johns@boeing.com", "9999", true, false);
   public static final UserToken Keith_Johnson = UserToken.create(25923706, "Keith Johnson", "keithj@boeing.com", "1010", true, false);
   public static final UserToken Michael_Alex = UserToken.create(1580073488, "Michael Alex", "michaela@boeing.com", "1212", true, false);
   public static final UserToken Janice_Michael = UserToken.create(608369853, "Janice Michael", "janicem@boeing.com", "1313", true, false);
   public static final UserToken Maichael_Johnson = UserToken.create(1944108438, "Maichael Johnson", "maichaelj@boeing.com", "1414", true, false);
   public static final UserToken Roland_Stevens = UserToken.create(785160932, "Roland Stevens", "rolands@boeing.com", "1515", true, false);
   public static final UserToken Jeffery_Kay = UserToken.create(86470632, "Jeffery Kay", "jeffk@boeing.com", "1616", true, false);
   public static final UserToken Karmen_John = UserToken.create(208906425, "Karmen John", "karmenj@boeing.com", "1717", true, false);
   public static final UserToken Steven_Michael = UserToken.create(170310871, "Steven Michael", "stevenm@boeing.com", "1818", true, false);
   public static final UserToken Jason_Stevens = UserToken.create(322597199, "Jason Stevens", "jasons@boeing.com", "1919", true, false);
   public static final UserToken Michael_John = UserToken.create(1290938604, "Michael John", "michaelj@boeing.com", "2121", true, false);
   public static final UserToken Kay_Wheeler = UserToken.create(1645633521, "Kay Wheeler", "kayw@boeing.com", "2323", true, false);
   public static final UserToken Inactive_Steve = UserToken.create(5808093, "Inactive Steve", "insactiveSteve@boeing.com", "2424", false, false);

   public static List<UserToken> values = Arrays.asList(Joe_Smith, Kay_Jones, Jason_Michael, Alex_Kay, Kay_Jason, //
      Steven_Kohn, John_Stevens, Keith_Johnson, Michael_Alex, Janice_Michael, Maichael_Johnson, Roland_Stevens, //
      Jeffery_Kay, Karmen_John, Steven_Michael, Jason_Stevens, Michael_John, Kay_Wheeler, Inactive_Steve);

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