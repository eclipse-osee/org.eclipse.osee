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

import org.eclipse.osee.display.api.components.SearchResultComponent;
import org.eclipse.osee.display.api.data.DisplayOptions;
import org.eclipse.osee.display.api.data.SearchResultMatch;
import org.eclipse.osee.display.api.data.StyledText;
import org.eclipse.osee.display.api.data.ViewArtifact;
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

   private ViewArtifact artifact;
   private final OseeArtifactNameLinkComponent artifactName = new OseeArtifactNameLinkComponent();
   private final OseeBreadcrumbComponent breadcrumbComp = new OseeBreadcrumbComponent();
   private final VerticalLayout vLayout_Matches = new VerticalLayout();
   private final Label artifactType = new Label("", Label.CONTENT_XHTML);
   private final int TOPBOTTOM_VERT_SPACE = 8;
   private boolean isLayoutComplete = false;

   @Override
   public void attach() {
      if (!isLayoutComplete) {
         createLayout();
         isLayoutComplete = true;
      } else {
         breadcrumbComp.updateLayout();
      }
   }

   private void createLayout() {
      setSizeUndefined();

      HorizontalLayout row0 = new HorizontalLayout();

      Label spacer1 = new Label("");
      spacer1.setHeight(null);
      spacer1.setWidth(15, UNITS_PIXELS);
      artifactType.setStyleName(CssConstants.OSEE_SEARCHRESULT_ARTTYPE);

      Label bottomSpacer = new Label("");
      bottomSpacer.setHeight(TOPBOTTOM_VERT_SPACE, UNITS_PIXELS);

      Label topSpacer = new Label("");
      topSpacer.setHeight(TOPBOTTOM_VERT_SPACE, UNITS_PIXELS);

      row0.addComponent(artifactName);
      row0.addComponent(spacer1);
      row0.addComponent(artifactType);

      addComponent(topSpacer);
      addComponent(row0);

      addComponent(breadcrumbComp);

      addComponent(vLayout_Matches);
      addComponent(bottomSpacer);

      row0.setComponentAlignment(artifactName, Alignment.BOTTOM_LEFT);
      row0.setComponentAlignment(artifactType, Alignment.MIDDLE_LEFT);
   }

   @Override
   public void setArtifact(ViewArtifact artifact) {
      this.artifact = artifact;
      artifactName.setArtifact(this.artifact);
      breadcrumbComp.setArtifact(this.artifact);
      artifactType.setCaption(String.format("[%s]", artifact.getArtifactType()));

   }

   @Override
   public void addSearchResultMatch(SearchResultMatch match) {
      OseeSearchResultMatchComponent matchComp = new OseeSearchResultMatchComponent(match);
      vLayout_Matches.addComponent(matchComp);
   }

   @Override
   public void setErrorMessage(String shortMsg, String longMsg, MsgType msgType) {
      // do nothing
   }

   @Override
   public void setDisplayOptions(DisplayOptions options) {
      if (options != null) {
         boolean showVerbose = options.getVerboseResults();
         vLayout_Matches.setVisible(showVerbose);
         breadcrumbComp.setVisible(showVerbose);
      } else {
         ComponentUtility.logWarn("OseeSearchResultComponent.setDisplayOptions - WARNING: null value detected.", this);
      }
   }

   private class OseeSearchResultMatchComponent extends HorizontalLayout {
      public OseeSearchResultMatchComponent(SearchResultMatch match) {
         Label matchLabel = new Label(String.format("%s: ", match.getAttributeType()), Label.CONTENT_XHTML);
         matchLabel.setStyleName(CssConstants.OSEE_ATTRNAME);
         Label spacer4 = new Label();
         spacer4.setWidth(15, UNITS_PIXELS);

         int firstMatch = -1;
         int charsSinceFirst = 0;
         int displaySize = 50;
         String styleOpen = "<SPAN style=\"BACKGROUND-COLOR: #ffff00\">";
         String styleClose = "</SPAN>";

         StringBuilder builder = new StringBuilder();
         for (StyledText text : match.getData()) {
            if (text.isHighLighted()) {
               if (firstMatch == -1) {
                  firstMatch = builder.length();
               }
               builder.append(styleOpen);
               if (charsSinceFirst <= 50) {
                  displaySize += styleOpen.length() + styleClose.length();
               }
            }

            if (firstMatch != -1) {
               charsSinceFirst += text.getData().length();
            }
            builder.append(text.getData());

            if (text.isHighLighted()) {
               builder.append(styleClose);
            }
         }

         if (builder.length() > displaySize) {
            int end = Math.min(firstMatch + displaySize, builder.length());
            builder.delete(end, builder.length());
            builder.delete(0, firstMatch);
            if (end != builder.length()) {
               builder.append("...");
            }
            if (firstMatch != 0) {
               builder.insert(0, "...");
            }
         }

         Label matchLabelHint = new Label(builder.toString(), Label.CONTENT_XHTML);
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
