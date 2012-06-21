/*
 * Created on Jun 20, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workdef.api;

import java.util.Collection;

public interface IAtsWidgetOptionHandler {

   Collection<WidgetOption> getXOptions();

   void add(WidgetOption requiredForTransition);

   boolean contains(WidgetOption requiredForTransition);

}