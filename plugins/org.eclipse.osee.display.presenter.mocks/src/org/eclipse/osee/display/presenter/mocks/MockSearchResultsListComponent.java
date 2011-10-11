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

import org.eclipse.osee.display.api.components.SearchResultComponent;
import org.eclipse.osee.display.api.components.SearchResultsListComponent;

/**
 * @author John Misinco
 */
public class MockSearchResultsListComponent implements SearchResultsListComponent {

   private boolean clearAllCalled = false;
   private String errorMessage = "";

   public boolean isClearAllCalled() {
      return clearAllCalled;
   }

   public String getErrorMessage() {
      return errorMessage;
   }

   @Override
   public void clearAll() {
      clearAllCalled = true;
   }

   @Override
   public SearchResultComponent createSearchResult() {
      return new MockSearchResultComponent();
   }

   @Override
   public void setErrorMessage(String message) {
      errorMessage = message;
   }

}