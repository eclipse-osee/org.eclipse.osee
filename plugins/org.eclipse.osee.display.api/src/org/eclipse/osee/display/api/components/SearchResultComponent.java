/*
 * Created on Sep 30, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.display.api.components;

import org.eclipse.osee.display.api.data.WebArtifact;
import org.eclipse.osee.display.api.data.SearchResultMatch;

public interface SearchResultComponent {

   void setArtifact(WebArtifact artifact);

   void addSearchResultMatch(SearchResultMatch match);
}
