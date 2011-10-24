/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.view.web.search;

import org.eclipse.osee.ats.view.web.AtsUiApplication;
import org.eclipse.osee.ats.view.web.components.AtsSearchHeaderImpl;
import org.eclipse.osee.display.view.web.search.OseeSearchResultsView;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public class AtsSearchResultsView extends OseeSearchResultsView {

   private final boolean isLayoutComplete = false;

   @Override
   protected void initComponents() {
      try {
         AtsUiApplication atsApp = (AtsUiApplication) this.getApplication();
         searchPresenter = atsApp.getAtsWebSearchPresenter();
         searchHeader = new AtsSearchHeaderImpl();
      } catch (Exception e) {
         System.out.println("AtsSearchResultsView.attach - CRITICAL ERROR: (AtsUiApplication) this.getApplication() threw an exception.");
      }
   }

   @Override
   protected void callInit(String url) {
      searchPresenter.initSearchResults(url, searchHeader, searchResultsListComponent, null);
   }
}
