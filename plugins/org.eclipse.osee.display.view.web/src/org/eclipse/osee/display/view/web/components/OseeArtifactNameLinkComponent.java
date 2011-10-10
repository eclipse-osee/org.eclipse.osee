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

import org.eclipse.osee.display.api.data.WebArtifact;
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

   private boolean populated = false;
   private SearchPresenter searchPresenter = null;
   private SearchNavigator navigator = null;

   public OseeArtifactNameLinkComponent(WebArtifact artifact) {
      this(artifact, CssConstants.OSEE_SEARCHRESULT_ARTNAME);
   }

   @Override
   public void attach() {
      if (!populated) {
         super.attach();
         try {
            OseeUiApplication app = (OseeUiApplication) this.getApplication();
            searchPresenter = app.getSearchPresenter();
            navigator = app.getNavigator();
         } catch (Exception e) {
            System.out.println("OseeArtifactNameLinkComponent.attach - CRITICAL ERROR: (OseeUiApplication) this.getApplication() threw an exception.");
         }
      }
      populated = true;
   }

   public OseeArtifactNameLinkComponent(final WebArtifact artifact, String styleName) {
      super();

      Link artifactNameLink = new Link();
      artifactNameLink.setCaption(artifact.getArtifactName());
      artifactNameLink.setStyleName(styleName);
      this.addComponent(artifactNameLink);

      this.addListener(new LayoutClickListener() {

         @Override
         public void layoutClick(LayoutClickEvent event) {
            searchPresenter.selectArtifact(artifact, navigator);
         }
      });

      //      Map<String, String> parameterMap = new HashMap<String, String>();
      //      parameterMap.put(OseeRoadMapAndNavigation.ARTIFACT, artifact.getGuid());
      //      String paramString = OseeRoadMapAndNavigation.parameterMapToRequestString(parameterMap);
      //      Resource artifactLink = new ExternalResource(String.format("ats#AtsArtifactView%s", paramString));
      //      artifactNameLink.setResource(artifactLink);

   }
}
