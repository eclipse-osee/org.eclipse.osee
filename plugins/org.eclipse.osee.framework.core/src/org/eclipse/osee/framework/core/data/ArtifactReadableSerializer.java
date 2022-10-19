/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.framework.core.data;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.osee.framework.core.enums.RelationSide;

/**
 * @author Dominic Guss
 */
@SuppressWarnings("serial")
public class ArtifactReadableSerializer extends StdScalarSerializer<@NonNull ArtifactReadable> {

   private final Map<JsonStreamContext, HashSet<ArtifactId>> visitedMap = new HashMap<>();
   private static final HashSet<Integer> artifactHashes = new HashSet<>();

   public ArtifactReadableSerializer() {
      super(ArtifactReadable.class);
   }

   public static void saveHashedEntity(Integer hashedEntity) {
      artifactHashes.add(hashedEntity);
   }

   @Override
   public void serialize(ArtifactReadable artifactReadable, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
      HashSet<ArtifactId> visitedArtifacts;
      JsonStreamContext outputContext = jgen.getOutputContext();
      Integer hashedValue = System.identityHashCode(artifactReadable);

      visitedArtifacts = visitedMap.get(outputContext);
      if (visitedArtifacts == null) {
         visitedArtifacts = new HashSet<>();
         visitedMap.put(outputContext, visitedArtifacts);
      }

      writeArtifact(artifactReadable, jgen, visitedArtifacts);
      // Will only remove from the visitedMap on the last artifactReadable
      if (artifactHashes.remove(hashedValue)) {
         visitedMap.remove(outputContext);
      }
   }

   private void writeArtifact(ArtifactReadable artifactReadable, JsonGenerator jgen, HashSet<ArtifactId> visitedArtifacts) throws IOException {
      jgen.writeStartObject();
      jgen.writeStringField("id", artifactReadable.getIdString());
      jgen.writeStringField("name", artifactReadable.getName());

      if (!visitedArtifacts.contains(artifactReadable)) {
         visitedArtifacts.add(artifactReadable);
         jgen.writeStringField("type", artifactReadable.getArtifactType().getName());
         writeAttributesFields(artifactReadable, jgen);
         writeRelationsFields(artifactReadable, jgen, visitedArtifacts);
      }
      jgen.writeEndObject();
   }

   private void writeAttributesFields(ArtifactReadable artifactReadable, JsonGenerator jgen) throws IOException {
      Collection<AttributeTypeToken> types = artifactReadable.getExistingAttributeTypes();
      if (types.size() < 1) {
         return;
      }

      jgen.writeArrayFieldStart("attributes");
      for (AttributeTypeToken type : types) {
         List<?> values = artifactReadable.getAttributeValues(type);
         for (Object value : values) {
            jgen.writeStartObject();
            jgen.writeStringField(type.getName(), value.toString());
            jgen.writeEndObject();
         }
      }
      jgen.writeEndArray();
   }

   private void writeRelationsFields(ArtifactReadable artifactReadable, JsonGenerator jgen, HashSet<ArtifactId> visitedArtifacts) throws IOException {
      Collection<RelationTypeToken> relationTypes = artifactReadable.getExistingRelationTypes();
      if (relationTypes.size() < 1) {
         return;
      }

      jgen.writeArrayFieldStart("relations");
      for (RelationTypeToken type : relationTypes) {
         writeRelationSideFields(artifactReadable, type, RelationSide.SIDE_A, jgen, visitedArtifacts);
         writeRelationSideFields(artifactReadable, type, RelationSide.SIDE_B, jgen, visitedArtifacts);
      }
      jgen.writeEndArray();
   }

   private void writeRelationSideFields(ArtifactReadable artifactReadable, RelationTypeToken relationType, RelationSide relationSide, JsonGenerator jgen, HashSet<ArtifactId> visitedArtifacts) throws IOException {
      List<ArtifactReadable> related =
         artifactReadable.getRelated(new RelationTypeSide(relationType, relationSide), ArtifactTypeToken.SENTINEL);

      if (related.isEmpty()) {
         return;
      }

      jgen.writeStartObject();
      jgen.writeArrayFieldStart(relationType.getSideName(relationSide));
      for (ArtifactReadable relatedArtifact : related) {
         writeArtifact(relatedArtifact, jgen, visitedArtifacts);
      }
      jgen.writeEndArray();
      jgen.writeEndObject();
   }
}
