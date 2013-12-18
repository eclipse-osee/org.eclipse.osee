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