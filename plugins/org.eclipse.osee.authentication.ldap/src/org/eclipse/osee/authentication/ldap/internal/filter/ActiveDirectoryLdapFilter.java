/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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