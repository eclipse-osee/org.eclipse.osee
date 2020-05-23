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

package org.eclipse.osee.orcs.rest.admin;

import java.io.File;
import java.io.IOException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.jdk.core.text.Rule;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Ryan D. Brooks
 * @author Morgan E. Cook
 */
@Path("/")
public final class LinkUpdateResource {
   private final OrcsApi orcsApi;

   public LinkUpdateResource(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   @Path("links")
   @POST
   @Produces(MediaType.TEXT_HTML)
   public String fixLinks(@QueryParam("path") String path) throws IOException {
      Rule rule = new UpdateLinksRule(orcsApi);
      rule.setFileNamePattern(".*html");
      int modCount = rule.process(new File(path));
      return "Number of files modified: " + modCount;
   }
}
