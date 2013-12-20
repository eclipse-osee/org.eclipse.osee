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
import org.eclipse.osee.display.view.web.AbstractCommonView;
import org.eclipse.osee.display.view.web.components.OseeArtifactNameLinkComponent;
import org.eclipse.osee.display.view.web.components.OseeAttributeComponent;
import org.eclipse.osee.display.view.web.components.OseeBreadcrumbComponent;
import org.eclipse.osee.display.view.web.components.OseeLeftMarginContainer;
import org.eclipse.osee.display.view.web.components.OseeRelationsComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public abstract class OseeArtifactView extends AbstractCommonView implements ArtifactHeaderComponent {

   protected final OseeRelationsComponent relationsComp = new OseeRelationsComponent();
   protected final OseeAttributeComponent attributeComp = new OseeAttributeComponent();
   private final OseeBreadcrumbComponent breadcrumbComp = new OseeBreadcrumbComponent(true);
   private final OseeArtifactNameLinkComponent artifactName = new OseeArtifactNameLinkComponent(true);
   private final Label artifactType = new Label("", Label.CONTENT_XHTML);
   private ViewArtifact artifact;
   private final int LEFTMARGIN_WIDTH = 5;

   @Override
   protected void createLayout() {
      setSizeFull();

      getSearchHeader().setWidth(100, UNITS_PERCENTAGE);
      getSearchHeader().setHeight(null);

      Label spacer = new Label();
      spacer.setHeight(5, UNITS_PIXELS);

      HorizontalLayout hLayout_LeftMargAndBody = new HorizontalLayout();
      hLayout_LeftMargAndBody.setSizeFull();
      Label leftMarginSpace = new Label("");
      leftMarginSpace.setWidth(LEFTMARGIN_WIDTH, UNITS_PIXELS);
      hLayout_LeftMargAndBody.addComponent(leftMarginSpace);

      VerticalLayout vLayout_OutBody = new VerticalLayout();
      vLayout_OutBody.setSizeFull();

      Label vSpacer = new Label();
      vSpacer.setHeight(5, UNITS_PIXELS);

      artifactName.setSizeUndefined();

      Label spacer1 = new Label();
      spacer1.setWidth(10, UNITS_PIXELS);
      spacer1.setHeight(null);

      artifactType.setSizeUndefined();

      HorizontalLayout hLayout_ArtNameAndType = new HorizontalLayout();
      hLayout_ArtNameAndType.setSizeUndefined();

      VerticalLayout artRelSpacer = new VerticalLayout();
      artRelSpacer.setHeight(15, UNITS_PIXELS);

      VerticalLayout vLayout_Body = new VerticalLayout();
      vLayout_Body.setMargin(false, false, false, true);
      vLayout_Body.setHeight(null);
      vLayout_Body.setWidth(100, UNITS_PERCENTAGE);

      VerticalLayout relAttrSpacer = new VerticalLayout();
      relAttrSpacer.setHeight(15, UNITS_PIXELS);

      VerticalLayout bottomSpacer = new VerticalLayout();

      OseeLeftMarginContainer leftMarginContainerBreadcrumb = new OseeLeftMarginContainer();

      Panel panel_Body = new Panel();
      panel_Body.setScrollable(true);
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

      leftMarginContainerBreadcrumb.addComponent(breadcrumbComp);

      vLayout_OutBody.addComponent(leftMarginContainerBreadcrumb);
      vLayout_OutBody.addComponent(panel_Body);

      hLayout_LeftMargAndBody.addComponent(vLayout_OutBody);

      addComponent(getSearchHeader());
      addComponent(spacer);
      addComponent(hLayout_LeftMargAndBody);

      hLayout_ArtNameAndType.setComponentAlignment(artifactType, Alignment.BOTTOM_CENTER);
      vLayout_Body.setExpandRatio(bottomSpacer, 1.0f);
      vLayout_OutBody.setExpandRatio(panel_Body, 1.0f);
      hLayout_LeftMargAndBody.setExpandRatio(vLayout_OutBody, 1.0f);
      setComponentAlignment(getSearchHeader(), Alignment.TOP_LEFT);
      setExpandRatio(hLayout_LeftMargAndBody, 1.0f);
   }

   private void updateLayout() {
      if (artifact != null) {
         breadcrumbComp.setArtifact(artifact);
         artifactType.setCaption(String.format("[%s]", artifact.getArtifactType()));
         artifactName.setArtifact(artifact);
      }
   }

   @Override
   public void clearAll() {
      artifact = null;
      updateLayout();
   }

   @Override
   public void setArtifact(ViewArtifact artifact) {
      this.artifact = artifact;
      updateLayout();
   }

   @Override
   public void setErrorMessage(String shortMsg, String longMsg, MsgType msgType) {
      // do nothing
   }

   @Override
   public String getViewTitle() {
      if (this.artifact != null) {
         return artifact.getArtifactName();
      } else {
         return "";
      }
   }
}
