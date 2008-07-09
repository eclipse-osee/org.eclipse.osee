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
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.jdk.core.type.ObjectPair;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.skynet.core.linking.HttpProcessor;
import org.eclipse.osee.framework.skynet.core.linking.HttpUrlBuilder;
import org.eclipse.osee.framework.skynet.core.linking.HttpProcessor.AcquireResult;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.Match;

/**
 * @author Roberto E. Escobar
 */
public class RemoteArtifactSearch implements ISearchQuery {
   private RemoteArtifactSearchResult searchResult;
   private Map<String, String> parameters;
   private int numberOfMatches;
   private boolean isSearchComplete;

   public RemoteArtifactSearch(String query, Map<String, Boolean> options) {
      this.searchResult = new RemoteArtifactSearchResult();
      this.isSearchComplete = false;
      this.numberOfMatches = 0;
      this.parameters = new HashMap<String, String>();
      this.parameters.put("query", query);
      if (options != null) {
         for (String optionName : options.keySet()) {
            this.parameters.put(optionName, options.get(optionName).toString());
         }
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.search.ui.ISearchQuery#canRerun()
    */
   @Override
   public boolean canRerun() {
      return true;
   }

   /* (non-Javadoc)
    * @see org.eclipse.search.ui.ISearchQuery#canRunInBackground()
    */
   @Override
   public boolean canRunInBackground() {
      return true;
   }

   /* (non-Javadoc)
    * @see org.eclipse.search.ui.ISearchQuery#getLabel()
    */
   @Override
   public String getLabel() {
      return "Remote Artifact Search";
   }

   /* (non-Javadoc)
    * @see org.eclipse.search.ui.ISearchQuery#getSearchResult()
    */
   @Override
   public ISearchResult getSearchResult() {
      return searchResult;
   }

   private void reset() {
      this.isSearchComplete = false;
      this.searchResult.removeAll();
      this.numberOfMatches = 0;
   }

   /* (non-Javadoc)
    * @see org.eclipse.search.ui.ISearchQuery#run(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public IStatus run(IProgressMonitor monitor) throws OperationCanceledException {
      reset();
      try {
         ObjectPair<Integer, Integer> queryIdAndSize = executeSearch();
         if (queryIdAndSize != null) {
            List<Artifact> artifacts =
                  ArtifactLoader.loadArtifactsFromQuery(queryIdAndSize.object1, ArtifactLoad.FULL, null,
                        queryIdAndSize.object2, false);
            for (Artifact artifact : artifacts) {
               Match match = new Match(artifact, 1, 2);
               searchResult.addMatch(match);
               this.numberOfMatches++;
            }
         }
      } catch (Exception ex) {
         OSEELog.logException(getClass(), ex, true);
      }
      this.isSearchComplete = true;
      return new MultiStatus(NewSearchUI.PLUGIN_ID, IStatus.OK, "OK", null);
   }

   private ObjectPair<Integer, Integer> executeSearch() throws Exception {
      ObjectPair<Integer, Integer> toReturn = null;
      String url = HttpUrlBuilder.getInstance().getOsgiServletServiceUrl("search", parameters);
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
      }
      return toReturn;
   }

   public final class RemoteArtifactSearchResult extends AbstractArtifactSearchResult {

      /* (non-Javadoc)
       * @see org.eclipse.search.ui.ISearchResult#getImageDescriptor()
       */
      @Override
      public ImageDescriptor getImageDescriptor() {
         return null;
      }

      /* (non-Javadoc)
       * @see org.eclipse.search.ui.ISearchResult#getLabel()
       */
      @Override
      public String getLabel() {
         return parameters.get("query") + " - " + (isSearchComplete ? (numberOfMatches + " matches") : "busy");
      }

      /* (non-Javadoc)
       * @see org.eclipse.search.ui.ISearchResult#getQuery()
       */
      @Override
      public ISearchQuery getQuery() {
         return RemoteArtifactSearch.this;
      }

      /* (non-Javadoc)
       * @see org.eclipse.search.ui.ISearchResult#getTooltip()
       */
      @Override
      public String getTooltip() {
         return getLabel();
      }
   }
}
