package org.eclipse.osee.display.presenter.mocks;

import org.eclipse.osee.display.api.search.SearchNavigator;

public class MockSearchNavigator implements SearchNavigator {
   private String resultsUrl, artifactUrl;

   public String getResultsUrl() {
      return resultsUrl;
   }

   public String getArtifactUrl() {
      return artifactUrl;
   }

   @Override
   public void navigateSearchResults(String url) {
      resultsUrl = url;
   }

   @Override
   public void navigateArtifactPage(String url) {
      artifactUrl = url;
   }

}