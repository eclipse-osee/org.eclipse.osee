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
   private final String[] attributeTypeGuids;

   private HttpSearchInfo(String branchId, SearchOptions options, String queryString, String... attributeTypeGuids) {
      super();
      this.branchId = branchId;
      this.options = options;
      this.queryString = queryString;
      this.attributeTypeGuids = attributeTypeGuids;
   }

   public String getQuery() {
      return queryString;
   }

   @Override
   public String toString() {
      return queryString;
   }

   public int getId() {
      return Integer.parseInt(this.branchId);
   }

   public SearchOptions getOptions() {
      return options;
   }

   public String[] getAttributeTypeGuids() {
      return attributeTypeGuids;
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

      String[] attributeTypeStrs = propertyStore.getArray("attributeType");
      return new HttpSearchInfo(propertyStore.get("branchId"), options, propertyStore.get("query"),attributeTypeStrs
            );
   }
}
