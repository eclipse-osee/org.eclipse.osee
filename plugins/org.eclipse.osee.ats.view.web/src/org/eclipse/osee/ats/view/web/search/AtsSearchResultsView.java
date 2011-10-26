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

   @Override
   protected void initComponents() {
      setSearchHeader(new AtsSearchHeaderImpl());
   }

   @Override
   protected void callInit(String url) {
      AtsUiApplication atsApp = (AtsUiApplication) this.getApplication();
      atsApp.getPresenter().initSearchResults(url, getSearchHeader(), searchResultsListComponent,
         searchResultsListComponent.getDisplayOptionsComponent());
   }
}
