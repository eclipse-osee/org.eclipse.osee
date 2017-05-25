/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author Donald G. Dunne
 */
@Path("type")
public class AttributeTypeResource {

   // Allows insertion of contextual objects into the class,
   // e.g. ServletContext, Request, Response, UriInfo
   @Context
   private final UriInfo uriInfo;
   @Context
   private final Request request;

   private final BranchId branchId;
   private final Long artifactUuid;
   private final Long attributeTypeId;
   private final TransactionId transactionId;

   public AttributeTypeResource(UriInfo uriInfo, Request request, BranchId branchId, Long artifactUuid) {
      this(uriInfo, request, branchId, artifactUuid, -1L, TransactionId.SENTINEL);
   }

   public AttributeTypeResource(UriInfo uriInfo, Request request, BranchId branchId, Long artifactUuid, Long attributeTypeId) {
      this(uriInfo, request, branchId, artifactUuid, attributeTypeId, TransactionId.SENTINEL);
   }

   public AttributeTypeResource(UriInfo uriInfo, Request request, BranchId branchId, Long artifactUuid, Long attributeTypeId, TransactionId transactionId) {
      this.uriInfo = uriInfo;
      this.request = request;
      this.branchId = branchId;
      this.artifactUuid = artifactUuid;
      this.attributeTypeId = attributeTypeId;
      this.transactionId = transactionId;
   }

   @GET
   @Produces(MediaType.TEXT_HTML)
   public Response getResponse() {
      ResponseBuilder builder = Response.noContent();
      try {
         QueryFactory factory = OrcsApplication.getOrcsApi().getQueryFactory();
         QueryBuilder queryBuilder = factory.fromBranch(branchId).andUuid(artifactUuid);
         if (transactionId.getId() > 0) {
            queryBuilder.fromTransaction(transactionId);
         }
         ArtifactReadable exactlyOne = queryBuilder.getResults().getExactlyOne();

         if (attributeTypeId <= 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(AHTML.beginMultiColumnTable(95));
            sb.append(AHTML.addRowMultiColumnTable(AHTML.bold("Valid Types")));
            sb.append(AHTML.addRowMultiColumnTable(""));
            for (AttributeTypeToken attrType : exactlyOne.getValidAttributeTypes()) {
               sb.append(AHTML.addRowMultiColumnTable(AHTML.bold("Name:"), attrType.getName()));
               sb.append(AHTML.addRowMultiColumnTable(AHTML.bold("AttributeTypeId:"),
                  AHTML.getHyperlink(String.format("/orcs/branch/%d/artifact/%d/attribute/type/%d", branchId,
                     artifactUuid, attrType.getId()), attrType.getIdString())));
               sb.append(AHTML.addRowMultiColumnTable(""));
            }
            sb.append(AHTML.endMultiColumnTable());
            return Response.ok(AHTML.simplePage(sb.toString())).build();
         } else {
            List<AttributeReadable<Object>> attrs = new ArrayList<>();
            if (attributeTypeId > 0) {
               AttributeTypeId attributeType =
                  OrcsApplication.getOrcsApi().getOrcsTypes().getAttributeTypes().get((long) attributeTypeId);
               for (AttributeReadable<Object> attr : exactlyOne.getAttributes(attributeType)) {
                  attrs.add(attr);
               }
            } else {
               attrs.addAll(exactlyOne.getAttributes().getList());
            }

            if (attrs.size() == 1) {
               builder = Response.ok();
               AttributeReadable<?> attribute = attrs.iterator().next();
               String mediaType = OrcsApplication.getOrcsApi().getOrcsTypes().getAttributeTypes().getMediaType(
                  attribute.getAttributeType());
               String fileExtension =
                  OrcsApplication.getOrcsApi().getOrcsTypes().getAttributeTypes().getFileTypeExtension(
                     attribute.getAttributeType());
               if (mediaType.isEmpty() || mediaType.startsWith("text")) {
                  builder.entity(attribute.getDisplayableString());
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
            } else if (attrs.size() > 1) {
               String values = Collections.toString("</br>", attrs);
               return Response.ok(AHTML.simplePage(values)).build();

            } else {
               AttributeTypeToken attributeType =
                  OrcsApplication.getOrcsApi().getOrcsTypes().getAttributeTypes().get((long) attributeTypeId);
               return Response.ok(AHTML.simplePage(String.format("No attributes of type [%s][%d] found.",
                  attributeType.getName(), attributeType.getId()))).build();
            }
         }
      } catch (Exception ex) {
         throw new WebApplicationException(ex);
      }
      return builder.build();
   }
}
