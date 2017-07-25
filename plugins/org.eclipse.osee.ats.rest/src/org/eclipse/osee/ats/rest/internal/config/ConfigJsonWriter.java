package org.eclipse.osee.ats.rest.internal.config;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.agile.IAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.country.IAtsCountry;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.insertion.IAtsInsertion;
import org.eclipse.osee.ats.api.insertion.IAtsInsertionActivity;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.jaxrs.mvc.IdentityView;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.data.AttributeTypes;

/**
 * @author Donald G. Dunne
 */
@Provider
public class ConfigJsonWriter implements MessageBodyWriter<IAtsConfigObject> {

   private JsonFactory jsonFactory;
   private IAtsServer atsServer;

   public void setAtsServer(IAtsServer atsServer) {
      this.atsServer = atsServer;
   }

   public void start() {
      jsonFactory = org.eclipse.osee.ats.rest.util.JsonFactory.create();
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
      boolean assignableFrom = IAtsConfigObject.class.isAssignableFrom(type);
      return assignableFrom && MediaType.APPLICATION_JSON_TYPE.equals(mediaType);
   }

   private boolean matches(Class<? extends Annotation> toMatch, Annotation[] annotations) {
      for (Annotation annotation : annotations) {
         if (annotation.annotationType().isAssignableFrom(toMatch)) {
            return true;
         }
      }
      return false;
   }

   private AttributeTypes getAttributeTypes() {
      return atsServer.getOrcsApi().getOrcsTypes().getAttributeTypes();
   }

   @Override
   public void writeTo(IAtsConfigObject config, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
      JsonGenerator writer = null;
      try {
         writer = jsonFactory.createJsonGenerator(entityStream);
         writer.writeStartArray();
         addProgramObject(atsServer, config, annotations, writer, matches(IdentityView.class, annotations),
            getAttributeTypes());
         writer.writeEndArray();
      } finally {
         if (writer != null) {
            writer.flush();
         }
      }
   }

   public static void addProgramObject(IAtsServer atsServer, IAtsObject atsObject, Annotation[] annotations, JsonGenerator writer, boolean identityView, AttributeTypes attributeTypes) throws IOException, JsonGenerationException, JsonProcessingException {
      ArtifactReadable artifact = atsServer.getArtifact(atsObject);
      writer.writeStartObject();
      writer.writeNumberField("uuid", getUuid(atsObject, atsServer));
      writer.writeStringField("Name", atsObject.getName());
      writer.writeStringField("Description", atsObject.getDescription());

      if (atsObject instanceof IAtsTeamDefinition) {
         if (!identityView) {
            writer.writeArrayFieldStart("version");
            for (ArtifactReadable verArt : artifact.getRelated(AtsRelationTypes.TeamDefinitionToVersion_Version)) {
               IAtsVersion version = atsServer.getConfigItemFactory().getVersion(verArt);
               addProgramObject(atsServer, version, annotations, writer, true, attributeTypes);
            }
            writer.writeEndArray();
         }
      }
      if (atsObject instanceof IAtsVersion) {
         if (!identityView) {
            writer.writeArrayFieldStart("workflow");
            for (ArtifactReadable workArt : artifact.getRelated(
               AtsRelationTypes.TeamWorkflowTargetedForVersion_Workflow)) {
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
         writer.writeStringField("Namespace", atsServer.getProgramService().getNamespace(program));
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
            Collection<IAtsProgram> programs = atsServer.getProgramService().getPrograms(country);
            for (IAtsProgram program : programs) {
               writer.writeStartObject();
               writer.writeNumberField("uuid", program.getId());
               writer.writeStringField("Name", program.getName());
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
         Collection<IAgileFeatureGroup> featureGroups = atsServer.getAgileService().getAgileFeatureGroups(team);
         for (IAgileFeatureGroup group : featureGroups) {
            writer.writeStartObject();
            writer.writeNumberField("uuid", group.getId());
            writer.writeStringField("Name", group.getName());
            writer.writeBooleanField("active", group.isActive());
            writer.writeEndObject();
         }
         writer.writeEndArray();
         writer.writeArrayFieldStart("sprints");
         Collection<IAgileSprint> agileSprints = atsServer.getAgileService().getAgileSprints(team);
         for (IAgileSprint sprint : agileSprints) {
            writer.writeStartObject();
            writer.writeNumberField("uuid", sprint.getId());
            writer.writeStringField("Name", sprint.getName());
            writer.writeBooleanField("active", sprint.isActive());
            writer.writeEndObject();
         }
         writer.writeEndArray();
         ArtifactReadable teamArt = atsServer.getArtifact(team);
         ArtifactReadable backlogArt =
            teamArt.getRelated(AtsRelationTypes.AgileTeamToBacklog_Backlog).getAtMostOneOrNull();
         writer.writeStringField("Backlog Uuid", backlogArt != null ? String.valueOf(backlogArt.getId()) : "");
         writer.writeStringField("Backlog", backlogArt != null ? String.valueOf(backlogArt.getName()) : "");
      }
      if (!identityView) {
         addAttributeData(writer, attributeTypes, artifact, false);
      }
      writer.writeEndObject();
   }

   public static void addAttributeData(JsonGenerator writer, AttributeTypes attributeTypes, ArtifactReadable artifact, boolean fieldsAsIds) throws IOException, JsonGenerationException, JsonProcessingException {
      Collection<AttributeTypeToken> attrTypes = attributeTypes.getAll();
      ResultSet<? extends AttributeReadable<Object>> attributes = artifact.getAttributes();
      if (!attributes.isEmpty()) {
         for (AttributeTypeToken attrType : attrTypes) {
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
                        writer.writeObject(value);
                     }
                     writer.writeEndArray();
                  } else if (attributeValues.size() == 1) {
                     Object value = attributeValues.iterator().next();
                     if (fieldsAsIds) {
                        writer.writeObjectField(attrType.getIdString(), value);
                     } else {
                        writer.writeObjectField(attrType.getName(), value);
                     }
                  }

               }
            }
         }
      }
   }

   private static void addArtifactIdentity(JsonGenerator writer, ArtifactReadable workArt) throws IOException, JsonGenerationException, JsonProcessingException {
      writer.writeStartObject();
      writer.writeNumberField("uuid", workArt.getId());
      writer.writeStringField("Name", workArt.getName());
      writer.writeEndObject();
   }

   public static Long getUuid(IAtsObject atsObject, IAtsServices services) {
      long uuid = atsObject.getId();
      if (uuid <= 0L) {
         uuid = ((ArtifactReadable) services.getArtifact(atsObject)).getId();
      }
      return uuid;
   }
}