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

import org.eclipse.osee.ats.api.search.AtsSearchPresenter;
import org.eclipse.osee.display.api.search.SearchNavigator;
import org.eclipse.osee.display.api.search.SearchPresenter;
import org.eclipse.osee.display.view.web.OseeAppData;
import com.vaadin.Application;

/**
 * @author Shawn F. Cook AppData contains thread-safe session-global data based on Vaadin demonstation:
 * https://vaadin.com/book/-/page/advanced.global.html
 */
public class AtsAppData extends OseeAppData {

   public AtsAppData(Application app) {
      super(app);
   }

   public static AtsNavigator getAtsNavigator() {
      return (AtsNavigator) OseeAppData.getNavigator();
   }

   public static AtsSearchPresenter getAtsWebSearchPresenter() {
      return (AtsSearchPresenter) OseeAppData.getSearchPresenter();
   }

   @Override
   protected SearchNavigator createNavigator() {
      return new AtsNavigator();
   }

   @Override
   protected SearchPresenter createSearchPresenter() {
      return AtsWebSearchPresenter_TestBackend.getInstance();
   }
}
