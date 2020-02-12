/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.workitem;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.WorkItemWriterOptions;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.ats.rest.internal.config.ConfigJsonWriter;
import org.eclipse.osee.ats.rest.internal.util.ActionPage;
import org.eclipse.osee.ats.rest.internal.util.TargetedVersion;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.IRelationLink;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jaxrs.mvc.IdentityView;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.data.AttributeTypes;

/**
 * @author Donald G. Dunne
 */
@Provider
public class WorkItemJsonWriter implements MessageBodyWriter<IAtsWorkItem> {

   private static final String ATS_UI_ACTION_PREFIX = "/ats/ui/action/ID";
   private JsonFactory jsonFactory;
   private AtsApi atsApi;
   private OrcsApi orcsApi;

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public void setAtsServer(IAtsServer atsServer) {
      this.atsApi = atsServer;
   }

   public void start() {
      jsonFactory = JsonUtil.getFactory();
   }

   public void stop() {
      jsonFactory = null;
   }

   @Override
   public long getSize(IAtsWorkItem data, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return -1;
   }

   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      boolean assignableFrom = IAtsWorkItem.class.isAssignableFrom(type);
      return assignableFrom && MediaType.APPLICATION_JSON_TYPE.equals(mediaType);
   }

   private static boolean matches(Class<? extends Annotation> toMatch, Annotation[] annotations) {
      for (Annotation annotation : annotations) {
         if (annotation.annotationType().isAssignableFrom(toMatch)) {
            return true;
         }
      }
      return false;
   }

   private AttributeTypes getAttributeTypes() {
      return orcsApi.getOrcsTypes().getAttributeTypes();
   }

   @Override
   public void writeTo(IAtsWorkItem config, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
      JsonGenerator writer = null;
      try {
         writer = jsonFactory.createGenerator(entityStream);
         addWorkItem(atsApi, orcsApi, config, annotations, writer, matches(IdentityView.class, annotations),
            getAttributeTypes(), Collections.emptyList());
      } finally {
         if (writer != null) {
            writer.flush();
         }
      }
   }

   protected static void addWorkItem(AtsApi atsApi, OrcsApi orcsApi, IAtsWorkItem workItem, Annotation[] annotations, JsonGenerator writer, boolean identityView, AttributeTypes attributeTypes, List<WorkItemWriterOptions> options) throws IOException, JsonGenerationException, JsonProcessingException {

      ArtifactReadable workItemArt = (ArtifactReadable) workItem.getStoreObject();
      writer.writeStartObject();
      writer.writeNumberField("id", workItem.getId());
      writer.writeStringField("Name", workItem.getName());
      String atsId = workItemArt.getSoleAttributeValue(AtsAttributeTypes.AtsId, "");
      writer.writeStringField("AtsId", atsId);
      writer.writeStringField("ArtifactType", workItemArt.getArtifactType().getName());
      String actionUrl = AtsUtil.getActionUrl(atsId, ATS_UI_ACTION_PREFIX, atsApi);
      writer.writeStringField("actionLocation", actionUrl);
      if (!identityView) {
         ConfigJsonWriter.addAttributeData(writer, attributeTypes, workItemArt, options, atsApi, orcsApi);
         writer.writeStringField("TeamName", ActionPage.getTeamStr(atsApi, workItemArt));
         writer.writeStringField("Assignees", workItem.getStateMgr().getAssigneesStr());
         if (options.contains(WorkItemWriterOptions.WriteRelatedAsTokens)) {
            writer.writeArrayFieldStart("AssigneesTokens");
            for (IAtsUser assignee : workItem.getStateMgr().getAssignees()) {
               writer.writeStartObject();
               writer.writeStringField("id", assignee.getIdString());
               writer.writeStringField("name", assignee.getName());
               writer.writeEndObject();
            }
            writer.writeEndArray();
         }
         writer.writeStringField("ChangeType", workItemArt.getSoleAttributeAsString(AtsAttributeTypes.ChangeType, ""));
         writer.writeStringField("Priority", workItemArt.getSoleAttributeAsString(AtsAttributeTypes.Priority, ""));
         writer.writeStringField("State", workItem.getStateMgr().getCurrentStateName());
         if (options.contains(WorkItemWriterOptions.DatesAsLong)) {
            writer.writeStringField("CreatedDate", String.valueOf(workItem.getCreatedDate().getTime()));
         } else {
            writer.writeStringField("CreatedDate", DateUtil.get(workItem.getCreatedDate(), DateUtil.MMDDYY));
         }
         writer.writeStringField("CreatedBy", workItem.getCreatedBy().getName());
      }
      if (!identityView || matches(TargetedVersion.class, annotations)) {
         IAtsTeamWorkflow teamWf = workItem.getParentTeamWorkflow();
         if (teamWf != null) {
            IAtsVersion version = atsApi.getVersionService().getTargetedVersion(teamWf);
            writer.writeStringField("TargetedVersion", version == null ? "" : version.getName());
            if (options.contains(WorkItemWriterOptions.WriteRelatedAsTokens)) {
               writer.writeObjectFieldStart("TargetedVersionToken");
               writer.writeStringField("id", version == null ? "" : version.getIdString());
               writer.writeStringField("name", version == null ? "" : version.getName());
               writer.writeEndObject();
            }
         }
      }
      writer.writeEndObject();
   }

   protected static void addWorkItemWithIds(AtsApi atsApi, OrcsApi orcsApi, IAtsWorkItem workItem, Annotation[] annotations, JsonGenerator writer, boolean identityView, List<WorkItemWriterOptions> options) throws IOException, JsonGenerationException, JsonProcessingException {
      ArtifactReadable workItemArt = (ArtifactReadable) workItem.getStoreObject();
      writer.writeStartObject();
      writer.writeNumberField("id", workItem.getId());
      String atsId = workItemArt.getSoleAttributeValue(AtsAttributeTypes.AtsId, "");
      writer.writeStringField("AtsId", atsId);
      writer.writeStringField("ArtifactType", workItemArt.getArtifactType().getName());
      String actionUrl = AtsUtil.getActionUrl(atsId, ATS_UI_ACTION_PREFIX, atsApi);
      writer.writeStringField("actionLocation", actionUrl);
      if (!identityView) {
         ConfigJsonWriter.addAttributeDataWithIds(writer, workItemArt, options, atsApi, orcsApi);
         writer.writeStringField("TeamName", ActionPage.getTeamStr(atsApi, workItemArt));
         writeAssignees(writer, workItemArt, workItem);
         writeType(writer, workItemArt, workItem, "ChangeType", AtsAttributeTypes.ChangeType);
         writeType(writer, workItemArt, workItem, "Priority", AtsAttributeTypes.Priority);
         writeState(writer, workItemArt, workItem);
         if (options.contains(WorkItemWriterOptions.DatesAsLong)) {
            writer.writeStringField("CreatedDate", String.valueOf(workItem.getCreatedDate().getTime()));
         } else {
            writer.writeStringField("CreatedDate", DateUtil.get(workItem.getCreatedDate(), DateUtil.MMDDYY));
         }
         writer.writeStringField("CreatedBy", workItem.getCreatedBy().getName());
         writeTargetedVersion(atsApi, writer, workItemArt, workItem, options);
      }
      writer.writeEndObject();
   }

   private static void writeAssignees(JsonGenerator writer, ArtifactReadable action, IAtsWorkItem workItem) throws IOException, JsonGenerationException, JsonProcessingException {
      AttributeReadable<Object> attr =
         action.getAttributeById(AttributeId.valueOf(action.getSoleAttributeId(AtsAttributeTypes.CurrentState)));
      writer.writeArrayFieldStart("AssigneesTokens");
      for (IAtsUser assignee : workItem.getStateMgr().getAssignees()) {
         writer.writeStartObject();
         writer.writeStringField("id", assignee.getIdString());
         writer.writeStringField("name", assignee.getName());
         writer.writeNumberField("gammaId", attr.getGammaId().getId());
         writer.writeEndObject();
      }
      writer.writeEndArray();
   }

   private static void writeType(JsonGenerator writer, ArtifactReadable action, IAtsWorkItem workItem, String fieldName, AttributeTypeToken attrType) throws IOException, JsonGenerationException, JsonProcessingException {
      writer.writeObjectFieldStart(fieldName);
      String attrValue = action.getSoleAttributeAsString(attrType, "");
      GammaId gammaId = GammaId.SENTINEL;
      if (Strings.isValid(attrValue)) {
         AttributeReadable<Object> attr =
            action.getAttributeById(AttributeId.valueOf(action.getSoleAttributeId(attrType)));
         gammaId = attr.getGammaId();
      }
      writer.writeObjectField("value", attrValue);
      writer.writeNumberField("gammaId", gammaId.getId());
      writer.writeEndObject();
   }

   private static void writeState(JsonGenerator writer, ArtifactReadable action, IAtsWorkItem workItem) throws IOException, JsonGenerationException, JsonProcessingException {
      writer.writeObjectFieldStart("State");
      AttributeReadable<Object> attr =
         action.getAttributeById(AttributeId.valueOf(action.getSoleAttributeId(AtsAttributeTypes.CurrentState)));
      writer.writeObjectField("value", workItem.getStateMgr().getCurrentStateName());
      writer.writeNumberField("gammaId", attr.getGammaId().getId());
      writer.writeEndObject();
   }

   private static void writeTargetedVersion(AtsApi atsApi, JsonGenerator writer, ArtifactReadable action, IAtsWorkItem workItem, List<WorkItemWriterOptions> options) throws IOException, JsonGenerationException, JsonProcessingException {
      IAtsTeamWorkflow teamWf = workItem.getParentTeamWorkflow();
      if (teamWf != null) {
         ResultSet<IRelationLink> relations =
            action.getRelations(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version);
         if (!relations.isEmpty()) {
            writer.writeObjectFieldStart("TargetedVersion");
            String versionStr = atsApi.getWorkItemService().getTargetedVersionStr(teamWf);
            writer.writeObjectField("value", versionStr);
            writer.writeNumberField("gammaId", relations.iterator().next().getGammaId().getId());
            writer.writeEndObject();
            if (options.contains(WorkItemWriterOptions.WriteRelatedAsTokens)) {
               ArtifactToken version = atsApi.getRelationResolver().getRelatedOrNull(action,
                  AtsRelationTypes.TeamWorkflowTargetedForVersion_Version);
               writer.writeObjectFieldStart("TargetedVersionToken");
               writer.writeStringField("id", String.valueOf(relations.iterator().next().getArtIdB()));
               writer.writeNumberField("gammaId", relations.iterator().next().getGammaId().getId());
               writer.writeStringField("name", version.getName());
               writer.writeEndObject();
            }
         }
      }
   }
}