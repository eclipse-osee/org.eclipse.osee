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

import org.eclipse.osee.ats.api.components.AtsSearchHeaderComponent;
import org.eclipse.osee.ats.api.search.AtsSearchPresenter;
import org.eclipse.osee.ats.view.web.AtsUiApplication;
import org.eclipse.osee.ats.view.web.components.AtsSearchHeaderImpl;
import org.eclipse.osee.display.view.web.search.OseeSearchHeaderComponent;
import org.eclipse.osee.display.view.web.search.OseeSearchResultsView;
import org.eclipse.osee.vaadin.widgets.Navigator;
import com.vaadin.Application;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public class AtsSearchResultsView extends OseeSearchResultsView {

   private boolean populated = false;
   private AtsSearchPresenter searchPresenter;
   private String requestedDataId = "";

   @Override
   public void attach() {
      if (!populated) {
         try {
            AtsUiApplication atsApp = (AtsUiApplication) this.getApplication();
            searchPresenter = atsApp.getAtsWebSearchPresenter();
            searchHeader = atsApp.getAtsSearchHeaderComponent();
            searchResultsListComponent.setSearchHeaderComponent(searchHeader);
            callInitSearchHome();
            createLayout();
         } catch (Exception e) {
            System.out.println("OseeArtifactNameLinkComponent.attach - CRITICAL ERROR: (AtsUiApplication) this.getApplication() threw an exception.");
         }
      }
      populated = true;
   }

   @Override
   protected OseeSearchHeaderComponent getOseeSearchHeader() {
      return new AtsSearchHeaderImpl(false);
   }

   private void callInitSearchHome() {
      if (searchPresenter != null) {
         try {
            searchPresenter.initSearchResults(requestedDataId, (AtsSearchHeaderComponent) searchHeader,
               searchResultsListComponent);
         } catch (Exception e) {
            System.out.println("AtsSearchResultsView.callInitSearchHome - CRITICAL ERROR: casting threw an exception.");
         }
      }
   }

   @Override
   public void navigateTo(String requestedDataId) {
      super.navigateTo(requestedDataId);
      this.requestedDataId = requestedDataId;
      callInitSearchHome();
   }

   @Override
   public void init(Navigator navigator, Application application) {
      super.init(navigator, application);
   }
}
