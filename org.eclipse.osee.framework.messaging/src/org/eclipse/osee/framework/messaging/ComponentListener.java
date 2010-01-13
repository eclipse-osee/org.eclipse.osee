/*
 * Created on Aug 5, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging;

/**
 * @author b1528444
 *
 */
public interface ComponentListener {
   
   void onComponentAvailable(Component component);
   void onComponentNotAvailable(Component component);

}
