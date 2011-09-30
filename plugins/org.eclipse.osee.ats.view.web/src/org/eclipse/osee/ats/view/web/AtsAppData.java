/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rightsimport com.vaadin.Application;
import com.vaadin.service.ApplicationContext.TransactionListener;
he Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.view.web;

import org.eclipse.osee.ats.api.search.AtsWebSearchPresenter;
import org.eclipse.osee.display.api.search.SearchPresenter;
import org.eclipse.osee.display.view.web.OseeAppData;
import org.eclipse.osee.vaadin.widgets.Navigator;
import com.vaadin.Application;

/**
 * @author Shawn F. Cook AppData contains thread-safe session-global data based on Vaadin demonstation:
 * https://vaadin.com/book/-/page/advanced.global.html
 */
public class AtsAppData extends OseeAppData {

   public AtsAppData(Application app) {
      super(app);
   }

   @Override
   public void transactionStart(Application application, Object transactionData) {
      // Set this data instance of this application
      // as the one active in the current thread. 
      if (this.app == application) {
         instance.set(this);
      }
   }

   @Override
   public void transactionEnd(Application application, Object transactionData) {
      // Clear the reference to avoid potential problems
      if (this.app == application) {
         instance.set(null);
      }
   }

   public static AtsNavigator getAtsNavigator() {
      return (AtsNavigator) OseeAppData.getNavigator();
   }

   public static AtsWebSearchPresenter getAtsWebSearchPresenter() {
      return (AtsWebSearchPresenter) OseeAppData.getAtsBackend();
   }

   @Override
   protected Navigator createNavigator() {
      return new AtsNavigator();
   }

   @Override
   protected SearchPresenter createSearchPresenter() {
      return AtsWebSearchPresenter_TestBackend.getInstance();
   }
}
