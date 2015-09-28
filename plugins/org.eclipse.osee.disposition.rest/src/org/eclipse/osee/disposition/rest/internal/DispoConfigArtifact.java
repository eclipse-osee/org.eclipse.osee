/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest.internal;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.disposition.model.DispoConfig;
import org.eclipse.osee.disposition.model.ResolutionMethod;
import org.eclipse.osee.disposition.rest.util.DispoUtil;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
      List<ResolutionMethod> toReturn = new ArrayList<>();
      List<String> attributes = artifact.getAttributeValues(CoreAttributeTypes.GeneralStringData);
      String resolutionsJson = "[]";

      for (String attribute : attributes) {
         if (attribute.startsWith("RESOLUTION_METHODS")) {
            resolutionsJson = attribute.replaceFirst("RESOLUTION_METHODS=", "");
            break;
         }
      }

      try {
         JSONArray jArray = DispoUtil.asJSONArray(resolutionsJson);
         for (int i = 0; i < jArray.length(); i++) {
            JSONObject resolutionMethodJObject = jArray.getJSONObject(i);
            ResolutionMethod method = DispoUtil.jsonObjToResolutionMethod(resolutionMethodJObject);
            toReturn.add(method);
         }
         return toReturn;
      } catch (JSONException ex) {
         throw new OseeCoreException("Invalid Resolutions JSON in Dispo Config", ex);
      }
   }
}
