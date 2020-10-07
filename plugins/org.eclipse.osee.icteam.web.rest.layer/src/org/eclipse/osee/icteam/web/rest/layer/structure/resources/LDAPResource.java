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
package org.eclipse.osee.icteam.web.rest.layer.structure.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.authentication.ldap.core.LDAPAuthenticationProvider;

/**
 * @author Ajay Chandrahasan LDAP resource providing LDAP user details
 */
@Path("LDAP")
public class LDAPResource {
   private static final String PWD = "pwd";
   private static final String UID = "userId";
   private static final String DOMAIN = "domain";

   /**
    * This function is used to get LDAP user
    *
    * @param json {@link String} LDAP user
    * @return json {@link String} LDAP user details
    */
   @POST
   @Produces(MediaType.TEXT_PLAIN)
   @Consumes(MediaType.TEXT_PLAIN)
   @Path("getLdapUser")
   public String getLDAPUserDetails(String json) {
      if ((json != null) && !json.equals("")) {
         String userId = json.substring(json.indexOf(UID) + UID.length() + 1, json.indexOf(PWD));
         String pwd = json.substring(json.indexOf(PWD) + PWD.length() + 1, json.indexOf(DOMAIN));
         String domain = json.substring(json.indexOf(DOMAIN) + DOMAIN.length() + 1, json.length());
         LDAPAuthenticationProvider ldapProviser = new LDAPAuthenticationProvider();
         boolean check = ldapProviser.getLDAPUserDetails(userId, pwd, domain);

         if (check) {
            StringBuffer result = new StringBuffer();
            result = result.append("dispName:");
            result = result.append(ldapProviser.getsDisplayName());
            result = result.append("userId:");
            result = result.append(ldapProviser.getsUserID());
            result = result.append("mail:");
            result = result.append(ldapProviser.getsMail());
            json = result.toString();
         }
      }

      return json;
   }
}
