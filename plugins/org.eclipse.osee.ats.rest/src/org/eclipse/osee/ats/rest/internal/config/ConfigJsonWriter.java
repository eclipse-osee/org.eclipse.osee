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
import org.eclipse.osee.ats.api.agile.AgileUtil;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.framework.core.data.IAttributeType;
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
      jsonFactory = org.eclipse.osee.ats.rest.internal.util.JsonFactory.create();
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
      return atsServer.getOrcsApi().getOrcsTypes(null).getAttributeTypes();
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
      ArtifactReadable artifact = (ArtifactReadable) atsObject.getStoreObject();
      writer.writeStartObject();
      writer.writeNumberField("uuid", getUuid(atsObject));
      writer.writeStringField("Name", atsObject.getName());
      writer.writeStringField("Description", atsObject.getDescription());

      if (atsObject instanceof IAtsTeamDefinition) {
         if (!identityView) {
            writer.writeArrayFieldStart("version");
            for (ArtifactReadable verArt : artifact.getRelated(AtsRelationTypes.TeamDefinitionToVersion_Version)) {
               IAtsVersion version = atsServer.getConfig().getSoleByGuid(verArt.getGuid(), IAtsVersion.class);
               addProgramObject(atsServer, version, annotations, writer, true, attributeTypes);
            }
            writer.writeEndArray();
         }
      }
      if (atsObject instanceof IAtsVersion) {
         if (!identityView) {
            writer.writeArrayFieldStart("workflow");
            for (ArtifactReadable workArt : artifact.getRelated(AtsRelationTypes.TeamWorkflowTargetedForVersion_Workflow)) {
               addArtifactIdentity(writer, workArt);
            }
            writer.writeEndArray();
         }
      } else if (atsObject instanceof IAtsProgram) {
         IAtsProgram program = (IAtsProgram) atsObject;
         writer.writeStringField("Namespace", program.getNamespace());
         writer.writeBooleanField("Active", program.isActive());
      } else if (atsObject instanceof IAgileTeam) {
         IAgileTeam program = (IAgileTeam) atsObject;
         writer.writeBooleanField("Active", program.isActive());
         writer.writeArrayFieldStart("featureGroups");
         for (ArtifactReadable child : artifact.getChildren()) {
            if (child.getName().equals(AgileUtil.FEATURE_GROUP_FOLDER_NAME)) {
               for (ArtifactReadable subChild : child.getChildren()) {
                  if (subChild.isOfType(AtsArtifactTypes.AgileFeatureGroup)) {
                     addArtifactIdentity(writer, subChild);
                  }
               }
            }
         }
         writer.writeEndArray();

      }
      if (!identityView) {
         addAttributeData(writer, attributeTypes, artifact);
      }
      writer.writeEndObject();
   }

   public static void addAttributeData(JsonGenerator writer, AttributeTypes attributeTypes, ArtifactReadable artifact) throws IOException, JsonGenerationException, JsonProcessingException {
      Collection<? extends IAttributeType> attrTypes = attributeTypes.getAll();
      ResultSet<? extends AttributeReadable<Object>> attributes = artifact.getAttributes();
      if (!attributes.isEmpty()) {
         for (IAttributeType attrType : attrTypes) {
            if (artifact.isAttributeTypeValid(attrType)) {
               List<Object> attributeValues = artifact.getAttributeValues(attrType);
               if (!attributeValues.isEmpty()) {

                  if (attributeValues.size() > 1) {
                     writer.writeArrayFieldStart(attrType.getName());
                     for (Object value : attributeValues) {
                        writer.writeObject(value);
                     }
                     writer.writeEndArray();
                  } else if (attributeValues.size() == 1) {
                     Object value = attributeValues.iterator().next();
                     writer.writeObjectField(attrType.getName(), value);
                  }

               }
            }
         }
      }
   }

   private static void addArtifactIdentity(JsonGenerator writer, ArtifactReadable workArt) throws IOException, JsonGenerationException, JsonProcessingException {
      writer.writeStartObject();
      writer.writeNumberField("uuid", workArt.getLocalId());
      writer.writeStringField("Name", workArt.getName());
      writer.writeEndObject();
   }

   public static Long getUuid(IAtsObject atsObject) {
      return ((ArtifactReadable) atsObject.getStoreObject()).getLocalId().longValue();
   }
}