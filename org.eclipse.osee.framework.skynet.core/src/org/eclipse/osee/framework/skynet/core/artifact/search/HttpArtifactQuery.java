/*
 * Created on Sep 3, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.artifact.search;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilder;
import org.eclipse.osee.framework.core.data.JoinUtility;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.type.ObjectPair;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.type.PropertyStoreWriter;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor.AcquireResult;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
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
   private final String[] attributeTypes;
   private final boolean includeDeleted;
   private final Branch branch;

   protected HttpArtifactQuery(Branch branch, String queryString, boolean matchWordOrder, boolean includeDeleted, String... attributeTypes) {
      this.branch = branch;
      this.matchWordOrder = matchWordOrder;
      this.includeDeleted = includeDeleted;
      this.attributeTypes = attributeTypes != null ? attributeTypes : new String[0];
      this.queryString = queryString;
   }

   private String getSearchUrl(String sessionId) throws OseeDataStoreException {
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("sessionId", sessionId);
      return HttpUrlBuilder.getInstance().getOsgiServletServiceUrl(OseeServerContext.SEARCH_CONTEXT, parameters);
   }

   private CharBackedInputStream getSearchParameters(String sessionId) throws IOException {
      CharBackedInputStream backedInputStream = new CharBackedInputStream();

      PropertyStore propertyStore = new PropertyStore(sessionId);
      propertyStore.put("branchId", branch.getBranchId());
      propertyStore.put("query", queryString);
      propertyStore.put("include deleted", includeDeleted);
      propertyStore.put("match word order", matchWordOrder);
      propertyStore.put("attributeType", attributeTypes);

      PropertyStoreWriter writer = new PropertyStoreWriter();
      writer.save(propertyStore, backedInputStream.getWriter());

      return backedInputStream;
   }

   public List<Artifact> getArtifacts(ArtifactLoad loadLevel, ISearchConfirmer confirmer, boolean reload, boolean historical, boolean allowDeleted) throws Exception {
      List<Artifact> toReturn = null;
      ObjectPair<Integer, Integer> queryIdAndSize = executeSearch();
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

   private ObjectPair<Integer, Integer> executeSearch() throws Exception {
      ObjectPair<Integer, Integer> toReturn = null;
      Result result = SkynetActivator.areOSEEServicesAvailable();
      if (result.isTrue()) {
         String sessionId = ClientSessionManager.getSessionId();
         CharBackedInputStream inputStream = null;
         try {
            inputStream = getSearchParameters(sessionId);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            AcquireResult httpRequestResult =
                  HttpProcessor.post(new URL(getSearchUrl(sessionId)), inputStream, "application/xml", "UTF-8",
                        outputStream);
            if (httpRequestResult.getCode() == HttpURLConnection.HTTP_ACCEPTED) {
               String queryIdString = outputStream.toString("UTF-8");
               if (Strings.isValid(queryIdString)) {
                  String[] entries = queryIdString.split(",\\s*");
                  if (entries.length >= 2) {
                     toReturn = new ObjectPair<Integer, Integer>(new Integer(entries[0]), new Integer(entries[1]));
                  }
               }
            } else if (httpRequestResult.getCode() != HttpURLConnection.HTTP_NO_CONTENT) {
               throw new Exception(String.format("Search error due to bad request: url[%s] status code: [%s]",
                     inputStream.toString(), httpRequestResult.getCode()));
            }
         } finally {
            if (inputStream != null) {
               inputStream.close();
            }
         }
      } else {
         throw new Exception(String.format("Unable to perform search: %s", result.getText()));
      }
      return toReturn;
   }
}
