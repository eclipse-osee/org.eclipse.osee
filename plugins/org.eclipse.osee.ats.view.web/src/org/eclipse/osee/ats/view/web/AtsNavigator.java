/*
 * Created on Sep 30, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.view.web;

import org.eclipse.osee.ats.view.web.search.AtsSearchResultsView;
import org.eclipse.osee.display.api.search.SearchNavigator;
import org.eclipse.osee.vaadin.widgets.Navigator;

public class AtsNavigator extends Navigator implements SearchNavigator {

   @Override
   public void navigateSearchResults(String url) {
      String classUri = getUri(AtsSearchResultsView.class);
      this.navigateTo(String.format("%s%s", classUri, url));
   }

   @Override
   public void navigateArtifactPage(String url) {
      //TODO:
   }
}
