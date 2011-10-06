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

   protected OseeSearchHeaderComponent oseeSearchHeader;
   protected OseeSearchResultsListComponent searchResultsListComponent;

   public OseeSearchResultsView() {
      oseeSearchHeader = getOseeSearchHeader();
      searchResultsListComponent = new OseeSearchResultsListComponent();
   }

   private void initLayout() {
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

      leftMarginAndBody.addComponent(bodyVertLayout);
      bodyVertLayout.setSizeFull();
      leftMarginAndBody.setExpandRatio(bodyVertLayout, 1.0f);

      final VerticalLayout vertLayout = new VerticalLayout();
      vertLayout.addComponent(oseeSearchHeader);
      vertLayout.setComponentAlignment(oseeSearchHeader, Alignment.TOP_LEFT);
      oseeSearchHeader.setWidth(100, UNITS_PERCENTAGE);
      oseeSearchHeader.setHeight(null);
      vertLayout.addComponent(leftMarginAndBody);
      vertLayout.setExpandRatio(leftMarginAndBody, 1.0f);

      vertLayout.setSizeFull();
      setCompositionRoot(vertLayout);
   }

   @Override
   public void init(Navigator navigator, Application application) {
      initComponents();
      initLayout();
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
