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

   protected OseeSearchHeaderComponent oseeSearchHeader = getOseeSearchHeader();
   protected OseeSearchResultsListComponent searchResultsListComponent = new OseeSearchResultsListComponent();

   @Override
   public void init(Navigator navigator, Application application) {
      initComponents();

      setSizeFull();

      final HorizontalLayout headerHorzLayout = new HorizontalLayout();
      headerHorzLayout.addComponent(oseeSearchHeader);
      headerHorzLayout.setComponentAlignment(oseeSearchHeader, Alignment.TOP_LEFT);
      oseeSearchHeader.setSizeUndefined();

      //This is a fixed-height spacer that simply forms a nice margin between the search
      //  results and the search header.
      //      Label topSpacer = new Label("");
      //      topSpacer.setHeight(85, UNITS_PIXELS);
      //      topSpacer.setSizeUndefined();

      HorizontalLayout leftMarginAndBody = new HorizontalLayout();
      leftMarginAndBody.setSizeFull();
      Label leftMarginSpace = new Label("");
      leftMarginSpace.setWidth(80, UNITS_PIXELS);
      leftMarginAndBody.addComponent(leftMarginSpace);
      leftMarginAndBody.addComponent(searchResultsListComponent);
      searchResultsListComponent.setSizeFull();
      leftMarginAndBody.setExpandRatio(searchResultsListComponent, 1.0f);

      final VerticalLayout vertLayout = new VerticalLayout();
      vertLayout.addComponent(headerHorzLayout);
      //      vertLayout.addComponent(topSpacer);
      vertLayout.addComponent(leftMarginAndBody);
      vertLayout.setExpandRatio(leftMarginAndBody, 1.0f);

      vertLayout.setSizeFull();
      setCompositionRoot(vertLayout);
   }

   protected OseeSearchHeaderComponent getOseeSearchHeader() {
      return new OseeSearchHeaderComponent();
   }

   @Override
   public void navigateTo(String requestedDataId) {
      //Do nothing
   }

   @Override
   public String getWarningForNavigatingFrom() {
      return null;
   }

   protected void initComponents() {
      //Do nothing
   }

}
