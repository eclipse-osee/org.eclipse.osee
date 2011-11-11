/*
 * Created on Sep 30, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.api.components;

import org.eclipse.osee.display.api.components.SearchHeaderComponent;
import org.eclipse.osee.display.api.data.WebId;

public interface AtsSearchHeaderComponent extends SearchHeaderComponent {

   void addProgram(WebId program);

   void clearBuilds();

   void addBuild(WebId build);

   void setSearchCriteria(WebId program, WebId build, boolean nameOnly, String searchPhrase);

}
