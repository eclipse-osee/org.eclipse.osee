/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.authentication.ldap.internal.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.PartialResultException;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import org.eclipse.osee.authentication.ldap.LdapSearchScope;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public final class LdapQuery {

   private final Log logger;
   private String base;
   private LdapSearchScope searchScope;
   private VariablePattern pattern;

   private final Set<String> itemsToReturn = new HashSet<>();

   public LdapQuery(Log logger) {
      this.logger = logger;
   }

   public String getBase() {
      return base;
   }

   public LdapSearchScope getSearchScope() {
      return searchScope;
   }

   public VariablePattern getPattern() {
      return pattern;
   }

   public List<String> getParameters() {
      return pattern.getVariableNames();
   }

   public LdapQuery base(String base) {
      this.base = base;
      return this;
   }

   public LdapQuery scope(LdapSearchScope searchScope) {
      this.searchScope = searchScope;
      return this;
   }

   public LdapQuery pattern(VariablePattern pattern) {
      this.pattern = pattern;
      return this;
   }

   public LdapQuery fields(Iterable<String> fields) {
      for (String field : fields) {
         itemsToReturn.add(field);
      }
      return this;
   }

   public LdapQuery field(String... fields) {
      return fields(Arrays.asList(fields));
   }

   public ResultSet<LdapEntry> search(DirContext connection, Map<String, String> bindings) throws NamingException {
      String searchPattern = pattern.getIndexedPattern();
      String[] searchBindings = pattern.getVariableValues(bindings);
      String[] attributesToReturn = null;
      LdapSearchScope searchDepth = LdapUtil.getValue(searchScope, LdapSearchScope.SUBTREE_SCOPE);
      if (!itemsToReturn.isEmpty()) {
         attributesToReturn = itemsToReturn.toArray(new String[itemsToReturn.size()]);
      }
      List<LdapEntry> results = new LinkedList<>();
      search(results, connection, base, searchPattern, searchBindings, attributesToReturn, searchDepth);
      return ResultSets.newResultSet(results);
   }

   private void search(List<LdapEntry> results, DirContext connection, String searchPath, String expressionFilter, String[] filterArgs, String[] attributesToReturn, LdapSearchScope searchScope) throws NamingException {
      long startTime = System.currentTimeMillis();
      long endTime = startTime;
      boolean traceEnabled = logger.isTraceEnabled();
      String queryParams = String.format("scope[%s] searchPath[%s] filterExp[%s] filterArgs%s attributesToReturn%s", //
         searchScope, searchPath, expressionFilter, filterArgs != null ? Arrays.deepToString(filterArgs) : "[]",
         attributesToReturn != null ? Arrays.deepToString(attributesToReturn) : "[]");

      if (traceEnabled) {
         logger.trace("[%s] started - %s", getClass().getSimpleName(), queryParams);
      }
      try {
         SearchControls searchControls = new SearchControls();
         searchControls.setSearchScope(searchScope.asSearchDepth());
         searchControls.setReturningAttributes(attributesToReturn);
         NamingEnumeration<SearchResult> resultsEnum =
            connection.search(searchPath, expressionFilter, filterArgs, searchControls);
         if (resultsEnum != null) {
            try {
               AttributeProcessor processor = createProcessor();
               while (resultsEnum.hasMore()) {
                  SearchResult rSet = resultsEnum.next();
                  LdapEntry entry = processor.process(rSet);
                  results.add(entry);
               }
            } catch (PartialResultException ex) {
               logger.warn(ex, "LDAP Query partial results - %s", queryParams);
            } finally {
               resultsEnum.close();
            }
         }
      } finally {
         if (traceEnabled) {
            endTime = System.currentTimeMillis() - startTime;
            logger.trace("[%s] completed [%s] - %s", getClass().getSimpleName(), Lib.asTimeString(endTime),
               queryParams);
         }
      }
   }

   private AttributeProcessor createProcessor() {
      return itemsToReturn.isEmpty() ? new AllAttributeProcessor() : new SelectiveAttributeProcessor();
   }

   private static abstract class AttributeProcessor {

      public LdapEntry process(SearchResult rSet) {
         Map<String, Attribute> attributes = new HashMap<>();

         String distinguishedName = rSet.getNameInNamespace();
         Attribute attribute = new BasicAttribute(LdapEntry.LDAP_ENTRY__DISTINGUISHED_NAME_KEY, distinguishedName);
         attributes.put(LdapEntry.LDAP_ENTRY__DISTINGUISHED_NAME_KEY, attribute);

         process(rSet, attributes);
         return new LdapEntry(attributes);
      }

      protected abstract void process(SearchResult rSet, Map<String, Attribute> attributes);
   }

   private static class AllAttributeProcessor extends AttributeProcessor {

      @Override
      protected void process(SearchResult rSet, Map<String, Attribute> attributes) {
         NamingEnumeration<? extends Attribute> enumeration = rSet.getAttributes().getAll();
         while (enumeration.hasMoreElements()) {
            final Attribute attribute = enumeration.nextElement();
            if (attribute != null && attribute.size() > 0) {
               attributes.put(attribute.getID(), attribute);
            }
         }
      }
   }

   private class SelectiveAttributeProcessor extends AttributeProcessor {

      @Override
      protected void process(SearchResult rSet, Map<String, Attribute> attributes) {
         for (final String attName : itemsToReturn) {
            Attribute attribute = rSet.getAttributes().get(attName);
            if (attribute != null && attribute.size() > 0) {
               attributes.put(attName, attribute);
            }
         }
      }
   }
}
