/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.define.rest.md;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.define.api.md.DefineMarkdownEndpoint;
import org.eclipse.osee.define.api.md.DefineMarkdownImportData;
import org.eclipse.osee.define.rest.md.operations.DefineMarkdownOperations;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Donald G. Dunne
 */
@Path("md")
public final class DefineMarkdownEndpointImpl implements DefineMarkdownEndpoint {
   private final JdbcClient jdbcClient;
   private final OrcsApi orcsApi;

   public DefineMarkdownEndpointImpl(JdbcClient jdbcClient, OrcsApi orcsApi) {
      this.jdbcClient = jdbcClient;
      this.orcsApi = orcsApi;
   }

   @PUT
   @Path("import")
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @Override
   public DefineMarkdownImportData importMarkdown(DefineMarkdownImportData data) {
      DefineMarkdownOperations ops = new DefineMarkdownOperations(jdbcClient, orcsApi);
      return ops.importMarkdown(data);
   }
}