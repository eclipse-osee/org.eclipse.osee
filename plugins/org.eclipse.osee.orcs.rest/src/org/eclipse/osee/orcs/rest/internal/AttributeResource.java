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
package org.eclipse.osee.orcs.rest.internal;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * @author Roberto E. Escobar
 */
public class AttributeResource {

   @Context
   private final UriInfo uriInfo;
   @Context
   private final Request request;

   private final Long branchUuid;
   private final Long artifactUuid;
   private final int attrId;
   private final int transactionId;

   public AttributeResource(UriInfo uriInfo, Request request, Long branchUuid, Long artifactUuid, int attributeId) {
      this(uriInfo, request, branchUuid, artifactUuid, attributeId, -1);
   }

   public AttributeResource(UriInfo uriInfo, Request request, Long branchUuid, Long artifactUuid, int attributeId, int transactionId) {
      this.uriInfo = uriInfo;
      this.request = request;
      this.branchUuid = branchUuid;
      this.artifactUuid = artifactUuid;
      this.attrId = attributeId;
      this.transactionId = transactionId;
   }

   @GET
   @Produces(MediaType.TEXT_PLAIN)
   public String getAsText() throws OseeCoreException {
      IOseeBranch branch = TokenFactory.createBranch(branchUuid, "");
      QueryFactory factory = OrcsApplication.getOrcsApi().getQueryFactory();
      QueryBuilder queryBuilder = factory.fromBranch(branch).andUuid(artifactUuid);
      if (transactionId > 0) {
         queryBuilder.fromTransaction(transactionId);
      }
      ArtifactReadable exactlyOne = queryBuilder.getResults().getExactlyOne();

      Optional<? extends AttributeReadable<Object>> item =
         Iterables.tryFind(exactlyOne.getAttributes(), new Predicate<AttributeReadable<Object>>() {
            @Override
            public boolean apply(AttributeReadable<Object> attribute) {
               return attribute.getLocalId() == attrId;
            }
         });

      String toReturn = "";
      if (item.isPresent()) {
         Object value = item.get().getValue();
         if (value != null) {
            toReturn = value.toString();
         }
      }
      return toReturn;
   }
}
