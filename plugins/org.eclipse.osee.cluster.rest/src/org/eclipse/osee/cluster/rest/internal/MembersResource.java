/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.cluster.rest.internal;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.cluster.Member;

/**
 * @author Roberto E. Escobar
 */
@Path("members")
public class MembersResource {

   @Context
   UriInfo uriInfo;
   @Context
   Request request;

   @GET
   @Produces(MediaType.TEXT_XML)
   public List<XmlMember> getMemberBrowser() {
      List<XmlMember> todos = new ArrayList<>();
      for (Member member : ClusterRestApplication.getMembers()) {
         todos.add(ClusterUtil.fromMember(member));
      }
      return todos;
   }

   @GET
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   public List<XmlMember> getMembers() {
      List<XmlMember> todos = new ArrayList<>();
      for (Member member : ClusterRestApplication.getMembers()) {
         todos.add(ClusterUtil.fromMember(member));
      }
      return todos;
   }

   @GET
   @Path("count")
   @Produces(MediaType.TEXT_PLAIN)
   public String getCount() {
      int count = ClusterRestApplication.getMembers().size();
      return String.valueOf(count);
   }

   @Path("{member}")
   public MemberResource getTodo(@PathParam("member") String id) {
      return new MemberResource(uriInfo, request, id);
   }
}
