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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.OrcsTypeSheet;
import org.eclipse.osee.framework.core.data.OrcsTypesConfig;
import org.eclipse.osee.framework.core.data.OrcsTypesData;
import org.eclipse.osee.framework.core.data.OrcsTypesSheet;
import org.eclipse.osee.framework.core.data.OrcsTypesVersion;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreTupleTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.jaxrs.OseeWebApplicationException;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
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
   private final JdbcService jdbcService;

   public TypesEndpointImpl(OrcsApi orcsApi, EventAdmin eventAdmin, JdbcService jdbcService) {
      this.orcsApi = orcsApi;
      this.eventAdmin = eventAdmin;
      this.jdbcService = jdbcService;
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
      public InputStream getContent() {
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
      for (AttributeTypeToken type : attributeTypes.getAll()) {
         if (attributeTypes.isEnumerated(type)) {
            JaxEnumAttribute enumAttr = createJaxEnumAttribute(attributeTypes, type);
            attributes.add(enumAttr);
         }
      }
      return Response.ok(attributes).build();

   }

   private JaxEnumAttribute createJaxEnumAttribute(AttributeTypes attributeTypes, AttributeTypeToken type) {
      JaxEnumAttribute enumAttr = new JaxEnumAttribute();
      enumAttr.setName(type.getName());
      enumAttr.setDescription(type.getDescription());
      enumAttr.setUuid(type.getIdString());
      enumAttr.setDataProvider(attributeTypes.getAttributeProviderId(type));
      enumAttr.setDefaultValue(attributeTypes.getDefaultValue(type));
      enumAttr.setMax(attributeTypes.getMaxOccurrences(type));
      enumAttr.setMin(attributeTypes.getMinOccurrences(type));
      enumAttr.setMediaType(attributeTypes.getMediaType(type));
      EnumType enumType = attributeTypes.getEnumType(type);
      enumAttr.setEnumTypeName(enumType.getName());
      enumAttr.setEnumTypeUuid(enumType.getIdString());
      for (EnumEntry enumEntry : enumType.values()) {
         JaxEnumEntry entry = new JaxEnumEntry();
         entry.setName(enumEntry.getName());
         String guid = enumEntry.getGuid();
         Long uuid = null;
         if (Strings.isNumeric(guid)) {
            uuid = Long.valueOf(guid);
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
      AttributeTypeToken attrType = orcsApi.getOrcsTypes().getAttributeTypes().get(uuid);
      JaxEnumAttribute jaxEnumAttribute = createJaxEnumAttribute(orcsApi.getOrcsTypes().getAttributeTypes(), attrType);
      return Response.ok().entity(jaxEnumAttribute).build();
   }

   @Override
   public Response getEnumEntries(Long uuid) {
      AttributeTypeToken attrType = orcsApi.getOrcsTypes().getAttributeTypes().get(uuid);
      JaxEnumAttribute jaxEnumAttribute = createJaxEnumAttribute(orcsApi.getOrcsTypes().getAttributeTypes(), attrType);
      return Response.ok().entity(jaxEnumAttribute.getEntries()).build();
   }

   @Override
   public Response importOrcsTypes(OrcsTypesData typesData) {
      TransactionBuilder tx =
         orcsApi.getTransactionFactory().createTransaction(COMMON, SystemUser.OseeSystem, "Add Types to Common Branch");
      ArtifactId typesFolder = orcsApi.getQueryFactory().fromBranch(COMMON).andId(
         CoreArtifactTokens.OseeTypesFolder).getResults().getAtMostOneOrNull();
      if (typesFolder == null) {
         ArtifactId rootArt = orcsApi.getQueryFactory().fromBranch(COMMON).andId(
            CoreArtifactTokens.DefaultHierarchyRoot).getResults().getExactlyOne();
         typesFolder = tx.createArtifact(CoreArtifactTokens.OseeTypesFolder);
         tx.addChildren(rootArt, typesFolder);
      }
      for (OrcsTypeSheet sheet : typesData.getSheets()) {
         Long id = Lib.generateArtifactIdAsInt();
         if (Strings.isNumeric(sheet.getId())) {
            id = Long.valueOf(sheet.getId());
         }
         ArtifactId artifact =
            tx.createArtifact(CoreArtifactTypes.OseeTypeDefinition, sheet.getName().replaceFirst("^.*\\.", ""), id);
         tx.setSoleAttributeValue(artifact, CoreAttributeTypes.Active, true);
         tx.setSoleAttributeFromString(artifact, CoreAttributeTypes.UriGeneralStringData, sheet.getTypesSheet());
         tx.addChildren(typesFolder, artifact);
      }
      tx.commit();

      tx = orcsApi.getTransactionFactory().createTransaction(COMMON, SystemUser.OseeSystem,
         "Add OseeTypeDef Tuples to Common Branch");
      for (ArtifactReadable artifact : orcsApi.getQueryFactory().fromBranch(COMMON).andTypeEquals(
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

   public static final String LOAD_OSEE_TYPE_DEF_NAME_AND_ID =
      "select attr.value, attr.art_id, attr.attr_id, attr.attr_type_id from osee_attribute attr, osee_txs txs where txs.BRANCH_ID = ? " + //
         "and attr.gamma_id = txs.gamma_id and txs.TX_CURRENT = 1 and attr.art_id " + //
         "in (select distinct art_id from osee_attribute where attr_id in (ATTR_IDS)) order by attr_type_id desc";

   @Override
   public Response getConfig() {

      OrcsTypesConfig config = new OrcsTypesConfig();
      config.setCurrentVersion(OrcsTypesData.OSEE_TYPE_VERSION.intValue());
      List<Integer> attrIds = new LinkedList<>();

      jdbcService.getClient().runQuery(stmt1 -> {
         int version = stmt1.getInt("e1");
         final OrcsTypesVersion typeVersion = new OrcsTypesVersion();
         config.getVersions().add(typeVersion);
         typeVersion.setVersionNum(version);

         jdbcService.getClient().runQuery(stmt2 -> {
            OrcsTypesSheet sheet = new OrcsTypesSheet();
            sheet.setAttrId(stmt2.getInt("attr_id"));
            attrIds.add(new Long(sheet.getAttrId()).intValue());
            typeVersion.getSheets().add(sheet);
         }, OrcsTypes.LOAD_OSEE_TYPE_DEF_URIS, CoreTupleTypes.OseeTypeDef, CoreBranches.COMMON, TxChange.CURRENT,
            typeVersion.getVersionNum(), TxChange.CURRENT);

      }, OrcsTypes.LOAD_OSEE_TYPE_VERSIONS, CoreTupleTypes.OseeTypeDef.getId());

      String query = LOAD_OSEE_TYPE_DEF_NAME_AND_ID.replace("ATTR_IDS", Collections.toString(",", attrIds));
      jdbcService.getClient().runQuery(stmt -> {
         long attrId = stmt.getLong("attr_id");
         Long attrTypeId = stmt.getLong("attr_type_id");
         long artId = stmt.getLong("art_id");
         if (CoreAttributeTypes.UriGeneralStringData.equals(attrTypeId)) {
            for (OrcsTypesSheet sheet : getSheetsFromAttrId(attrId, config)) {
               sheet.setArtifactId(artId);
            }
         } else if (CoreAttributeTypes.Name.equals(attrTypeId)) {
            for (OrcsTypesSheet sheet : getSheetsFromArtId(artId, config)) {
               sheet.setName(stmt.getString("value"));
            }
         }
      }, query, CoreBranches.COMMON.getId());
      return Response.ok(config).build();
   }

   private Collection<OrcsTypesSheet> getSheetsFromArtId(Long artId, OrcsTypesConfig config) {
      List<OrcsTypesSheet> sheets = new LinkedList<>();
      for (OrcsTypesVersion version : config.getVersions()) {
         for (OrcsTypesSheet sheet : version.getSheets()) {
            if (artId.equals(sheet.getArtifactId())) {
               sheets.add(sheet);
            }
         }
      }
      return sheets;
   }

   private Collection<OrcsTypesSheet> getSheetsFromAttrId(Long attrId, OrcsTypesConfig config) {
      List<OrcsTypesSheet> sheets = new LinkedList<>();
      for (OrcsTypesVersion version : config.getVersions()) {
         for (OrcsTypesSheet sheet : version.getSheets()) {
            if (attrId.equals(sheet.getAttrId())) {
               sheets.add(sheet);
            }
         }
      }
      return sheets;
   }

   @Override
   public Response getConfigSheets() {
      List<OrcsTypesSheet> sheets = new LinkedList<>();
      for (ArtifactReadable art : orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andTypeEquals(
         CoreArtifactTypes.OseeTypeDefinition).getResults()) {
         OrcsTypesSheet sheet = new OrcsTypesSheet();
         sheet.setArtifactId(art.getId());
         sheet.setName(art.getName());
         ResultSet<? extends AttributeReadable<Object>> attributes =
            art.getAttributes(CoreAttributeTypes.UriGeneralStringData);
         if (!attributes.isEmpty()) {
            sheet.setAttrId(attributes.iterator().next().getId());
         }
         sheets.add(sheet);
      }
      return Response.ok(sheets).build();
   }

   @SuppressWarnings("unused")
   @Override
   public Response setConfigSheets(OrcsTypesVersion version) {
      // clear out existing config, if any
      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(COMMON, SystemUser.OseeSystem,
         "Set OSEE Types Configuration");
      long verNum = version.getVersionNum();

      Iterable<Long> attrIds = orcsApi.getQueryFactory().tupleQuery().getTuple2Raw(CoreTupleTypes.OseeTypeDef, COMMON,
         Long.valueOf(version.getVersionNum()));
      for (Long attrId : attrIds) {
         throw new OseeStateException("Configuration already exist for version %s; these need to be manually removed",
            version);
      }

      // add type configuration
      for (OrcsTypesSheet sheet : version.getSheets()) {
         tx.addTuple2(CoreTupleTypes.OseeTypeDef, verNum, AttributeId.valueOf(sheet.getAttrId()));
      }

      tx.commit();
      return Response.ok(version).build();
   }

}
