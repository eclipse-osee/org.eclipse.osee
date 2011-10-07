package org.eclipse.osee.display.presenter.mocks;

import org.eclipse.osee.display.api.components.SearchHeaderComponent;

public class MockSearchHeaderComponent implements SearchHeaderComponent {

   private boolean clearAllCalled = false;
   private String errorMessage = "";

   public boolean isClearAllCalled() {
      return clearAllCalled;
   }

   public String getErrorMessage() {
      return errorMessage;
   }

   @Override
   public void setErrorMessage(String message) {
      errorMessage = message;
   }

   @Override
   public void clearAll() {
      clearAllCalled = true;
   }

}