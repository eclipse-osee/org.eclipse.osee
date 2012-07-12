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
package org.eclipse.osee.display.presenter.mocks;

import org.eclipse.osee.display.api.search.SearchNavigator;

/**
 * @author John R. Misinco
 */
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