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
import java.io.PrintWriter;
import java.util.Collection;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.model.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.server.ISessionManager;
import org.eclipse.osee.framework.core.server.SecureOseeHttpServlet;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.JoinUtility.ArtifactJoinQuery;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.manager.servlet.data.HttpSearchInfo;
import org.eclipse.osee.framework.manager.servlet.internal.Activator;
import org.eclipse.osee.framework.search.engine.ISearchEngine;
import org.eclipse.osee.framework.search.engine.MatchLocation;
import org.eclipse.osee.framework.search.engine.SearchOptions.SearchOptionsEnum;
import org.eclipse.osee.framework.search.engine.SearchResult;
import org.eclipse.osee.framework.search.engine.SearchResult.ArtifactMatch;

/**
 * @author Roberto E. Escobar
 */
public class SearchEngineServlet extends SecureOseeHttpServlet {

   private static final long serialVersionUID = 3722992788943330970L;

   private final ISearchEngine searchEngine;
   private final IOseeCachingService cacheService;

   public SearchEngineServlet(ISessionManager sessionManager, ISearchEngine searchEngine, IOseeCachingService cacheService) {
      super(sessionManager);
      this.searchEngine = searchEngine;
      this.cacheService = cacheService;
   }

   @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      try {
         HttpSearchInfo searchInfo = HttpSearchInfo.loadFromPost(request);

         String clientVersion = ModCompatible.getClientVersion(getSessionManager(), request.getParameter("sessionId"));
         boolean isCompatible = ModCompatible.is_0_9_2_Compatible(clientVersion);

         executeSearch(isCompatible, searchInfo, response, false);
      } catch (Exception ex) {
         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         response.setContentType("text/plain");
         OseeLog.log(Activator.class, Level.SEVERE,
            String.format("Failed to respond to a search engine servlet request [%s]", request.getRequestURL()), ex);
         response.getWriter().write(Lib.exceptionToString(ex));
      }
   }

   private void executeSearch(boolean isCompatible, HttpSearchInfo searchInfo, HttpServletResponse response, boolean wasFromGet) throws IOException {
      try {
         AttributeTypeCache attributeTypeCache = cacheService.getAttributeTypeCache();

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
         response.setStatus(wasFromGet ? HttpServletResponse.SC_OK : HttpServletResponse.SC_ACCEPTED);
         if (!results.isEmpty()) {
            long start = System.currentTimeMillis();
            if (!searchInfo.getOptions().getBoolean(SearchOptionsEnum.as_xml.asStringOption())) {
               sendAsDbJoin(response, results);
            } else {
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
      response.getWriter().flush();
      response.getWriter().close();
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

   /**
    * <match artId="" branchId=""> <attr gammaId=""><location start="" end="" /></attr> </match>
    */
   private void sendAsXml(HttpServletResponse response, SearchResult results) throws Exception {
      response.setCharacterEncoding("UTF-8");
      response.setContentType("text/xml");
      PrintWriter writer = response.getWriter();

      writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
      writer.write("<search>");
      for (Integer branchId : results.getBranchIds()) {
         writer.write(String.format("<match branchId=\"%s\">", branchId));
         for (ArtifactMatch match : results.getArtifacts(branchId)) {
            writer.write(String.format("<art artId=\"%s\" >", match.getArtId()));
            for (Long gammaId : match.getAttributes()) {
               Collection<MatchLocation> locations = match.getMatchLocations(gammaId);
               writer.write(String.format("<attr gammaId=\"%s\">", gammaId));
               if (locations != null) {
                  for (MatchLocation location : locations) {
                     writer.write(String.format("<location start=\"%s\" end=\"%s\" />", location.getStartPosition(),
                        location.getEndPosition()));
                  }
               }
               writer.write("</attr>");
            }
            writer.write("</art>");
         }
         writer.write("</match>");
      }
      writer.write("</search>");
   }
}
