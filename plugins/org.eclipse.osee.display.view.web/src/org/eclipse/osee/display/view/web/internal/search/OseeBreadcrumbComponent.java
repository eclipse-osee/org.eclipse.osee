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

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.display.api.data.Artifact;
import org.eclipse.osee.vaadin.widgets.Navigator;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public class OseeBreadcrumbComponent extends HorizontalLayout {
   private Navigator navigator;
   private Artifact artifact;

   private void init() {
      this.removeAllComponents();

      Collection<Artifact> crumbs = new ArrayList<Artifact>();
      if (artifact != null && artifact.getAncestry() != null && artifact.getAncestry().size() > 0) {
         crumbs.addAll(artifact.getAncestry());
      }

      int count = 0;
      int manyBreadCrumbs = crumbs.size();
      for (Artifact crumb : crumbs) {
         ArtifactNameLinkComponent crumbLabel =
            new ArtifactNameLinkComponent(navigator, crumb, CssConstants.OSEE_BREADCRUMB_ARTNAME);
         addComponent(crumbLabel);
         if (count < manyBreadCrumbs - 1) {
            Label delimiter = new Label("&nbsp; >> &nbsp;", Label.CONTENT_XHTML);
            delimiter.setHeight(null);
            addComponent(delimiter);
         }
         count++;
      }
   }

   public OseeBreadcrumbComponent(Navigator navigator, Artifact artifact) {
      this.navigator = navigator;
      this.artifact = artifact;

      init();
   }

   public void setArtifact(Artifact artifact) {
      this.artifact = artifact;
      init();
   }

   public void setNavigator(Navigator navigator) {
      this.navigator = navigator;
   }

   public Navigator getNavigator() {
      return navigator;
   }
}
