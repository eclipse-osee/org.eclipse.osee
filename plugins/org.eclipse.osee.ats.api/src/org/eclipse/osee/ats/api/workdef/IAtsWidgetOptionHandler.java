/*
 * Created on Jun 20, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.api.workdef;

import java.util.Collection;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWidgetOptionHandler {

   Collection<WidgetOption> getXOptions();

   void add(WidgetOption requiredForTransition);

   boolean contains(WidgetOption requiredForTransition);

}