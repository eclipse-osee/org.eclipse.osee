/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.view.web.internal.search;

import org.eclipse.osee.display.api.search.SearchView;
import org.eclipse.osee.vaadin.widgets.Navigator;
import com.vaadin.Application;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public class OseeSearchResultsView extends CustomComponent implements Navigator.View, SearchView {

   private OseeSearchHeaderComponent oseeSearchHeader;
   //private final SearchPresenter searchPresenter = new SearchPresenter(this);
   private final OseeWebBackend webBackend = new OseeWebBackend();
   private Navigator navigator;
   private final VerticalLayout searchResultsVertLayout = new VerticalLayout();

   @Override
   public void init(Navigator navigator, Application application) {
      this.navigator = navigator;
      setSizeFull();
      oseeSearchHeader = new OseeSearchHeaderComponent(false);

      final HorizontalLayout headerHorzLayout = new HorizontalLayout();
      headerHorzLayout.addComponent(oseeSearchHeader);
      headerHorzLayout.setComponentAlignment(oseeSearchHeader, Alignment.TOP_LEFT);

      final HorizontalLayout bodyHorzLayout = new HorizontalLayout();
      bodyHorzLayout.setSizeFull();
      Label spacer = new Label("");
      spacer.setWidth(85, UNITS_PIXELS);
      bodyHorzLayout.addComponent(spacer);
      bodyHorzLayout.addComponent(searchResultsVertLayout);
      searchResultsVertLayout.setSizeFull();
      bodyHorzLayout.setExpandRatio(searchResultsVertLayout, 1.0f);

      final VerticalLayout vertLayout = new VerticalLayout();
      vertLayout.addComponent(headerHorzLayout);
      vertLayout.addComponent(bodyHorzLayout);
      vertLayout.setSizeFull();
      vertLayout.setExpandRatio(bodyHorzLayout, 1.0f);

      setCompositionRoot(vertLayout);
   }

   @Override
   public void navigateTo(String requestedDataId) {
      //TODO: parse request string and properly populate searchCriteria
      //      SearchCriteria searchCriteria = new SearchCriteria(null, null, false, "");
      //      webBackend.getSearchResults(this, searchCriteria);
   }

   @Override
   public String getWarningForNavigatingFrom() {
      return null;
   }

   //   @Override
   //   public void setSearchResults(Collection<SearchResult> searchResults) {
   //      searchResultsVertLayout.removeAllComponents();
   //      if (navigator != null) {
   //         for (SearchResult result : searchResults) {
   //            OseeSearchResultComponent resultComponent = new OseeSearchResultComponent(navigator, result);
   //            searchResultsVertLayout.addComponent(resultComponent);
   //         }
   //      }
   //      Label spacer1 = new Label("");
   //      searchResultsVertLayout.addComponent(spacer1);
   //      searchResultsVertLayout.setExpandRatio(spacer1, 1.0f);
   //   }

   //   @Override
   //   public void setProgramsAndBuilds(ProgramsAndBuilds builds) {
   //   }
   //
   //   @Override
   //   public void setArtifact(Artifact artifact) {
   //   }
   //
   //   @Override
   //   public void setProgram(Program program) {
   //   }
   //
   //   @Override
   //   public void setBuild(Build build) {
   //   }
   //
   //   @Override
   //   public void setErrorMessage(String message) {
   //   }

}
