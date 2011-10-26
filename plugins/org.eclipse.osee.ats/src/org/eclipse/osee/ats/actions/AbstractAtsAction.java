/*
 * Created on Oct 24, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class AbstractAtsAction extends Action {

   public AbstractAtsAction(String string, ImageDescriptor imageDescriptor) {
      super(string, imageDescriptor);
   }

   public AbstractAtsAction(String string) {
      super(string);
   }

   public AbstractAtsAction() {
      super();
   }

   public AbstractAtsAction(String string, int asPushButton) {
      super(string, asPushButton);
   }

   public void runWithException() throws Exception {
      // provided for subclass implementation
   }

   @Override
   public void run() {
      try {
         runWithException();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

   }

}
