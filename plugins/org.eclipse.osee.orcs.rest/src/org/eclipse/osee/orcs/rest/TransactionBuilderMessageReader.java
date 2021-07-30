/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.orcs.rest;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.transaction.TransactionFactory;

/**
 * Deserializes JSON into TransactionBuilder for REST calls. This provider is programmatically registered via
 * OrcsApplication.start()
 *
 * @author Ryan D. Brooks
 */
public class TransactionBuilderMessageReader implements MessageBodyReader<TransactionBuilder> {
   private final OrcsApi orcsApi;
   private final TransactionFactory txFactory;
   private final JaxRsApi jaxRsApi;
   private final OrcsTokenService tokenService;
   private final QueryFactory query;

   public TransactionBuilderMessageReader(OrcsApi orcsApi) {
      this.txFactory = orcsApi.getTransactionFactory();
      this.jaxRsApi = orcsApi.jaxRsApi();
      this.orcsApi = orcsApi;
      this.tokenService = orcsApi.tokenService();
      this.query = orcsApi.getQueryFactory();
   }

   @Override
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return type == TransactionBuilder.class;
   }

   @Override
   public TransactionBuilder readFrom(Class<TransactionBuilder> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
      JsonNode readTree = jaxRsApi.readTree(Lib.inputStreamToString(entityStream));
      BranchId branch = BranchId.valueOf(readTree.get("branch").asLong());
      Map<String, ArtifactToken> artifactsByName = new HashMap<>();

      String txComment = readTree.get("txComment").asText();
      if (Strings.isInValid(txComment)) {
         txComment = "create transaction REST call";
      }

      TransactionBuilder tx = txFactory.createTransaction(branch, orcsApi.userService().getUser(), txComment);

      createArtifacts(readTree, artifactsByName, tx);
      modifyArtifacts(readTree, artifactsByName, tx);
      deleteArtifacts(readTree, artifactsByName, tx);

      return tx;
   }

   private void createArtifacts(JsonNode root, Map<String, ArtifactToken> artifactsByName, TransactionBuilder tx) {
      if (root.has("createArtifacts")) {
         for (JsonNode artifactJson : root.get("createArtifacts")) {
            ApplicabilityId appId;
            if (artifactJson.has("applicabilityId")) {
               appId = ApplicabilityId.valueOf(artifactJson.get("applicabilityId").asLong());
            } else {
               appId = ApplicabilityId.BASE;
            }

            ArtifactTypeToken artifactType = getArtifactType(artifactJson);
            ArtifactToken artifact = tx.createArtifact(artifactType, artifactJson.get("name").asText(), appId);
            artifactsByName.put(artifact.getName(), artifact);

            readAttributes(tx, artifactJson, artifact, "attributes");
            readrelations(tx, artifactsByName, artifactJson, artifact);
         }
      }
   }

   private void modifyArtifacts(JsonNode root, Map<String, ArtifactToken> artifactsByName, TransactionBuilder tx) {
      if (root.has("modifyArtifacts")) {
         for (JsonNode artifactJson : root.get("modifyArtifacts")) {
            ArtifactToken artifact =
               query.fromBranch(tx.getBranch()).andUuid(artifactJson.get("id").asLong()).asArtifactToken();
            artifactsByName.put(artifact.getName(), artifact);

            if (artifactJson.has("applicabilityId")) {
               tx.setApplicability(artifact, ApplicabilityId.valueOf(artifactJson.get("applicabilityId").asLong()));
            }

            readAttributes(tx, artifactJson, artifact, "setAttributes");
            addAttributes(tx, artifactJson, artifact, "addAttributes");
            deleteAttributes(tx, artifactJson, artifact, "deleteAttributes");
         }
      }
   }

   private void deleteArtifacts(JsonNode root, Map<String, ArtifactToken> artifactsByName, TransactionBuilder tx) {
      if (root.has("deleteArtifacts")) {
         for (JsonNode artifactId : root.get("deleteArtifacts")) {
            tx.deleteArtifact(ArtifactId.valueOf(artifactId.asLong()));
         }
      }
   }

   private void readAttributes(TransactionBuilder tx, JsonNode artifactJson, ArtifactToken artifact, String attributesNodeName) {
      if (artifactJson.has(attributesNodeName)) {
         Iterator<Entry<String, JsonNode>> attributes = artifactJson.get(attributesNodeName).fields();
         while (attributes.hasNext()) {
            Entry<String, JsonNode> attribute = attributes.next();
            AttributeTypeGeneric<?> attributeType = getAttributeType(attribute.getKey());
            JsonNode value = attribute.getValue();
            if (value.isArray()) {
               ArrayList<String> values = new ArrayList<>();
               for (JsonNode attrValue : value) {
                  values.add(attrValue.asText());
               }
               tx.setAttributesFromStrings(artifact, attributeType, values);
            } else {
               tx.setSoleAttributeFromString(artifact, attributeType, value.asText());
            }
         }
      }
   }

   private void addAttributes(TransactionBuilder tx, JsonNode artifactJson, ArtifactToken artifact, String attributesNodeName) {
      if (artifactJson.has(attributesNodeName)) {
         Iterator<Entry<String, JsonNode>> attributes = artifactJson.get(attributesNodeName).fields();
         while (attributes.hasNext()) {
            Entry<String, JsonNode> attribute = attributes.next();
            AttributeTypeGeneric<?> attributeType = getAttributeType(attribute.getKey());
            JsonNode value = attribute.getValue();
            if (value.isArray()) {
               for (JsonNode attrValue : value) {
                  tx.createAttribute(artifact, attributeType, attrValue);
               }
            } else {
               tx.createAttribute(artifact, attributeType, value.asText());
            }
         }
      }
   }

   private void deleteAttributes(TransactionBuilder tx, JsonNode artifactJson, ArtifactToken artifact, String attributesNodeName) {
      if (artifactJson.has(attributesNodeName)) {
         for (JsonNode attributeTypeString : artifactJson.get(attributesNodeName)) {
            tx.deleteAttributes(artifact, getAttributeType(attributeTypeString.asText()));
         }
      }
   }

   private void readrelations(TransactionBuilder tx, Map<String, ArtifactToken> artifactsByName, JsonNode artifactJson, ArtifactToken artifact) {
      if (artifactJson.has("relations")) {
         Iterator<Entry<String, JsonNode>> relations = artifactJson.get("relations").fields();
         while (relations.hasNext()) {
            Entry<String, JsonNode> relation = relations.next();
            RelationTypeToken relationType = getRelationType(relation.getKey());
            JsonNode relationNode = relation.getValue();
            if (relationNode.isTextual() || relationNode.isArray()) {
               relate(tx, artifactsByName, relationNode, relationType, artifact, RelationSide.SIDE_A, "");
            } else if (relationNode.isObject()) {
               String rationale = relationNode.has("rationale") ? relationNode.get("rationale").asText() : "";
               if (relationNode.has("sideA")) {
                  relate(tx, artifactsByName, relationNode.get("sideA"), relationType, artifact, RelationSide.SIDE_B,
                     rationale);
               }
               if (relationNode.has("sideB")) { //both sides are allowed for the same relation entry
                  relate(tx, artifactsByName, relationNode.get("sideB"), relationType, artifact, RelationSide.SIDE_A,
                     rationale);
               }
            } else {
               throw new OseeStateException("Json Node of unexpected type %s", relationNode.getNodeType());
            }
         }
      }
   }

   private void relate(TransactionBuilder tx, Map<String, ArtifactToken> artifactsByName, JsonNode relations, RelationTypeToken relationType, ArtifactToken artifact, RelationSide side, String rationale) {
      if (relations.isTextual()) {
         ArtifactToken otherArtifact = getArtifactByName(tx, artifactsByName, relations.asText());
         relate(tx, relationType, artifact, side, rationale, otherArtifact);
      } else if (relations.isArray()) {
         for (JsonNode name : relations) {
            ArtifactToken otherArtifact = getArtifactByName(tx, artifactsByName, name.asText());
            relate(tx, relationType, artifact, side, rationale, otherArtifact);
         }
      } else {
         throw new OseeStateException("Json Node of unexpected type %s", relations.getNodeType());
      }
   }

   private void relate(TransactionBuilder tx, RelationTypeToken relationType, ArtifactToken artifact, RelationSide side, String rationale, ArtifactToken otherArtifact) {
      if (side.isSideA()) {
         tx.relate(artifact, relationType, otherArtifact, rationale);
      } else {
         tx.relate(otherArtifact, relationType, artifact, rationale);
      }
   }

   private ArtifactToken getArtifactByName(TransactionBuilder tx, Map<String, ArtifactToken> artifactsByName, String name) {
      ArtifactToken artifact = artifactsByName.get(name);
      if (artifact != null) {
         return artifact;
      }
      return query.fromBranch(tx.getBranch()).andNameEquals(name).asArtifactToken();
   }

   private ArtifactTypeToken getArtifactType(JsonNode artifactJson) {
      JsonNode typeNode = artifactJson.get("type");

      if (typeNode == null || Strings.isInValid(typeNode.asText())) {
         throw new OseeArgumentException("The artifact type must be specified");
      }
      String typeString = typeNode.asText();
      if (Strings.isNumeric(typeString)) {
         return tokenService.getArtifactType(Long.valueOf(typeString));
      }
      return tokenService.getArtifactType(typeString);
   }

   private AttributeTypeGeneric<?> getAttributeType(String typeString) {
      if (Strings.isInValid(typeString)) {
         throw new OseeArgumentException("The attribute type must be specified");
      }
      if (Strings.isNumeric(typeString)) {
         return tokenService.getAttributeType(Long.valueOf(typeString));
      }
      return tokenService.getAttributeType(typeString);
   }

   private RelationTypeToken getRelationType(String typeString) {
      if (Strings.isInValid(typeString)) {
         throw new OseeArgumentException("The relation type must be specified");
      }
      if (Strings.isNumeric(typeString)) {
         return tokenService.getRelationType(Long.valueOf(typeString));
      }
      return tokenService.getRelationType(typeString);
   }
}