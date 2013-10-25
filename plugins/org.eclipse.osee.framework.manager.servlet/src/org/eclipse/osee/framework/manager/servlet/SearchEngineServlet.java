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
import java.util.Collection;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.ResultSet;
import org.eclipse.osee.framework.core.enums.CaseType;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.enums.TokenDelimiterMatch;
import org.eclipse.osee.framework.core.enums.TokenOrderType;
import org.eclipse.osee.framework.core.message.SearchOptions;
import org.eclipse.osee.framework.core.message.SearchRequest;
import org.eclipse.osee.framework.core.message.SearchResponse;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.server.ISessionManager;
import org.eclipse.osee.framework.core.server.SecureOseeHttpServlet;
import org.eclipse.osee.framework.core.translation.IDataTranslationService;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.manager.servlet.internal.ApplicationContextFactory;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.search.Match;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author Roberto E. Escobar
 */
public class SearchEngineServlet extends SecureOseeHttpServlet {

   private static final long serialVersionUID = 3722992788943330970L;

   private final IDataTranslationService translationService;
   private final OrcsApi orcsApi;

   public SearchEngineServlet(Log logger, ISessionManager sessionManager, IDataTranslationService translationService, OrcsApi orcsApi) {
      super(logger, sessionManager);
      this.translationService = translationService;
      this.orcsApi = orcsApi;
   }

   private ApplicationContext getContext(HttpServletRequest req) {
      return ApplicationContextFactory.createContext(getSessionId(req));
   }

   @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      try {
         SearchRequest searchRequest =
            translationService.convert(request.getInputStream(), CoreTranslatorId.SEARCH_REQUEST);
         SearchOptions options = searchRequest.getOptions();

         TokenOrderType operator = options.isMatchWordOrder() ? TokenOrderType.MATCH_ORDER : TokenOrderType.ANY_ORDER;
         CaseType caseType = options.isCaseSensitive() ? CaseType.MATCH_CASE : CaseType.IGNORE_CASE;

         QueryFactory factory = orcsApi.getQueryFactory(getContext(request));
         QueryBuilder builder = factory.fromBranch(searchRequest.getBranch());
         builder.includeDeleted(options.getDeletionFlag().areDeletedAllowed());

         Collection<IAttributeType> attributeTypes = options.getAttributeTypeFilter();
         if (attributeTypes.isEmpty()) {
            attributeTypes = Collections.singleton(QueryBuilder.ANY_ATTRIBUTE_TYPE);
         }
         builder.and(attributeTypes, searchRequest.getRawSearch(), TokenDelimiterMatch.ANY, operator, caseType);

         BranchCache branchCache = orcsApi.getBranchCache();

         SearchResponse searchResponse = new SearchResponse();
         if (options.isFindAllLocationsEnabled()) {
            ResultSet<Match<ArtifactReadable, AttributeReadable<?>>> results = builder.getMatches();
            for (Match<ArtifactReadable, AttributeReadable<?>> match : results) {
               ArtifactReadable artifact = match.getItem();
               long branchId = branchCache.getLocalId(artifact.getBranch());
               for (AttributeReadable<?> attribute : match.getElements()) {
                  searchResponse.add(branchId, artifact.getLocalId(), attribute.getGammaId(),
                     match.getLocation(attribute));
               }
            }
         } else {
            ResultSet<ArtifactReadable> results = builder.getResults();
            for (ArtifactReadable artifact : results) {
               long branchId = branchCache.getLocalId(artifact.getBranch());
               searchResponse.add(branchId, artifact.getLocalId(), -1);
            }
         }

         response.setStatus(HttpServletResponse.SC_ACCEPTED);
         response.setContentType("text/xml");
         response.setCharacterEncoding("UTF-8");

         InputStream inputStream = translationService.convertToStream(searchResponse, CoreTranslatorId.SEARCH_RESPONSE);
         Lib.inputStreamToOutputStream(inputStream, response.getOutputStream());

      } catch (Exception ex) {
         getLogger().error(ex, "Failed to respond to a search engine servlet request [%s]", request.getRequestURL());
         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         response.setContentType("text/plain");
         response.getWriter().write(Lib.exceptionToString(ex));
         response.getWriter().flush();
         response.getWriter().close();
      }
   }
}
