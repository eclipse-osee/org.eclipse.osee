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
import org.eclipse.osee.display.api.data.DisplayOptions;
import org.eclipse.osee.display.api.data.SearchResultMatch;
import org.eclipse.osee.display.api.data.ViewArtifact;

/**
 * @author John R. Misinco
 */
public class MockSearchResultComponent implements SearchResultComponent {

   private ViewArtifact artifact;
   private final List<SearchResultMatch> matches = new ArrayList<SearchResultMatch>();

   public ViewArtifact getArtifact() {
      return artifact;
   }

   public List<SearchResultMatch> getMatches() {
      return matches;
   }

   @Override
   public void setArtifact(ViewArtifact artifact) {
      this.artifact = artifact;
   }

   @Override
   public void addSearchResultMatch(SearchResultMatch match) {
      this.matches.add(match);
   }

   @Override
   public void setErrorMessage(String shortMsg, String longMsg, MsgType msgType) {
      // do nothing
   }

   @Override
   public void setDisplayOptions(DisplayOptions options) {
      // do nothing
   }
}