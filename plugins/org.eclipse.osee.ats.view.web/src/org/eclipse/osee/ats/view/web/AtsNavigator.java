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
package org.eclipse.osee.ats.view.web;

import org.eclipse.osee.ats.view.web.search.AtsArtifactView;
import org.eclipse.osee.ats.view.web.search.AtsSearchResultsView;
import org.eclipse.osee.display.api.components.SearchHeaderComponent;
import org.eclipse.osee.display.api.data.ViewSearchParameters;
import org.eclipse.osee.display.api.search.SearchNavigator;
import org.eclipse.osee.display.view.web.OseeUiApplication;
import org.eclipse.osee.vaadin.widgets.Navigator;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public class AtsNavigator extends Navigator implements SearchNavigator {

   @Override
   public void navigateSearchResults(String url) {
      updateGlobalUrlState(url);
      String classUri = getUri(AtsSearchResultsView.class);
      this.navigateTo(String.format("%s%s", classUri, url));
   }

   @Override
   public void navigateArtifactPage(String url) {
      updateGlobalUrlState(url);
      String classUri = getUri(AtsArtifactView.class);
      this.navigateTo(String.format("%s%s", classUri, url));
   }

   private void updateGlobalUrlState(String url) {
      OseeUiApplication<SearchHeaderComponent, ViewSearchParameters> app =
         (OseeUiApplication<SearchHeaderComponent, ViewSearchParameters>) getApplication();
      app.setRequestedDataId(url);
   }
}
