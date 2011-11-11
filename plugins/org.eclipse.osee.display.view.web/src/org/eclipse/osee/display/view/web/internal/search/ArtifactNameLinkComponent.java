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

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.display.api.data.Artifact;
import org.eclipse.osee.vaadin.widgets.Navigator;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.Resource;
import com.vaadin.ui.Link;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public class ArtifactNameLinkComponent extends Link {

   public ArtifactNameLinkComponent(Navigator navigator, Artifact artifact) {
      this(navigator, artifact, CssConstants.OSEE_SEARCHRESULT_ARTNAME);
   }

   public ArtifactNameLinkComponent(Navigator navigator, Artifact artifact, String styleName) {
      super();

      this.setCaption(artifact.getArtifactName());
      Map<String, String> parameterMap = new HashMap<String, String>();
      parameterMap.put(OseeRoadMapAndNavigation.ARTIFACT, artifact.getGuid());
      String paramString = OseeRoadMapAndNavigation.parameterMapToRequestString(parameterMap);
      Resource artifactLink = new ExternalResource(String.format("%s", navigator.getUri(OseeArtifactView.class)));
      this.setResource(artifactLink);

      setStyleName(styleName);
   }
}
