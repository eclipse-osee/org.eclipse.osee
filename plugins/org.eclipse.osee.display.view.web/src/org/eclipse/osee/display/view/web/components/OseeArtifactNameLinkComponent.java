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

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.display.api.data.WebArtifact;
import org.eclipse.osee.display.view.web.CssConstants;
import org.eclipse.osee.display.view.web.internal.search.OseeRoadMapAndNavigation;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.Resource;
import com.vaadin.ui.Link;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public class OseeArtifactNameLinkComponent extends Link {

   public OseeArtifactNameLinkComponent(WebArtifact artifact) {
      this(artifact, CssConstants.OSEE_SEARCHRESULT_ARTNAME);
   }

   public OseeArtifactNameLinkComponent(WebArtifact artifact, String styleName) {
      this(artifact.getArtifactName(), artifact.getGuid(), styleName);
   }

   public OseeArtifactNameLinkComponent(String artName, String artGuid) {
      this(artName, artGuid, CssConstants.OSEE_SEARCHRESULT_ARTNAME);
   }

   public OseeArtifactNameLinkComponent(String artName, String artGuid, String styleName) {
      super();

      this.setCaption(artName);
      Map<String, String> parameterMap = new HashMap<String, String>();
      parameterMap.put(OseeRoadMapAndNavigation.ARTIFACT, artGuid);
      String paramString = OseeRoadMapAndNavigation.parameterMapToRequestString(parameterMap);
      Resource artifactLink = new ExternalResource(String.format("ats#AtsArtifactView%s", paramString));
      this.setResource(artifactLink);

      setStyleName(styleName);
   }
}
