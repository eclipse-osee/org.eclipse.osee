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

package org.eclipse.osee.web.deploy;

import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author Ryan Baldwin
 */
@Path("")
public class OseeWebEndpoint {

   private final String path = "/OSEE-INF/web/dist/browser/index.html";

   @GET
   @Produces({MediaType.TEXT_HTML})
   public InputStream get() {
      return getClass().getResourceAsStream(path);
   }

   @GET
   @Path("{var:.+}")
   @Produces({MediaType.TEXT_HTML})
   public InputStream getPle() {
      return getClass().getResourceAsStream(path);
   }

}