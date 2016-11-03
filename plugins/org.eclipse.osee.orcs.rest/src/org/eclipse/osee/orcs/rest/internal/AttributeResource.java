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

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.net.MediaType;
import java.net.URLEncoder;
import javax.ws.rs.GET;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author Roberto E. Escobar
 */
public class AttributeResource {

   @Context
   private final UriInfo uriInfo;
   @Context
   private final Request request;

   private final BranchId branchId;
   private final Long artifactUuid;
   private final AttributeId attrId;
   private final TransactionId transactionId;
   private boolean textOut = false;

   public AttributeResource(UriInfo uriInfo, Request request, BranchId branchId, Long artifactUuid, AttributeId attributeId) {
      this(uriInfo, request, branchId, artifactUuid, attributeId, TransactionId.SENTINEL);
   }

   public AttributeResource(UriInfo uriInfo, Request request, BranchId branchId, Long artifactUuid, AttributeId attributeId, TransactionId transactionId) {
      this.uriInfo = uriInfo;
      this.request = request;
      this.branchId = branchId;
      this.artifactUuid = artifactUuid;
      this.attrId = attributeId;
      this.transactionId = transactionId;
   }

   public void setTextOut(boolean value) {
      textOut = value;
   }

   @GET
   public Response getResponse() {
      ResponseBuilder builder = Response.noContent();
      try {
         QueryFactory factory = OrcsApplication.getOrcsApi().getQueryFactory();
         QueryBuilder queryBuilder = factory.fromBranch(branchId).andUuid(artifactUuid);
         if (transactionId.isValid()) {
            queryBuilder.fromTransaction(transactionId);
         }
         ArtifactReadable exactlyOne = queryBuilder.getResults().getExactlyOne();

         Optional<? extends AttributeReadable<Object>> item =
            Iterables.tryFind(exactlyOne.getAttributes(), new Predicate<AttributeReadable<Object>>() {
               @Override
               public boolean apply(AttributeReadable<Object> attribute) {
                  return attrId.equals(attribute);
               }
            });

         if (item.isPresent()) {
            Object value = item.get();
            if (value instanceof AttributeReadable<?>) {
               builder = Response.ok();
               AttributeReadable<?> attribute = (AttributeReadable<?>) value;
               String mediaType = OrcsApplication.getOrcsApi().getOrcsTypes().getAttributeTypes().getMediaType(
                  attribute.getAttributeType());
               String fileExtension =
                  OrcsApplication.getOrcsApi().getOrcsTypes().getAttributeTypes().getFileTypeExtension(
                     attribute.getAttributeType());
               if (mediaType.isEmpty() || mediaType.startsWith("text") || textOut) {
                  builder.entity(attribute.getDisplayableString());
                  builder.header("Content-type", MediaType.PLAIN_TEXT_UTF_8);
               } else {
                  ResultSet<? extends AttributeReadable<Object>> results =
                     exactlyOne.getAttributes(CoreAttributeTypes.Extension);
                  AttributeReadable<Object> extension = results.getOneOrNull();
                  if (extension != null) {
                     fileExtension = extension.getDisplayableString();
                  }
                  Object content = attribute.getValue();
                  builder.entity(content);
                  builder.header("Content-type", mediaType);
                  String filename = URLEncoder.encode(exactlyOne.getName() + "." + fileExtension, "UTF-8");
                  builder.header("Content-Disposition", "attachment; filename=" + filename);
               }
            }
         } else {
            builder = Response.status(Status.NOT_FOUND);
         }
      } catch (Exception ex) {
         throw new WebApplicationException(ex);
      }
      return builder.build();
   }
}
