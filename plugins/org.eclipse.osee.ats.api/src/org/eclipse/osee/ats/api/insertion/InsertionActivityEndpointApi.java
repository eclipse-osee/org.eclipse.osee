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

package org.eclipse.osee.ats.api.insertion;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.ats.api.config.BaseConfigEndpointApi;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;

/**
 * @author Donald G. Dunne
 */
@Path("insertionactivityep")
@Swagger
public interface InsertionActivityEndpointApi extends BaseConfigEndpointApi<JaxInsertionActivity> {

   @PUT
   @Consumes(MediaType.APPLICATION_JSON)
   public Response update(JaxInsertionActivity activity) throws Exception;

}
