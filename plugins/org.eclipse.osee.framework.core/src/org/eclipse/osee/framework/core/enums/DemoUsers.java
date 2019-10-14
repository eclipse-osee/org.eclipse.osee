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

import static org.eclipse.osee.framework.core.enums.CoreUserGroups.AgileUser;
import static org.eclipse.osee.framework.core.enums.CoreUserGroups.EarnedValueUser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.UserToken;

/**
 * @author Donald G. Dunne
 */
public final class DemoUsers {

   public static List<UserToken> values = new ArrayList<UserToken>();

   // @formatter:off
   public static final UserToken Joe_Smith = create(61106791, "Joe Smith", "joe@boeing.com", "3333", true, Arrays.asList("3333"), AgileUser, EarnedValueUser);
   public static final UserToken Kay_Jones = create(5896672, "Kay Jones", "kay@boeing.com", "4444", true, AgileUser, EarnedValueUser);
   public static final UserToken Jason_Michael = create(277990, "Jason Michael", "jason@boeing.com", "5555", true, CoreUserGroups.OseeAdmin, CoreUserGroups.OseeAccessAdmin);
   public static final UserToken Alex_Kay = create(8006939, "Alex Kay", "", "6666", true, AgileUser, EarnedValueUser);
   public static final UserToken Kay_Jason = create(1402067349, "Kay Jason", "kayj@boeing.com", "7777", true, AgileUser, EarnedValueUser);
   public static final UserToken Steven_Kohn = create(1668581959, "Steven Kohn", "stevenk@boeing.com", "8888", true, AgileUser, EarnedValueUser);
   public static final UserToken John_Stevens = create(1643660349, "John Stevens", "johns@boeing.com", "9999", true, AgileUser, EarnedValueUser);
   public static final UserToken Keith_Johnson = create(25923706, "Keith Johnson", "keithj@boeing.com", "1010", true, AgileUser, EarnedValueUser);
   public static final UserToken Michael_Alex = create(1580073488, "Michael Alex", "michaela@boeing.com", "1212", true, AgileUser, EarnedValueUser);
   public static final UserToken Janice_Michael = create(608369853, "Janice Michael", "janicem@boeing.com", "1313", true, AgileUser, EarnedValueUser);
   public static final UserToken Maichael_Johnson = create(1944108438, "Maichael Johnson", "maichaelj@boeing.com", "1414", true, AgileUser, EarnedValueUser);
   public static final UserToken Roland_Stevens = create(785160932, "Roland Stevens", "rolands@boeing.com", "1515", true, AgileUser, EarnedValueUser);
   public static final UserToken Jeffery_Kay = create(86470632, "Jeffery Kay", "jeffk@boeing.com", "1616", true, AgileUser, EarnedValueUser);
   public static final UserToken Karmen_John = create(208906425, "Karmen John", "karmenj@boeing.com", "1717", true, AgileUser, EarnedValueUser);
   public static final UserToken Steven_Michael = create(170310871, "Steven Michael", "stevenm@boeing.com", "1818", true, AgileUser, EarnedValueUser);
   public static final UserToken Jason_Stevens = create(322597199, "Jason Stevens", "jasons@boeing.com", "1919", true, AgileUser, EarnedValueUser);
   public static final UserToken Michael_John = create(1290938604, "Michael John", "michaelj@boeing.com", "2121", true, AgileUser, EarnedValueUser);
   public static final UserToken Kay_Wheeler = create(1645633521, "Kay Wheeler", "kayw@boeing.com", "2323", true, AgileUser, EarnedValueUser);
   public static final UserToken Inactive_Steve = create(5808093, "Inactive Steve", "insactiveSteve@boeing.com", "2424", false, AgileUser, EarnedValueUser);

   public static final String Joe_Smith_And_Kay_Jones = DemoUsers.Joe_Smith.getName() + "; " + DemoUsers.Kay_Jones.getName();
   public static final String Kay_Jones_And_Joe_Smith = DemoUsers.Kay_Jones.getName() + "; " + DemoUsers.Joe_Smith.getName();
   // @formatter:on

   private static UserToken create(long id, String name, String email, String userId, boolean active, Collection<String> loginIds, IUserGroupArtifactToken... roles) {
      UserToken token = UserToken.create(id, name, email, userId, active, loginIds, roles);
      values.add(token);
      return token;
   }

   private static UserToken create(long id, String name, String email, String userId, boolean active, IUserGroupArtifactToken... roles) {
      UserToken token = UserToken.create(id, name, email, userId, active, roles);
      values.add(token);
      return token;
   }

   private DemoUsers() {
      // Constants
   }

   public static List<UserToken> values() {
      return values;
   }

}