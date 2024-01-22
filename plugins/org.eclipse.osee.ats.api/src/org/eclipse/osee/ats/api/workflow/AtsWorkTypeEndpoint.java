/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.ats.api.workflow;

import java.util.Collection;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.framework.jdk.core.annotation.Swagger;

/**
 * @author Ryan T. Baldwin
 */
@Path("workType")
@Swagger
public interface AtsWorkTypeEndpoint {

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   Collection<WorkType> get();

}
