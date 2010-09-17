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
import java.io.InputStream;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.message.SearchRequest;
import org.eclipse.osee.framework.core.message.SearchResponse;
import org.eclipse.osee.framework.core.server.ISessionManager;
import org.eclipse.osee.framework.core.server.SecureOseeHttpServlet;
import org.eclipse.osee.framework.core.translation.IDataTranslationService;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.manager.servlet.internal.Activator;
import org.eclipse.osee.framework.search.engine.ISearchEngine;

/**
 * @author Roberto E. Escobar
 */
public class SearchEngineServlet extends SecureOseeHttpServlet {

   private static final long serialVersionUID = 3722992788943330970L;

   private final ISearchEngine searchEngine;
   private final IDataTranslationService translationService;

   public SearchEngineServlet(ISessionManager sessionManager, ISearchEngine searchEngine, IDataTranslationService translationService) {
      super(sessionManager);
      this.searchEngine = searchEngine;
      this.translationService = translationService;
   }

   @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      try {
         SearchRequest searchRequest =
            translationService.convert(request.getInputStream(), CoreTranslatorId.SEARCH_REQUEST);

         SearchResponse searchResponse = new SearchResponse();
         searchEngine.search(searchRequest, searchResponse);

         response.setStatus(HttpServletResponse.SC_ACCEPTED);
         response.setContentType("text/xml");
         response.setCharacterEncoding("UTF-8");

         InputStream inputStream = translationService.convertToStream(searchResponse, CoreTranslatorId.SEARCH_RESPONSE);
         Lib.inputStreamToOutputStream(inputStream, response.getOutputStream());

      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE,
            String.format("Failed to respond to a search engine servlet request [%s]", request.getRequestURL()), ex);
         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         response.setContentType("text/plain");
         response.getWriter().write(Lib.exceptionToString(ex));
         response.getWriter().flush();
         response.getWriter().close();
      }
   }

   //         StringWriter writer = new StringWriter();
   //         IOperation operation = new SearchResultToXmlOperation(results, writer);
   //         Operations.executeWork(operation);
   //
   //         response.setStatus(HttpServletResponse.SC_ACCEPTED);
   //         response.setCharacterEncoding("UTF-8");
   //         response.setContentType("text/xml");
   //         response.getWriter().write(writer.toString());
   //
   //         if (results.isEmpty() && Strings.isValid(results.getErrorMessage())) {
   //
   //            sendEmptyAsXml(response, results);
   //         } else if (!results.isEmpty()) {
   //            long start = System.currentTimeMillis();
   //            if (!searchInfo.getOptions().getBoolean(SearchOptionsEnum.as_xml.asStringOption())) {
   //               sendAsDbJoin(response, results);
   //            } else {
   //               response.setCharacterEncoding("UTF-8");
   //               response.setContentType("text/xml");
   //               sendAsXml(response, results);
   //            }
   //            System.out.println(String.format("Search for [%s] - [%d results sent in %d ms]", searchInfo.getQuery(),
   //               results.size(), System.currentTimeMillis() - start));
   //         } else {
   //            response.setCharacterEncoding("UTF-8");
   //            response.setContentType("text/plain");
   //         }
   //   private void sendAsDbJoin(HttpServletResponse response, SearchResult results) throws Exception {
   //      response.setCharacterEncoding("UTF-8");
   //      response.setContentType("text/plain");
   //
   //      ArtifactJoinQuery joinQuery = JoinUtility.createArtifactJoinQuery();
   //      for (Integer branchId : results.getBranchIds()) {
   //         for (Integer artId : results.getArtifactIds(branchId)) {
   //            joinQuery.add(artId, branchId);
   //         }
   //      }
   //      joinQuery.store();
   //      response.getWriter().write(String.format("%d,%d", joinQuery.getQueryId(), joinQuery.size()));
   //   }

}
