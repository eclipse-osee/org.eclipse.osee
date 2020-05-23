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

package org.eclipse.osee.authentication.ldap.internal.util;

import java.util.Set;
import org.eclipse.osee.authentication.ldap.LdapConfiguration;
import org.eclipse.osee.authentication.ldap.LdapSearchScope;
import org.eclipse.osee.authentication.ldap.internal.LdapFilter;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public final class LdapUtil {

   private LdapUtil() {
      // Utility class
   }

   public static <T> T getValue(T actualValue, T defaultValue) {
      return actualValue != null ? actualValue : defaultValue;
   }

   public static String stringField(Set<String> fieldsToGet, String actualValue, String defaultValue) {
      String field = actualValue;
      if (!Strings.isValid(field)) {
         field = defaultValue;
      }
      if (field != null) {
         fieldsToGet.add(field);
      }
      return field;
   }

   public static VariablePattern patternField(Set<String> fieldsToGet, String actualValue, String defaultValue) {
      VariablePattern field = asPattern(actualValue, defaultValue);
      if (field != null) {
         fieldsToGet.addAll(field.getVariableNames());
      }
      return field;
   }

   private static VariablePattern asPattern(String expression, String defaultValue) {
      String value = Strings.isValid(expression) ? expression : defaultValue;

      VariablePattern toReturn = null;
      if (value != null) {
         if (!value.contains("${")) {
            value = String.format("${%s}", value);
         }
         toReturn = VariablePattern.newPattern(value);
      }
      return toReturn;
   }

   public static LdapFilter newLdapFilter(LdapConfiguration config) {
      final String usernameVariable = config.getUserNameVariableName();

      final LdapSearchScope scope = config.getAccountSearchScope();
      final String accountBase = config.getAccountBase();
      final String accountPattern = config.getAccountPattern();
      final String username = config.getAccountUserName();
      final String displayName = config.getAccountDisplayName();
      final String email = config.getAccountEmailAddress();

      final String groupBase = config.getGroupBase();
      final String groupMemberPattern = config.getGroupByGroupMemberPattern();
      final String groupName = config.getGroupName();
      final String groupPattern = config.getGroupPattern();
      final LdapSearchScope groupSearchScope = config.getGroupSearchScope();
      final String memberof = config.getGroupMembersOf();
      final boolean isGroupMembershipInAccounts = Strings.isValid(memberof);
      return new LdapFilter() {

         @Override
         public LdapSearchScope getGroupSearchScope() {
            return groupSearchScope;
         }

         @Override
         public String getGroupPattern() {
            return groupPattern;
         }

         @Override
         public String getGroupName() {
            return groupName;
         }

         @Override
         public String getGroupByGroupMemberPattern() {
            return groupMemberPattern;
         }

         @Override
         public String getGroupBase() {
            return groupBase;
         }

         @Override
         public String getUserNameVariableName() {
            return usernameVariable;
         }

         @Override
         public String getAccountUserName() {
            return username;
         }

         @Override
         public LdapSearchScope getAccountSearchScope() {
            return scope;
         }

         @Override
         public String getAccountPattern() {
            return accountPattern;
         }

         @Override
         public String getGroupMembersOf() {
            return memberof;
         }

         @Override
         public String getAccountEmailAddress() {
            return email;
         }

         @Override
         public String getAccountDisplayName() {
            return displayName;
         }

         @Override
         public String getAccountBase() {
            return accountBase;
         }

         @Override
         public boolean isGroupMembershipPartOfAccount() {
            return isGroupMembershipInAccounts;
         }
      };
   }
}
