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
import org.eclipse.osee.display.api.data.ViewArtifact;
import org.eclipse.osee.display.api.search.SearchPresenter;
import org.eclipse.osee.display.view.web.components.OseeArtifactNameLinkComponent;
import org.eclipse.osee.display.view.web.components.OseeAttributeComponent;
import org.eclipse.osee.display.view.web.components.OseeBreadcrumbComponent;
import org.eclipse.osee.display.view.web.components.OseeRelationsComponent;
import org.eclipse.osee.display.view.web.components.OseeSearchHeaderComponent;
import org.eclipse.osee.vaadin.widgets.Navigator;
import com.vaadin.Application;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public class OseeArtifactView extends CustomComponent implements Navigator.View, ArtifactHeaderComponent {

   protected SearchPresenter searchPresenter = null;
   protected OseeSearchHeaderComponent searchHeader;
   protected OseeRelationsComponent relationsComp = new OseeRelationsComponent();
   protected OseeAttributeComponent attributeComp = new OseeAttributeComponent();
   private final OseeBreadcrumbComponent breadcrumbComp = new OseeBreadcrumbComponent(null);
   private ViewArtifact artifact;
   private final int LEFTMARGIN_WIDTH = 5;

   @Override
   public void attach() {
      //TODO: remove?
   }

   protected void createLayout() {
      setSizeFull();

      searchHeader.setWidth(100, UNITS_PERCENTAGE);
      searchHeader.setHeight(null);

      Label spacer = new Label();
      spacer.setHeight(5, UNITS_PIXELS);

      HorizontalLayout hLayout_LeftMargAndBody = new HorizontalLayout();
      hLayout_LeftMargAndBody.setSizeFull();
      Label leftMarginSpace = new Label("");
      leftMarginSpace.setWidth(LEFTMARGIN_WIDTH, UNITS_PIXELS);
      hLayout_LeftMargAndBody.addComponent(leftMarginSpace);

      if (artifact != null) {
         VerticalLayout vLayout_OutBody = new VerticalLayout();
         vLayout_OutBody.setSizeFull();

         breadcrumbComp.setArtifact(artifact);

         Label vSpacer = new Label();
         vSpacer.setHeight(5, UNITS_PIXELS);

         OseeArtifactNameLinkComponent artifactName = new OseeArtifactNameLinkComponent(artifact);
         artifactName.setSizeUndefined();

         Label spacer1 = new Label();
         spacer1.setWidth(10, UNITS_PIXELS);
         spacer1.setHeight(null);

         Label artifactType = new Label(String.format("[%s]", artifact.getArtifactType()), Label.CONTENT_XHTML);
         artifactType.setSizeUndefined();

         HorizontalLayout hLayout_ArtNameAndType = new HorizontalLayout();
         hLayout_ArtNameAndType.setSizeUndefined();

         VerticalLayout artRelSpacer = new VerticalLayout();
         artRelSpacer.setHeight(15, UNITS_PIXELS);

         VerticalLayout vLayout_Body = new VerticalLayout();
         vLayout_Body.setMargin(false, false, false, true);
         vLayout_Body.setSizeFull();

         VerticalLayout relAttrSpacer = new VerticalLayout();
         relAttrSpacer.setHeight(15, UNITS_PIXELS);

         VerticalLayout bottomSpacer = new VerticalLayout();

         Panel panel_Body = new Panel();
         panel_Body.setScrollable(true);
         panel_Body.getContent().setSizeUndefined();
         panel_Body.setSizeFull();

         hLayout_ArtNameAndType.addComponent(artifactName);
         hLayout_ArtNameAndType.addComponent(spacer1);
         hLayout_ArtNameAndType.addComponent(artifactType);

         vLayout_Body.addComponent(vSpacer);
         vLayout_Body.addComponent(hLayout_ArtNameAndType);
         vLayout_Body.addComponent(artRelSpacer);
         vLayout_Body.addComponent(relationsComp);
         vLayout_Body.addComponent(relAttrSpacer);
         vLayout_Body.addComponent(attributeComp);
         vLayout_Body.addComponent(bottomSpacer);

         panel_Body.setContent(vLayout_Body);

         vLayout_OutBody.addComponent(breadcrumbComp);
         vLayout_OutBody.addComponent(panel_Body);

         hLayout_LeftMargAndBody.addComponent(vLayout_OutBody);

         hLayout_ArtNameAndType.setComponentAlignment(artifactType, Alignment.BOTTOM_CENTER);
         vLayout_Body.setExpandRatio(bottomSpacer, 1.0f);
         vLayout_OutBody.setExpandRatio(panel_Body, 1.0f);
         hLayout_LeftMargAndBody.setExpandRatio(vLayout_OutBody, 1.0f);
      }

      final VerticalLayout vertLayout = new VerticalLayout();
      vertLayout.setSizeFull();

      vertLayout.addComponent(searchHeader);
      vertLayout.addComponent(spacer);
      vertLayout.addComponent(hLayout_LeftMargAndBody);

      vertLayout.setComponentAlignment(searchHeader, Alignment.TOP_LEFT);
      vertLayout.setExpandRatio(hLayout_LeftMargAndBody, 1.0f);

      setCompositionRoot(vertLayout);
   }

   @Override
   public void init(Navigator navigator, Application application) {
      //Do nothing.
   }

   @Override
   public String getWarningForNavigatingFrom() {
      return null;
   }

   @Override
   public void clearAll() {
      this.artifact = null;
      createLayout();
   }

   @Override
   public void setArtifact(ViewArtifact artifact) {
      this.artifact = artifact;
      createLayout();
   }

   @Override
   public void setErrorMessage(String message) {
      //TODO:
   }

   @Override
   public void navigateTo(String requestedDataId) {
      if (searchHeader != null) {
         searchHeader.createLayout();
      }
      createLayout();
   }
}
