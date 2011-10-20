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

import java.util.Collection;
import org.eclipse.osee.display.api.data.ViewArtifact;
import org.eclipse.osee.display.view.web.CssConstants;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public class OseeBreadcrumbComponent extends HorizontalLayout {
   private ViewArtifact artifact;

   public OseeBreadcrumbComponent(ViewArtifact artifact) {
      this.artifact = artifact;
      createLayout();
   }

   public OseeBreadcrumbComponent() {
      createLayout();
   }

   private void createLayout() {
      removeAllComponents();

      if (artifact != null && artifact.getAncestry() != null) {
         Collection<ViewArtifact> ancestryList = artifact.getAncestry();
         if (ancestryList.size() > 0) {
            Object[] ancestryArray = ancestryList.toArray();
            for (int i = ancestryArray.length - 1; i >= 0; i--) {
               ViewArtifact ancestor = (ViewArtifact) ancestryArray[i];
               OseeArtifactNameLinkComponent crumbLabel =
                  new OseeArtifactNameLinkComponent(ancestor, CssConstants.OSEE_BREADCRUMB_ARTNAME);
               addComponent(crumbLabel);
               if (i > 0) {
                  Label delimiter = new Label("&nbsp; >> &nbsp;", Label.CONTENT_XHTML);
                  delimiter.setHeight(null);
                  addComponent(delimiter);
               }
            }
         }
      }
   }

   public void setArtifact(ViewArtifact artifact) {
      this.artifact = artifact;
      createLayout();
   }
}
