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

import org.eclipse.osee.display.api.components.ArtifactHeaderComponent;
import org.eclipse.osee.display.api.data.WebArtifact;
import org.eclipse.osee.display.api.search.SearchPresenter;
import org.eclipse.osee.display.view.web.OseeAppData;
import org.eclipse.osee.display.view.web.components.OseeArtifactNameLinkComponent;
import org.eclipse.osee.display.view.web.components.OseeAttributeComponent;
import org.eclipse.osee.display.view.web.components.OseeBreadcrumbComponent;
import org.eclipse.osee.display.view.web.components.OseeRelationsComponent;
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
public class OseeArtifactView extends CustomComponent implements Navigator.View, ArtifactHeaderComponent {

   protected SearchPresenter searchPresenter = OseeAppData.getSearchPresenter();
   protected OseeSearchHeaderComponent oseeSearchHeader = getOseeSearchHeader();
   protected OseeRelationsComponent relationsComp = new OseeRelationsComponent();
   protected OseeAttributeComponent attributeComp = new OseeAttributeComponent();
   private final OseeBreadcrumbComponent breadcrumbComp = new OseeBreadcrumbComponent(null);
   private WebArtifact artifact;

   private void initLayout() {
      setSizeFull();

      HorizontalLayout leftMarginAndBody = new HorizontalLayout();
      leftMarginAndBody.setSizeFull();
      Label leftMarginSpace = new Label("");
      leftMarginSpace.setWidth(80, UNITS_PIXELS);
      leftMarginAndBody.addComponent(leftMarginSpace);

      if (artifact != null) {
         VerticalLayout bodyVertLayout = new VerticalLayout();
         breadcrumbComp.setArtifact(artifact);
         OseeArtifactNameLinkComponent artifactName = new OseeArtifactNameLinkComponent(artifact);
         bodyVertLayout.addComponent(breadcrumbComp);
         bodyVertLayout.addComponent(artifactName);
         VerticalLayout artRelSpacer = new VerticalLayout();
         artRelSpacer.setHeight(15, UNITS_PIXELS);
         bodyVertLayout.addComponent(artRelSpacer);
         bodyVertLayout.addComponent(relationsComp);
         VerticalLayout relAttrSpacer = new VerticalLayout();
         relAttrSpacer.setHeight(15, UNITS_PIXELS);
         bodyVertLayout.addComponent(relAttrSpacer);
         bodyVertLayout.addComponent(attributeComp);
         VerticalLayout bottomSpacer = new VerticalLayout();
         bodyVertLayout.addComponent(bottomSpacer);
         bodyVertLayout.setExpandRatio(bottomSpacer, 1.0f);

         leftMarginAndBody.addComponent(bodyVertLayout);
         bodyVertLayout.setSizeFull();
         leftMarginAndBody.setExpandRatio(bodyVertLayout, 1.0f);
      }

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
      initLayout();
   }

   @Override
   public void navigateTo(String requestedDataId) {
      searchPresenter.initArtifactPage(requestedDataId, oseeSearchHeader, this, relationsComp, attributeComp);
   }

   @Override
   public String getWarningForNavigatingFrom() {
      return null;
   }

   protected OseeSearchHeaderComponent getOseeSearchHeader() {
      return new OseeSearchHeaderComponent();
   }

   @Override
   public void clearAll() {
      this.artifact = null;
      initLayout();
   }

   @Override
   public void setArtifact(WebArtifact artifact) {
      this.artifact = artifact;
      initLayout();
   }

   @Override
   public void setErrorMessage(String message) {
      //TODO:
   }
}
