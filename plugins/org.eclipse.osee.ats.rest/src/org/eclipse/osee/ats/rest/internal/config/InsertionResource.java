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
package org.eclipse.osee.ats.rest.internal.config;

import javax.ws.rs.Path;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.ats.rest.util.AbstractConfigResource;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * Donald G. Dunne
 */
@Path("insertion")
public class InsertionResource extends AbstractConfigResource {

   public InsertionResource(IAtsServer atsServer, OrcsApi orcsApi) {
      super(AtsArtifactTypes.Insertion, atsServer, orcsApi);
   }

}
