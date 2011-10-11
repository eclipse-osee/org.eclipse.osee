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
package org.eclipse.osee.display.view.web.search;

import org.eclipse.osee.display.view.web.components.OseeSearchResultsListComponent;
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
public class OseeSearchResultsView extends CustomComponent implements Navigator.View {

   protected OseeSearchHeaderComponent searchHeader;
   protected final OseeSearchResultsListComponent searchResultsListComponent = new OseeSearchResultsListComponent();

   protected void createLayout() {
      setSizeFull();

      HorizontalLayout leftMarginAndBody = new HorizontalLayout();
      leftMarginAndBody.setSizeFull();
      Label leftMarginSpace = new Label("");
      leftMarginSpace.setWidth(80, UNITS_PIXELS);
      leftMarginAndBody.addComponent(leftMarginSpace);

      VerticalLayout bodyVertLayout = new VerticalLayout();
      leftMarginAndBody.addComponent(searchResultsListComponent);
      searchResultsListComponent.setSizeFull();
      leftMarginAndBody.setExpandRatio(searchResultsListComponent, 1.0f);

      final VerticalLayout vertLayout = new VerticalLayout();
      if (searchHeader != null) {
         searchHeader.setShowOseeTitleAbove(false);
         vertLayout.addComponent(searchHeader);
         vertLayout.setComponentAlignment(searchHeader, Alignment.TOP_LEFT);
         searchHeader.setWidth(100, UNITS_PERCENTAGE);
         searchHeader.setHeight(null);
      }
      vertLayout.addComponent(leftMarginAndBody);
      vertLayout.setExpandRatio(leftMarginAndBody, 1.0f);

      vertLayout.setSizeFull();
      setCompositionRoot(vertLayout);
   }

   @Override
   public void init(Navigator navigator, Application application) {
      //Do nothing.
   }

   protected OseeSearchHeaderComponent getOseeSearchHeader() {
      return new OseeSearchHeaderComponent();
   }

   @Override
   public void navigateTo(String requestedDataId) {
      if (searchHeader != null) {
         searchHeader.createLayout();
      }
      createLayout();
   }

   @Override
   public String getWarningForNavigatingFrom() {
      return null;
   }

}
