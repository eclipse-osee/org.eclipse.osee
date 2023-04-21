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

package org.eclipse.osee.mim;

import com.fasterxml.jackson.annotation.JsonView;
import java.util.Collection;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;
import org.eclipse.osee.framework.jdk.core.type.NamedId;
import org.eclipse.osee.mim.types.InterfaceLogicalTypeGeneric;

/**
 * @author Audrey Denk
 */
@Path("logicalType")
@Swagger
public interface LogicalTypeEndpoint {

   @GET()
   @JsonView(InterfaceLogicalTypeView.Simple.class)
   @Produces(MediaType.APPLICATION_JSON)
   public Collection<? extends NamedId> getLogicalTypes();

   @GET
   @Path("{type}")
   @JsonView(InterfaceLogicalTypeView.Detailed.class)
   @Produces(MediaType.APPLICATION_JSON)
   public InterfaceLogicalTypeGeneric getLogicalTypeFields(@PathParam("type") String type);

}
