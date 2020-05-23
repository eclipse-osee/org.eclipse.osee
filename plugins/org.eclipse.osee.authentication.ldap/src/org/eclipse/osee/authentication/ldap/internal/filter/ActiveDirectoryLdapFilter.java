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

package org.eclipse.osee.authentication.ldap.internal.filter;

/**
 * @author Roberto E. Escobar
 */
public final class ActiveDirectoryLdapFilter extends AbstractLdapFilter {

   @Override
   public String getAccountPattern() {
      return "(&(objectClass=user)(sAMAccountName=${username}))";
   }

   @Override
   public String getAccountUserName() {
      return "${sAMAccountName.toLowerCase}";
   }

   @Override
   public String getAccountDisplayName() {
      return "${givenName} ${sn}";
   }

   @Override
   public String getAccountEmailAddress() {
      return "mail";
   }

   @Override
   public String getGroupMembersOf() {
      return "memberOf";
   }

   @Override
   public boolean isGroupMembershipPartOfAccount() {
      return true;
   }

   @Override
   public String getGroupPattern() {
      return "(&(objectClass=group)(cn=${groupname}))";
   }

   @Override
   public String getGroupName() {
      return "cn";
   }

   @Override
   public String getGroupByGroupMemberPattern() {
      return null;
   }

}