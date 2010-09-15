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
package org.eclipse.osee.framework.manager.servlet;

import java.io.IOException;
import java.io.StringWriter;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.model.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.server.ISessionManager;
import org.eclipse.osee.framework.core.server.SecureOseeHttpServlet;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.JoinUtility.ArtifactJoinQuery;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.manager.servlet.data.HttpSearchInfo;
import org.eclipse.osee.framework.manager.servlet.internal.Activator;
import org.eclipse.osee.framework.search.engine.ISearchEngine;
import org.eclipse.osee.framework.search.engine.SearchOptions.SearchOptionsEnum;
import org.eclipse.osee.framework.search.engine.SearchResult;
import org.eclipse.osee.framework.search.engine.SearchResultToXmlOperation;

/**
 * @author Roberto E. Escobar
 */
public class SearchEngineServlet extends SecureOseeHttpServlet {

   private static final long serialVersionUID = 3722992788943330970L;

   private final ISearchEngine searchEngine;
   private final AttributeTypeCache attributeTypeCache;

   public SearchEngineServlet(ISessionManager sessionManager, ISearchEngine searchEngine, AttributeTypeCache attributeTypeCache) {
      super(sessionManager);
      this.searchEngine = searchEngine;
      this.attributeTypeCache = attributeTypeCache;
   }

   @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      try {
         HttpSearchInfo searchInfo = HttpSearchInfo.loadFromPost(request);

         String clientVersion = ModCompatible.getClientVersion(getSessionManager(), request.getParameter("sessionId"));
         boolean isCompatible = ModCompatible.is_0_9_2_Compatible(clientVersion);

         executeSearch(isCompatible, searchInfo, response);
      } catch (Exception ex) {
         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         response.setContentType("text/plain");
         OseeLog.log(Activator.class, Level.SEVERE,
            String.format("Failed to respond to a search engine servlet request [%s]", request.getRequestURL()), ex);
         response.getWriter().write(Lib.exceptionToString(ex));
      }
   }

   private void executeSearch(boolean isCompatible, HttpSearchInfo searchInfo, HttpServletResponse response) throws IOException {
      try {
         String[] attributeTypeGuids = searchInfo.getAttributeTypeGuids();
         AttributeType[] attributeTypes = new AttributeType[attributeTypeGuids.length];

         int index = 0;
         for (String attributeTypeValue : attributeTypeGuids) {
            if (!isCompatible) {
               attributeTypes[index++] = attributeTypeCache.getBySoleName(attributeTypeValue);
            } else {
               attributeTypes[index++] = attributeTypeCache.getByGuid(attributeTypeValue);
            }
         }

         SearchResult results =
            searchEngine.search(searchInfo.getQuery(), searchInfo.getId(), searchInfo.getOptions(), attributeTypes);
         StringWriter writer = new StringWriter();
         IOperation operation = new SearchResultToXmlOperation(results, writer);
         Operations.executeWork(operation);

         response.setStatus(HttpServletResponse.SC_ACCEPTED);
         response.setCharacterEncoding("UTF-8");
         response.setContentType("text/xml");
         response.getWriter().write(writer.toString());

         if (results.isEmpty() && Strings.isValid(results.getErrorMessage())) {

            sendEmptyAsXml(response, results);
         } else if (!results.isEmpty()) {
            long start = System.currentTimeMillis();
            if (!searchInfo.getOptions().getBoolean(SearchOptionsEnum.as_xml.asStringOption())) {
               sendAsDbJoin(response, results);
            } else {
               response.setCharacterEncoding("UTF-8");
               response.setContentType("text/xml");
               sendAsXml(response, results);
            }
            System.out.println(String.format("Search for [%s] - [%d results sent in %d ms]", searchInfo.getQuery(),
               results.size(), System.currentTimeMillis() - start));
         } else {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/plain");
         }
      } catch (Exception ex) {
         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         response.setContentType("text/plain");
         OseeLog.log(Activator.class, Level.SEVERE,
            String.format("Failed to respond to a search engine servlet request [%s]", searchInfo.toString()), ex);
         response.getWriter().write(Lib.exceptionToString(ex));
      }
   }

   private void sendAsDbJoin(HttpServletResponse response, SearchResult results) throws Exception {
      response.setCharacterEncoding("UTF-8");
      response.setContentType("text/plain");

      ArtifactJoinQuery joinQuery = JoinUtility.createArtifactJoinQuery();
      for (Integer branchId : results.getBranchIds()) {
         for (Integer artId : results.getArtifactIds(branchId)) {
            joinQuery.add(artId, branchId);
         }
      }
      joinQuery.store();
      response.getWriter().write(String.format("%d,%d", joinQuery.getQueryId(), joinQuery.size()));
   }
}
