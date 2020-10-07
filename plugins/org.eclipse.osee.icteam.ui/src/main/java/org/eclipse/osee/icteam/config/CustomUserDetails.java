/*********************************************************************
 * Copyright (c) 2020 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.icteam.config;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapUserDetails;

/**
 * @author Ajay Chandrahasan
 */
public class CustomUserDetails implements LdapUserDetails {

   private String userGuid;
   private String userFullName;
   private final LdapUserDetails details;

   CustomUserDetails(final LdapUserDetails details, final String userGuid, final String userFullName) {
      this.details = details;
      this.userGuid = userGuid;
      this.userFullName = userFullName;
   }

   /**
    * @return the userFullName
    */
   public String getUserFullName() {
      return this.userFullName;
   }

   /**
    * @param userFullName the userFullName to set
    */
   public void setUserFullName(final String userFullName) {
      this.userFullName = userFullName;
   }

   /**
    * @return the userGuid
    */
   public String getUserGuid() {
      return this.userGuid;
   }

   /**
    * @param userGuid the userGuid to set
    */
   public void setUserGuid(final String userGuid) {
      this.userGuid = userGuid;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Collection<? extends GrantedAuthority> getAuthorities() {
      return this.details.getAuthorities();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getPassword() {
      return this.details.getPassword();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getUsername() {
      return this.details.getUsername();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isAccountNonExpired() {
      return this.details.isAccountNonExpired();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isAccountNonLocked() {
      return this.details.isAccountNonLocked();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isCredentialsNonExpired() {
      return this.details.isCredentialsNonExpired();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isEnabled() {
      return this.details.isEnabled();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void eraseCredentials() {

   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getDn() {
      return this.details.getDn();
   }

}
