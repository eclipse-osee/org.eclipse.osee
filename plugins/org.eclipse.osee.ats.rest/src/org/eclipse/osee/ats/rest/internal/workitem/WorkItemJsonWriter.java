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
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.WorkItemWriterOptions;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.ats.rest.internal.config.ConfigJsonWriter;
import org.eclipse.osee.ats.rest.internal.util.ActionPage;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.jaxrs.mvc.IdentityView;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeTypes;

/**
 * @author Donald G. Dunne
 */
@Provider
public class WorkItemJsonWriter implements MessageBodyWriter<IAtsWorkItem> {

   private JsonFactory jsonFactory;
   private IAtsServer atsServer;
   private static final String ATS_UI_ACTION_PREFIX = "/ats/ui/action/ID";

   public void setAtsServer(IAtsServer atsServer) {
      this.atsServer = atsServer;
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
   public void writeTo(IAtsWorkItem config, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
      JsonGenerator writer = null;
      try {
         writer = jsonFactory.createJsonGenerator(entityStream);
         addWorkItem(atsServer, config, annotations, writer, matches(IdentityView.class, annotations),
            getAttributeTypes(), Collections.emptyList());
      } finally {
         if (writer != null) {
            writer.flush();
         }
      }
   }

   protected static void addWorkItem(IAtsServer atsServer, IAtsWorkItem config, Annotation[] annotations, JsonGenerator writer, boolean identityView, AttributeTypes attributeTypes, List<WorkItemWriterOptions> options) throws IOException, JsonGenerationException, JsonProcessingException {
      ArtifactReadable action = (ArtifactReadable) config.getStoreObject();
      writer.writeStartObject();
      writer.writeNumberField("id", ConfigJsonWriter.getId(config, atsServer));
      writer.writeStringField("Name", config.getName());
      String atsId = action.getSoleAttributeValue(AtsAttributeTypes.AtsId, "");
      writer.writeStringField("AtsId", atsId);
      writer.writeStringField("ArtifactType", action.getArtifactType().getName());
      String actionUrl = AtsUtilCore.getActionUrl(atsId, ATS_UI_ACTION_PREFIX, atsServer);
      writer.writeStringField("actionLocation", actionUrl);
      if (!identityView) {
         ConfigJsonWriter.addAttributeData(writer, attributeTypes, action, options, atsServer);
         writer.writeStringField("TeamName", ActionPage.getTeamStr(atsServer, action));
         IAtsWorkItem workItem = atsServer.getWorkItemFactory().getWorkItem(action);
         writer.writeStringField("Assignees", workItem.getStateMgr().getAssigneesStr());
         writer.writeStringField("ChangeType", action.getSoleAttributeAsString(AtsAttributeTypes.ChangeType, ""));
         writer.writeStringField("Priority", action.getSoleAttributeAsString(AtsAttributeTypes.PriorityType, ""));
         writer.writeStringField("State", workItem.getStateMgr().getCurrentStateName());
         writer.writeStringField("CreatedDate", DateUtil.get(workItem.getCreatedDate(), DateUtil.MMDDYY));
         writer.writeStringField("CreatedBy", workItem.getCreatedBy().getName());
         IAtsTeamWorkflow teamWf = workItem.getParentTeamWorkflow();
         if (teamWf != null) {
            String version = atsServer.getWorkItemService().getTargetedVersionStr(teamWf);
            writer.writeStringField("TargetedVersion", version);
         }
      }
      writer.writeEndObject();
   }
}