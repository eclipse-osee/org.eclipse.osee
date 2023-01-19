/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.orcs.rest.model;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.eclipse.osee.framework.core.data.TransactionToken;

/**
 * @author Christopher Rebuck
 */
@Path("gc")
public interface GridCommanderEndpoint {
   @GET
   @Path("user/commands")
   @Produces(APPLICATION_JSON)
   List<UserContext> getUserCommands();

   @GET
   @Path("user/history")
   @Produces(APPLICATION_JSON)
   ExecutedCommandHistory asExecutedCommandHistoryTable();

   @POST
   @Path("context")
   @Produces(APPLICATION_JSON)
   TransactionToken createDefaultContext();

}