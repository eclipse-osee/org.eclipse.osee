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
package org.eclipse.osee.icteam.web.rest.layer.util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;

import org.eclipse.osee.framework.authentication.ldap.core.Activator;
import org.eclipse.osee.framework.authentication.ldap.core.internal.LDAPConnector;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * Create and Search for the User from LDAP server Util Class for LDAP user Search
 * 
 * @author Ajay Chandrahasan 
 */

public class LdapUtil {

  /**
   * Searches the user in the LDAP directory and returns searchResult Object for the User which holds the data like mail
   * id etc
   * 
   * @param username UserName in the form of sAMAccountName of user in LDAP Server
   * @return search result object
   */
  public static List<org.eclipse.osee.icteam.common.clientserver.LdapUserDetailsWrapper> getLDAPSearchResult(
      final String username) {

    List<org.eclipse.osee.icteam.common.clientserver.LdapUserDetailsWrapper> userList =
        new ArrayList<org.eclipse.osee.icteam.common.clientserver.LdapUserDetailsWrapper>();

    SearchResult searchResult = null;
    // LDAPConnector connector = LDAPConnector.getConnector();

    String searchFilter =
        "(&(objectCategory=person)(|(sAMAccountName=" + username + "*)(givenName=" + username + "*)(displayname=" +
            username + "*)))";


    // String searchFilter = "(&(objectCategory=person)(displayname="
    // + username + "))";
    SearchControls searchControls = new SearchControls();
    searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
    try {
      // connector.init();
      NamingEnumeration<SearchResult> results = null;
      InitialLdapContext ctxInstance = LDAPConnector.getCtxInstance();
      String getsearchBaseFromDb = LDAPConnector.getsearchBaseFromDb();
      if (ctxInstance != null) {
        results = ctxInstance.search(getsearchBaseFromDb, searchFilter, searchControls);
        ctxInstance.close();
        LDAPConnector.setCtxInstance(null);
      }

      if (results != null) {

        Attributes attribs = null;
        @SuppressWarnings("rawtypes")
        NamingEnumeration allMembers = null;
        String accoutnName;


        while (results.hasMoreElements()) {
          searchResult = results.nextElement();
          attribs = searchResult.getAttributes();


          if (attribs.size() > 0) {

            org.eclipse.osee.icteam.common.clientserver.LdapUserDetailsWrapper userDetails =
                new org.eclipse.osee.icteam.common.clientserver.LdapUserDetailsWrapper();

            Attribute attributeName = attribs.get("displayname");
            if (attributeName != null) {
              NamingEnumeration nameMembers = attributeName.getAll();
              while ((nameMembers != null) && nameMembers.hasMoreElements()) {
                String displayName = (String) nameMembers.next();
                userDetails.setDisplayName(displayName);
              }
            }

            Attribute attributeUserID = attribs.get("sAMAccountName");
            if (attributeUserID != null) {
              NamingEnumeration accNameMembers = attributeUserID.getAll();
              while ((accNameMembers != null) && accNameMembers.hasMoreElements()) {
                String accName = (String) accNameMembers.next();
                userDetails.setUserId(accName);
              }
            }

            Attribute attributeMail = attribs.get("mail");
            if (attributeMail != null) {
              NamingEnumeration mailMembers = attributeMail.getAll();
              while ((mailMembers != null) && mailMembers.hasMoreElements()) {
                String mailName = (String) mailMembers.next();
                userDetails.setMail(mailName);
              }
            }

            userList.add(userDetails);
          }
        }
      }
    }

    catch (NamingException e) {
      OseeLog.logf(Activator.class, Level.INFO, e.getMessage(), (Object) null);
    }
    finally {

    }
    return userList;
  }



}
