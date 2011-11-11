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
package org.eclipse.osee.display.view.web.internal.search;

import org.eclipse.osee.display.api.components.ArtifactHeaderComponent;
import org.eclipse.osee.display.api.components.AttributeComponent;
import org.eclipse.osee.display.api.components.RelationComponent;
import org.eclipse.osee.display.api.components.SearchHeaderComponent;
import org.eclipse.osee.display.api.components.SearchResultsListComponent;
import org.eclipse.osee.display.api.data.WebId;
import org.eclipse.osee.display.api.search.SearchPresenter;

/**
 * @author Shawn F. Cook
 */
public class OseeWebBackend implements SearchPresenter {

   @Override
   public void initSearchHome(SearchHeaderComponent searchHeaderComp) {
   }

   @Override
   public void initSearchResults(String url, SearchHeaderComponent searchHeaderComp, SearchResultsListComponent searchResultsComp) {
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
   //   private final OseeWebBackendTestData oseeWebBackendTestData = OseeWebBackendTestData.getSingleinstance();

   //   @Override
   //   public void getProgramsAndBuilds(SearchView webView) {
   //      webView.setProgramsAndBuilds(OseeWebBackendTestData.getBuilds());
   //   }
   //
   //   @Override
   //   public void getSearchResults(SearchView webView, SearchCriteria searchCriteria) {
   //      Collection<SearchResult> searchResults = new ArrayList<SearchResult>();
   //
   //      SearchResultMatch match0 =
   //         new SearchResultMatch("Word Template Content", "... page ({<b>COM</b>_PAGE}) provides...", 10);
   //      SearchResultMatch match1 = new SearchResultMatch("Subsystem", "<b>Com</b>munications", 1);
   //      SearchResultMatch match2 = new SearchResultMatch("Partition", "<b>COM</b>, CND", 1);
   //
   //      Map<String, Artifact> artifacts = oseeWebBackendTestData.getArtifacts();
   //      for (Artifact art : artifacts.values()) {
   //         SearchResult result = new SearchResult(art, Arrays.asList(match0, match1, match2));
   //         searchResults.add(result);
   //      }
   //
   //      webView.setSearchResults(searchResults);
   //   }

   //   @Override
   //   public void getArtifact(SearchView webView, String artifactGuid) {
   //      Artifact artifact = oseeWebBackendTestData.getArtifactWithGuid(artifactGuid);
   //      webView.setArtifact(artifact);
   //   }
   //
   //   public void getProgramFromArtUuid(SearchView webView, String artifactUuid) {
   //      Program program = oseeWebBackendTestData.getProgramForArtUuid(artifactUuid);
   //      webView.setProgram(program);
   //   }
   //
   //   public void getBuildFromArtUuid(SearchView webView, String artifactUuid) {
   //      Build build = oseeWebBackendTestData.getBuildForArtUuid(artifactUuid);
   //      webView.setBuild(build);
   //   }
}
