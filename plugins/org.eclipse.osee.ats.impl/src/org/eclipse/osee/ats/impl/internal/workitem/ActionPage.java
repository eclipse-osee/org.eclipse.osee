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
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workdef.IAtsCompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.IAtsLayoutItem;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.ats.impl.action.ActionLoadLevel;
import org.eclipse.osee.ats.impl.resource.AtsResourceTokens;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
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

   private String pageTemplate;
   private final ActionLoadLevel actionLoadLevel;
   private PageCreator page;
   private IAtsWorkItem workItem;
   private final IResourceRegistry registry;
   private final String title;
   private final ArtifactReadable action;
   private final IAtsServer atsServer;
   private final Log logger;

   public ActionPage(Log logger, IAtsServer atsServer, IResourceRegistry registry, ArtifactReadable action, String title, ActionLoadLevel actionLoadLevel) {
      this.logger = logger;
      this.atsServer = atsServer;
      this.registry = registry;
      this.action = action;
      this.title = title;
      this.actionLoadLevel = actionLoadLevel;
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
      page.addKeyValuePair("createdDate", workItem.getWorkData().getCreatedDate().toString());
      page.addKeyValuePair("version", getVersion(workItem));
      page.addKeyValuePair("workDef", getWorkDefStr(workItem));
      page.addKeyValuePair("guid", workItem.getGuid());
      if (page.getValue("transition") == null) {
         page.addKeyValuePair("transition", "");
      }

      addStates(page, workItem, action);
      addDebug(page, workItem, action);

      return page.realizePage(AtsResourceTokens.AtsActionHtml);
   }

   private String getWorkDefStr(IAtsWorkItem workItem) {
      String results = "<only on full>";
      if (isShowHeaderFull()) {
         results = workItem.getWorkDefinition().getName();
      }
      return results;
   }

   private String getAssigneesStr(IAtsWorkItem workItem, ArtifactReadable action) {
      String results = "";
      if (isShowHeaderFull()) {
         results = workItem.getStateMgr().getAssigneesStr();
      } else {
         String currState = action.getSoleAttributeAsString(AtsAttributeTypes.CurrentState);
         String assignees = currState.split(";")[1];
         assignees = assignees.replaceAll("><", "; ");
         assignees = assignees.replaceAll(">", "");
         assignees = assignees.replaceAll("<", "");
         results = assignees;
      }
      return results;
   }

   private String getTeamStr(ArtifactReadable action) {
      String results = action.getSoleAttributeAsString(AtsAttributeTypes.TeamDefinition);
      if (isShowHeaderFull()) {
         results = atsServer.getArtifactByGuid(results).getName();
      }
      return results;
   }

   private String getAIStr(ArtifactReadable action) {
      String results = action.getSoleAttributeAsString(AtsAttributeTypes.ActionableItem);
      if (isShowHeaderFull()) {
         results = atsServer.getArtifactByGuid(results).getName();
      }
      return results;
   }

   private String getCreatedByStr(IAtsWorkItem workItem, ArtifactReadable action) {
      String results = action.getSoleAttributeAsString(AtsAttributeTypes.CreatedBy);
      if (isShowHeaderFull()) {
         results = workItem.getWorkData().getCreatedBy().getName();
      }
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
      if (isShowHeaderFull()) {
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
      }
      return version;
   }

   private void addStates(PageCreator page, IAtsWorkItem workItem, ArtifactReadable action) throws Exception {
      StringBuilder statesSb = new StringBuilder();
      if (isShowStates()) {
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
      }
      page.addKeyValuePair("states", statesSb.toString());
   }

   private boolean isShowStates() {
      return (actionLoadLevel == ActionLoadLevel.STATE);
   }

   private boolean isShowHeaderFull() {
      return (actionLoadLevel == ActionLoadLevel.HEADER_FULL);
   }

   private void addWidgets(StringBuilder sb, IAtsWorkItem workItem, Collection<IAtsLayoutItem> items) {
      addLayoutItems(sb, workItem, items);
   }

   private void addLayoutItems(StringBuilder sb, IAtsWorkItem workItem, Collection<IAtsLayoutItem> items) {
      for (IAtsLayoutItem layout : items) {
         if (layout instanceof IAtsCompositeLayoutItem) {
            addWidgets(sb, workItem, ((IAtsCompositeLayoutItem) layout).getaLayoutItems());
         } else {
            addWidget(sb, workItem, (IAtsWidgetDefinition) layout);
         }
      }
   }

   private void addWidget(StringBuilder sb, IAtsWorkItem workItem, IAtsWidgetDefinition layout) {
      sb.append("<tr><td>");
      sb.append(layout.getName());
      sb.append(": <b>");
      try {
         IAttributeType attributeType = atsServer.getAttributeResolver().getAttributeType(layout.getAtrributeName());
         Collection<String> attributesToStringList =
            atsServer.getAttributeResolver().getAttributesToStringList(workItem, attributeType);
         if (attributesToStringList.size() > 1) {
            sb.append(attributesToStringList.toString());
         } else if (attributesToStringList.size() == 1) {
            sb.append(attributesToStringList.iterator().next().toString());
         }
      } catch (OseeCoreException ex) {
         sb.append("exception: " + ex.getLocalizedMessage());
      }
      sb.append("</b></td></tr>");
   }

   private String getStateHtmlTemplate() throws IOException {
      if (pageTemplate == null) {
         pageTemplate = Lib.inputStreamToString(AtsResourceTokens.class.getResource("html/atsState.html").openStream());
      }
      return pageTemplate;
   }

   private static void addDebug(PageCreator page, IAtsWorkItem workItem, ArtifactReadable artifact) {
      StringBuilder sb = new StringBuilder();
      try {
         for (AttributeReadable<?> attr : artifact.getAttributes()) {
            sb.append(String.format("%s [%s]\n", attr.getAttributeType(),
               AHTML.textToHtml(String.valueOf(attr.getValue()))));
         }
      } catch (OseeCoreException ex) {
         sb.append("exception: " + ex.getLocalizedMessage());
      }
      page.addKeyValuePair("debug", sb.toString());
   }

}
