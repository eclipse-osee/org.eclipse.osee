/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.agile.operations;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.JaxAtsObject;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class EndpointOperations {

   private final AtsApi atsApi;

   public EndpointOperations(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   public ArtifactToken filterOutActiveIfSet(Boolean active, ArtifactToken art) {
      Boolean storedActive = atsApi.getAttributeResolver().getSoleAttributeValue(art, AtsAttributeTypes.Active, null);
      if (storedActive != null) {
         if (!storedActive.equals(active)) {
            return null;
         }
      }
      return art;
   }

   public Boolean getActiveQueryParamOrNull(UriInfo uriInfo) {
      // determine if active query param was sent in
      Boolean active = null;
      if (uriInfo != null) {
         MultivaluedMap<String, String> qp = uriInfo.getQueryParameters(true);
         String activeStr = qp.getFirst("active");
         if (Strings.isValid(activeStr)) {
            active = "true".equals(activeStr);
         }
      }
      return active;
   }

   /**
    * @return list of artifact tokens for artifact type with check for active=true/false/null
    */
   public List<JaxAtsObject> getActiveArtifactTypeTokens(IArtifactType artifactType, UriInfo uriInfo) throws Exception {
      Boolean active = getActiveQueryParamOrNull(uriInfo);
      List<JaxAtsObject> teams = new ArrayList<>();
      for (ArtifactToken art : atsApi.getQueryService().getArtifacts(artifactType)) {
         if (active != null) {
            if (filterOutActiveIfSet(active, art) == null) {
               continue;
            }
         }
         JaxAtsObject token = new JaxAtsObject();
         token.setName(art.getName());
         token.setId(art.getId());
         teams.add(token);
      }
      return teams;
   }

}
