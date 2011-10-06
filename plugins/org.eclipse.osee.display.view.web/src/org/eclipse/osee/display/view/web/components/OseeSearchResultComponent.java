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

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.display.api.components.SearchResultComponent;
import org.eclipse.osee.display.api.data.WebArtifact;
import org.eclipse.osee.display.api.data.SearchResultMatch;
import org.eclipse.osee.display.view.web.CssConstants;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public class OseeSearchResultComponent extends VerticalLayout implements SearchResultComponent {

   private WebArtifact artifact;
   private final Collection<SearchResultMatch> matches = new ArrayList<SearchResultMatch>();

   public OseeSearchResultComponent() {
      //Stupid hack. Web layout is driving me crazy.
      //setHeight(65, UNITS_PIXELS);
      this.setSizeUndefined();
   }

   private void createLayout() {
      //    Layout:
      //     (0)   ArtName [ArtType]
      //     (1)   breadcrumb #1 >> breadcrumb #2 >> 
      //     (2)    match hint #1
      //     (n-1)  match hint #n
      //      ...
      //     (n)   [spacer]

      HorizontalLayout row0 = new HorizontalLayout();

      OseeArtifactNameLinkComponent artifactName =
         new OseeArtifactNameLinkComponent(artifact, CssConstants.OSEE_SEARCHRESULT_ARTNAME);
      Label spacer1 = new Label("");
      spacer1.setHeight(null);
      spacer1.setWidth(15, UNITS_PIXELS);
      Label artifactType = new Label(String.format("[%s]", artifact.getArtifactType()), Label.CONTENT_XHTML);
      artifactType.setStyleName(CssConstants.OSEE_SEARCHRESULT_ARTTYPE);
      row0.addComponent(artifactName);
      row0.addComponent(spacer1);
      row0.addComponent(artifactType);
      row0.setComponentAlignment(artifactName, Alignment.BOTTOM_LEFT);
      row0.setComponentAlignment(artifactType, Alignment.MIDDLE_LEFT);

      OseeBreadcrumbComponent breadcrumbComp = new OseeBreadcrumbComponent(artifact);

      addComponent(row0);
      addComponent(breadcrumbComp);

      for (SearchResultMatch match : matches) {
         OseeSearchResultMatchComponent matchComp = new OseeSearchResultMatchComponent(match);
         addComponent(matchComp);
      }

      Label spacer2 = new Label("");
      spacer2.setHeight(15, UNITS_PIXELS);
      addComponent(spacer2);
   }

   @Override
   public void setArtifact(WebArtifact artifact) {
      this.artifact = artifact;
      removeAllComponents();
      createLayout();
   }

   @Override
   public void addSearchResultMatch(SearchResultMatch match) {
      matches.add(match);
      removeAllComponents();
      createLayout();
   }

   private class OseeSearchResultMatchComponent extends HorizontalLayout {
      public OseeSearchResultMatchComponent(SearchResultMatch match) {
         Label matchLabel = new Label(String.format("%s: ", match.getAttributeType(), Label.CONTENT_XHTML));
         matchLabel.setStyleName(CssConstants.OSEE_SEARCHRESULT_MATCH);
         Label spacer4 = new Label();
         spacer4.setWidth(15, UNITS_PIXELS);
         Label matchLabelHint = new Label(String.format("%s", match.getMatchHint()), Label.CONTENT_XHTML);
         Label spacer3 = new Label();
         spacer3.setWidth(15, UNITS_PIXELS);
         Label matchManyLabel = new Label(String.format("(%d matches)", match.getManyMatches()), Label.CONTENT_XHTML);
         matchManyLabel.setStyleName(CssConstants.OSEE_SEARCHRESULT_MATCH_MANY);

         addComponent(matchLabel);
         addComponent(spacer4);
         addComponent(matchLabelHint);
         addComponent(spacer3);
         addComponent(matchManyLabel);
      }
   }
}
