/**
 * <copyright> Copyright (c) Robert Bosch Engineering and Business Solutions Ltd India.All rights reserved. This program
 * and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html </copyright>
 */
package org.eclipse.osee.framework.authentication.ldap.core;

import java.util.Hashtable;
import javax.naming.Context;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.authentication.ldap.core.internal.LDAPConnector;
import org.eclipse.osee.framework.authentication.ldap.core.service.ILDAPService;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.data.OseeCredential;
import org.eclipse.osee.framework.core.server.AbstractAuthenticationProvider;
import org.osgi.framework.Bundle;

/**
 * This class gives the implementation of the LDAP authentication mechanism. if the user is included in the LDAP
 * directory , then the user gets authentication.
 * 
 * @author Swapna
 */
public class LDAPAuthenticationProvider extends AbstractAuthenticationProvider {

   /**
    * String to to store extension point ID.
    */
   private final String extensionPointId = Messages.LDAPServiceExtensionPointID;

   /**
    * LDAP server URL prefix
    */
   private final String LDAP_SERVER_PREFIX = "ldap://";

   /**
    * To hold the instance of the LDAPConnector
    */
   private LDAPConnector connector;

   /**
    * This function runs only if the user is authenticated and returns a user token from the user data store.
    * 
    * @param credential sent my the OSee sesion manager which has the OSEE client user details
    * @return Returns IUserToken created for the given OSeeCrendtial.
    */
   @Override
   public UserToken asOseeUserId(final OseeCredential credential) {
      String userName = credential.getUserName();
      UserToken userToken = getUserTokenFromOseeDb(userName);
      return userToken != null ? userToken : createUserToken(true, userName, userName, "", true);
   }

   /**
    * The authentication of the user is decided by the return value of this function.
    * 
    * @param credential sent my the OSee sesion manager which has the OSEE client user details
    * @return Returns true if user is authenticated using LDAP authentication mechanism otherwise false.
    */
   @Override
   public boolean authenticate(final OseeCredential credential) {

      boolean authLDAP = checkLDAPAuthenticated(credential);
      if (!authLDAP) {
         getLogger().error(Messages.LDAPAuthenticationFailed);
      }
      return authLDAP;

   }

   /**
    * This function decides the string which has to be used for authentication choice.
    * 
    * @return Protocal name
    */
   @Override
   public String getProtocol() {

      return Messages.LDAPAuthenticationProtocol;
   }

   /**
    * This function gets the user name and searches that in the LDAP directory. it returns true if exists and false
    * otherwise.
    * 
    * @param credential OSeeCrendetial object which contains the user information like username.
    * @return boolean status indicating whether the user is authenticated
    */
   protected boolean checkLDAPAuthenticated(final OseeCredential credential) {
      boolean authenticate = false;

      ILDAPService ldapService = getLDAPServiceProviderExtension();
      if (ldapService == null) {
         return authenticate;
      }
      if (this.connector == null) {
         Hashtable<String, String> props = createLDAPContextProperies(ldapService);

         this.connector = new LDAPConnector(getLogger(), props, ldapService.getLDAPSearchBase());

      }
      /**
       * If the connection to LDAP failed then return without checking if the ser is having LDAP user account
       */
      if (this.connector == null) {
         return authenticate;
      }

      authenticate = this.connector.isLDAPUSer(credential.getUserName());

      return authenticate;
   }

   /**
    * This method creates the environment properties required for LDAPContext
    * 
    * @param ldapService
    * @return
    */
   private Hashtable<String, String> createLDAPContextProperies(final ILDAPService ldapService) {

      Hashtable<String, String> env = new Hashtable<>();
      // The factory responsible to LDAPContext creation
      env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
      // Context Authentication type
      env.put(Context.SECURITY_AUTHENTICATION, "simple");
      // LDAP Root or Fixed or User accoun to connect
      env.put(Context.SECURITY_PRINCIPAL, ldapService.getLDAPUsername());
      // LDAP Root or Fixed or User crendtials(pwd) to connect
      env.put(Context.SECURITY_CREDENTIALS, ldapService.getLDAPCrendentials());
      // LDAP server URL , the url always starts with ldap:// then we append the server and port details
      String ldapURL = this.LDAP_SERVER_PREFIX + ldapService.getLDAPServerName() + ":" + ldapService.getLDAPPort();
      env.put(Context.PROVIDER_URL, ldapURL);

      return env;
   }

   /**
    * This function handles the extension point by getting the user name , password and LDAP URL implemented by the
    * clients.
    */
   private ILDAPService getLDAPServiceProviderExtension() {
      IConfigurationElement[] config =
         Platform.getExtensionRegistry().getConfigurationElementsFor(this.extensionPointId);

      try {
         for (IConfigurationElement e : config) {

            final String className = e.getAttribute(Messages.LDAPServiceExtensionPointAttribute);

            String bundleName = e.getContributor().getName();

            if (className != null && bundleName != null) {

               Bundle bundle = Platform.getBundle(bundleName);
               Object obj1 = bundle.loadClass(className).newInstance();

               if (obj1 != null && obj1 instanceof ILDAPService) {

                  return (ILDAPService) obj1;

               }
            }
         }
      } catch (Exception ex) {
         getLogger().error(ex, "Error reading LDAPServiceExtensionPoints");
      }
      return null;
   }

}
