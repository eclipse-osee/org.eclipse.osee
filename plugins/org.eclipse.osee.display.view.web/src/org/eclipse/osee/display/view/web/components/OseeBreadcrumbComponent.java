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
import org.eclipse.osee.display.api.data.WebArtifact;
import org.eclipse.osee.display.api.data.WebId;
import org.eclipse.osee.display.view.web.CssConstants;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public class OseeBreadcrumbComponent extends HorizontalLayout {
   private WebArtifact artifact;

   private void init() {
      this.removeAllComponents();

      if (artifact != null && artifact.getAncestry() != null) {
         Collection<WebId> ancestryList = artifact.getAncestry();
         if (ancestryList.size() > 0) {
            Object[] ancestryArray = ancestryList.toArray();
            for (int i = ancestryArray.length - 1; i >= 0; i--) {
               WebId ancestor = (WebId) ancestryArray[i];
               OseeArtifactNameLinkComponent crumbLabel =
                  new OseeArtifactNameLinkComponent(ancestor.getName(), ancestor.getGuid(),
                     CssConstants.OSEE_BREADCRUMB_ARTNAME);
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

   public OseeBreadcrumbComponent(WebArtifact artifact) {
      this.artifact = artifact;

      init();
   }

   public void setArtifact(WebArtifact artifact) {
      this.artifact = artifact;
      init();
   }
}
