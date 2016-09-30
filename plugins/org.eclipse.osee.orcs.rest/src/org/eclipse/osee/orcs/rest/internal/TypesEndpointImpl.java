/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.orcs.rest.internal.OrcsRestUtil.executeCallable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.OrcsTypeSheet;
import org.eclipse.osee.framework.core.data.OrcsTypesData;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreTupleTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.HexUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.jaxrs.OseeWebApplicationException;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.data.EnumEntry;
import org.eclipse.osee.orcs.data.EnumType;
import org.eclipse.osee.orcs.data.JaxEnumAttribute;
import org.eclipse.osee.orcs.data.JaxEnumEntry;
import org.eclipse.osee.orcs.data.OrcsTopicEvents;
import org.eclipse.osee.orcs.rest.model.TypesEndpoint;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

/**
 * @author Roberto E. Escobar
 */
public class TypesEndpointImpl implements TypesEndpoint {

   private final OrcsApi orcsApi;
   private final EventAdmin eventAdmin;

   public TypesEndpointImpl(OrcsApi orcsApi, EventAdmin eventAdmin) {
      this.orcsApi = orcsApi;
      this.eventAdmin = eventAdmin;
   }

   private OrcsTypes getOrcsTypes() {
      return orcsApi.getOrcsTypes();
   }

   @Override
   public Response getTypes() {
      return Response.ok().entity(new StreamingOutput() {

         @Override
         public void write(OutputStream output) throws WebApplicationException {
            Callable<Void> op = getOrcsTypes().writeTypes(output);
            executeCallable(op);
         }
      }).build();
   }

   @Override
   public Response setTypes(final InputStream inputStream) {
      IResource resource = asResource("http.osee.model", inputStream);
      Callable<Void> op = getOrcsTypes().loadTypes(resource);
      executeCallable(op);
      getOrcsTypes().invalidateAll();
      return Response.ok().build();
   }

   @Override
   public Response invalidateCaches() {
      getOrcsTypes().invalidateAll();
      return Response.ok().build();
   }

   private IResource asResource(final String fileName, final InputStream inputStream) {
      byte[] bytes;
      try {
         String types = Lib.inputStreamToString(inputStream);
         bytes = types.getBytes("UTF-8");
      } catch (IOException ex1) {
         throw new OseeWebApplicationException(Status.BAD_REQUEST, "Error parsing data");
      }
      return new ByteResource(fileName, bytes);
   }

   private static final class ByteResource implements IResource {

      private final String filename;
      private final byte[] bytes;

      public ByteResource(String filename, byte[] bytes) {
         super();
         this.filename = filename;
         this.bytes = bytes;
      }

      @Override
      public InputStream getContent() throws OseeCoreException {
         return new ByteArrayInputStream(bytes);
      }

      @Override
      public URI getLocation() {
         String modelName = filename;
         if (!modelName.endsWith(".osee")) {
            modelName += ".osee";
         }
         try {
            return new URI("osee:/" + modelName);
         } catch (URISyntaxException ex) {
            throw new OseeCoreException(ex, "Error creating URI for [%s]", modelName);
         }
      }

      @Override
      public String getName() {
         return filename;
      }

      @Override
      public boolean isCompressed() {
         return false;
      }
   }

   @Override
   public Response getEnums() {
      List<JaxEnumAttribute> attributes = new ArrayList<>();
      AttributeTypes attributeTypes = orcsApi.getOrcsTypes().getAttributeTypes();
      for (IAttributeType type : attributeTypes.getAll()) {
         if (attributeTypes.isEnumerated(type)) {
            JaxEnumAttribute enumAttr = createJaxEnumAttribute(attributeTypes, type);
            attributes.add(enumAttr);
         }
      }
      return Response.ok(attributes).build();

   }

   private JaxEnumAttribute createJaxEnumAttribute(AttributeTypes attributeTypes, IAttributeType type) {
      JaxEnumAttribute enumAttr = new JaxEnumAttribute();
      enumAttr.setName(type.getName());
      enumAttr.setDescription(type.getDescription());
      enumAttr.setUuid(type.getGuid().toString());
      enumAttr.setDataProvider(attributeTypes.getAttributeProviderId(type));
      enumAttr.setDefaultValue(attributeTypes.getDefaultValue(type));
      enumAttr.setMax(attributeTypes.getMaxOccurrences(type));
      enumAttr.setMin(attributeTypes.getMinOccurrences(type));
      enumAttr.setMediaType(attributeTypes.getMediaType(type));
      EnumType enumType = attributeTypes.getEnumType(type);
      enumAttr.setEnumTypeName(enumType.getName());
      enumAttr.setEnumTypeUuid(enumType.getId().toString());
      for (EnumEntry enumEntry : enumType.values()) {
         JaxEnumEntry entry = new JaxEnumEntry();
         entry.setName(enumEntry.getName());
         Long uuid = null;
         String guid = enumEntry.getGuid();
         if (Strings.isNumeric(guid)) {
            uuid = Long.valueOf(guid);
         } else if (HexUtil.isHexString(guid)) {
            uuid = HexUtil.toLong(guid);
         }
         if (uuid != null) {
            entry.setUuid(uuid);
         }
         enumAttr.getEntries().add(entry);
      }
      return enumAttr;
   }

   @Override
   public Response getEnums(Long uuid) {
      IAttributeType attrType = orcsApi.getOrcsTypes().getAttributeTypes().get(uuid);
      JaxEnumAttribute jaxEnumAttribute = createJaxEnumAttribute(orcsApi.getOrcsTypes().getAttributeTypes(), attrType);
      return Response.ok().entity(jaxEnumAttribute).build();
   }

   @Override
   public Response getEnumEntries(Long uuid) {
      IAttributeType attrType = orcsApi.getOrcsTypes().getAttributeTypes().get(uuid);
      JaxEnumAttribute jaxEnumAttribute = createJaxEnumAttribute(orcsApi.getOrcsTypes().getAttributeTypes(), attrType);
      return Response.ok().entity(jaxEnumAttribute.getEntries()).build();
   }

   @Override
   public Response importOrcsTypes(OrcsTypesData typesData) {
      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(COMMON.getId(), SystemUser.OseeSystem,
         "Add Types to Common Branch");
      for (OrcsTypeSheet sheet : typesData.getSheets()) {
         String guid = GUID.create();
         if (Strings.isValid(sheet.getGuid())) {
            guid = sheet.getGuid();
         }
         Long id = Lib.generateArtifactIdAsInt();
         if (Strings.isNumeric(sheet.getId())) {
            id = Long.valueOf(sheet.getId());
         }
         ArtifactId artifact = tx.createArtifact(CoreArtifactTypes.OseeTypeDefinition, sheet.getName(), guid, id);
         tx.setSoleAttributeValue(artifact, CoreAttributeTypes.Active, true);
         tx.setSoleAttributeFromString(artifact, CoreAttributeTypes.UriGeneralStringData, sheet.getTypesSheet());
      }
      tx.commit();

      tx = orcsApi.getTransactionFactory().createTransaction(COMMON.getId(), SystemUser.OseeSystem,
         "Add OseeTypeDef Tuples to Common Branch");
      for (ArtifactReadable artifact : orcsApi.getQueryFactory().fromBranch(COMMON).andIsOfType(
         CoreArtifactTypes.OseeTypeDefinition).getResults()) {
         tx.addTuple2(CoreTupleTypes.OseeTypeDef, OrcsTypesData.OSEE_TYPE_VERSION,
            artifact.getAttributes(CoreAttributeTypes.UriGeneralStringData).iterator().next());
      }
      tx.commit();
      return Response.ok().entity("Success").build();
   }

   @Override
   public Response dbInit() {
      Event event = new Event(OrcsTopicEvents.DBINIT_IMPORT_TYPES, (Map<String, ?>) null);
      eventAdmin.postEvent(event);
      return Response.ok().entity("Success").build();
   }

}
