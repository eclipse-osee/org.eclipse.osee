/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.presenter;

import java.util.List;
import org.eclipse.osee.display.api.components.ArtifactHeaderComponent;
import org.eclipse.osee.display.api.components.AttributeComponent;
import org.eclipse.osee.display.api.components.RelationComponent;
import org.eclipse.osee.display.api.components.SearchHeaderComponent;
import org.eclipse.osee.display.api.components.SearchResultComponent;
import org.eclipse.osee.display.api.components.SearchResultsListComponent;
import org.eclipse.osee.display.api.data.SearchResultMatch;
import org.eclipse.osee.display.api.data.WebId;
import org.eclipse.osee.display.api.search.SearchPresenter;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.data.ReadableArtifact;

/*
 * @author John Misinco
 */
public class WebSearchPresenter implements SearchPresenter {

   protected final ArtifactProvider artifactProvider;

   public WebSearchPresenter(ArtifactProvider artifactProvider) {
      this.artifactProvider = artifactProvider;
   }

   @Override
   public void initSearchHome(SearchHeaderComponent searchHeaderComp) {
   }

   @Override
   public void initSearchResults(String url, SearchHeaderComponent searchHeaderComp, SearchResultsListComponent searchResultsComp) {
      searchResultsComp.clearAll();
      SearchParameters params = decode(url);
      List<ReadableArtifact> searchResults = null;
      try {
         searchResults =
            artifactProvider.getSearchResults(TokenFactory.createBranch(params.getBranchId(), ""), params.isNameOnly(),
               params.getSearchPhrase());
      } catch (OseeCoreException ex) {
         searchResultsComp.setErrorMessage("Error while searching");
         return;
      }
      for (ReadableArtifact art : searchResults) {
         SearchResultComponent searchResult = searchResultsComp.createSearchResult();
         org.eclipse.osee.display.api.data.WebArtifact displayArtifact = null; // = new org.eclipse.osee.display.api.data.Artifact();
         searchResult.setArtifact(displayArtifact);
         SearchResultMatch match = null;
         searchResult.addSearchResultMatch(match);
      }
   }

   @Override
   public void selectArtifact(WebId id) {
   }

   @Override
   public void initArtifactPage(String url, SearchHeaderComponent searchHeaderComp, ArtifactHeaderComponent artHeaderComp, RelationComponent relComp, AttributeComponent attrComp) {
   }

   @Override
   public void selectRelationType(WebId id) {
   }

   protected String encode(WebId id, boolean nameOnly, String searchPhrase) {
      StringBuilder sb = new StringBuilder();
      sb.append("?branch=");
      sb.append(id.getGuid());
      sb.append("?nameOnly=");
      sb.append(nameOnly);
      sb.append("?search=");
      sb.append(searchPhrase);
      return sb.toString().replaceAll("\\s", "%20");
   }

   private SearchParameters decode(String url) {
      String[] tokens = url.split("\\?");
      String branchId = tokens[0].split("=")[1];
      boolean nameOnly = tokens[1].split("=")[1].equalsIgnoreCase("true") ? true : false;
      String searchPhrase = tokens[2].split("=")[1];
      searchPhrase = searchPhrase.replaceAll("%20", " ");
      return new SearchParameters(branchId, nameOnly, searchPhrase);
   }

   private class SearchParameters {

      private final String branchId;
      private final boolean nameOnly;
      private final String searchPhrase;

      public SearchParameters(String branchId, boolean nameOnly, String searchPhrase) {
         this.branchId = branchId;
         this.nameOnly = nameOnly;
         this.searchPhrase = searchPhrase;
      }

      public String getBranchId() {
         return branchId;
      }

      public boolean isNameOnly() {
         return nameOnly;
      }

      public String getSearchPhrase() {
         return searchPhrase;
      }

   }

}
