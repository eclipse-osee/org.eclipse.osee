/*
 * Created on Aug 5, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public interface ComponentListener {
   
   void onComponentAvailable(Component component);
   void onComponentNotAvailable(Component component);

}
