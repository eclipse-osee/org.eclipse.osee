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

import javax.ws.rs.Path;
import org.eclipse.osee.ats.api.agile.AgileEndpointApi;
import org.eclipse.osee.ats.impl.IAtsServer;

/**
 * @author Donald G. Dunne
 */
@Path("agile")
public class AgileEndpointImpl implements AgileEndpointApi {

   private final IAtsServer atsServer;

   public AgileEndpointImpl(IAtsServer atsServer) {
      this.atsServer = atsServer;
   }

   @Override
   public AgileTeamEndpointImpl team() throws Exception {
      return new AgileTeamEndpointImpl(atsServer);
   }

   @Override
   public String get() {
      return "Agile Resource";
   }

}
