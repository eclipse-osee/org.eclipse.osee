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

import java.util.Map;
import org.eclipse.osee.display.api.data.WebArtifact;
import org.eclipse.osee.display.api.search.SearchView;
import org.eclipse.osee.display.view.web.components.ArtifactNameLinkComponent;
import org.eclipse.osee.display.view.web.search.OseeSearchHeaderComponent;
import org.eclipse.osee.vaadin.widgets.Navigator;
import com.vaadin.Application;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public class OseeArtifactView extends CustomComponent implements Navigator.View, SearchView {

   private OseeSearchHeaderComponent oseeSearchHeader;
   private Navigator navigator;
   private OseeRelationsComponent relationsComp;
   private final OseeBreadcrumbComponent breadcrumbComp = new OseeBreadcrumbComponent(null, null);
   private WebArtifact artifact;

   private void initLayout() {
      final VerticalLayout vertLayout = new VerticalLayout();
      vertLayout.setSizeFull();

      if (artifact != null) {
         vertLayout.removeAllComponents();

         ArtifactNameLinkComponent artifactName = new ArtifactNameLinkComponent(artifact);

         final VerticalLayout paddedVertLayout = new VerticalLayout();
         paddedVertLayout.setSizeFull();
         paddedVertLayout.setMargin(true);
         paddedVertLayout.addComponent(breadcrumbComp);
         paddedVertLayout.addComponent(artifactName);
         paddedVertLayout.addComponent(relationsComp);
         paddedVertLayout.setExpandRatio(relationsComp, 1.0f);

         vertLayout.addComponent(oseeSearchHeader);
         vertLayout.setComponentAlignment(oseeSearchHeader, Alignment.TOP_LEFT);
         vertLayout.addComponent(paddedVertLayout);
         vertLayout.setExpandRatio(paddedVertLayout, 1.0f);

      }
      setCompositionRoot(vertLayout);
   }

   @Override
   public void init(Navigator navigator, Application application) {
      this.navigator = navigator;

      oseeSearchHeader = new OseeSearchHeaderComponent();
      breadcrumbComp.setNavigator(navigator);
      //relationsComp = new OseeRelationsComponent(navigator, webBackend, null);

      setSizeFull();
      initLayout();
   }

   @Override
   public void navigateTo(String requestedDataId) {
      //TODO: PROTOTYPE - Replace this with actual code that properly parses the request string
      Map<String, String> paramMap = OseeRoadMapAndNavigation.requestStringToParameterMap(requestedDataId);
      String artifactGuid = paramMap.get(OseeRoadMapAndNavigation.ARTIFACT);
      //      webBackend.getArtifact(this, artifactGuid);
   }

   @Override
   public String getWarningForNavigatingFrom() {
      return null;
   }

   //   @Override
   //   public String getWarningForNavigatingFrom() {
   //      return null;
   //   }
   //
   //   @Override
   //   public void setProgramsAndBuilds(ProgramsAndBuilds builds) {
   //      //Do nothing
   //   }
   //
   //   @Override
   //   public void setSearchResults(Collection<SearchResult> searchResults) {
   //      //Do nothing
   //   }
   //
   //   @Override
   //   public void setArtifact(Artifact artifact) {
   //      breadcrumbComp.setArtifact(artifact);
   //      relationsComp.setArtifact(artifact);
   //      oseeSearchHeader.setArtifact(artifact);
   //      this.artifact = artifact;
   //      initLayout();
   //   }
   //
   //   @Override
   //   public void setProgram(Program program) {
   //      //Do nothing
   //   }
   //
   //   @Override
   //   public void setBuild(Build build) {
   //      //Do nothing
   //   }
   //
   //   @Override
   //   public void setErrorMessage(String message) {
   //   }

}
