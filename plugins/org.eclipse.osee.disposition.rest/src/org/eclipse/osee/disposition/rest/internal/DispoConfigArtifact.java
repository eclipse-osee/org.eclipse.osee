/*********************************************************************
 * Copyright (c) 2015 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.disposition.rest.internal;

import java.util.List;
import org.eclipse.osee.disposition.model.DispoConfig;
import org.eclipse.osee.disposition.model.ResolutionMethod;
import org.eclipse.osee.disposition.rest.util.DispoUtil;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

/**
 * @author Angel Avila
 */
public class DispoConfigArtifact implements DispoConfig {

   private final ArtifactReadable artifact;

   public DispoConfigArtifact(ArtifactReadable artifact) {
      this.artifact = artifact;
   }

   @Override
   public List<ResolutionMethod> getValidResolutions() {
      List<String> attributes = artifact.getAttributeValues(CoreAttributeTypes.GeneralStringData);
      String resolutions = "";
      for (String attribute : attributes) {
         if (attribute.startsWith("RESOLUTION_METHODS")) {
            resolutions = attribute.replaceFirst("RESOLUTION_METHODS=", "");
            break;
         }
      }
      return DispoUtil.jsonStringToList(resolutions, ResolutionMethod.class);
   }
}
