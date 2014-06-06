/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.config.AtsConfiguration;
import org.eclipse.osee.ats.api.config.AtsConfigurations;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
@Path("config")
public final class ConfigResource {

   private final OrcsApi orcsApi;

   public ConfigResource(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public AtsConfigurations get() throws Exception {
      ResultSet<ArtifactReadable> artifacts =
         orcsApi.getQueryFactory(null).fromBranch(CoreBranches.COMMON).andTypeEquals(AtsArtifactTypes.Configuration).getResults();
      AtsConfigurations configs = new AtsConfigurations();
      for (ArtifactReadable art : artifacts) {
         AtsConfiguration config = new AtsConfiguration();
         configs.getConfigs().add(config);
         config.setName(art.getName());
         config.setUuid(art.getLocalId());
         config.setBranchUuid(Long.valueOf(art.getSoleAttributeValue(AtsAttributeTypes.AtsConfiguredBranch, "0L")));
         config.setIsDefault(art.getSoleAttributeValue(AtsAttributeTypes.Default, false));
      }
      return configs;
   }

}
