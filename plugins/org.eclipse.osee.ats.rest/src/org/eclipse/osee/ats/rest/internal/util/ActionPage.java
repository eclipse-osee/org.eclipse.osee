/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.workdef.IAtsCompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.IAtsLayoutItem;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ViewModel;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;

/**
 * @author Donald G Dunne
 */
public class ActionPage {

   private static final String DEFECT_TABLE_HEADER =
      "<table border=\"1\" align=\"center\" width=\"98%\"><tr><th>Severity</th><th>Disposition</th><th>Closed</th><th>User</th><th>Created</th><th>Injected</th><th>Description</th><th>Location</th><th>Resolution</th></tr>";
   private static final String ROLE_TABLE_HEADER =
      "<table border=\"1\" align=\"center\" width=\"90%\"><tr><th>Role</th><th>User</th><th>Completed</th><th>Hours Spent</th></tr>";
   private static final String COMMIT_MANAGER_WIDGET_NAME = "Commit Manager";
   private static final String REVIEW_DEFECT_WIDGET_NAME = "Review Defect";
   private static final String ROLE_WIDGET_NAME = "Role";
   private String pageTemplate;
   private IAtsWorkItem workItem;
   private final ArtifactReadable action;
   private final AtsApi atsApi;
   private final Log logger;
   private boolean addTransition = false;
   private static final List<String> roleKeys = Arrays.asList("role", "userId", "completed", "hoursSpent");
   private static final List<String> defectKeys = Arrays.asList("severity", "disposition", "closed", "user", "date",
      "injectionActivity", "description", "location", "resolution");
   private static List<String> ignoredWidgets;
   private final boolean details;

   public ActionPage(Log logger, AtsApi atsApi, IAtsWorkItem workItem, boolean details) {
      this(logger, atsApi, (ArtifactReadable) workItem.getStoreObject(), details);
      this.workItem = workItem;
   }

   public ActionPage(Log logger, AtsApi atsApi, ArtifactReadable action, boolean details) {
      this.logger = logger;
      this.atsApi = atsApi;
      this.action = action;
      this.details = details;
   }

   private IAtsWorkItem getWorkItem() {
      if (workItem == null) {
         workItem = atsApi.getWorkItemService().getWorkItem(action);
      }
      return workItem;
   }

   public ViewModel generate() throws Exception {
      IAtsWorkItem workItem = getWorkItem();
      Conditions.checkNotNull(workItem, "workItem");

      ViewModel page = new ViewModel("action.html");
      page.param("title", action.getSoleAttributeAsString(AtsAttributeTypes.Title, ""));
      page.param("team", getTeamStr(atsApi, action));
      page.param("ais", getAIStr(action));
      page.param("state", workItem.getStateMgr().getCurrentStateName());
      page.param("assignees", getAssigneesStr(workItem, action));
      page.param("id", workItem.getId());
      page.param("atsId", workItem.getAtsId());
      page.param("originator", getCreatedByStr(workItem, action));
      page.param("priority", action.getSoleAttributeAsString(AtsAttributeTypes.PriorityType, ""));
      page.param("changeType", action.getSoleAttributeAsString(AtsAttributeTypes.ChangeType, ""));
      page.param("needBy", action.getSoleAttributeAsString(AtsAttributeTypes.NeedBy, ""));
      page.param("workflow", action.getArtifactType().toString());
      page.param("createdDate", workItem.getCreatedDate().toString());
      page.param("version", getVersion(workItem));
      page.param("workDef", getWorkDefStr(workItem));
      if (!addTransition) {
         page.param("transition", "");
      } else {
         addTransitionStates(page);
      }

      addStates(page, workItem, action);
      addDetails(page, workItem, action);

      return page;
   }

   private String getWorkDefStr(IAtsWorkItem workItem) {
      return workItem.getWorkDefinition().getName();
   }

   public static String getAssigneesStr(IAtsWorkItem workItem, ArtifactReadable action) {
      return workItem.getStateMgr().getAssigneesStr();
   }

   public static String getTeamStr(AtsApi atsApi, ArtifactReadable action) {
      String results = "";
      ArtifactId artId = atsApi.getAttributeResolver().getSoleArtifactIdReference(action,
         AtsAttributeTypes.TeamDefinitionReference, ArtifactId.SENTINEL);
      if (artId.isValid()) {
         results = atsApi.getQueryService().getArtifact(artId).getName();
      } else {
         ArtifactReadable teamWf = getParentTeamWf(action);
         if (teamWf.isValid() && teamWf.notEqual(action)) {
            results = getTeamStr(atsApi, teamWf);
         }
      }
      return results;
   }

   private static ArtifactReadable getParentTeamWf(ArtifactReadable action) {
      ArtifactReadable teamWf = null;
      if (action.isOfType(AtsArtifactTypes.TeamWorkflow)) {
         teamWf = action;
      } else if (action.isOfType(AtsArtifactTypes.ReviewArtifact)) {
         teamWf =
            action.getRelated(AtsRelationTypes.TeamWorkflowToReview_Team).getOneOrDefault(ArtifactReadable.SENTINEL);
      } else if (action.isOfType(AtsArtifactTypes.Task)) {
         teamWf = action.getRelated(AtsRelationTypes.TeamWfToTask_TeamWf).getOneOrDefault(ArtifactReadable.SENTINEL);
      }
      return teamWf;
   }

   private String getAIStr(ArtifactReadable action) {
      StringBuilder sb = new StringBuilder();
      ArtifactReadable teamWf = getParentTeamWf(action);
      if (teamWf.isValid()) {
         Collection<ArtifactId> artifactIds = teamWf.getAttributeValues(AtsAttributeTypes.ActionableItemReference);
         for (ArtifactId artifactId : artifactIds) {
            sb.append(atsApi.getQueryService().getArtifact(artifactId));
            sb.append(", ");
         }
      }
      return sb.toString().replaceFirst(", $", "");
   }

   public static String getCreatedByStr(IAtsWorkItem workItem, ArtifactReadable action) {
      String results = action.getSoleAttributeAsString(AtsAttributeTypes.CreatedBy);
      results = workItem.getCreatedBy().getName();
      return results;
   }

   public void addTransitionStates(ViewModel page) {
      try {
         IAtsWorkItem workItem = getWorkItem();
         String html = OseeInf.getResourceContents("templates/transition.html", getClass());
         html = html.replaceAll("PUT_POST_URL_HERE", "/ats/action/state");
         html = html.replaceAll("PUT_ATS_ID_HERE", workItem.getAtsId());
         html = html.replaceFirst("PUT_TO_STATE_LIST_HERE", getToStateList());
         String defaultToStateValue = "";
         IAtsStateDefinition defaultToState = workItem.getStateDefinition().getDefaultToState();
         if (defaultToState != null) {
            defaultToStateValue = "value=\"" + defaultToState.getName() + "\"";
         }
         html = html.replaceAll("PUT_DEFAULT_TO_STATE_VALUE_HERE", defaultToStateValue);
         page.param("transition", html);
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
   }

   private String getToStateList() {
      StringBuilder sb = new StringBuilder("<datalist id=\"ToStateList\">\n");
      for (IAtsStateDefinition state : workItem.getStateDefinition().getToStates()) {
         sb.append("<option value=\"");
         sb.append(state.getName());
         sb.append("\" id=\"");
         sb.append(state.getName());
         sb.append("\">\n");
      }
      sb.append("</datalist>");
      return sb.toString();
   }

   private String getVersion(IAtsWorkItem workItem) {
      String version = "<on full load>";
      try {
         IAtsTeamWorkflow teamWf = workItem.getParentTeamWorkflow();
         String str = atsApi.getWorkItemService().getTargetedVersionStr(teamWf);
         if (Strings.isValid(str)) {
            version = str;
         }
      } catch (OseeCoreException ex) {
         logger.error(ex, "Error getting version for [%s]", workItem);
         version = "exception: " + ex.getLocalizedMessage();
      }
      return version;
   }

   private void addStates(ViewModel page, IAtsWorkItem workItem, ArtifactReadable action) throws Exception {
      StringBuilder statesSb = new StringBuilder();
      IAtsWorkDefinition workDefinition = workItem.getWorkDefinition();
      IAtsStateManager stateMgr = workItem.getStateMgr();
      Collection<String> visitedStates = stateMgr.getVisitedStateNames();
      List<IAtsStateDefinition> statesOrderedByOrdinal =
         atsApi.getWorkDefinitionService().getStatesOrderedByOrdinal(workDefinition);
      for (int index = statesOrderedByOrdinal.size() - 1; index >= 0; index--) {
         IAtsStateDefinition state = statesOrderedByOrdinal.get(index);
         if (visitedStates.contains(state.getName())) {
            String stateHtmlTemplate = getStateHtmlTemplate();

            String stateName = state.getName();
            if (stateName.equals(stateMgr.getCurrentStateName())) {
               stateName = String.format("CURRENT STATE => <b>%s</b>", stateName);
               if (stateMgr.getStateType().isCompleted()) {
                  stateName = String.format("%s - on <b>%s</b> - by <b>%s</b>", stateName,
                     DateUtil.getMMDDYYHHMM(workItem.getCompletedDate()), workItem.getCompletedBy().getName());
               } else if (stateMgr.getStateType().isCancelled()) {
                  stateName = String.format("%s - on <b>%s</b> - by <b>%s</b><br/>from <b>%s</b> - reason <b>[%s]</b>",
                     stateName, DateUtil.getMMDDYYHHMM(workItem.getCancelledDate()),
                     workItem.getCancelledBy().getName(), workItem.getCancelledFromState(),
                     workItem.getCancelledReason());
               }
            }
            stateHtmlTemplate = stateHtmlTemplate.replace("TITLE", stateName);

            StringBuilder widgets = new StringBuilder();
            addWidgets(widgets, workItem, state.getLayoutItems());
            stateHtmlTemplate = stateHtmlTemplate.replace("WIDGETS", widgets.toString());

            statesSb.append(stateHtmlTemplate);
         }
      }
      page.param("states", statesSb.toString());
   }

   private void addWidgets(StringBuilder sb, IAtsWorkItem workItem, Collection<IAtsLayoutItem> items) {
      addLayoutItems(sb, workItem, items);
   }

   private boolean inComposite = false;

   private void addLayoutItems(StringBuilder sb, IAtsWorkItem workItem, Collection<IAtsLayoutItem> items) {
      for (IAtsLayoutItem layout : items) {
         if (layout instanceof IAtsCompositeLayoutItem) {
            inComposite = true;
            sb.append("<tr><td><table width=\"100%\"><tr>");
            addWidgets(sb, workItem, ((IAtsCompositeLayoutItem) layout).getaLayoutItems());
            sb.append("</tr></table></td></tr>");
            inComposite = false;
         } else {
            IAtsWidgetDefinition widget = (IAtsWidgetDefinition) layout;
            if (!getIgnoreWidgetNames().contains(widget.getName())) {
               if (!inComposite) {
                  sb.append("<tr><td>");
               } else {
                  sb.append("<td>");
               }
               addWidget(sb, workItem, widget);
               if (!inComposite) {
                  sb.append("</td></tr>");
               } else {
                  sb.append("</td>");
               }
            }
         }
      }
   }

   private void addWidget(StringBuilder sb, IAtsWorkItem workItem, IAtsWidgetDefinition widget) {
      if (widget.getName().equals(ROLE_WIDGET_NAME)) {
         addRoleWidget(sb, workItem, widget);
      } else if (widget.getName().equals(REVIEW_DEFECT_WIDGET_NAME)) {
         addDefectWidget(sb, workItem, widget);
      } else if (widget.getName().equals(COMMIT_MANAGER_WIDGET_NAME)) {
         addCommitManager(sb, workItem, widget);
      } else {
         addWidgetDefault(sb, workItem, widget);
      }
   }

   private void addCommitManager(StringBuilder sb, IAtsWorkItem workItem2, IAtsWidgetDefinition widget) {
      sb.append("Commit Manager: ");
      IOseeBranch branch = atsApi.getBranchService().getBranch((IAtsTeamWorkflow) workItem);
      if (branch.isValid()) {
         sb.append(branch.getName());
      }
   }

   private List<String> getIgnoreWidgetNames() {
      if (ignoredWidgets == null) {
         ignoredWidgets = new ArrayList<>();
         String configValue = atsApi.getConfigValue("IgnoredWidgetNames");
         if (Strings.isValid(configValue)) {
            for (String widgetName : configValue.split(";")) {
               ignoredWidgets.add(widgetName);
            }
         }
      }
      return ignoredWidgets;
   }

   private void addRoleWidget(StringBuilder sb, IAtsWorkItem workItem, IAtsWidgetDefinition widget) {
      sb.append("Roles: ");
      Collection<String> roles =
         atsApi.getAttributeResolver().getAttributesToStringList(workItem, AtsAttributeTypes.Role);
      if (!roles.isEmpty()) {
         sb.append(ROLE_TABLE_HEADER);
         for (String xml : roles) {
            sb.append("<tr>");
            for (String key : roleKeys) {
               sb.append("<td>");
               String data = AXml.getTagData(xml, key);
               if (key.equals("userId")) {
                  data = atsApi.getUserService().getUserById(data).getName();
               }
               sb.append(data);
               sb.append("</td>");
            }
            sb.append("</tr>");
         }
         sb.append("</table>");
      }
   }

   private void addDefectWidget(StringBuilder sb, IAtsWorkItem workItem, IAtsWidgetDefinition widget) {
      sb.append("Defects: ");
      Collection<String> defects =
         atsApi.getAttributeResolver().getAttributesToStringList(workItem, AtsAttributeTypes.ReviewDefect);
      if (!defects.isEmpty()) {
         sb.append(DEFECT_TABLE_HEADER);
         for (String xml : defects) {
            sb.append("<tr>");
            for (String key : defectKeys) {
               sb.append("<td>");
               String data = AXml.getTagData(xml, key);
               if (key.equals("user")) {
                  data = atsApi.getUserService().getUserById(data).getName();
               } else if (key.equals("date")) {
                  data = DateUtil.getDateStr(new Date(Long.valueOf(data)), DateUtil.MMDDYYHHMM);
               }
               sb.append(data);
               sb.append("</td>");
            }
            sb.append("</tr>");
         }
         sb.append("</table>");
      }
   }

   private void addWidgetDefault(StringBuilder sb, IAtsWorkItem workItem, IAtsWidgetDefinition widget) {
      sb.append(widget.getName());
      try {
         AttributeTypeToken attrType = widget.getAttributeType();
         if (attrType != null) {
            sb.append(": <b>");
            Collection<String> attributesToStringList =
               atsApi.getAttributeResolver().getAttributesToStringList(workItem, attrType);
            if (attributesToStringList.size() > 1) {
               sb.append(attributesToStringList.toString());
            } else if (attributesToStringList.size() == 1) {
               sb.append(AHTML.textToHtml(String.valueOf(attributesToStringList.iterator().next())));
            }
            sb.append("</b>");
         }
      } catch (OseeCoreException ex) {
         sb.append("exception: " + ex.getLocalizedMessage());
      }
   }

   private String getStateHtmlTemplate() throws Exception {
      if (pageTemplate == null) {
         pageTemplate = OseeInf.getResourceContents("templates/state.html", getClass());
      }
      return pageTemplate;
   }

   private void addDetails(ViewModel page, IAtsWorkItem workItem, ArtifactReadable artifact) {
      StringBuilder sb = new StringBuilder();
      if (details) {
         try {
            addDetail(sb, "Artifact Type", artifact.getArtifactType().getName());
            sb.append("</br><b>Attribute Raw Data:</b></br>");
            for (AttributeReadable<?> attr : artifact.getAttributes()) {
               addDetail(sb, attr.getAttributeType().getName(), AHTML.textToHtml(String.valueOf(attr.getValue())));
            }
         } catch (OseeCoreException ex) {
            sb.append("exception: " + ex.getLocalizedMessage());
         }
      }
      page.param("details", sb.toString());
   }

   private static void addDetail(StringBuilder sb, String key, String value) {
      sb.append(key);
      sb.append(": <b>");
      sb.append(value);
      sb.append("</b></br>");
   }

   public boolean isAddTransition() {
      return addTransition;
   }

   public void setAddTransition(boolean addTransition) {
      this.addTransition = addTransition;
   }

}
