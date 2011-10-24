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
import org.eclipse.osee.display.view.web.OseeUiApplication;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Link;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public class OseeArtifactNameLinkComponent extends HorizontalLayout {

   private boolean isLayoutComplete = false;
   private SearchPresenter<?, ?> searchPresenter = null;
   private SearchNavigator navigator = null;
   private ViewArtifact artifact = null;
   private final Link artifactNameLink = new Link();

   public OseeArtifactNameLinkComponent(ViewArtifact artifact) {
      this(artifact, CssConstants.OSEE_SEARCHRESULT_ARTNAME);
   }

   public OseeArtifactNameLinkComponent(final ViewArtifact artifact, String styleName) {
      this.artifact = artifact;
      artifactNameLink.setStyleName(styleName);

      addListener(new LayoutClickListener() {
         @Override
         public void layoutClick(LayoutClickEvent event) {
            OseeUiApplication<?, ?> app = (OseeUiApplication<?, ?>) getApplication();
            String url = "";
            if (app != null) {
               url = app.getRequestedDataId();
            }
            searchPresenter.selectArtifact(url, OseeArtifactNameLinkComponent.this.artifact, navigator);
         }
      });
   }

   public OseeArtifactNameLinkComponent() {
      this(null, CssConstants.OSEE_SEARCHRESULT_ARTNAME);
   }

   @Override
   public void attach() {
      if (!isLayoutComplete) {
         try {
            OseeUiApplication<?, ?> app = (OseeUiApplication<?, ?>) this.getApplication();
            searchPresenter = app.getSearchPresenter();
            navigator = app.getNavigator();
         } catch (Exception e) {
            System.out.println("OseeArtifactNameLinkComponent.attach - CRITICAL ERROR: (AtsUiApplication) this.getApplication() threw an exception.");
         }
         createLayout();
         isLayoutComplete = true;
      }
   }

   private void createLayout() {
      if (artifact != null) {
         artifactNameLink.setCaption(artifact.getArtifactName());
      }

      addComponent(artifactNameLink);
   }

   private void updateLayout() {
      if (artifact != null) {
         artifactNameLink.setCaption(artifact.getArtifactName());
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
