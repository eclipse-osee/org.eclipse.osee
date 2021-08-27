/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.ats.api.agile.jira;

import javax.ws.rs.Consumes;
import javax.ws.rs.Encoded;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

/**
 * @author Stephen J. Molaro
 */

@Path("jira")
public interface JiraEndpoint {

   @POST
   @Path("authenticate/{jiraUrl}")
   @Consumes(MediaType.APPLICATION_JSON)
   String authenticate(@PathParam("jiraUrl") String jiraUrl, @Encoded String jsonPayload);

}