/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.orcs.rest.internal;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.AttributeDataTransfer;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeReadable;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.Multiplicity;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.model.AttributeEndpoint;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author Roberto E. Escobar
 */
public class AttributeEndpointImpl implements AttributeEndpoint {
   private final QueryBuilder query;
   private final UriInfo uriInfo;
   private final BranchId branch;
   private final ArtifactId artifactId;
   private final OrcsApi orcsApi;

   public AttributeEndpointImpl(ArtifactId artifactId, BranchId branch, OrcsApi orcsApi, QueryBuilder query, UriInfo uriInfo) {
      this.artifactId = artifactId;
      this.query = query;
      this.uriInfo = uriInfo;
      this.branch = branch;
      this.orcsApi = orcsApi;
   }

   @Override
   public String getAttributesAsHtml(ArtifactId artifactId) {
      ArtifactReadable artifact = query.andId(artifactId).getResults().getExactlyOne();
      HtmlWriter writer = new HtmlWriter(uriInfo, orcsApi);
      return writer.toHtml(artifact.getAttributes());
   }

   @Override
   public List<AttributeDataTransfer> getAttributes(ArtifactId artifactId) {
      List<AttributeDataTransfer> data = new ArrayList<>();
      QueryBuilder queryBuilder = query.andId(artifactId);
      ArtifactReadable exactlyOne = queryBuilder.getResults().getExactlyOne();
      ResultSet<? extends AttributeReadable<Object>> attributes = exactlyOne.getAttributes();
      for (AttributeReadable<Object> item : attributes) {
         data.add(getDataFromAttribute(exactlyOne, item));
      }
      return data;
   }

   private AttributeDataTransfer getDataFromAttribute(ArtifactReadable artifact, AttributeReadable<Object> attribute) {
      AttributeDataTransfer data = new AttributeDataTransfer();
      AttributeTypeToken type = attribute.getAttributeType();
      data.setAttributeName(type.getName());
      data.setAttrId(attribute);
      data.setAttrTypeId(type);
      data.setBaseType(getBaseTypeName(type));
      data.setDescription(type.getDescription());
      data.setDisplayHint(type.getDisplayHints().toString());
      data.setExtension(type.getFileExtension());
      data.setGammaId(attribute.getGammaId());
      data.setMediaType(type.getMediaType());
      data.setMultiplicity(getMultiplicity(artifact, type));
      data.setValidEnums(getValidEnums(type));
      data.setValue(attribute.getValue().toString());
      return data;
   }

   private String getMultiplicity(ArtifactReadable artifact, AttributeTypeToken type) {
      Multiplicity mult = artifact.getArtifactType().getMultiplicity(type);
      return mult.toString();
   }

   private String[] getValidEnums(AttributeTypeToken type) {
      String[] empty = new String[] {"EMPTY"};
      if (type.isEnumerated()) {
         Set<String> enums = type.toEnum().getEnumStrValues();
         if (enums.size() > 0) {
            return enums.toArray(new String[enums.size()]);
         } else {
            return empty;
         }
      } else {
         return empty;
      }
   }

   private String getBaseTypeName(AttributeTypeToken type) {
      if (type.isArtifactId()) {
         return "ART_ID";
      }
      if (type.isBoolean()) {
         return "BOOLEAN";
      }
      if (type.isBranchId()) {
         return "BRANCH_ID";
      }
      if (type.isDate()) {
         return "DATE";
      }
      if (type.isDouble()) {
         return "DOUBLE";
      }
      if (type.isEnumerated()) {
         return "ENUM";
      }
      if (type.isInputStream()) {
         return "STREAM";
      }
      if (type.isInteger()) {
         return "INTEGER";
      }
      if (type.isLong()) {
         return "LONG";
      }
      if (type.isString()) {
         return "STRING";
      }
      if (type.isUri()) {
         return "URI";
      }
      if (type.isJavaObject()) {
         return "JAVA_OBJECT";
      }
      return "COMPLEX";
   }

   @Override
   public Response getAttribute(AttributeId attributeId) {
      return getAttributeResponse(attributeId, TransactionId.SENTINEL, false);
   }

   @Override
   public Response getAttributeWithGammaAsText(AttributeId attributeId, TransactionId transaction) {
      return getAttributeResponse(attributeId, transaction, true);
   }

   @Override
   public Response getAttributeWithGamma(AttributeId attributeId, TransactionId transaction) {
      return getAttributeResponse(attributeId, transaction, false);
   }

   private Response getAttributeResponse(AttributeId attributeId, TransactionId transaction, boolean textOut) {
      ResponseBuilder builder = Response.noContent();
      try {
         QueryBuilder queryBuilder = query.andId(artifactId);
         if (transaction.isValid()) {
            queryBuilder.fromTransaction(transaction);
         }
         ArtifactReadable exactlyOne = queryBuilder.getResults().getExactlyOne();

         Optional<? extends AttributeReadable<Object>> item =
            Iterables.tryFind(exactlyOne.getAttributes(), new Predicate<AttributeReadable<Object>>() {
               @Override
               public boolean apply(AttributeReadable<Object> attribute) {
                  return attributeId.equals(attribute);
               }
            });

         if (item.isPresent()) {
            Object value = item.get();
            if (value instanceof AttributeReadable<?>) {
               builder = Response.ok();
               AttributeReadable<?> attribute = (AttributeReadable<?>) value;
               String mediaType = attribute.getAttributeType().getMediaType();
               String fileExtension = attribute.getAttributeType().getFileExtension();
               if (mediaType.isEmpty() || mediaType.startsWith("text") || textOut) {
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
            }
         } else {
            builder = Response.status(Status.NOT_FOUND);
         }
      } catch (Exception ex) {
         throw new WebApplicationException(ex);
      }
      return builder.build();
   }

   @Override
   public Response getAttributeTypes() {
      return getAttributeTypeResponse();
   }

   @Override
   public Response getAttributeTypeValues(AttributeTypeToken attributeType) {
      return getAttributeTypeResponse(TransactionId.SENTINEL, attributeType);
   }

   @Override
   public Response getAttributeTypeValuesForTransaction(AttributeTypeToken attributeType, TransactionId transaction) {
      return getAttributeTypeResponse(transaction, attributeType);
   }

   private Response getAttributeTypeResponse(TransactionId transaction, AttributeTypeToken attributeType) {
      ResponseBuilder builder = Response.noContent();
      try {
         QueryBuilder queryBuilder = query.andId(artifactId);
         if (transaction.isValid()) {
            queryBuilder.fromTransaction(transaction);
         }
         ArtifactReadable exactlyOne = queryBuilder.getResults().getExactlyOne();

         List<AttributeReadable<Object>> attrs = new ArrayList<>();
         for (AttributeReadable<Object> attr : exactlyOne.getAttributes(attributeType)) {
            attrs.add(attr);
         }

         if (attrs.size() == 1) {
            builder = Response.ok();
            AttributeReadable<?> attribute = attrs.iterator().next();
            String mediaType = attribute.getAttributeType().getMediaType();
            String fileExtension = attribute.getAttributeType().getFileExtension();
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
            return Response.ok(
               AHTML.simplePage(String.format("No attributes of type [%s] found.", attributeType))).build();
         }
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
      return builder.build();
   }

   private Response getAttributeTypeResponse() {
      try {
         QueryBuilder queryBuilder = query.andId(artifactId);
         ArtifactReadable exactlyOne = queryBuilder.getResults().getExactlyOne();
         StringBuilder sb = new StringBuilder();
         sb.append(AHTML.beginMultiColumnTable(95));
         sb.append(AHTML.addRowMultiColumnTable(AHTML.bold("Valid Types")));
         sb.append(AHTML.addRowMultiColumnTable(""));
         for (AttributeTypeToken attrType : exactlyOne.getValidAttributeTypes()) {
            sb.append(AHTML.addRowMultiColumnTable(AHTML.bold("Name:"), attrType.getName()));
            sb.append(AHTML.addRowMultiColumnTable(AHTML.bold("Attribute Type:"),
               AHTML.getHyperlink(String.format("/orcs/branch/%s/artifact/%s/attribute/type/%s", branch, artifactId,
                  attrType.getIdString()), attrType.getIdString())));
            sb.append(AHTML.addRowMultiColumnTable(""));
         }
         sb.append(AHTML.endMultiColumnTable());
         return Response.ok(AHTML.simplePage(sb.toString())).build();
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }
}