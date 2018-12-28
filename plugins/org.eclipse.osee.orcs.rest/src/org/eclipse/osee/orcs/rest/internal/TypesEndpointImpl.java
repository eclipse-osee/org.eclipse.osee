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
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.OrcsTypesConfig;
import org.eclipse.osee.framework.core.data.OrcsTypesData;
import org.eclipse.osee.framework.core.data.OrcsTypesSheet;
import org.eclipse.osee.framework.core.data.OrcsTypesVersion;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreTupleTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.enums.TxCurrent;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
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
import org.eclipse.osee.orcs.rest.model.TypesEndpoint;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Roberto E. Escobar
 */
public class TypesEndpointImpl implements TypesEndpoint {
   private final OrcsApi orcsApi;
   private final JdbcService jdbcService;

   public TypesEndpointImpl(OrcsApi orcsApi, JdbcService jdbcService) {
      this.orcsApi = orcsApi;
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
   public Response invalidateCaches() {
      getOrcsTypes().invalidateAll();
      return Response.ok().build();
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
         }, OrcsTypes.LOAD_OSEE_TYPE_DEF_URIS, CoreTupleTypes.OseeTypeDef, CoreBranches.COMMON, TxCurrent.CURRENT,
            typeVersion.getVersionNum(), TxCurrent.CURRENT);

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
