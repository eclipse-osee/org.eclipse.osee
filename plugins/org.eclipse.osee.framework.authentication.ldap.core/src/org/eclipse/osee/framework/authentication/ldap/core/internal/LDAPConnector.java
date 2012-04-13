/**
 * <copyright> Copyright (c) Robert Bosch Engineering and Business Solutions Ltd India.All rights reserved. This program
 * and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html </copyright>
 */
package org.eclipse.osee.framework.authentication.ldap.core.internal;

import java.util.Hashtable;
import java.util.logging.Level;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.eclipse.osee.framework.authentication.ldap.core.Activator;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * This is class responsible for making connection to LDAP.<br>
 * It makes connection to LDAP server directory if the given <br>
 * user account is a valid LDAP user account.
 * 
 * @author Swapna
 */
public class LDAPConnector {

  /**
   * String to store the searchBase
   */
  private final String searchBase;
  /**
   * LDAP Context
   */
  private LdapContext ctx;
  /**
   * environment properties for LDAP context
   */
  Hashtable<String, String> env = null;


  /**
   * Constructor of the LDAPConnector class
   * 
   * @param env Hashtable of envinorment properties required for LDAPContext
   * @param searchBase String containing DC values required for searching LDAP e.g., "DC=eclipse,DC=com"
   */
  public LDAPConnector(final Hashtable<String, String> env, final String searchBase) {
    this.env = env;
    this.searchBase = searchBase;
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
    }
    catch (NamingException e) {
      OseeLog.logf(Activator.class, Level.SEVERE, "Failed to establish LDAP connection", (Object) null);

    }
    if (this.ctx != null) {
      OseeLog.logf(Activator.class, Level.INFO, "LDAP connection established", (Object) null);

    }

  }

  /**
   * Searches the user in the LDAP directory and returns true if present and false if not present
   * 
   * @param username UserName in the form of sAMAccountName of user in LDAP Server
   * @return search result true if given username exisits in LDAP User Directory otherwise false
   */
  public boolean isLDAPUSer(final String username) {
    boolean searchResultFlag = false;
    String searchFilter = "(&(objectCategory=person)(sAMAccountName=" + username + "))";
    SearchControls searchControls = new SearchControls();
    searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
    try {
      init();
      NamingEnumeration<SearchResult> results = this.ctx.search(this.searchBase, searchFilter, searchControls);
      if (results == null) {
        return false;
      }
      SearchResult searchResult = null;
      Attributes attribs = null;
      @SuppressWarnings("rawtypes")
      NamingEnumeration allMembers = null;
      String accoutnName = null;

      while (results.hasMoreElements()) {
        searchResult = results.nextElement();
        attribs = searchResult.getAttributes();

        if (attribs.size() > 0) {

          Attribute attribute = attribs.get("sAMAccountName");
          allMembers = attribute.getAll();
          while ((allMembers != null) && allMembers.hasMoreElements()) {
            accoutnName = (String) allMembers.next();
            if (accoutnName.equalsIgnoreCase(username)) {
              searchResultFlag = true;
              break;
            }
          }
        }
        if (searchResultFlag) {
          break;
        }

      }

    }

    catch (NamingException e) {
      OseeLog.logf(Activator.class, Level.INFO, e.getMessage(), (Object) null);
    }
    finally {
      if (this.ctx != null) {
        try {
          this.ctx.close();
        }
        catch (NamingException e) {

          OseeLog.logf(Activator.class, Level.INFO, e.getMessage(), (Object) null);
        }
      }
    }
    return searchResultFlag;
  }


}
