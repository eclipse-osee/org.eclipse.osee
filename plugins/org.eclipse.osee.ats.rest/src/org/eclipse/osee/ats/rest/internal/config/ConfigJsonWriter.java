/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.config;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.agile.IAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.country.IAtsCountry;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.insertion.IAtsInsertion;
import org.eclipse.osee.ats.api.insertion.IAtsInsertionActivity;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.SkipAtsConfigJsonWriter;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.WorkItemWriterOptions;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.jaxrs.mvc.IdentityView;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.data.AttributeTypes;

/**
 * @author Donald G. Dunne
 */
@Provider
public class ConfigJsonWriter implements MessageBodyWriter<IAtsConfigObject> {

   private JsonFactory jsonFactory;
   private AtsApi atsApi;
   private OrcsApi orcsApi;

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public void setAtsServer(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   public void start() {
      jsonFactory = JsonUtil.getFactory();
   }

   public void stop() {
      jsonFactory = null;
   }

   @Override
   public long getSize(IAtsConfigObject data, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return -1;
   }

   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      if (JsonUtil.hasAnnotation(SkipAtsConfigJsonWriter.class, annotations)) {
         return false;
      }
      boolean assignableFrom = IAtsConfigObject.class.isAssignableFrom(type);
      return assignableFrom && MediaType.APPLICATION_JSON_TYPE.equals(mediaType);
   }

   private AttributeTypes getAttributeTypes() {
      return orcsApi.getOrcsTypes().getAttributeTypes();
   }

   @Override
   public void writeTo(IAtsConfigObject config, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
      JsonGenerator writer = null;
      try {
         writer = jsonFactory.createGenerator(entityStream);
         writer.writeStartArray();
         addProgramObject(atsApi, orcsApi, config, annotations, writer,
            JsonUtil.hasAnnotation(IdentityView.class, annotations), getAttributeTypes());
         writer.writeEndArray();
      } finally {
         if (writer != null) {
            writer.flush();
         }
      }
   }

   public static void addProgramObject(AtsApi atsApi, OrcsApi orcsApi, IAtsObject atsObject, Annotation[] annotations, JsonGenerator writer, boolean identityView, AttributeTypes attributeTypes) throws IOException, JsonGenerationException, JsonProcessingException {
      ArtifactReadable artifact = (ArtifactReadable) atsApi.getQueryService().getArtifact(atsObject);
      writer.writeStartObject();
      writer.writeNumberField("id", atsObject.getId());
      writer.writeStringField("name", atsObject.getName());
      writer.writeStringField("Description", atsObject.getDescription());

      if (atsObject instanceof IAtsTeamDefinition) {
         if (!identityView) {
            writer.writeArrayFieldStart("version");
            for (ArtifactReadable verArt : artifact.getRelated(AtsRelationTypes.TeamDefinitionToVersion_Version)) {
               IAtsVersion version = atsApi.getVersionService().getVersion(verArt);
               addProgramObject(atsApi, orcsApi, version, annotations, writer, true, attributeTypes);
            }
            writer.writeEndArray();
         }
      }
      if (atsObject instanceof IAtsVersion) {
         if (!identityView) {
            writer.writeArrayFieldStart("workflow");
            for (ArtifactReadable workArt : artifact.getRelated(
               AtsRelationTypes.TeamWorkflowTargetedForVersion_TeamWorkflow)) {
               addArtifactIdentity(writer, workArt);
            }
            writer.writeEndArray();
         }
      } else if (atsObject instanceof IAtsInsertionActivity) {
         IAtsInsertionActivity activity = (IAtsInsertionActivity) atsObject;
         writer.writeBooleanField("Active", activity.isActive());
         if (!identityView) {
            writer.writeArrayFieldStart("insertion");
            for (ArtifactReadable insertion : artifact.getRelated(
               AtsRelationTypes.InsertionToInsertionActivity_Insertion)) {
               addArtifactIdentity(writer, insertion);
            }
            writer.writeEndArray();
            writer.writeArrayFieldStart("workpackage");
            for (ArtifactReadable workPackage : artifact.getRelated(
               AtsRelationTypes.InsertionActivityToWorkPackage_WorkPackage)) {
               addArtifactIdentity(writer, workPackage);
            }
            writer.writeEndArray();
         }
      } else if (atsObject instanceof IAtsInsertion) {
         IAtsInsertion insertion = (IAtsInsertion) atsObject;
         writer.writeBooleanField("Active", insertion.isActive());
         if (!identityView) {
            writer.writeArrayFieldStart("program");
            for (ArtifactReadable program : artifact.getRelated(AtsRelationTypes.ProgramToInsertion_Program)) {
               addArtifactIdentity(writer, program);
            }
            writer.writeEndArray();
            writer.writeArrayFieldStart("insertionactivity");
            for (ArtifactReadable activity : artifact.getRelated(
               AtsRelationTypes.InsertionToInsertionActivity_InsertionActivity)) {
               addArtifactIdentity(writer, activity);
            }
            writer.writeEndArray();
         }
      } else if (atsObject instanceof IAtsProgram) {
         IAtsProgram program = (IAtsProgram) atsObject;
         writer.writeStringField("Namespace", atsApi.getProgramService().getNamespace(program));
         writer.writeBooleanField("Active", program.isActive());
         if (!identityView) {
            writer.writeArrayFieldStart("country");
            for (ArtifactReadable country : artifact.getRelated(AtsRelationTypes.CountryToProgram_Country)) {
               addArtifactIdentity(writer, country);
            }
            writer.writeEndArray();
            writer.writeArrayFieldStart("insertion");
            for (ArtifactReadable insertion : artifact.getRelated(AtsRelationTypes.ProgramToInsertion_Insertion)) {
               addArtifactIdentity(writer, insertion);
            }
            writer.writeEndArray();
         }
      } else if (atsObject instanceof IAtsCountry) {
         IAtsCountry country = (IAtsCountry) atsObject;
         if (!identityView) {
            writer.writeArrayFieldStart("programs");
            Collection<IAtsProgram> programs = atsApi.getProgramService().getPrograms(country);
            for (IAtsProgram program : programs) {
               writer.writeStartObject();
               writer.writeNumberField("id", program.getId());
               writer.writeStringField("name", program.getName());
               writer.writeBooleanField("active", program.isActive());
               writer.writeEndObject();
            }
            writer.writeEndArray();
         }
      } else if (atsObject instanceof IAgileTeam) {
         IAgileTeam team = (IAgileTeam) atsObject;
         writer.writeBooleanField("Active", team.isActive());
         writer.writeStringField("Description", team.getDescription());
         writer.writeArrayFieldStart("featureGroups");
         Collection<IAgileFeatureGroup> featureGroups = atsApi.getAgileService().getAgileFeatureGroups(team);
         for (IAgileFeatureGroup group : featureGroups) {
            writer.writeStartObject();
            writer.writeNumberField("id", group.getId());
            writer.writeStringField("name", group.getName());
            writer.writeBooleanField("active", group.isActive());
            writer.writeEndObject();
         }
         writer.writeEndArray();
         writer.writeArrayFieldStart("sprints");
         Collection<IAgileSprint> agileSprints = atsApi.getAgileService().getAgileSprints(team);
         for (IAgileSprint sprint : agileSprints) {
            writer.writeStartObject();
            writer.writeNumberField("id", sprint.getId());
            writer.writeStringField("name", sprint.getName());
            writer.writeBooleanField("active", sprint.isActive());
            writer.writeEndObject();
         }
         writer.writeEndArray();
         ArtifactReadable teamArt = (ArtifactReadable) atsApi.getQueryService().getArtifact(team);
         ArtifactReadable backlogArt =
            teamArt.getRelated(AtsRelationTypes.AgileTeamToBacklog_Backlog).getAtMostOneOrDefault(
               ArtifactReadable.SENTINEL);
         writer.writeStringField("Backlog Id", backlogArt.isValid() ? backlogArt.getIdString() : "");
         writer.writeStringField("Backlog", backlogArt.isValid() ? backlogArt.getName() : "");
      }
      if (!identityView) {
         addAttributeData(writer, attributeTypes, artifact, Collections.emptyList(), atsApi, orcsApi);
      }
      writer.writeEndObject();
   }

   public static void addAttributeData(JsonGenerator writer, AttributeTypes attributeTypes, ArtifactReadable artifact, List<WorkItemWriterOptions> options, AtsApi atsApi, OrcsApi orcsApi) throws IOException, JsonGenerationException, JsonProcessingException {
      Collection<AttributeTypeToken> attrTypes = attributeTypes.getAll();
      ResultSet<? extends AttributeReadable<Object>> attributes = artifact.getAttributes();
      boolean fieldsAsIds = options.contains(WorkItemWriterOptions.KeysAsIds);
      boolean datesAsLong = options.contains(WorkItemWriterOptions.DatesAsLong);
      if (!attributes.isEmpty()) {
         for (AttributeTypeToken attrType : attrTypes) {
            boolean isDateType = orcsApi.getOrcsTypes().getAttributeTypes().isDateType(attrType);
            if (artifact.isAttributeTypeValid(attrType)) {
               List<Object> attributeValues = artifact.getAttributeValues(attrType);
               if (!attributeValues.isEmpty()) {

                  if (attributeValues.size() > 1) {
                     if (fieldsAsIds) {
                        writer.writeArrayFieldStart(attrType.getIdString());
                     } else {
                        writer.writeArrayFieldStart(attrType.getName());
                     }
                     for (Object value : attributeValues) {
                        writeObjectValue(writer, datesAsLong, isDateType, value);
                     }
                     writer.writeEndArray();
                  } else if (attributeValues.size() == 1) {

                     String field = fieldsAsIds ? attrType.getIdString() : attrType.getName();
                     writer.writeFieldName(field);

                     Object value = attributeValues.iterator().next();
                     writeObjectValue(writer, datesAsLong, isDateType, value);
                  }

               }
            }
         }
      }
   }

   public static void addAttributeDataWithIds(JsonGenerator writer, ArtifactReadable artifact, List<WorkItemWriterOptions> options, AtsApi atsApi, OrcsApi orcsApi) throws IOException, JsonGenerationException, JsonProcessingException {

      ResultSet<? extends AttributeReadable<Object>> attributes = artifact.getAttributes();
      boolean keysAsIds = options.contains(WorkItemWriterOptions.KeysAsIds);
      boolean datesAsLong = options.contains(WorkItemWriterOptions.DatesAsLong);

      if (!attributes.isEmpty()) {
         List<Long> writtenTypes = new LinkedList<>();
         HashCollection<String, AttributeReadable<Object>> attrIdToAttrsMap = getAttributeMap(attributes);
         for (String attrId : attrIdToAttrsMap.keySet()) {
            List<AttributeReadable<Object>> attributeValues = attrIdToAttrsMap.getValues(attrId);
            AttributeTypeToken attrType = attributeValues.iterator().next().getAttributeType();
            if (!writtenTypes.contains(attrType.getId())) {
               writtenTypes.add(attrType.getId());
               boolean isDateType = orcsApi.getOrcsTypes().getAttributeTypes().isDateType(attrType);

               if (attributeValues.size() > 1) {
                  if (keysAsIds) {
                     writer.writeArrayFieldStart(attrType.getIdString());
                  } else {
                     writer.writeArrayFieldStart(attrType.getName());
                  }
                  for (AttributeReadable<Object> attr : attributeValues) {
                     writer.writeStartObject();
                     writeObjectValueField(writer, datesAsLong, isDateType, attr.getDisplayableString());
                     writer.writeNumberField("attrId", attr.getId());
                     writer.writeNumberField("gammaId", attr.getGammaId().getId());
                     writer.writeEndObject();
                  }
                  writer.writeEndArray();
               } else if (attributeValues.size() == 1) {
                  AttributeReadable<Object> attr = attributeValues.iterator().next();
                  if (keysAsIds) {
                     writer.writeObjectFieldStart(attrType.getIdString());
                  } else {
                     writer.writeObjectFieldStart(attrType.getName());
                  }
                  writeObjectValueField(writer, datesAsLong, isDateType, attr.getValue());
                  writer.writeNumberField("attrId", attr.getId());
                  writer.writeNumberField("gammaId", attr.getGammaId().getId());
                  writer.writeEndObject();
               }
            }
         }
      }
   }

   private static HashCollection<String, AttributeReadable<Object>> getAttributeMap(ResultSet<? extends AttributeReadable<Object>> attributes) {
      HashCollection<String, AttributeReadable<Object>> attrIdToAttrsMap = new HashCollection<>();
      for (AttributeReadable<Object> attr : attributes) {
         attrIdToAttrsMap.put(attr.getAttributeType().getIdString(), attr);
      }
      return attrIdToAttrsMap;
   }

   private static void writeObjectValue(JsonGenerator writer, boolean datesAsLong, boolean isDateType, Object value) throws IOException, JsonGenerationException, JsonProcessingException {
      if (isDateType) {
         if (datesAsLong) {
            writer.writeString(String.valueOf(((Date) value).getTime()));
         } else {
            writer.writeString(DateUtil.getMMDDYY((Date) value));
         }
      } else {
         writer.writeObject(value);
      }
   }

   private static void writeObjectValueField(JsonGenerator writer, boolean datesAsLong, boolean isDateType, Object value) throws IOException, JsonGenerationException, JsonProcessingException {
      if (isDateType) {
         if (datesAsLong) {
            writer.writeStringField("value", String.valueOf(((Date) value).getTime()));
         } else {
            writer.writeStringField("value", DateUtil.getMMDDYY((Date) value));
         }
      } else {
         writer.writeObjectField("value", value);
      }
   }

   private static void addArtifactIdentity(JsonGenerator writer, ArtifactReadable workArt) throws IOException, JsonGenerationException, JsonProcessingException {
      writer.writeStartObject();
      writer.writeNumberField("id", workArt.getId());
      writer.writeStringField("name", workArt.getName());
      writer.writeEndObject();
   }
}
