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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author Christopher Rebuck
 */
@Path("gc")
public interface GridCommanderEndpoint {
   @GET
   @Path("user/commands")
   @Produces(MediaType.APPLICATION_JSON)
   UserWithContexts getUserCommands();

   @GET
   @Path("user/history")
   @Produces(MediaType.APPLICATION_JSON)
   ExecutedCommandHistory asExecutedCommandHistoryTable();

}