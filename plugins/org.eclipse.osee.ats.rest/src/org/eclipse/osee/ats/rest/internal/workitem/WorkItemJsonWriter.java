/*********************************************************************
 * Copyright (c) 2014 Boeing
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.commit.CommitConfigItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.review.ReviewDefectItem;
import org.eclipse.osee.ats.api.review.UserRole;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.TeamWorkflowDetails;
import org.eclipse.osee.ats.api.workflow.WorkItemWriterOptions;
import org.eclipse.osee.ats.core.review.ReviewDefectManager;
import org.eclipse.osee.ats.core.review.UserRoleManager;
import org.eclipse.osee.ats.rest.AtsApiServer;
import org.eclipse.osee.ats.rest.internal.config.ConfigJsonWriter;
import org.eclipse.osee.ats.rest.internal.util.ActionPage;
import org.eclipse.osee.ats.rest.internal.util.TargetedVersion;
import org.eclipse.osee.ats.rest.util.WorkItemJsonProvider;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactWithRelations;
import org.eclipse.osee.framework.core.data.AttributeReadable;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.IRelationLink;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jaxrs.mvc.IdentityView;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.TransactionReadable;

/**
 * @author Donald G. Dunne
 */
@Provider
public class WorkItemJsonWriter implements MessageBodyWriter<IAtsWorkItem> {

   private static final String ATS_UI_ACTION_PREFIX = "/ats/ui/action/ID";
   private JsonFactory jsonFactory;
   private AtsApi atsApiServer;
   private OrcsApi orcsApi;
   private static Set<WorkItemJsonProvider> jsonProviders = new HashSet<>();

   public void addJsonProvider(WorkItemJsonProvider jsonProvider) {
      WorkItemJsonWriter.jsonProviders.add(jsonProvider);
   }

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public void setAtsApiServer(AtsApiServer atsApiServer) {
      this.atsApiServer = atsApiServer;
   }

   public void start() {
      jsonFactory = JsonUtil.getFactory();
   }

   public void stop() {
      jsonFactory = null;
   }

   @Override
   public long getSize(IAtsWorkItem data, Class<?> type, Type genericType, Annotation[] annotations,
      MediaType mediaType) {
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

   @Override
   public void writeTo(IAtsWorkItem config, Class<?> type, Type genericType, Annotation[] annotations,
      MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
      throws IOException, WebApplicationException {
      JsonGenerator writer = null;
      try {
         writer = jsonFactory.createGenerator(entityStream);
         addWorkItem(atsApiServer, orcsApi, config, annotations, writer, matches(IdentityView.class, annotations),
            Collections.emptyList());
      } finally {
         if (writer != null) {
            writer.flush();
         }
      }
   }

   protected static void addWorkItem(AtsApi atsApi, OrcsApi orcsApi, IAtsWorkItem workItem, Annotation[] annotations,
      JsonGenerator writer, boolean identityView, List<WorkItemWriterOptions> options)
      throws IOException, JsonGenerationException, JsonProcessingException {

      ArtifactReadable workItemArt = (ArtifactReadable) workItem.getStoreObject();
      writer.writeStartObject();
      writer.writeNumberField("id", workItem.getId());
      writer.writeStringField("Name", workItem.getName());
      String atsId = workItemArt.getSoleAttributeValue(AtsAttributeTypes.AtsId, "");
      writer.writeStringField("AtsId", atsId);
      IAtsAction action = workItem.getParentAction();
      if (!workItem.isGoal()) {
         writer.writeStringField("ActionAtsId", action == null ? "" : action.getAtsId());
      }
      IAtsTeamWorkflow teamWf = workItem.getParentTeamWorkflow();
      if (!workItem.isGoal()) {
         writer.writeStringField("TeamWfAtsId", teamWf == null ? "" : teamWf.getAtsId());
      }
      writer.writeStringField("ArtifactType", workItemArt.getArtifactType().getName());
      String actionUrl = AtsUtil.getActionUrl(atsId, ATS_UI_ACTION_PREFIX, atsApi);
      writer.writeStringField("actionLocation", actionUrl);
      // Add items from extended work items
      for (WorkItemJsonProvider jsonProvider : jsonProviders) {
         jsonProvider.addFields(workItem, writer, atsApi, orcsApi);
      }
      if (!identityView) {
         ConfigJsonWriter.addAttributeData(writer, workItemArt, options, atsApi, orcsApi);
         if (!workItem.isGoal()) {
            writer.writeStringField("TeamName", ActionPage.getTeamStr(atsApi, workItemArt));
         }
         writer.writeArrayFieldStart("Assignees");
         for (AtsUser assignee : workItem.getAssignees()) {
            writer.writeStartObject();
            writer.writeStringField("id", assignee.getIdString());
            writer.writeStringField("name", assignee.getName());
            writer.writeStringField("email", assignee.getEmail());
            writer.writeEndObject();
         }
         writer.writeEndArray();
         if (options.contains(WorkItemWriterOptions.WriteRelatedAsTokens)) {
            writer.writeArrayFieldStart("AssigneesTokens");
            for (AtsUser assignee : workItem.getAssignees()) {
               writer.writeStartObject();
               writer.writeStringField("id", assignee.getIdString());
               writer.writeStringField("name", assignee.getName());
               writer.writeEndObject();
            }
            writer.writeEndArray();
         }
         writer.writeStringField("ChangeType", workItemArt.getSoleAttributeAsString(AtsAttributeTypes.ChangeType, ""));
         writer.writeStringField("Priority", workItemArt.getSoleAttributeAsString(AtsAttributeTypes.Priority, ""));
         writer.writeStringField("State", workItem.getCurrentStateName());
         if (options.contains(WorkItemWriterOptions.DatesAsLong)) {
            writer.writeStringField("CreatedDate", String.valueOf(workItem.getCreatedDate().getTime()));
         } else {
            writer.writeStringField("CreatedDate", DateUtil.get(workItem.getCreatedDate(), DateUtil.MMDDYY));
         }
         String userId = atsApi.getAttributeResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.CreatedBy, "");
         if (Strings.isValid(userId)) {
            try {
               AtsUser createdBy = workItem.getCreatedBy();
               writer.writeStringField("CreatedBy", createdBy.getName());
               writer.writeStringField("CreatedByEmail", createdBy.getEmail());
            } catch (Exception ex) {
               writer.writeStringField("CreatedBy", "Exception getting user: " + ex.getLocalizedMessage());
            }
         } else {
            writer.writeStringField("CreatedBy", "Invalid User Id");
         }
      }
      if (!identityView || matches(TargetedVersion.class, annotations)) {
         if (teamWf != null) {
            IAtsVersion version = atsApi.getVersionService().getTargetedVersion(teamWf);
            writer.writeStringField("TargetedVersion", version == null ? "" : version.getName());
            writer.writeStringField("TargetedVersionId", version == null ? "" : version.getIdString());
            if (options.contains(WorkItemWriterOptions.WriteRelatedAsTokens)) {
               writer.writeObjectFieldStart("TargetedVersionToken");
               writer.writeStringField("id", version == null ? "" : version.getIdString());
               writer.writeStringField("name", version == null ? "" : version.getName());
               writer.writeEndObject();
            }
         }
      }

      if (workItem.isTeamWorkflow()) {
         writeReviews(atsApi, writer, teamWf);
         writeCurrentState(atsApi, writer, teamWf);
         writeToStates(atsApi, writer, teamWf);
         writePreviousStates(atsApi, writer, teamWf);
         if (matches(TeamWorkflowDetails.class, annotations)) {
            writeTeamWorkflowDetails(orcsApi, atsApi, writer, teamWf);
         }
      }

      if (workItem.isReview()) {
         writeAttachments(atsApi, workItem, writer);
         if (workItem.isOfType(AtsArtifactTypes.PeerToPeerReview)) {
            writeRoles(atsApi, writer, workItem);
            writeDefects(atsApi, writer, workItem);
         }
      }

      TransactionId lastModTransId = ((ArtifactReadable) workItem.getStoreObject()).getLastModifiedTransaction();
      TransactionReadable tx = orcsApi.getTransactionFactory().getTx(lastModTransId);
      writer.writeStringField("LastModDate", tx.getDate().toString());
      writer.writeEndObject();
   }

   protected static void addWorkItemWithIds(AtsApi atsApi, OrcsApi orcsApi, IAtsWorkItem workItem,
      Annotation[] annotations, JsonGenerator writer, boolean identityView, List<WorkItemWriterOptions> options)
      throws IOException, JsonGenerationException, JsonProcessingException {
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

   private static void writeAssignees(JsonGenerator writer, ArtifactReadable action, IAtsWorkItem workItem)
      throws IOException, JsonGenerationException, JsonProcessingException {
      AttributeReadable<Object> attr =
         action.getAttributeById(action.getSoleAttributeId(AtsAttributeTypes.CurrentStateAssignee));
      writer.writeArrayFieldStart("AssigneesTokens");
      for (AtsUser assignee : workItem.getAssignees()) {
         writer.writeStartObject();
         writer.writeStringField("id", assignee.getIdString());
         writer.writeStringField("name", assignee.getName());
         writer.writeNumberField("gammaId", attr.getGammaId().getId());
         writer.writeEndObject();
      }
      writer.writeEndArray();
   }

   private static void writeType(JsonGenerator writer, ArtifactReadable action, IAtsWorkItem workItem, String fieldName,
      AttributeTypeToken attrType) throws IOException, JsonGenerationException, JsonProcessingException {
      writer.writeObjectFieldStart(fieldName);
      String attrValue = action.getSoleAttributeAsString(attrType, "");
      GammaId gammaId = GammaId.SENTINEL;
      if (Strings.isValid(attrValue)) {
         AttributeReadable<Object> attr = action.getAttributeById(action.getSoleAttributeId(attrType));
         gammaId = attr.getGammaId();
      }
      writer.writeObjectField("value", attrValue);
      writer.writeNumberField("gammaId", gammaId.getId());
      writer.writeEndObject();
   }

   private static void writeState(JsonGenerator writer, ArtifactReadable action, IAtsWorkItem workItem)
      throws IOException, JsonGenerationException, JsonProcessingException {
      writer.writeObjectFieldStart("State");
      AttributeReadable<Object> attr =
         action.getAttributeById(action.getSoleAttributeId(AtsAttributeTypes.CurrentStateName));
      writer.writeObjectField("value", workItem.getCurrentStateName());
      writer.writeNumberField("gammaId", attr.getGammaId().getId());
      writer.writeEndObject();
   }

   private static void writeTargetedVersion(AtsApi atsApi, JsonGenerator writer, ArtifactReadable action,
      IAtsWorkItem workItem, List<WorkItemWriterOptions> options)
      throws IOException, JsonGenerationException, JsonProcessingException {
      IAtsTeamWorkflow teamWf = workItem.getParentTeamWorkflow();
      if (teamWf != null) {
         ResultSet<IRelationLink> relations =
            action.getRelations(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version);
         if (relations != null && !relations.isEmpty()) {
            writer.writeObjectFieldStart("TargetedVersion");
            String versionStr = atsApi.getWorkItemService().getTargetedVersionStr(teamWf);
            writer.writeObjectField("value", versionStr);
            writer.writeNumberField("gammaId", relations.iterator().next().getGammaId().getId());
            writer.writeEndObject();
            if (options.contains(WorkItemWriterOptions.WriteRelatedAsTokens)) {
               ArtifactToken version = atsApi.getRelationResolver().getRelatedOrNull(action,
                  AtsRelationTypes.TeamWorkflowTargetedForVersion_Version);
               writer.writeObjectFieldStart("TargetedVersionToken");
               writer.writeStringField("id", relations.iterator().next().getArtifactIdB().getIdString());
               writer.writeNumberField("gammaId", relations.iterator().next().getGammaId().getId());
               writer.writeStringField("name", version.getName());
               writer.writeEndObject();
            }
         }
      }
   }

   private static void writeAttachments(AtsApi atsApi, IAtsWorkItem workItem, JsonGenerator writer) throws IOException {
      writer.writeArrayFieldStart("Attachments");

      Set<ArtifactToken> relationSet = new HashSet<>();
      relationSet.addAll(
         atsApi.getRelationResolver().getRelated(workItem, CoreRelationTypes.SupportingInfo_SupportingInfo));
      relationSet.addAll(
         atsApi.getRelationResolver().getRelated(workItem, CoreRelationTypes.SupportingInfo_IsSupportedBy));

      for (ArtifactToken token : relationSet) {
         writer.writeStartObject();
         writer.writeStringField("id", token.getIdString());
         writer.writeStringField("name", token.getName());
         writer.writeStringField("type", token.getArtifactType().getName());
         writer.writeEndObject();
      }

      writer.writeEndArray();
   }

   private static void writeRoles(AtsApi atsApi, JsonGenerator writer, IAtsWorkItem workItem) throws IOException {
      writer.writeArrayFieldStart("roles");

      UserRoleManager manager = new UserRoleManager((IAtsPeerToPeerReview) workItem, atsApi);

      // pre-process
      Map<String, UserRole> roleMap = new HashMap<>();
      for (UserRole role : manager.getUserRoles()) {
         roleMap.put(role.getUserId(), role);
      }

      // Write user
      for (AtsUser user : atsApi.getUserService().getUsersByUserIds(roleMap.keySet())) {
         writer.writeStartObject();
         writer.writeStringField("id", user.getIdString());
         writer.writeStringField("name", user.getName());
         writer.writeStringField("email", user.getEmail());

         UserRole userRole = roleMap.get(user.getUserId());
         writer.writeStringField("role", userRole.getRole().getName());
         writer.writeBooleanField("completed", userRole.isCompleted());
         writer.writeNumberField("hours spent", userRole.getHoursSpent());
         writer.writeEndObject();
      }

      writer.writeEndArray();
   }

   private static void formatUser(AtsUser user, String userFieldName, JsonGenerator writer) throws IOException {
      writer.writeArrayFieldStart(userFieldName);
      writer.writeStartObject();
      writer.writeStringField("id", user.getIdString());
      writer.writeStringField("name", user.getName());
      writer.writeStringField("email", user.getEmail());
      writer.writeEndObject();
      writer.writeEndArray();
   }

   private static void writeDefects(AtsApi atsApi, JsonGenerator writer, IAtsWorkItem workItem) throws IOException {
      writer.writeArrayFieldStart("defects");

      ReviewDefectManager manager = new ReviewDefectManager((IAtsPeerToPeerReview) workItem, atsApi);

      for (ReviewDefectItem defect : manager.getDefectItems()) {
         writer.writeStartObject();
         writer.writeNumberField("id", defect.getId());
         writer.writeStringField("severity", defect.getSeverity().toString());
         writer.writeStringField("disposition", defect.getDisposition().toString());
         writer.writeStringField("injection activity", defect.getInjectionActivity().toString());
         writer.writeStringField("date", defect.getDate().toString());
         if (Strings.isValidAndNonBlank(defect.getUserId())) {
            AtsUser user = atsApi.getUserService().getUserByUserId(defect.getUserId());
            formatUser(user, "user", writer);
         }
         writer.writeStringField("description", defect.getDescription());
         writer.writeStringField("location", defect.getLocation());
         writer.writeStringField("resolution", defect.getResolution());
         writer.writeBooleanField("closed", defect.isClosed());
         writer.writeStringField("notes", defect.getNotes());
         if (Strings.isValidAndNonBlank(defect.getClosedUserId())) {
            AtsUser closedUser = atsApi.getUserService().getUserByUserId(defect.getClosedUserId());
            formatUser(closedUser, "closed user", writer);
         }
         writer.writeEndObject();
      }

      writer.writeEndArray();
   }

   private static void writeReviews(AtsApi atsApi, JsonGenerator writer, IAtsTeamWorkflow teamWf) throws IOException {
      writer.writeArrayFieldStart("Reviews");
      for (IAtsAbstractReview review : atsApi.getReviewService().getReviews(teamWf)) {
         writer.writeStartObject();
         writer.writeStringField("id", review.getIdString());
         writer.writeStringField("state", review.getCurrentStateName());
         writer.writeStringField("type", review.getArtifactType().getName());
         writer.writeEndObject();
      }

      writer.writeEndArray();
   }

   private static void writeCurrentState(AtsApi atsApi, JsonGenerator writer, IAtsTeamWorkflow teamWf)
      throws IOException {
      writer.writeObjectFieldStart("currentState");
      writeStateInner(atsApi, writer, teamWf.getStateDefinition());
      writer.writeEndObject();
   }

   private static void writeToStates(AtsApi atsApi, JsonGenerator writer, IAtsTeamWorkflow teamWf) throws IOException {
      writer.writeArrayFieldStart("toStates");
      for (StateDefinition state : teamWf.getStateDefinition().getToStates()) {
         writeState(atsApi, writer, state);
      }
      writer.writeEndArray();
   }

   private static void writePreviousStates(AtsApi atsApi, JsonGenerator writer, IAtsTeamWorkflow teamWf)
      throws IOException {
      writer.writeArrayFieldStart("previousStates");
      //loop over states until you get to a toState
      for (StateDefinition state : teamWf.getWorkDefinition().getStates()) {
         if (teamWf.getStateDefinition().getToStates().contains(state)) {
            break;
         }
         writeState(atsApi, writer, state);
      }
      writer.writeEndArray();
   }

   private static void writeState(AtsApi atsApi, JsonGenerator writer, StateDefinition state) throws IOException {
      writer.writeStartObject();
      writeStateInner(atsApi, writer, state);
      writer.writeEndObject();
   }

   private static void writeStateInner(AtsApi atsApi, JsonGenerator writer, StateDefinition state) throws IOException {
      writer.writeObjectField("state", state.getName());
      writer.writeArrayFieldStart("rules");
      for (String rule : atsApi.getWorkDefinitionService().getWidgetsFromLayoutItems(state).stream().flatMap(
         layout -> layout.getOptions().getXOptions().stream()).map(option -> option.name()).collect(
            Collectors.toSet())) {
         writer.writeString(rule);
      }

      writer.writeEndArray();
      boolean hasCommitManager = atsApi.getWorkDefinitionService().getWidgetsFromLayoutItems(state).stream().map(
         widget -> widget.getName()).anyMatch(name -> name.equals("Commit Manager"));
      writer.writeObjectField("committable", hasCommitManager);
   }

   private static void writeTeamWorkflowDetails(OrcsApi orcsApi, AtsApi atsApi, JsonGenerator writer,
      IAtsTeamWorkflow teamWf) throws IOException {
      ArtifactReadable artReadable =
         orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andId(teamWf.getArtifactId()).asArtifact();
      List<ArtifactToken> leads =
         orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIsOfType(CoreArtifactTypes.User).andRelatedTo(
            AtsRelationTypes.TeamLead_Team, teamWf.getTeamDefinition().getArtifactId()).asArtifactTokens();
      BranchId branch = atsApi.getBranchService().getBranch(teamWf);
      BranchId parentBranch = BranchId.SENTINEL;
      BranchState branchState = BranchState.DELETED; // Setting to deleted by default so branchEditable returns false if a working branch hasn't been created yet.
      CommitConfigItem parentBranchConfig =
         atsApi.getBranchService().getParentBranchConfigArtifactConfiguredToCommitTo(teamWf);
      if (parentBranchConfig != null) {
         parentBranch = parentBranchConfig.getBaselineBranchId();
      }
      if (branch.isValid()) {
         branchState = atsApi.getBranchService().getBranchState(branch);
      }

      writer.writeObjectField("artifact", new ArtifactWithRelations(artReadable, orcsApi.tokenService(), false));
      writer.writeObjectField("leads", leads);
      writer.writeObjectField("parentBranch", parentBranch);
      writer.writeObjectField("workingBranch", branch);
      writer.writeObjectField("branchesToCommitTo", atsApi.getBranchService().getBranchesLeftToCommit(teamWf));
      writer.writeObjectField("branchEditable",
         branchState.equals(BranchState.CREATED) || branchState.equals(BranchState.MODIFIED));

   }
}