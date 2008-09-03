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
package org.eclipse.osee.framework.ui.skynet.search;

import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.db.connection.core.JoinUtility;
import org.eclipse.osee.framework.jdk.core.type.ObjectPair;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor;
import org.eclipse.osee.framework.jdk.core.util.OseeApplicationServerContext;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor.AcquireResult;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.skynet.core.linking.HttpUrlBuilder;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

/**
 * @author Roberto E. Escobar
 */
final class RemoteArtifactSearch extends AbstractArtifactSearchQuery {
   private Map<String, String> parameters;

   RemoteArtifactSearch(String query, int branchId, Map<String, Boolean> options) {
      this.parameters = new HashMap<String, String>();
      this.parameters.put("query", query);
      this.parameters.put("branchId", Integer.toString(branchId));
      if (options != null) {
         for (String optionName : options.keySet()) {
            this.parameters.put(optionName, options.get(optionName).toString());
         }
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.search.AbstractArtifactSearchQuery#getArtifacts()
    */
   @Override
   public Collection<Artifact> getArtifacts() throws Exception {
      Collection<Artifact> toReturn = null;
      ObjectPair<Integer, Integer> queryIdAndSize = executeSearch();
      if (queryIdAndSize != null && queryIdAndSize.object2 > 0) {
         try {
            toReturn =
                  ArtifactLoader.loadArtifactsFromQueryId(queryIdAndSize.object1, ArtifactLoad.FULL, null,
                        queryIdAndSize.object2, false, false);
         } finally {
            JoinUtility.deleteQuery(JoinUtility.JoinItem.ARTIFACT, queryIdAndSize.object1.intValue());
         }
      }
      if (toReturn == null) {
         toReturn = java.util.Collections.emptyList();
      }
      return toReturn;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.search.AbstractArtifactSearchQuery#getCriteriaLabel()
    */
   @Override
   public String getCriteriaLabel() {
      return parameters.get("query");
   }

   private ObjectPair<Integer, Integer> executeSearch() throws Exception {
      ObjectPair<Integer, Integer> toReturn = null;
      Result result = SkynetGuiPlugin.areOSEEServicesAvailable();
      if (result.isTrue()) {
         String url =
               HttpUrlBuilder.getInstance().getOsgiServletServiceUrl(OseeApplicationServerContext.SEARCH_CONTEXT,
                     parameters);
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         AcquireResult httpRequestResult = HttpProcessor.acquire(new URL(url), outputStream);
         if (httpRequestResult.wasSuccessful()) {
            String queryIdString = outputStream.toString("UTF-8");
            if (Strings.isValid(queryIdString)) {
               String[] entries = queryIdString.split(",\\s*");
               if (entries.length >= 2) {
                  toReturn = new ObjectPair<Integer, Integer>(new Integer(entries[0]), new Integer(entries[1]));
               }
            }
         } else if (httpRequestResult.getCode() != HttpURLConnection.HTTP_NO_CONTENT) {
            throw new Exception(String.format("Search error due to bad request: url[%s] status code: [%s]", url,
                  httpRequestResult.getCode()));
         }
      } else {
         throw new Exception(String.format("Unable to perform search: %s", result.getText()));
      }
      return toReturn;
   }

}
