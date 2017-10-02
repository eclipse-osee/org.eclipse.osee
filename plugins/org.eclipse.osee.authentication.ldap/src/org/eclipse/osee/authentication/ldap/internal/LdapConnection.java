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
package org.eclipse.osee.authentication.ldap.internal;

import static org.eclipse.osee.authentication.ldap.internal.util.LdapUtil.getValue;
import static org.eclipse.osee.authentication.ldap.internal.util.LdapUtil.patternField;
import static org.eclipse.osee.authentication.ldap.internal.util.LdapUtil.stringField;
import java.io.Closeable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.security.auth.login.AccountException;
import org.eclipse.osee.authentication.ldap.LdapAuthenticationType;
import org.eclipse.osee.authentication.ldap.LdapSearchScope;
import org.eclipse.osee.authentication.ldap.internal.filter.ActiveDirectoryLdapFilter;
import org.eclipse.osee.authentication.ldap.internal.filter.Rfc2307CompliantLdapFilter;
import org.eclipse.osee.authentication.ldap.internal.util.LdapEntry;
import org.eclipse.osee.authentication.ldap.internal.util.LdapQuery;
import org.eclipse.osee.authentication.ldap.internal.util.LdapUtil;
import org.eclipse.osee.authentication.ldap.internal.util.VariablePattern;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class LdapConnection implements Closeable {

   private static final LdapFilter RFC_2307_LDAP_FILTER = new Rfc2307CompliantLdapFilter();
   private static final LdapFilter ACTIVE_DIRECTORY_LDAP_FILTER = new ActiveDirectoryLdapFilter();

   private static final String LDAP_CAPABILITIES__ATTRIBUTE_ID = "supportedCapabilities";
   private static final String LDAP_CAPABILITIES__ACTIVE_DIRECTORY_OID = "1.2.840.113556.1.4.800";
   private static final String LDAP_CAPABILITIES__ACTIVE_DIRECTORY_APPLICATION_MODE_OID = "1.2.840.113556.1.4.1851";

   private final Log logger;
   private final DirContext connection;
   private final ContextConfigProvider provider;
   private LdapFilter defaultFilterSettings;

   public LdapConnection(Log logger, ContextConfigProvider provider, DirContext connection) {
      this.logger = logger;
      this.provider = provider;
      this.connection = connection;
   }

   private String getServerAddress() {
      return provider.getServerAddress();
   }

   @Override
   public void close() {
      close(connection);
   }

   public LdapFilter getDefaultFilter() {
      if (defaultFilterSettings == null) {
         boolean isActiveDirectory = false;
         try {
            isActiveDirectory = hasActiveDirectoryCapabilities();
         } catch (NamingException ex) {
            logger.warn(ex,
               "Unable to determine LDAP server capabilities for [%s] defaulting to RFC-2307 compliant LDAP.",
               getServerAddress());
         }
         if (isActiveDirectory) {
            defaultFilterSettings = ACTIVE_DIRECTORY_LDAP_FILTER;
         } else {
            defaultFilterSettings = RFC_2307_LDAP_FILTER;
         }
      }
      return defaultFilterSettings;
   }

   private boolean hasActiveDirectoryCapabilities() throws NamingException {
      Attributes rootAtts = connection.getAttributes("");
      Attribute attribute = rootAtts.get(LDAP_CAPABILITIES__ATTRIBUTE_ID);
      return attribute != null && //
         (attribute.contains(LDAP_CAPABILITIES__ACTIVE_DIRECTORY_OID) || //
            attribute.contains(LDAP_CAPABILITIES__ACTIVE_DIRECTORY_APPLICATION_MODE_OID));
   }

   public boolean authenticate(String username, String password) throws AccountException {
      Hashtable<String, String> env = provider.getContextConfig();
      env.put(Context.SECURITY_AUTHENTICATION, LdapAuthenticationType.SIMPLE.getContextAuthenticationName());
      env.put(Context.SECURITY_PRINCIPAL, getValue(username, ""));
      env.put(Context.SECURITY_CREDENTIALS, getValue(password, ""));
      boolean result = false;
      DirContext connection = null;
      try {
         connection = new InitialDirContext(env);
         result = true;
      } catch (NamingException ex) {
         throw new AccountException("Incorrect userName or password");
      } finally {
         close(connection);
      }
      return result;
   }

   private void close(DirContext connection) {
      if (connection != null) {
         try {
            connection.close();
         } catch (NamingException ex) {
            logger.error(ex, "Error closing LDAP connected to [%s]", getServerAddress());
         }
      }
   }

   public LdapAccount findAccount(LdapFilter filter, String username) throws NamingException {
      LdapFilter defaultFilter = getDefaultFilter();

      String usernameVariableName =
         LdapUtil.getValue(filter.getUserNameVariableName(), defaultFilter.getUserNameVariableName());

      String searchBase = LdapUtil.getValue(filter.getAccountBase(), defaultFilter.getAccountBase());
      LdapSearchScope accountSearchScope =
         LdapUtil.getValue(filter.getAccountSearchScope(), defaultFilter.getAccountSearchScope());

      Set<String> fieldsToGet = new HashSet<>();
      VariablePattern accountSearchPattern =
         patternField(fieldsToGet, filter.getAccountPattern(), defaultFilter.getAccountPattern());
      VariablePattern accountFullName =
         patternField(fieldsToGet, filter.getAccountDisplayName(), defaultFilter.getAccountDisplayName());
      VariablePattern accountEmailAddress =
         patternField(fieldsToGet, filter.getAccountEmailAddress(), defaultFilter.getAccountEmailAddress());
      VariablePattern accountUserName =
         patternField(fieldsToGet, filter.getAccountUserName(), defaultFilter.getAccountUserName());

      String memberOfField = stringField(fieldsToGet, filter.getGroupMembersOf(), defaultFilter.getGroupMembersOf());

      boolean memberOfFieldInAccount = Strings.isValid(memberOfField) || defaultFilter.isGroupMembershipPartOfAccount();
      if (memberOfFieldInAccount) {
         VariablePattern groupByGroupMemberPattern = patternField(fieldsToGet, filter.getGroupByGroupMemberPattern(),
            defaultFilter.getGroupByGroupMemberPattern());
         if (groupByGroupMemberPattern != null) {
            for (String variables : groupByGroupMemberPattern.getVariableNames()) {
               fieldsToGet.add(variables);
            }
         }
      }

      LdapQuery query = new LdapQuery(logger)//
         .base(searchBase)//
         .pattern(accountSearchPattern)//
         .fields(fieldsToGet)//
         .scope(accountSearchScope);

      HashMap<String, String> params = new HashMap<>();
      params.put(usernameVariableName, username);

      ResultSet<LdapEntry> results = query.search(connection, params);
      LdapEntry entry = results.getExactlyOne();
      return new LdapAccount(entry, accountFullName, accountEmailAddress, accountUserName);
   }

   public Set<LdapGroup> findGroups(LdapFilter filter, String username) throws NamingException {
      return findGroups(filter, username, null);
   }

   public Set<LdapGroup> findGroups(LdapFilter filter, String username, LdapAccount account) throws NamingException {
      LdapFilter defaultFilter = getDefaultFilter();

      String usernameVariableName =
         LdapUtil.getValue(filter.getUserNameVariableName(), defaultFilter.getUserNameVariableName());

      String searchBase = LdapUtil.getValue(filter.getGroupBase(), defaultFilter.getGroupBase());
      LdapSearchScope groupSearchScope =
         LdapUtil.getValue(filter.getGroupSearchScope(), defaultFilter.getGroupSearchScope());

      Set<String> fieldsToGet = new HashSet<>();
      VariablePattern groupNamePattern = patternField(fieldsToGet, filter.getGroupName(), defaultFilter.getGroupName());
      VariablePattern groupByGroupMemberPattern =
         patternField(fieldsToGet, filter.getGroupByGroupMemberPattern(), defaultFilter.getGroupByGroupMemberPattern());

      // In account on in group trees
      String memberOfField = LdapUtil.getValue(filter.getGroupMembersOf(), defaultFilter.getGroupMembersOf());

      ResultSet<LdapEntry> results;
      boolean isGroupMembershipInAccount =
         groupByGroupMemberPattern == null || defaultFilter.isGroupMembershipPartOfAccount();
      if (isGroupMembershipInAccount) {
         throw new UnsupportedOperationException("Not yet implemented");
      } else {
         LdapQuery query = new LdapQuery(logger) //
            .base(searchBase)//
            .pattern(groupByGroupMemberPattern)//
            .fields(fieldsToGet)//
            .scope(groupSearchScope);

         HashMap<String, String> params = new HashMap<>();
         for (String key : query.getParameters()) {
            String value = account.getField(key);
            if (Strings.isValid(value)) {
               params.put(key, value);
            }
         }
         params.put(usernameVariableName, username);
         results = query.search(connection, params);
      }

      Set<LdapGroup> groups = new HashSet<>();
      Set<String> groupDns = new HashSet<>();
      for (LdapEntry entry : results) {
         findSubGroups(groups, groupDns, groupNamePattern, memberOfField, entry);
      }
      return groups;
   }

   private void findSubGroups(Set<LdapGroup> groups, Set<String> groupDns, VariablePattern groupNamePattern, String memberOfField, LdapEntry entry) throws NamingException {
      String currentGroupDn = entry.getDistinguishedName();
      boolean wasAdded = groupDns.add(currentGroupDn);
      if (wasAdded) {
         LdapGroup group = new LdapGroup(entry, groupNamePattern);
         groups.add(group);
         if (Strings.isValid(memberOfField)) {
            throw new UnsupportedOperationException("Not yet implemented");
         }
      }
   }
}
