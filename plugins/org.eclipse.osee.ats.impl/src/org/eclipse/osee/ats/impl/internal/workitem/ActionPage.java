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
package org.eclipse.osee.ats.impl.internal.workitem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
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
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.ats.impl.resource.AtsResourceTokens;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.template.engine.PageCreator;
import org.eclipse.osee.template.engine.PageFactory;
import org.eclipse.osee.template.engine.StringOptionsRule;

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
   private PageCreator page;
   private IAtsWorkItem workItem;
   private final IResourceRegistry registry;
   private final String title;
   private final ArtifactReadable action;
   private final IAtsServer atsServer;
   private final Log logger;
   private boolean addTransition = false;
   private static final List<String> roleKeys = Arrays.asList("role", "userId", "completed", "hoursSpent");
   private static final List<String> defectKeys = Arrays.asList("severity", "disposition", "closed", "user", "date",
      "injectionActivity", "description", "location", "resolution");
   private static List<String> ignoredWidgets;
   private final boolean details;

   public ActionPage(Log logger, IAtsServer atsServer, IResourceRegistry registry, ArtifactReadable action, String title, boolean details) {
      this.logger = logger;
      this.atsServer = atsServer;
      this.registry = registry;
      this.action = action;
      this.title = title;
      this.details = details;
   }

   private IAtsWorkItem getWorkItem() {
      if (workItem == null) {
         workItem = atsServer.getWorkItemFactory().getWorkItem(action);
      }
      return workItem;
   }

   public PageCreator getPage() {
      if (page == null) {
         page = PageFactory.newPageCreator(registry, "pageTitle", title);
      }
      return page;
   }

   public String generate() throws Exception {
      IAtsWorkItem workItem = getWorkItem();
      Conditions.checkNotNull(workItem, "workItem");
      PageCreator page = getPage();

      page.readKeyValuePairs(AtsResourceTokens.AtsValuesHtml);
      page.addKeyValuePair("title", action.getSoleAttributeAsString(AtsAttributeTypes.Title, ""));
      page.addKeyValuePair("description", action.getSoleAttributeAsString(AtsAttributeTypes.Description, ""));
      page.addKeyValuePair("team", getTeamStr(action));
      page.addKeyValuePair("ais", getAIStr(action));
      page.addKeyValuePair("state", atsServer.getWorkItemService().getCurrentStateName(workItem));
      page.addKeyValuePair("assignees", getAssigneesStr(workItem, action));
      page.addKeyValuePair("id", workItem.getGuid());
      page.addKeyValuePair("atsId", workItem.getAtsId());
      page.addKeyValuePair("originator", getCreatedByStr(workItem, action));
      page.addKeyValuePair("priority", action.getSoleAttributeAsString(AtsAttributeTypes.PriorityType, ""));
      page.addKeyValuePair("changeType", action.getSoleAttributeAsString(AtsAttributeTypes.ChangeType, ""));
      page.addKeyValuePair("needBy", action.getSoleAttributeAsString(AtsAttributeTypes.NeedBy, ""));
      page.addKeyValuePair("workflow", action.getArtifactType().toString());
      page.addKeyValuePair("createdDate", workItem.getCreatedDate().toString());
      page.addKeyValuePair("version", getVersion(workItem));
      page.addKeyValuePair("workDef", getWorkDefStr(workItem));
      page.addKeyValuePair("guid", workItem.getGuid());
      if (page.getValue("transition") == null) {
         page.addKeyValuePair("transition", "");
      }

      addStates(page, workItem, action);
      addDetails(page, workItem, action);

      return page.realizePage(AtsResourceTokens.AtsActionHtml);
   }

   private String getWorkDefStr(IAtsWorkItem workItem) {
      return workItem.getWorkDefinition().getName();
   }

   private String getAssigneesStr(IAtsWorkItem workItem, ArtifactReadable action) {
      return workItem.getStateMgr().getAssigneesStr();
   }

   private String getTeamStr(ArtifactReadable action) {
      String results = action.getSoleAttributeAsString(AtsAttributeTypes.TeamDefinition, "");
      if (Strings.isValid(results)) {
         results = atsServer.getArtifactByGuid(results).getName();
      } else {
         ArtifactReadable teamWf = getParentTeamWf(action);
         if (teamWf != null) {
            results = getTeamStr(teamWf);
         }
      }
      return results;
   }

   private ArtifactReadable getParentTeamWf(ArtifactReadable action2) {
      ArtifactReadable teamWf = null;
      if (action.isOfType(AtsArtifactTypes.TeamWorkflow)) {
         teamWf = action;
      } else if (action.isOfType(AtsArtifactTypes.ReviewArtifact)) {
         teamWf = action.getRelated(AtsRelationTypes.TeamWorkflowToReview_Team).getOneOrNull();
      } else if (action.isOfType(AtsArtifactTypes.Task)) {
         teamWf = action.getRelated(AtsRelationTypes.TeamWfToTask_TeamWf).getOneOrNull();
      }
      return teamWf;
   }

   private String getAIStr(ArtifactReadable action) {
      StringBuilder sb = new StringBuilder();
      ArtifactReadable teamWf = getParentTeamWf(action);
      if (teamWf != null) {
         for (AttributeReadable<Object> aiGuid : teamWf.getAttributes(AtsAttributeTypes.ActionableItem)) {
            sb.append(atsServer.getArtifactByGuid(aiGuid.toString()).getName());
            sb.append(", ");
         }
      }
      return sb.toString().replaceFirst(", $", "");
   }

   private String getCreatedByStr(IAtsWorkItem workItem, ArtifactReadable action) {
      String results = action.getSoleAttributeAsString(AtsAttributeTypes.CreatedBy);
      results = workItem.getCreatedBy().getName();
      return results;
   }

   public void addTransitionStates() throws OseeCoreException {
      IAtsWorkItem workItem = getWorkItem();
      PageCreator transPage = new PageCreator(registry);
      transPage.addKeyValuePair("guid", workItem.getGuid());
      StringOptionsRule rule = new StringOptionsRule("ToStateList", "ToStateList");
      for (IAtsStateDefinition state : workItem.getStateDefinition().getToStates()) {
         rule.getOptions().add(state.getName());
      }
      transPage.addSubstitution(rule);
      String defaultToStateValue = "";
      IAtsStateDefinition defaultToState = workItem.getStateDefinition().getDefaultToState();
      if (defaultToState != null) {
         defaultToStateValue = "value=\"" + defaultToState.getName() + "\"";
      }
      transPage.addKeyValuePair("defaultToStateValue", defaultToStateValue);

      PageCreator page = getPage();
      page.addKeyValuePair("transition", transPage.realizePage(AtsResourceTokens.TransitionHtml));
   }

   private String getVersion(IAtsWorkItem workItem) {
      String version = "<on full load>";
      try {
         IAtsTeamWorkflow teamWf = workItem.getParentTeamWorkflow();
         String str = atsServer.getWorkItemService().getTargetedVersionStr(teamWf);
         if (Strings.isValid(str)) {
            version = str;
         }
      } catch (OseeCoreException ex) {
         logger.error(ex, "Error getting version for [%s]", workItem);
         version = "exception: " + ex.getLocalizedMessage();
      }
      return version;
   }

   private void addStates(PageCreator page, IAtsWorkItem workItem, ArtifactReadable action) throws Exception {
      StringBuilder statesSb = new StringBuilder();
      IAtsWorkDefinition workDefinition = workItem.getWorkDefinition();
      Collection<String> visitedStates = workItem.getStateMgr().getVisitedStateNames();
      List<IAtsStateDefinition> statesOrderedByOrdinal =
         atsServer.getWorkDefService().getStatesOrderedByOrdinal(workDefinition);
      for (int index = statesOrderedByOrdinal.size() - 1; index >= 0; index--) {
         IAtsStateDefinition state = statesOrderedByOrdinal.get(index);
         if (visitedStates.contains(state.getName())) {
            String stateHtmlTemplate = getStateHtmlTemplate();

            String stateName = state.getName();
            if (stateName.equals(workItem.getStateMgr().getCurrentStateName())) {
               stateName = "CURRENT STATE => " + stateName;
            }
            stateHtmlTemplate = stateHtmlTemplate.replace("TITLE", stateName);

            StringBuilder widgets = new StringBuilder();
            addWidgets(widgets, workItem, state.getLayoutItems());
            stateHtmlTemplate = stateHtmlTemplate.replace("WIDGETS", widgets.toString());

            statesSb.append(stateHtmlTemplate);
         }
      }
      page.addKeyValuePair("states", statesSb.toString());
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
      IOseeBranch branch = atsServer.getBranchService().getBranch((IAtsTeamWorkflow) workItem);
      if (branch != null) {
         sb.append(branch.getName());
      }
   }

   private List<String> getIgnoreWidgetNames() {
      if (ignoredWidgets == null) {
         ignoredWidgets = new ArrayList<String>();
         for (String widgetName : atsServer.getConfigValue("IgnoredWidgetNames").split(";")) {
            ignoredWidgets.add(widgetName);
         }
      }
      return ignoredWidgets;
   }

   private void addRoleWidget(StringBuilder sb, IAtsWorkItem workItem, IAtsWidgetDefinition widget) {
      sb.append("Roles: ");
      Collection<String> roles =
         atsServer.getAttributeResolver().getAttributesToStringList(workItem, AtsAttributeTypes.Role);
      if (!roles.isEmpty()) {
         sb.append(ROLE_TABLE_HEADER);
         for (String xml : roles) {
            sb.append("<tr>");
            for (String key : roleKeys) {
               sb.append("<td>");
               String data = AXml.getTagData(xml, key);
               if (key.equals("userId")) {
                  data = atsServer.getUserService().getUserById(data).getName();
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
         atsServer.getAttributeResolver().getAttributesToStringList(workItem, AtsAttributeTypes.ReviewDefect);
      if (!defects.isEmpty()) {
         sb.append(DEFECT_TABLE_HEADER);
         for (String xml : defects) {
            sb.append("<tr>");
            for (String key : defectKeys) {
               sb.append("<td>");
               String data = AXml.getTagData(xml, key);
               if (key.equals("user")) {
                  data = atsServer.getUserService().getUserById(data).getName();
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
      sb.append(": <b>");
      try {
         IAttributeType attributeType = atsServer.getAttributeResolver().getAttributeType(widget.getAtrributeName());
         Collection<String> attributesToStringList =
            atsServer.getAttributeResolver().getAttributesToStringList(workItem, attributeType);
         if (attributesToStringList.size() > 1) {
            sb.append(attributesToStringList.toString());
         } else if (attributesToStringList.size() == 1) {
            sb.append(String.valueOf(attributesToStringList.iterator().next()));
         }
      } catch (OseeCoreException ex) {
         sb.append("exception: " + ex.getLocalizedMessage());
      }
      sb.append("</b>");
   }

   private String getStateHtmlTemplate() throws IOException {
      if (pageTemplate == null) {
         pageTemplate = Lib.inputStreamToString(AtsResourceTokens.class.getResource("html/atsState.html").openStream());
      }
      return pageTemplate;
   }

   private void addDetails(PageCreator page, IAtsWorkItem workItem, ArtifactReadable artifact) {
      StringBuilder sb = new StringBuilder();
      if (details) {
         try {
            addDetail(sb, "Guid", artifact.getGuid());
            addDetail(sb, "Artifact Type", artifact.getArtifactType().getName());
            sb.append("</br><b>Attribute Raw Data:</b></br>");
            for (AttributeReadable<?> attr : artifact.getAttributes()) {
               addDetail(sb, attr.getAttributeType().getName(), AHTML.textToHtml(String.valueOf(attr.getValue())));
            }
         } catch (OseeCoreException ex) {
            sb.append("exception: " + ex.getLocalizedMessage());
         }
      }
      page.addKeyValuePair("details", sb.toString());
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
