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
package org.eclipse.osee.ats.rest.internal.agile;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.agile.AgileTeamEndpointApi;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.agile.NewAgileTeam;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.ats.impl.config.AbstractConfigResource;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jaxrs.mvc.IdentityView;

/**
 * Donald G. Dunne
 */
@Path("agile/team")
public class AgileTeamEndpointImpl extends AbstractConfigResource implements AgileTeamEndpointApi {

   public AgileTeamEndpointImpl(IAtsServer atsServer) {
      super(AtsArtifactTypes.AgileTeam, atsServer);
   }

   @Override
   @POST
   @IdentityView
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public NewAgileTeam createTeam(NewAgileTeam newTeam) throws Exception {
      // validate title
      if (!Strings.isValid(newTeam.getName())) {
         throw new OseeArgumentException("name is not valid");
      }

      String guid = newTeam.getGuid();
      if (guid == null) {
         guid = GUID.create();
      }

      IAgileTeam team = atsServer.getAgileService().createAgileTeam(newTeam.getName(), guid);
      NewAgileTeam created = new NewAgileTeam();
      created.setGuid(team.getGuid());
      created.setName(team.getName());
      return created;
   }
}
