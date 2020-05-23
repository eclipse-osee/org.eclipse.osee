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
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import org.eclipse.osee.logger.Log;

/**
 * This is class responsible for making connection to LDAP.<br>
 * It makes connection to LDAP server directory if the given <br>
 * user account is a valid LDAP user account.
 *
 * @author Swapna
 */
public class LDAPConnector {

   public static final String DEFAULT_ACCOUNT_SEARCH_FILTER = "(&(objectCategory=person)(sAMAccountName=%s))";
   public static final String DEFAULT_ACCOUNT_FIELD = "sAMAccountName";

   private final Log logger;

   /**
    * String to store the searchBase
    */
   private final String searchBase;

   /**
    * environment properties for LDAP context
    */
   private final Hashtable<String, String> env;

   /**
    * Account search pattern
    */
   private final String accountSearchFilter;

   /**
    * Account attributes field
    */
   private final String accountField;

   /**
    * Constructor of the LDAPConnector class
    *
    * @param logger logging object
    * @param env Hashtable of environment properties required for LDAPContext
    * @param searchBase String containing DC values required for searching LDAP e.g., "DC=eclipse,DC=com"
    */
   public LDAPConnector(final Log logger, final Hashtable<String, String> env, final String searchBase) {
      this(logger, env, searchBase, DEFAULT_ACCOUNT_SEARCH_FILTER, DEFAULT_ACCOUNT_FIELD);
   }

   /**
    * Constructor of the LDAPConnector class
    *
    * @param logger logging object
    * @param env Hashtable of environment properties required for LDAPContext
    * @param searchBase String containing DC values required for searching LDAP e.g., "DC=eclipse,DC=com"
    * @param accountSearchFilter user account search pattern filter
    * @param accountField user account attribute field name
    */
   public LDAPConnector(final Log logger, final Hashtable<String, String> env, final String searchBase, final String accountSearchFilter, final String accountField) {
      this.logger = logger;
      this.env = env;
      this.searchBase = searchBase;
      this.accountSearchFilter = accountSearchFilter;
      this.accountField = accountField;
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
   public String findAccountNameForUser(String username) throws NamingException {
      String toReturn = null;
      String searchFilter = String.format(accountSearchFilter, username);
      SearchControls searchControls = new SearchControls();
      searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

      LdapContext ldapContext = null;
      try {
         ldapContext = new InitialLdapContext(env, null);
         logger.info("LDAP connection established");

         NamingEnumeration<SearchResult> results = ldapContext.search(searchBase, searchFilter, searchControls);
         if (results != null) {
            while (results.hasMoreElements()) {
               SearchResult searchResult = results.nextElement();
               Attributes attribs = searchResult.getAttributes();
               if (attribs.size() > 0) {
                  Attribute attribute = attribs.get(accountField);
                  NamingEnumeration<?> allMembers = attribute.getAll();
                  while (allMembers != null && allMembers.hasMoreElements()) {
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
               logger.info(e, "Error closing LDAP context");
            }
         }
      }
      return toReturn;
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
         logger.info(ex, "Error authenticating LDAP user [%s]", username);
      }
      return result;
   }
}
