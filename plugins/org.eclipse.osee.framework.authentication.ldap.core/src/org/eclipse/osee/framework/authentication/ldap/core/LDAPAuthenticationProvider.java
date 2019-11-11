/*********************************************************************
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India
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

package org.eclipse.osee.framework.authentication.ldap.core;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.framework.authentication.ldap.core.internal.LDAPConnector;
import org.eclipse.osee.framework.authentication.ldap.core.service.ILDAPService;
import org.eclipse.osee.framework.core.data.OseeCredential;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.server.AbstractAuthenticationProvider;
import org.osgi.framework.Bundle;

/**
 * This class gives the implementation of the LDAP authentication mechanism. if the user is included in the LDAP
 * directory , then the user gets authentication.
 *
 * @author Ajay Chandrahasan
 */
public class LDAPAuthenticationProvider extends AbstractAuthenticationProvider {

   private final String extensionPointId = Messages.LDAPServiceExtensionPointID;
   private final String LDAP_SERVER_PREFIX = "ldap://";
   private LDAPConnector connector;
   private String sUserAbsoluteName;
   private String sDisplayName;
   private String sMail;
   private String sUserID;
   private static AtsApi atsServer;
   
   
   public String getsMail() {
	    return this.sMail;
   }
   
   public String getsDisplayName() {
	    return this.sDisplayName;
   }
   public String getsUserID() {
	    return this.sUserID;
   }
   
   public void setAtsApi(final IAtsServer atsServer) {
	   LDAPAuthenticationProvider.atsServer = atsServer;
   }

   public static AtsApi getAtsApi() {

	   return LDAPAuthenticationProvider.atsServer;
   }

   // for ReviewOsgiXml public void setLogger(Log logger)
   // for ReviewOsgiXml public void setOrcsApi(OrcsApi orcsApi)

   /**
    * This function runs only if the user is authenticated and returns a user token from the user data store.
    *
    * @param credential sent my the OSee sesion manager which has the OSEE client user details
    * @return IUserToken created for the given OSeeCrendtial.
    */
   @Override
   public UserToken asOseeUserId(final OseeCredential credential) {
      String userName = credential.getUserName();
      UserToken userToken = getUserTokenFromOseeDb(userName);
      return userToken != null ? userToken : createUserToken(userName, userName, "", true);
   }

   /**
    * The authentication of the user is decided by the return value of this function.
    *
    * @param credential sent my the OSee sesion manager which has the OSEE client user details
    * @return true if user is authenticated using LDAP authentication mechanism otherwise false.
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

   /*
    *  User details for LDAP login
    */
   public boolean getLDAPUserDetails(final String userName, final String password, final String domain) {
     OseeCredential credential = new OseeCredential();
     credential.setUserName(userName);
     credential.setPassword(password);
   //  credential.setDomain(domain);

     boolean check = checkLDAPAuthenticated(credential);
     if (check) {
       getDisplayNameMailfromLDAPUser(userName);
     }

     return check;
   }

   private void getDisplayNameMailfromLDAPUser(final String userName) {
	    if (this.connector != null) {
	      try {
	        SearchResult ldapUser = this.connector.getLdapUser(this.sUserAbsoluteName);
	        Attributes attribs = ldapUser.getAttributes();
	        if (attribs.size() > 0) {

	          Attribute attribute = attribs.get("displayname");
	          NamingEnumeration allMembers = attribute.getAll();
	          while ((allMembers != null) && allMembers.hasMoreElements()) {
	            this.sDisplayName = (String) allMembers.next();

	          }

	          attribute = attribs.get("mail");
	          allMembers = attribute.getAll();
	          while ((allMembers != null) && allMembers.hasMoreElements()) {
	            this.sMail = (String) allMembers.next();
	          }

	          attribute = attribs.get("sAMAccountName");
	          allMembers = attribute.getAll();
	          while ((allMembers != null) && allMembers.hasMoreElements()) {
	            this.sUserID = (String) allMembers.next();
	            /*
	             * to avoid case sensitive user creation
	             */
	            this.sUserID = this.sUserID.toLowerCase();
	          }

	        }

	      }
	      catch (Exception e) {
	    	  getLogger().error(e, "Error while retrieving LDAP User details" + e.getMessage());
	      }
	    }

	  }
}
