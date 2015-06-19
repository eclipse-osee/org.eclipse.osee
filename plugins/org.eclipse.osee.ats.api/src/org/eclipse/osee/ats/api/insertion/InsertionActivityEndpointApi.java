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
package org.eclipse.osee.ats.api.insertion;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.ats.api.config.BaseConfigEndpointApi;

/**
 * @author Donald G. Dunne
 */
@Path("insertionactivityep")
public interface InsertionActivityEndpointApi extends BaseConfigEndpointApi<JaxInsertionActivity> {

   @PUT
   @Consumes(MediaType.APPLICATION_JSON)
   public Response update(JaxInsertionActivity activity) throws Exception;

}
