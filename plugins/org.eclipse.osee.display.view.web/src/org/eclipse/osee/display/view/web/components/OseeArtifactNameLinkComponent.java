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
package org.eclipse.osee.display.view.web.components;

import org.eclipse.osee.display.api.data.ViewArtifact;
import org.eclipse.osee.display.api.search.SearchNavigator;
import org.eclipse.osee.display.api.search.SearchPresenter;
import org.eclipse.osee.display.view.web.CssConstants;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public class OseeArtifactNameLinkComponent extends HorizontalLayout {

   private boolean isLayoutComplete = false;
   private ViewArtifact artifact = null;
   private final Link artifactNameLink = new Link();
   private final Label artifactNameNOLink = new Label();
   private boolean noLink = false;

   public OseeArtifactNameLinkComponent(ViewArtifact artifact) {
      this(artifact, CssConstants.OSEE_SEARCHRESULT_ARTNAME, false);
   }

   public OseeArtifactNameLinkComponent(final ViewArtifact artifact, String styleName, boolean noLink) {
      this.noLink = noLink;
      this.artifact = artifact;
      artifactNameLink.setStyleName(styleName);
      artifactNameNOLink.setStyleName(CssConstants.OSEE_SEARCHRESULT_ARTNAME_NOLINK);

      if (!noLink) {
         addListener(new LayoutClickListener() {
            @Override
            public void layoutClick(LayoutClickEvent event) {
               String url = ComponentUtility.getUrl(OseeArtifactNameLinkComponent.this);
               SearchNavigator navigator = ComponentUtility.getNavigator(OseeArtifactNameLinkComponent.this);
               SearchPresenter<?, ?> presenter = ComponentUtility.getPresenter(OseeArtifactNameLinkComponent.this);
               presenter.selectArtifact(url, OseeArtifactNameLinkComponent.this.artifact, navigator);
            }
         });
      }
   }

   public OseeArtifactNameLinkComponent() {
      this(null, CssConstants.OSEE_SEARCHRESULT_ARTNAME, false);
   }

   public OseeArtifactNameLinkComponent(boolean noLink) {
      this(null, CssConstants.OSEE_SEARCHRESULT_ARTNAME, noLink);
   }

   @Override
   public void attach() {
      if (!isLayoutComplete) {
         createLayout();
         isLayoutComplete = true;
      }
   }

   private void createLayout() {
      if (artifact != null) {
         artifactNameLink.setCaption(artifact.getArtifactName());
         artifactNameNOLink.setValue(artifact.getArtifactName());
      }

      if (noLink) {
         addComponent(artifactNameNOLink);
      } else {
         addComponent(artifactNameLink);
      }
   }

   public void updateLayout() {
      if (artifact != null) {
         artifactNameLink.setCaption(artifact.getArtifactName());
         artifactNameNOLink.setValue(artifact.getArtifactName());
      }
   }

   public ViewArtifact getArtifact() {
      return artifact;
   }

   public void setArtifact(ViewArtifact artifact) {
      this.artifact = artifact;
      updateLayout();
   }

}
