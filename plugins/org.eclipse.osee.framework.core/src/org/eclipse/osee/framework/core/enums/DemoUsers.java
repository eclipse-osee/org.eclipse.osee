/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.framework.core.enums;

import static org.eclipse.osee.framework.core.enums.CoreUserGroups.AccountAdmin;
import static org.eclipse.osee.framework.core.enums.CoreUserGroups.AgileUser;
import static org.eclipse.osee.framework.core.enums.CoreUserGroups.DefaultArtifactEditor;
import static org.eclipse.osee.framework.core.enums.CoreUserGroups.EarnedValueUser;
import static org.eclipse.osee.framework.core.enums.CoreUserGroups.OseeAccessAdmin;
import static org.eclipse.osee.framework.core.enums.CoreUserGroups.OseeAdmin;
import static org.eclipse.osee.framework.core.enums.CoreUserGroups.Publishing;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.BootstrapUserProvider;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.UserToken;

/**
 * Defines demonstration users.
 *
 * @author Donald G. Dunne
 */
public class DemoUsers implements BootstrapUserProvider {

   public static List<UserToken> values = new ArrayList<UserToken>();

   public static final UserToken Joe_Smith = create(61106791L, "Joe Smith", "joe@google.com", "3333", true,
      Arrays.asList("3333"), AgileUser, EarnedValueUser, DefaultArtifactEditor, AccountAdmin, OseeAccessAdmin);

   public static final UserToken Kay_Jones =
      create(5896672L, "Kay Jones", "kay@google.com", "4444", true, AgileUser, EarnedValueUser);
   public static final UserToken Jason_Michael =
      create(277990L, "Jason Michael", "jason@google.com", "5555", true, OseeAdmin, OseeAccessAdmin);
   public static final UserToken Alex_Kay =
      create(8006939L, "Alex Kay", "alex.google.com", "6666", true, AgileUser, EarnedValueUser);
   public static final UserToken Kay_Jason =
      create(1402067349L, "Kay Jason", "kayj@google.com", "7777", true, AgileUser, EarnedValueUser);
   public static final UserToken Steven_Kohn =
      create(1668581959L, "Steven Kohn", "stevenk@google.com", "8888", true, AgileUser, EarnedValueUser);
   public static final UserToken John_Stevens =
      create(1643660349L, "John Stevens", "johns@google.com", "9999", true, AgileUser, EarnedValueUser);
   public static final UserToken Keith_Johnson =
      create(25923706L, "Keith Johnson", "keithj@google.com", "1010", true, AgileUser, EarnedValueUser);
   public static final UserToken Michael_Alex =
      create(1580073488L, "Michael Alex", "michaela@google.com", "1212", true, AgileUser, EarnedValueUser);
   public static final UserToken Janice_Michael =
      create(608369853L, "Janice Michael", "janicem@google.com", "1313", true, AgileUser, EarnedValueUser);
   public static final UserToken Maichael_Johnson =
      create(1944108438L, "Maichael Johnson", "maichaelj@google.com", "1414", true, AgileUser, EarnedValueUser);
   public static final UserToken Roland_Stevens =
      create(785160932L, "Roland Stevens", "rolands@google.com", "1515", true, AgileUser, EarnedValueUser);
   public static final UserToken Jeffery_Kay =
      create(86470632L, "Jeffery Kay", "jeffk@google.com", "1616", true, AgileUser, EarnedValueUser);
   public static final UserToken Karmen_John =
      create(208906425L, "Karmen John", "karmenj@google.com", "1717", true, AgileUser, EarnedValueUser);
   public static final UserToken Steven_Michael =
      create(170310871L, "Steven Michael", "stevenm@google.com", "1818", true, AgileUser, EarnedValueUser);
   public static final UserToken Jason_Stevens =
      create(322597199L, "Jason Stevens", "jasons@google.com", "1919", true, AgileUser, EarnedValueUser);
   public static final UserToken Michael_John =
      create(1290938604L, "Michael John", "michaelj@google.com", "2121", true, AgileUser, EarnedValueUser);
   public static final UserToken Kay_Wheeler =
      create(645633521L, "Kay Wheeler", "kayw@google.com", "2323", true, AgileUser, EarnedValueUser);
   public static final UserToken Inactive_Steve =
      create(5808093L, "Inactive Steve", "insactiveSteve@google.com", "2424", false, AgileUser, EarnedValueUser);
   public static final UserToken Choe_Yun_ui =
      create(1961856040L, "Choe Yun-ui", "yunuic@google.com", "1234", true, Arrays.asList("1234AD"), Publishing);

   public static final String Joe_Smith_And_Kay_Jones =
      DemoUsers.Joe_Smith.getName() + "; " + DemoUsers.Kay_Jones.getName();
   public static final String Kay_Jones_And_Joe_Smith =
      DemoUsers.Kay_Jones.getName() + "; " + DemoUsers.Joe_Smith.getName();

   private static UserToken create(long id, String name, String email, String userId, boolean active,
      List<String> loginIds, IUserGroupArtifactToken... roles) {
      UserToken token = UserToken.create(id, name, email, userId, active, loginIds, Arrays.asList(roles));
      values.add(token);
      return token;
   }

   private static UserToken create(long id, String name, String email, String userId, boolean active,
      IUserGroupArtifactToken... roles) {
      UserToken token = UserToken.create(id, name, email, userId, active, Arrays.asList(roles));
      values.add(token);
      return token;
   }

   public DemoUsers() {
      // Constants; for jax-rs
   }

   public static List<UserToken> values() {
      return values;
   }

   @Override
   public Collection<UserToken> getBootsrapUsers() {
      return values;
   }

}