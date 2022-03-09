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
import java.util.Map;
import java.util.function.Function;
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
import org.eclipse.osee.framework.jdk.core.type.NamedId;
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
   private final TransactionFactory txFactory;
   private final JaxRsApi jaxRsApi;
   private final OrcsTokenService tokenService;
   private final QueryFactory query;

   public TransactionBuilderMessageReader(OrcsApi orcsApi) {
      this.txFactory = orcsApi.getTransactionFactory();
      this.jaxRsApi = orcsApi.jaxRsApi();
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

      TransactionBuilder tx = txFactory.createTransaction(branch, txComment);

      createArtifacts(readTree, artifactsByName, tx);
      modifyArtifacts(readTree, artifactsByName, tx);
      deleteArtifacts(readTree, tx);
      deleteRelations(readTree, tx);
      addRelations(readTree, tx);

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

   private void deleteArtifacts(JsonNode root, TransactionBuilder tx) {
      if (root.has("deleteArtifacts")) {
         for (JsonNode artifactId : root.get("deleteArtifacts")) {
            tx.deleteArtifact(ArtifactId.valueOf(artifactId.asLong()));
         }
      }
   }

   private void deleteRelations(JsonNode root, TransactionBuilder tx) {
      if (root.has("deleteRelations")) {
         for (JsonNode relation : root.get("deleteRelations")) {
            RelationTypeToken relationType = getRelationType(relation);
            ArtifactId artA = ArtifactId.valueOf(relation.get("aArtId").asLong());
            ArtifactId artB = ArtifactId.valueOf(relation.get("bArtId").asLong());
            tx.unrelate(artA, relationType, artB);
         }
      }
   }

   private void addRelations(JsonNode root, TransactionBuilder tx) {
      if (root.has("addRelations")) {
         for (JsonNode relation : root.get("addRelations")) {
            RelationTypeToken relationType = getRelationType(relation);

            String rationale = relation.has("rationale") ? relation.get("rationale").asText() : "";
            ArtifactId relatedArtifact = relation.has("relatedArtifact") ? ArtifactId.valueOf(
               relation.get("relatedArtifact").asLong()) : ArtifactId.SENTINEL;
            String afterArtifact = relation.has("afterArtifact") ? relation.get("afterArtifact").asText() : "end";

            ArtifactId artA = ArtifactId.valueOf(relation.get("aArtId").asLong());
            ArtifactId artB = ArtifactId.valueOf(relation.get("bArtId").asLong());

            if (relationType.isNewRelationTable()) {
               tx.relate(artA, relationType, artB, relatedArtifact, afterArtifact);
            } else {
               tx.relate(artA, relationType, artB, rationale);
            }
         }
      }

   }

   private <R extends NamedId> R getToken(JsonNode node, Function<Long, R> getById, Function<String, R> getByName) {
      JsonNode id = node.get("typeId");
      if (id == null) {
         JsonNode typeNode = node.get("typeName");
         if (typeNode == null || Strings.isInValid(typeNode.asText())) {
            throw new OseeArgumentException("The type must be specified");
         }
         return getByName.apply(typeNode.asText());
      }
      return getById.apply(id.asLong());
   }

   private void readAttributes(TransactionBuilder tx, JsonNode artifactJson, ArtifactToken artifact, String attributesNodeName) {
      if (artifactJson.has(attributesNodeName)) {
         for (JsonNode attribute : artifactJson.get(attributesNodeName)) {
            AttributeTypeGeneric<?> attributeType = getAttributeType(attribute);
            JsonNode value = attribute.get("value");
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
         for (JsonNode attribute : artifactJson.get(attributesNodeName)) {
            AttributeTypeGeneric<?> attributeType = getAttributeType(attribute);
            JsonNode value = attribute.get("value");
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
         for (JsonNode attribute : artifactJson.get(attributesNodeName)) {
            tx.deleteAttributes(artifact, getAttributeType(attribute));
         }
      }
   }

   private void readrelations(TransactionBuilder tx, Map<String, ArtifactToken> artifactsByName, JsonNode artifactJson, ArtifactToken artifact) {
      if (artifactJson.has("relations")) {
         for (JsonNode relation : artifactJson.get("relations")) {
            RelationTypeToken relationType = getRelationType(relation);
            if (relation.isTextual() || relation.isArray()) {
               relate(tx, artifactsByName, relation, relationType, artifact, RelationSide.SIDE_A, "",
                  ArtifactId.SENTINEL, "end");
            } else if (relation.isObject()) {
               String rationale = relation.has("rationale") ? relation.get("rationale").asText() : "";
               ArtifactId relatedArtifact = relation.has("relatedArtifact") ? ArtifactId.valueOf(
                  relation.get("relatedArtifact").asLong()) : ArtifactId.SENTINEL;
               String afterArtifact = relation.has("afterArtifact") ? relation.get("afterArtifact").asText() : "end";

               if (relation.has("sideA")) {
                  relate(tx, artifactsByName, relation.get("sideA"), relationType, artifact, RelationSide.SIDE_B,
                     rationale, relatedArtifact, afterArtifact);
               }
               if (relation.has("sideB")) { //both sides are allowed for the same relation entry
                  relate(tx, artifactsByName, relation.get("sideB"), relationType, artifact, RelationSide.SIDE_A,
                     rationale, relatedArtifact, afterArtifact);
               }
            } else {
               throw new OseeStateException("Json Node of unexpected type %s", relation.getNodeType());
            }
         }
      }
   }

   private void relate(TransactionBuilder tx, Map<String, ArtifactToken> artifactsByName, JsonNode relations, RelationTypeToken relationType, ArtifactToken artifact, RelationSide side, String rationale, ArtifactId relatedArtifact, String afterArtifact) {
      if (relations.isTextual()) {
         relateOne(tx, artifactsByName, relations, relationType, artifact, side, rationale, relatedArtifact,
            afterArtifact);
      } else if (relations.isArray()) {
         for (JsonNode name : relations) {
            relateOne(tx, artifactsByName, name, relationType, artifact, side, rationale, relatedArtifact,
               afterArtifact);
         }
      } else {
         throw new OseeStateException("Json Node of unexpected type %s", relations.getNodeType());
      }
   }

   private void relate(TransactionBuilder tx, RelationTypeToken relationType, ArtifactToken artifact, RelationSide side, String rationale, ArtifactId otherArtifact, ArtifactId relatedArtifact, String afterArtifact) {
      if (side.isSideA()) {
         if (relationType.isNewRelationTable()) {
            tx.relate(artifact, relationType, otherArtifact, relatedArtifact, afterArtifact);
         } else {
            tx.relate(artifact, relationType, otherArtifact, rationale);
         }
      } else {
         if (relationType.isNewRelationTable()) {
            tx.relate(otherArtifact, relationType, artifact, relatedArtifact, afterArtifact);
         } else {

            tx.relate(otherArtifact, relationType, artifact, rationale);
         }
      }
   }

   private void relateOne(TransactionBuilder tx, Map<String, ArtifactToken> artifactsByName, JsonNode relation, RelationTypeToken relationType, ArtifactToken artifact, RelationSide side, String rationale, ArtifactId relatedArtifact, String afterArtifact) {
      ArtifactId otherArtifact;
      if (Strings.isNumeric(relation.asText(""))) {
         otherArtifact = ArtifactId.valueOf(relation.asLong());
      } else {
         otherArtifact = getArtifactByName(tx, artifactsByName, relation.asText());
      }
      relate(tx, relationType, artifact, side, rationale, otherArtifact, relatedArtifact, afterArtifact);
   }

   private ArtifactToken getArtifactByName(TransactionBuilder tx, Map<String, ArtifactToken> artifactsByName, String name) {
      ArtifactToken artifact = artifactsByName.get(name);
      if (artifact != null) {
         return artifact;
      }
      return query.fromBranch(tx.getBranch()).andNameEquals(name).asArtifactToken();
   }

   private ArtifactTypeToken getArtifactType(JsonNode artifactJson) {
      return getToken(artifactJson, tokenService::getArtifactType, tokenService::getArtifactType);
   }

   private AttributeTypeGeneric<?> getAttributeType(JsonNode attribute) {
      return getToken(attribute, tokenService::getAttributeType, tokenService::getAttributeType);
   }

   private RelationTypeToken getRelationType(JsonNode relation) {
      return getToken(relation, tokenService::getRelationType, tokenService::getRelationType);
   }
}