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
import java.util.Map;

import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.osee.icteam.service.IcteamHttpClient;
import org.eclipse.osee.icteam.utils.ObjectMapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

/**
 * @author Ajay Chandrahasan
 */

@Configuration
public class CustomUserDetailsContextMapper extends LdapUserDetailsMapper implements UserDetailsContextMapper {

  private final Log log = LogFactory.getLog(this.getClass());

  @Autowired
  IcteamHttpClient icteamHttpClient;

  @Autowired
  ObjectMapperUtil<Map> objectMapperUtil;

  /**
   * {@inheritDoc}
   */
  @Override
  public UserDetails mapUserFromContext(final DirContextOperations ctx, final String username,
      final Collection<? extends GrantedAuthority> authorities) {
    System.out.println("DN" + ctx.getDn());
    LdapUserDetailsImpl details = (LdapUserDetailsImpl) super.mapUserFromContext(ctx, username, authorities);
    this.log.info("DN from ctx: " + ctx.getDn()); // return correct DN
    this.log.info("Attributes size: " + ctx.getAttributes().size()); // always returns 0
    String UserGuid = null;
    String userFullName = null;
    String userResponse = this.icteamHttpClient.httpPost("/getproject/Users/getUserID", username, "application/json");
    if ((userResponse != null) && (userResponse.length() != 0)) {
      Map<String, String> UserDetails = this.objectMapperUtil.parseToObject(userResponse, Map.class);
      UserGuid = UserDetails.get("guid");
      try {
        userFullName = (String) ctx.getAttributes().get("givenname").get(0);
      }
      catch (NamingException e) {
        this.log.error("Error :: userName not present", e);
        userFullName = details.getUsername();
      }
    }
    else {
      throw new UsernameNotFoundException(username);
    }
    return new CustomUserDetails(details, UserGuid, userFullName);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void mapUserToContext(final UserDetails user, final DirContextAdapter ctx) {

  }

}
