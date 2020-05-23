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
public final class Rfc2307CompliantLdapFilter extends AbstractLdapFilter {

   @Override
   public String getAccountPattern() {
      return "(uid=${username})";
   }

   @Override
   public String getAccountUserName() {
      return "uid";
   }

   @Override
   public String getAccountDisplayName() {
      return "displayName";
   }

   @Override
   public String getAccountEmailAddress() {
      return "mail";
   }

   @Override
   public String getGroupMembersOf() {
      return null;
   }

   @Override
   public boolean isGroupMembershipPartOfAccount() {
      return false;
   }

   @Override
   public String getGroupPattern() {
      return "(cn=${groupname})";
   }

   @Override
   public String getGroupName() {
      return "cn";
   }

   @Override
   public String getGroupByGroupMemberPattern() {
      return "(|(memberUid=${username})(gidNumber=${gidNumber}))";
   }

}