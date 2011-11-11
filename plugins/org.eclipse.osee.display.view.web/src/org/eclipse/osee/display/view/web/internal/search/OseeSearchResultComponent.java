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

import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class OseeSearchResultComponent extends VerticalLayout {

   //   private final Navigator navigator;

   //   private final SearchResult result;

   //   public OseeSearchResultComponent(final Navigator navigator) {
   //      this.navigator = navigator;
   //      //      this.result = result;
   //
   //      //    Layout:
   //      //         ArtName [ArtType]
   //      //         breadcrumb #1 >> breadcrumb #2 >> 
   //      //         match hint #1
   //      //         match hint #2
   //      //         ...
   //
   //      HorizontalLayout row0 = new HorizontalLayout();
   //      HorizontalLayout row2 = new HorizontalLayout();
   //
   //      ArtifactNameLinkComponent artifactName =
   //         new ArtifactNameLinkComponent(navigator, result.getArtifact(), CssConstants.OSEE_SEARCHRESULT_ARTNAME);
   //      Label spacer1 = new Label("");
   //      spacer1.setHeight(null);
   //      spacer1.setWidth(15, UNITS_PIXELS);
   //      Label artifactType =
   //         new Label(String.format("[%s]", result.getArtifact().getArtifactType()), Label.CONTENT_XHTML);
   //      artifactType.setStyleName(CssConstants.OSEE_SEARCHRESULT_ARTTYPE);
   //      row0.addComponent(artifactName);
   //      row0.addComponent(spacer1);
   //      row0.addComponent(artifactType);
   //      row0.setComponentAlignment(artifactName, Alignment.BOTTOM_LEFT);
   //      row0.setComponentAlignment(artifactType, Alignment.MIDDLE_LEFT);
   //
   //      OseeBreadcrumbComponent breadcrumbComp = new OseeBreadcrumbComponent(navigator, result.getArtifact());
   //
   //      VerticalLayout matchVertLayout = new VerticalLayout();
   //      for (SearchResultMatch match : result.getMatches()) {
   //         HorizontalLayout matchHorzLayout = new HorizontalLayout();
   //         Label matchLabel = new Label(String.format("%s: ", match.getAttributeType(), Label.CONTENT_XHTML));
   //         matchLabel.setStyleName(CssConstants.OSEE_SEARCHRESULT_MATCH);
   //         Label spacer4 = new Label();
   //         spacer4.setWidth(15, UNITS_PIXELS);
   //         Label matchLabelHint = new Label(String.format("%s", match.getMatchHint()), Label.CONTENT_XHTML);
   //         Label spacer3 = new Label();
   //         spacer3.setWidth(15, UNITS_PIXELS);
   //         Label matchManyLabel = new Label(String.format("(%d matches)", match.getManyMatches()), Label.CONTENT_XHTML);
   //         matchManyLabel.setStyleName(CssConstants.OSEE_SEARCHRESULT_MATCH_MANY);
   //
   //         matchHorzLayout.addComponent(matchLabel);
   //         matchHorzLayout.addComponent(spacer4);
   //         matchHorzLayout.addComponent(matchLabelHint);
   //         matchHorzLayout.addComponent(spacer3);
   //         matchHorzLayout.addComponent(matchManyLabel);
   //
   //         matchVertLayout.addComponent(matchHorzLayout);
   //      }
   //      row2.addComponent(matchVertLayout);
   //
   //      Label spacer2 = new Label("");
   //      spacer2.setHeight(30, UNITS_PIXELS);
   //
   //      addComponent(row0);
   //      addComponent(breadcrumbComp);
   //      addComponent(row2);
   //      addComponent(spacer2);
   //   }
}
