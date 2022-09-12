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
package org.eclipse.osee.framework.authentication.ldap.core.internal;

import java.util.Hashtable;
import java.util.logging.Level;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import org.apache.commons.codec.binary.Base64;
import org.eclipse.osee.framework.authentication.ldap.core.Activator;
import org.eclipse.osee.framework.authentication.ldap.core.LDAPAuthenticationProvider;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.logger.Log;

/**
 * This is class responsible for making connection to LDAP.<br>
 * It makes connection to LDAP server directory if the given <br>
 * user account is a valid LDAP user account.
 *
 * @author Ajay Chandrahasan
 */
public class LDAPConnector {

   public static final String DEFAULT_ACCOUNT_SEARCH_FILTER = "(&(objectCategory=person)(sAMAccountName=%s))";
   public static final String DEFAULT_ACCOUNT_FIELD = "sAMAccountName";
   private static InitialLdapContext ctxInstance = null;
   private static LDAPConnector connector = null;

   /**
    * String to store the searchBase
    */
   private final String searchBase;

   private final Log logger;
   /**
    * environment properties for LDAP context
    */
   private final Hashtable<String, String> env;

   /**
    * Account search pattern
    */
   private final String accountSearchFilter;
   /**
    * LDAP Context
    */
   private LdapContext ctx;

   /**
    * Account attributes field
    */
   private final String accountField;

   /**
    * Constructor of the LDAPConnector class
    *
    * @param logger     logging object
    * @param env        Hashtable of environment properties required for LDAPContext
    * @param searchBase String containing DC values required for searching LDAP e.g., "DC=eclipse,DC=com"
    */
   public LDAPConnector(final Log logger, final Hashtable<String, String> env, final String searchBase) {
      this(logger, env, searchBase, DEFAULT_ACCOUNT_SEARCH_FILTER, DEFAULT_ACCOUNT_FIELD);
   }

   /**
    * Constructor of the LDAPConnector class
    *
    * @param logger              logging object
    * @param env                 Hashtable of environment properties required for LDAPContext
    * @param searchBase          String containing DC values required for searching LDAP e.g., "DC=eclipse,DC=com"
    * @param accountSearchFilter user account search pattern filter
    * @param accountField        user account attribute field name
    */
   public LDAPConnector(final Log logger, final Hashtable<String, String> env, final String searchBase, final String accountSearchFilter, final String accountField) {
      this.logger = logger;
      this.env = env;
      this.searchBase = searchBase;
      this.accountSearchFilter = accountSearchFilter;
      this.accountField = accountField;
   }

   public LdapContext getCtx() {
      return this.ctx;
   }

   /**
    * Creates the LDAP context with the values as in the env table <br>
    * and searches for the user account matching the username. <br>
    * The env table should have all the necessary info like LDAPCntxFactory,<br>
    * LDAP Security credentials, LDAP Security Principal (user name) <br>
    * LDAP Authentication type, etc ..
    *
    * @return accountName
    * @throws NamingException incase if the LDAPContext could not be created.
    */
   public String findAccountNameForUser(final String username) throws NamingException {
      String toReturn = null;
      String searchFilter = String.format(this.accountSearchFilter, username);
      SearchControls searchControls = new SearchControls();
      searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

      LdapContext ldapContext = null;
      try {
         ldapContext = new InitialLdapContext(this.env, null);
         this.logger.info("LDAP connection established");

         NamingEnumeration<SearchResult> results = ldapContext.search(this.searchBase, searchFilter, searchControls);

         if (results != null) {
            while (results.hasMoreElements()) {
               SearchResult searchResult = results.nextElement();
               Attributes attribs = searchResult.getAttributes();

               if (attribs.size() > 0) {
                  Attribute attribute = attribs.get(this.accountField);
                  NamingEnumeration<?> allMembers = attribute.getAll();

                  while ((allMembers != null) && allMembers.hasMoreElements()) {
                     String accountName = (String) allMembers.next();

                     if (username.equalsIgnoreCase(accountName)) {
                        toReturn = accountName;

                        break;
                     }
                  }
               }

               if (toReturn != null) {
                  break;
               }
            }
         }
      } finally {
         if (ldapContext != null) {
            try {
               ldapContext.close();
            } catch (NamingException e) {
               this.logger.info(e, "Error closing LDAP context");
            }
         }
      }

      return toReturn;
   }

   /**
    * Searches the user in the LDAP directory and returns true if present and false if not present
    *
    * @param username UserName in the form of sAMAccountName of user in LDAP Server
    * @return search result true if given username exisits in LDAP User Directory otherwise false
    */
   public SearchResult getLdapUser(final String username) {
      SearchResult searchResult = null;
      String searchFilter = "(&(objectCategory=person)(|(sAMAccountName=" + username + "*)(givenName=" + username
         + "*)(displayname=" + username + "*)))";
      SearchControls searchControls = new SearchControls();
      searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

      try {
         init();

         NamingEnumeration<SearchResult> results = null;

         if (this.ctx != null) {
            results = this.ctx.search(this.searchBase, searchFilter, searchControls);
         }

         if (results == null) {
            return null;
         }

         while (results.hasMoreElements()) {
            searchResult = results.nextElement();
         }
      } catch (NamingException e) {
         OseeLog.logf(Activator.class, Level.INFO, e.getMessage(), (Object) null);
      } finally {
         try {
            if (this.ctx != null) {
               this.ctx.close();
            }
         } catch (NamingException e) {
            this.logger.info(e, "Error closing LDAP context");
            OseeLog.logf(Activator.class, Level.INFO, e.getMessage(), (Object) null);
         }
      }
      return searchResult;
   }

   /**
    * Searches the user in the LDAP directory and returns true if present and false if not present
    *
    * @param username UserName in the form of sAMAccountName of user in LDAP Server
    * @return search result true if given username exists in LDAP User Directory otherwise false
    */
   public boolean isLDAPUSer(final String username) {
      boolean result = false;
      try {
         String accountName = findAccountNameForUser(username);
         result = accountName != null;
      } catch (NamingException ex) {
         this.logger.info(ex, "Error authenticating LDAP user [%s]", username);
      }
      return result;
   }

   /**
    * Initializes the LDAP context with the values as in the env table. <br>
    * The env table should have all the necessary info like LDAPCntxFactory,<br>
    * LDAP Secutiry credentials, LDAP Security Prinicipal (user name) <br>
    * LDAP Authentication type, etc ..
    *
    * @throws NamingException incase if the LDAPContext could not be created.
    */
   public void init() throws NamingException {
      try {
         this.ctx = new InitialLdapContext(this.env, null);
      } catch (NamingException e) {
         OseeLog.logf(Activator.class, Level.SEVERE, "Failed to establish LDAP connection", (Object) null);
      }

      if (this.ctx != null) {
         OseeLog.logf(Activator.class, Level.INFO, "LDAP connection established", (Object) null);
      }
   }

   public static InitialLdapContext getCtxInstance() {
      if (ctxInstance == null) {
         JdbcStatement statement = null;
         final String FETCH_USER_COMMAND = "SELECT * FROM ldap_details";
         try {
            statement = LDAPAuthenticationProvider.getAtsApi().getJdbcService().getClient().getStatement();
            statement.runPreparedQuery(FETCH_USER_COMMAND);
            while (statement.next()) {
               String ServerName = statement.getString("server_name");
               int Port = statement.getInt("port");
               String LDAPServerPrefix = "ldap://";
               String sUserName = statement.getString("user_name");
               String sPasswordEnc = statement.getString("password");
               byte[] decodeBase64 = Base64.decodeBase64(sPasswordEnc.getBytes());
               String sPassword = new String(decodeBase64);
               Hashtable<String, String> env = new Hashtable<String, String>();
               // The factory responsible to LDAPContext creation
               env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
               // Context Authentication type
               env.put(Context.SECURITY_AUTHENTICATION, "simple");
               // LDAP Root or Fixed or User accoun to connect
               env.put(Context.SECURITY_PRINCIPAL, sUserName);
               // LDAP Root or Fixed or User crendtials(pwd) to connect
               env.put(Context.SECURITY_CREDENTIALS, sPassword);
               // LDAP server URL , the url always starts with ldap:// then we
               // append
               // the server and port details
               String ldapURL = LDAPServerPrefix + ServerName + ":" + Port;
               env.put(Context.PROVIDER_URL, ldapURL);
               //    ctxInstance = new InitialLdapContext(env, null);
            }
            statement.close();
         } catch (OseeDataStoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         } catch (OseeCoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         } finally {
            Lib.close(statement);
         }
      }
      Hashtable<String, String> env2 = new Hashtable<String, String>();

      try {
         return new InitialLdapContext(env2, null);
      } catch (NamingException ex) {
         return null;
      }
   }

   public static LDAPConnector getConnector() {
      return connector;
   }

   public String getSearchBase() {
      return this.searchBase;
   }

   public static String getsearchBaseFromDb() {
      JdbcStatement statement = null;
      final String FETCH_USER_COMMAND = "SELECT * FROM ldap_details";

      try {
         statement = LDAPAuthenticationProvider.getAtsApi().getJdbcService().getClient().getStatement();
         statement.runPreparedQuery(FETCH_USER_COMMAND);

         while (statement.next()) {
            String SearchBase = statement.getString("search_base");

            return SearchBase;
         }
      } catch (OseeDataStoreException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } catch (OseeCoreException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      } finally {
         Lib.close(statement);
      }

      return null;
   }

   public static void setCtxInstance(final InitialLdapContext ctxInstance) {
      LDAPConnector.ctxInstance = ctxInstance;
   }
}
