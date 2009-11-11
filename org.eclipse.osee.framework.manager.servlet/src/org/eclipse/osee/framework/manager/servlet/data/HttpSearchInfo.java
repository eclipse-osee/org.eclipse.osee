/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.manager.servlet.data;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.type.PropertyStoreWriter;
import org.eclipse.osee.framework.search.engine.SearchOptions;
import org.eclipse.osee.framework.search.engine.SearchOptions.SearchOptionsEnum;

/**
 * @author Roberto E. Escobar
 */
public class HttpSearchInfo {

   private final String branchId;
   private final String queryString;
   private final SearchOptions options;
   private final String[] attributeTypes;

   public HttpSearchInfo(String branchId, SearchOptions options, String queryString, String... attributeTypes) {
      super();
      this.branchId = branchId;
      this.options = options;
      this.queryString = queryString;
      this.attributeTypes = attributeTypes != null ? attributeTypes : new String[0];
   }

   public String getQuery() {
      return queryString;
   }

   public String toString() {
      return queryString;
   }

   public int getId() {
      return Integer.parseInt(this.branchId);
   }

   public SearchOptions getOptions() {
      return options;
   }

   public String[] getAttributeTypes() {
      return attributeTypes;
   }

   @SuppressWarnings("unchecked")
   public static HttpSearchInfo loadFromGet(HttpServletRequest request) {
      SearchOptions options = new SearchOptions();
      String queryString = null;
      String branchId = null;
      String[] attributeTypes = null;
      Enumeration<String> enumeration = request.getParameterNames();
      while (enumeration.hasMoreElements()) {
         String name = enumeration.nextElement();
         String value = request.getParameter(name);
         if (name.equalsIgnoreCase("query")) {
            queryString = value;
         } else if (name.equalsIgnoreCase("branchId")) {
            branchId = value;
         } else {
            if (name.equalsIgnoreCase("name only")) {
               attributeTypes = new String[] {"Name"};
            } else {
               options.put(name.toLowerCase(), value);
            }
         }
      }
      return new HttpSearchInfo(branchId, options, queryString, attributeTypes);
   }

   public static HttpSearchInfo loadFromPost(HttpServletRequest request) throws Exception {
      PropertyStore propertyStore = new PropertyStore(request.getParameter("sessionId"));

      PropertyStoreWriter propertyStoreWriter = new PropertyStoreWriter();
      propertyStoreWriter.load(propertyStore, request.getInputStream());

      SearchOptions options = new SearchOptions();
      options.put(SearchOptionsEnum.include_deleted.asStringOption(), propertyStore.get("include deleted"));
      options.put(SearchOptionsEnum.match_word_order.asStringOption(), propertyStore.get("match word order"));
      options.put(SearchOptionsEnum.as_xml.asStringOption(), propertyStore.get("as xml"));
      options.put(SearchOptionsEnum.find_all_locations.asStringOption(), propertyStore.get("find all locations"));
      options.put(SearchOptionsEnum.case_sensitive.asStringOption(), propertyStore.get("case sensitive"));

      return new HttpSearchInfo(propertyStore.get("branchId"), options, propertyStore.get("query"),
            propertyStore.getArray("attributeType"));
   }
}
