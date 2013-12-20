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

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.display.api.components.SearchResultComponent;
import org.eclipse.osee.display.api.components.SearchResultsListComponent;

/**
 * @author John R. Misinco
 */
public class MockSearchResultsListComponent implements SearchResultsListComponent {

   private boolean clearAllCalled = false;
   private String errorMessage;
   private final List<MockSearchResultComponent> searchResults = new LinkedList<MockSearchResultComponent>();

   public boolean isClearAllCalled() {
      return clearAllCalled;
   }

   public String getErrorMessage() {
      return errorMessage;
   }

   public List<MockSearchResultComponent> getSearchResults() {
      return searchResults;
   }

   @Override
   public void clearAll() {
      clearAllCalled = true;
   }

   @Override
   public SearchResultComponent createSearchResult() {
      MockSearchResultComponent toReturn = new MockSearchResultComponent();
      searchResults.add(toReturn);
      return toReturn;
   }

   @Override
   public void setErrorMessage(String shortMsg, String longMsg, MsgType msgType) {
      errorMessage = shortMsg;
   }

   @Override
   public void noSearchResultsFound() {
      // do nothing
   }

}