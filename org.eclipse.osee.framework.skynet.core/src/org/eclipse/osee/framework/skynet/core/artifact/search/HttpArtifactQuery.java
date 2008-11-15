/*
 * Created on Sep 3, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.artifact.search;

import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilder;
import org.eclipse.osee.framework.core.data.JoinUtility;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationRequiredException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.type.ObjectPair;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor.AcquireResult;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.ISearchConfirmer;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * @author Roberto E. Escobar
 */
final class HttpArtifactQuery {
   private final String queryString;
   private final boolean matchWordOrder;
   private final boolean nameOnly;
   private final boolean includeDeleted;
   private final Branch branch;

   protected HttpArtifactQuery(String queryString, boolean matchWordOrder, boolean nameOnly, boolean includeDeleted, Branch branch) {
      this.branch = branch;
      this.matchWordOrder = matchWordOrder;
      this.includeDeleted = includeDeleted;
      this.nameOnly = nameOnly;
      this.queryString = queryString;
   }

   private String getSearchUrl() throws OseeDataStoreException, OseeAuthenticationRequiredException {
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("sessionId", ClientSessionManager.getSessionId());
      parameters.put("query", queryString);
      parameters.put("branchId", Integer.toString(branch.getBranchId()));
      if (includeDeleted) {
         parameters.put("include deleted", "true");
      }
      if (nameOnly) {
         parameters.put("name only", "true");
      }
      if (matchWordOrder) {
         parameters.put("match word order", "true");
      }
      return HttpUrlBuilder.getInstance().getOsgiServletServiceUrl(OseeServerContext.SEARCH_CONTEXT, parameters);
   }

   public List<Artifact> getArtifacts(ArtifactLoad loadLevel, ISearchConfirmer confirmer, boolean reload, boolean historical, boolean allowDeleted) throws Exception {
      List<Artifact> toReturn = null;
      ObjectPair<Integer, Integer> queryIdAndSize = executeSearch(getSearchUrl());
      if (queryIdAndSize != null && queryIdAndSize.object2 > 0) {
         try {
            toReturn =
                  ArtifactLoader.loadArtifactsFromQueryId(queryIdAndSize.object1, loadLevel, confirmer,
                        queryIdAndSize.object2, reload, historical, allowDeleted);
         } finally {
            JoinUtility.deleteQuery(JoinUtility.JoinItem.ARTIFACT, queryIdAndSize.object1.intValue());
         }
      }
      if (toReturn == null) {
         toReturn = java.util.Collections.emptyList();
      }
      return toReturn;
   }

   private ObjectPair<Integer, Integer> executeSearch(String searchUrl) throws Exception {
      ObjectPair<Integer, Integer> toReturn = null;
      Result result = SkynetActivator.areOSEEServicesAvailable();
      if (result.isTrue()) {
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         AcquireResult httpRequestResult = HttpProcessor.acquire(new URL(searchUrl), outputStream);
         if (httpRequestResult.wasSuccessful()) {
            String queryIdString = outputStream.toString("UTF-8");
            if (Strings.isValid(queryIdString)) {
               String[] entries = queryIdString.split(",\\s*");
               if (entries.length >= 2) {
                  toReturn = new ObjectPair<Integer, Integer>(new Integer(entries[0]), new Integer(entries[1]));
               }
            }
         } else if (httpRequestResult.getCode() != HttpURLConnection.HTTP_NO_CONTENT) {
            throw new Exception(String.format("Search error due to bad request: url[%s] status code: [%s]", searchUrl,
                  httpRequestResult.getCode()));
         }
      } else {
         throw new Exception(String.format("Unable to perform search: %s", result.getText()));
      }
      return toReturn;
   }
}
