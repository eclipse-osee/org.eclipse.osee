package org.eclipse.osee.display.presenter.mocks;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.display.api.components.SearchResultComponent;
import org.eclipse.osee.display.api.data.SearchResultMatch;
import org.eclipse.osee.display.api.data.WebArtifact;

public class MockSearchResultComponent implements SearchResultComponent {

   private WebArtifact artifact;
   private final List<SearchResultMatch> match = new ArrayList<SearchResultMatch>();

   public WebArtifact getArtifact() {
      return artifact;
   }

   public List<SearchResultMatch> getMatch() {
      return match;
   }

   @Override
   public void setArtifact(WebArtifact artifact) {
      this.artifact = artifact;
   }

   @Override
   public void addSearchResultMatch(SearchResultMatch match) {
      this.match.add(match);
   }
}