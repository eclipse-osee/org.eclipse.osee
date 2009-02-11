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
package org.eclipse.osee.framework.search.engine.servlet;

import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.type.PropertyStoreWriter;
import org.eclipse.osee.framework.search.engine.Options;

/**
 * @author Roberto E. Escobar
 */
class HttpSearchInfo {

   private final String branchId;
   private final String queryString;
   private final Options options;
   private final String[] attributeTypes;

   public HttpSearchInfo(String branchId, Options options, String queryString, String... attributeTypes) {
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

   public int getBranchId() {
      return Integer.parseInt(this.branchId);
   }

   public Options getOptions() {
      return options;
   }

   public String[] getAttributeTypes() {
      return attributeTypes;
   }

   @SuppressWarnings("unchecked")
   public static HttpSearchInfo loadFromGet(HttpServletRequest request) {
      Options options = new Options();
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

      Options options = new Options();
      options.put("include deleted", propertyStore.get("include deleted"));
      options.put("match word order", propertyStore.get("match word order"));
      options.put("as xml", propertyStore.get("as xml"));
      options.put("find all locations", propertyStore.get("find all locations"));

      return new HttpSearchInfo(propertyStore.get("branchId"), options, propertyStore.get("query"),
            propertyStore.getArray("attributeType"));
   }
}
