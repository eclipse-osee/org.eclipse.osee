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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.display.api.components.SearchResultComponent;
import org.eclipse.osee.display.api.data.SearchResultMatch;
import org.eclipse.osee.display.api.data.WebArtifact;

/**
 * @author John Misinco
 */
public class MockSearchResultComponent implements SearchResultComponent {

   private WebArtifact artifact;
   private final List<SearchResultMatch> matches = new ArrayList<SearchResultMatch>();

   public WebArtifact getArtifact() {
      return artifact;
   }

   public List<SearchResultMatch> getMatches() {
      return matches;
   }

   @Override
   public void setArtifact(WebArtifact artifact) {
      this.artifact = artifact;
   }

   @Override
   public void addSearchResultMatch(SearchResultMatch match) {
      this.matches.add(match);
   }

   @Override
   public void setErrorMessage(String message) {
   }

   @Override
   public void setShowVerboseSearchResults(boolean showVerboseSearchResults) {
   }
}