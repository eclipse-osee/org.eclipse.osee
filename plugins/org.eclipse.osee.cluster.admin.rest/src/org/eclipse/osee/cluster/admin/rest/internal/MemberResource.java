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
package org.eclipse.osee.cluster.admin.rest.internal;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.cluster.admin.ClusterAdmin;
import org.eclipse.osee.cluster.admin.Member;

/**
 * @author Roberto E. Escobar
 */
public class MemberResource {

   @Context
   UriInfo uriInfo;
   @Context
   Request request;
   String id;

   public MemberResource(UriInfo uriInfo, Request request, String id) {
      this.uriInfo = uriInfo;
      this.request = request;
      this.id = id;
   }

   @GET
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   public XmlMember getMember() {
      XmlMember toReturn = toXmlMember(id);
      if (toReturn == null) {
         throw new RuntimeException("Get: Member with " + id + " not found");
      }
      return toReturn;
   }

   @GET
   @Produces(MediaType.TEXT_XML)
   public XmlMember getMemberHTML() {
      XmlMember toReturn = toXmlMember(id);
      if (toReturn == null) {
         throw new RuntimeException("Get: Member with " + id + " not found");
      }
      return toReturn;
   }

   private XmlMember toXmlMember(String id) {
      XmlMember toReturn = null;
      ClusterAdmin admin = ClusterAdminApplication.getClusterAdmin();
      for (Member member : admin.getCluster().getMembers()) {
         String memberId = ClusterUtil.asId(member.getInetSocketAddress());
         if (memberId.equals(id)) {
            toReturn = ClusterUtil.fromMember(member);
            break;
         }
      }
      return toReturn;
   }
}
