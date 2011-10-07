package org.eclipse.osee.display.presenter.mocks;

import org.eclipse.osee.display.api.components.SearchResultComponent;
import org.eclipse.osee.display.api.components.SearchResultsListComponent;

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