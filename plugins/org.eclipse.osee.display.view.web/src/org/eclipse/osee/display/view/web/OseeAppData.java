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
package org.eclipse.osee.display.view.web;

import org.eclipse.osee.display.api.search.SearchPresenter;
import org.eclipse.osee.vaadin.widgets.Navigator;
import com.vaadin.Application;
import com.vaadin.service.ApplicationContext.TransactionListener;

/**
 * @author Shawn F. Cook AppData contains thread-safe session-global data based on Vaadin demonstation:
 * https://vaadin.com/book/-/page/advanced.global.html
 */
@SuppressWarnings("serial")
public class OseeAppData implements TransactionListener {

   protected final Application app; // For distinguishing between apps
   protected static ThreadLocal<OseeAppData> instance = new ThreadLocal<OseeAppData>();

   private final Navigator navigator = createNavigator();
   private final SearchPresenter atsBackend = createSearchPresenter();

   public OseeAppData(Application app) {
      this.app = app;

      // It's usable from now on in the current request
      instance.set(this);
   }

   @Override
   public void transactionStart(Application application, Object transactionData) {
   }

   @Override
   public void transactionEnd(Application application, Object transactionData) {
   }

   protected Navigator createNavigator() {
      return new Navigator();
   }

   protected SearchPresenter createSearchPresenter() {
      return null;
   }

   public static Navigator getNavigator() {
      return instance.get().navigator;
   }

   public static SearchPresenter getAtsBackend() {
      return instance.get().atsBackend;
   }
}
