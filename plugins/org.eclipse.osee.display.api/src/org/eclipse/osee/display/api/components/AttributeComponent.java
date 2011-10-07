/*
 * Created on Sep 30, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.display.api.components;


public interface AttributeComponent {

   void clearAll();

   void addAttribute(String type, String value);

   void setErrorMessage(String message);
}
