/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.world;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.config.TeamDefinition;
import org.eclipse.osee.ats.api.query.AtsSearchUserType;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.util.AttributeValue;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.search.widget.ActionableItemSearchWidget;
import org.eclipse.osee.ats.ide.search.widget.AttributeValuesSearchWidget;
import org.eclipse.osee.ats.ide.search.widget.ChangeTypeSearchWidget;
import org.eclipse.osee.ats.ide.search.widget.HoldStateSearchWidget;
import org.eclipse.osee.ats.ide.search.widget.ParamSearchWidget;
import org.eclipse.osee.ats.ide.search.widget.PrioritySearchWidget;
import org.eclipse.osee.ats.ide.search.widget.ReviewTypeSearchWidget;
import org.eclipse.osee.ats.ide.search.widget.StateNameSearchWidget;
import org.eclipse.osee.ats.ide.search.widget.StateTypeSearchWidget;
import org.eclipse.osee.ats.ide.search.widget.TeamDefListener;
import org.eclipse.osee.ats.ide.search.widget.TeamDefinitionSearchWidget;
import org.eclipse.osee.ats.ide.search.widget.TitleSearchWidget;
import org.eclipse.osee.ats.ide.search.widget.UserSearchWidget;
import org.eclipse.osee.ats.ide.search.widget.UserTypeSearchWidget;
import org.eclipse.osee.ats.ide.search.widget.VersionSearchWidget;
import org.eclipse.osee.ats.ide.search.widget.WorkItemTypeSearchWidget;
import org.eclipse.osee.ats.ide.world.search.WorldSearchItem;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.IDynamicWidgetLayoutListener;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public abstract class WorldEditorParameterSearchItem extends WorldSearchItem implements //
   IWorldEditorConsumer, IWorldEditorParameterProvider, IDynamicWidgetLayoutListener {

   private TableLoadOption[] tableLoadOptions;
   protected final Map<String, XWidget> xWidgets = new HashMap<>();
   protected StringBuilder xmlSb;
   private final Pattern displayName = Pattern.compile("displayName=\"(.*?)\"");
   private String shortName = "";
   private final List<String> widgetOrder = new LinkedList<>();
   private static List<WorkItemType> GOAL_SPRINT_BACKLOG_WORKITEMTYPES =
      Arrays.asList(WorkItemType.AgileBacklog, WorkItemType.AgileSprint, WorkItemType.Goal);
   private static List<WorkItemType> TEAM_DEF_WORKITEMTYPES = null;
   private TitleSearchWidget titleWidget;
   private StateTypeSearchWidget stateTypeWidget;
   private UserSearchWidget userWidget;
   private WorkItemTypeSearchWidget workItemTypeWidget;
   private TeamDefinitionSearchWidget teamDefWidget;
   private ActionableItemSearchWidget aiWidget;
   private VersionSearchWidget versionWidget;
   private StateNameSearchWidget stateNameWidget;
   private ChangeTypeSearchWidget changeTypeWidget;
   private PrioritySearchWidget priorityWidget;
   private UserTypeSearchWidget userTypeWidget;
   private ReviewTypeSearchWidget reviewTypeWidget;
   private HoldStateSearchWidget holdStateWidget;
   private AttributeValuesSearchWidget attrValuesWidget;
   protected WorldEditor worldEditor;
   private final Map<String, ParamSearchWidget> labelToParamSearchWidgets = new HashMap<>();

   public WorldEditorParameterSearchItem(String name, AtsImage oseeImage) {
      super(name, LoadView.WorldEditor, oseeImage);
   }

   public WorldEditorParameterSearchItem(WorldSearchItem worldSearchItem, AtsImage oseeImage) {
      super(worldSearchItem, oseeImage);
   }

   @Override
   public String getParameterXWidgetXml() {
      String xml = xmlSb.toString() + "</xWidgets>";
      return xml;
   }

   public Result isParameterSelectionValid() {
      try {
         if (getUserTypeWidget() != null && getUserTypeWidget().getSingle() == AtsSearchUserType.Assignee) {
            if (getAiWidget() != null && getAiWidget().get() != null && !getAiWidget().get().isEmpty() && getTeamDefWidget() != null && getTeamDefWidget().get() != null && !getTeamDefWidget().get().isEmpty()) {
               return new Result("Actionable Item(s) and Team Definition(s) are not compatible selections.");
            }
         }
         if (getWorkItemTypes().isEmpty() && (workItemTypeWidget != null && workItemTypeWidget.get().isEmpty())) {
            return new Result("You must select a workflow type.");
         }
         boolean teamExists = teamDefWidget != null;
         boolean teamSelected = teamDefWidget != null && teamDefWidget.get() != null && !teamDefWidget.get().isEmpty();
         boolean aiExists = aiWidget != null;
         boolean aiSelected = aiWidget != null && aiWidget.get() != null && !aiWidget.get().isEmpty();
         boolean teamDefWorkItemSel = isTeamDefWorkItemTypesSelected();
         if (teamDefWorkItemSel) {
            // Only Team Def exists
            if (teamExists && !aiExists) {
               if (!teamSelected) {
                  return new Result("You must select Team Definition(s).\n");
               }
            }
            // Only AI exists
            if (aiExists && !teamExists) {
               if (!aiSelected) {
                  return new Result("You must select either Actionable Item(s).\n");
               }
            }
            // Both Team Def and AI exist
            if (aiExists && teamExists) {
               if (!aiSelected && !teamSelected) {
                  return new Result("You must select either Actionable Item(s) or Team Definition(s).\n");
               }
            }

         }
         if (userTypeWidget != null) {
            AtsSearchUserType type = userTypeWidget.getSingle();
            AtsUser selUser = userWidget.getSingle();
            if (type != null && type != AtsSearchUserType.None && selUser == null) {
               return new Result("You must select User when User Type is selected.");
            }
            if (selUser != null && (type == null || type == AtsSearchUserType.None)) {
               return new Result("You must select User Type when User is selected.");
            }
         }
         return new Result(true);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return new Result("Exception: " + ex.getLocalizedMessage());
      }
   }

   static {
      TEAM_DEF_WORKITEMTYPES = new LinkedList<>();
      for (WorkItemType type : WorkItemType.values()) {
         if (!GOAL_SPRINT_BACKLOG_WORKITEMTYPES.contains(type)) {
            TEAM_DEF_WORKITEMTYPES.add(type);
         }
      }
   }

   private synchronized List<WorkItemType> getTeamDefWorkItemTypes() {
      return TEAM_DEF_WORKITEMTYPES;
   }

   /**
    * @return true if any of the work item types are related to team defs and ais
    */
   private boolean isTeamDefWorkItemTypesSelected() {
      List<WorkItemType> teamDefWorkItemTypes = getTeamDefWorkItemTypes();
      boolean sel = false;
      if (workItemTypeWidget != null) {
         sel = !Collections.setIntersection(teamDefWorkItemTypes, workItemTypeWidget.get()).isEmpty();
      } else {
         sel = !Collections.setIntersection(teamDefWorkItemTypes, getWorkItemTypeWidget().get()).isEmpty();
      }
      return sel;
   }

   @Override
   public void run(WorldEditor worldEditor, SearchType searchType, PendOp pendOp) {
      boolean pend = Arrays.asList(tableLoadOptions).contains(TableLoadOption.ForcePend) || pendOp.isPend();
      worldEditor.getWorldComposite().getXViewer().setForcePend(pend);
   }

   @Override
   public void setCustomizeData(CustomizeData customizeData) {
      // do nothing
   }

   @Override
   public void setTableLoadOptions(TableLoadOption... tableLoadOptions) {
      this.tableLoadOptions = tableLoadOptions;
   }

   /**
    * Called in the display thread to allow parameters to be retrieved or other setup prior to searching in background
    * thread.
    */
   public void setupSearch() {
      // do nothing
   }

   public void checkOrStartXmlSb() {
      if (xmlSb == null) {
         xmlSb = new StringBuilder("<xWidgets>");
      }
   }

   public void addWidgetXml(String widgetXml) {
      checkOrStartXmlSb();
      xmlSb.append(widgetXml);
      String displayName = getDisplayName(widgetXml);
      xWidgets.put(displayName, null);
      widgetOrder.add(displayName);
   }

   public void addWidgetXml(String widgetXml, ParamSearchWidget paramSrchWidget) {
      addWidgetXml(widgetXml);
      labelToParamSearchWidgets.put(paramSrchWidget.getName(), paramSrchWidget);
   }

   private String getDisplayName(String widgetXml) {
      Matcher matcher = displayName.matcher(widgetXml);
      if (matcher.find()) {
         return matcher.group(1);
      }
      throw new OseeArgumentException("WidgetXml must include displayName; Not found in [%s]", widgetXml);
   }

   @Override
   public void widgetCreated(XWidget widget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer swtXWidgetRenderer,
      XModifiedListener modListener, boolean isEditable) {
      xWidgets.put(widget.getLabel(), widget);
      ParamSearchWidget paramSearchWidget = labelToParamSearchWidgets.get(widget.getLabel());
      if (paramSearchWidget != null) {
         paramSearchWidget.widgetCreated(widget);
      }
   }

   @Override
   public String getSelectedName(SearchType searchType) {
      return Strings.truncate(getShortName(), WorldEditor.TITLE_MAX_LENGTH, true);
   }

   public String getShortName() {
      return shortName;
   }

   public void setShortName(String shortName) {
      this.shortName = shortName;
   }

   public String getShortNamePrefix() {
      return "";
   }

   @Override
   public void widgetCreating(XWidget widget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer swtXWidgetRenderer,
      XModifiedListener xModListener, boolean isEditable) {
      ParamSearchWidget paramSearchWidget = labelToParamSearchWidgets.get(widget.getLabel());
      if (paramSearchWidget != null) {
         paramSearchWidget.widgetCreating(widget);
      }
   }

   public void updateAisOrTeamDefs() {
      for (ParamSearchWidget pWidget : labelToParamSearchWidgets.values()) {
         if (pWidget instanceof TeamDefListener) {
            ((TeamDefListener) pWidget).updateAisOrTeamDefs();
         }
      }
   }

   @Override
   public IDynamicWidgetLayoutListener getDynamicWidgetLayoutListener() {
      return null;
   }

   @Override
   public IAtsVersion getTargetedVersionArtifact() {
      return null;
   }

   /**
    * Available for actions needing to be done after controls are created
    */
   @Override
   public void createParametersSectionCompleted(IManagedForm managedForm, Composite mainComp) {
      // do nothing
   }

   public String getBeginComposite(int beginComposite) {
      if (beginComposite > 0) {
         return String.format(" beginComposite=\"%d\" ", beginComposite);
      } else if (beginComposite < 0) {
         return String.format(" endComposite=\"true\" ");
      }
      return "";
   }

   public Map<String, XWidget> getxWidgets() {
      return xWidgets;
   }

   public TitleSearchWidget getTitleWidget() {
      if (titleWidget == null) {
         titleWidget = new TitleSearchWidget(this);
      }
      return titleWidget;
   }

   public StateTypeSearchWidget getStateTypeWidget() {
      if (stateTypeWidget == null) {
         stateTypeWidget = new StateTypeSearchWidget(this);
      }
      return stateTypeWidget;
   }

   public UserSearchWidget getUserWidget() {
      if (userWidget == null) {
         userWidget = new UserSearchWidget(this);
      }
      return userWidget;
   }

   public boolean isReviewSearch() {
      return false;
   }

   public WorkItemTypeSearchWidget getWorkItemTypeWidget() {
      if (workItemTypeWidget == null) {
         workItemTypeWidget = new WorkItemTypeSearchWidget(this);
         workItemTypeWidget.setReviewSearch(isReviewSearch());
      }
      return workItemTypeWidget;
   }

   /**
    * @return selected Team Definitions or computed Team Definitions from selected AIs
    */
   public Collection<TeamDefinition> getTeamDefs() {
      Collection<TeamDefinition> teamDefs = new HashSet<>();
      if (teamDefWidget != null) {
         teamDefs.addAll(teamDefWidget.get());
      }
      if (teamDefs.isEmpty() && aiWidget != null) {
         for (IAtsActionableItem ai : aiWidget.get()) {
            IAtsTeamDefinition teamDef = atsApi.getActionableItemService().getTeamDefinitionInherited(ai);
            if (teamDef != null) {
               teamDefs.add(atsApi.getConfigService().getConfigurations().getTeamDef(teamDef));
            }
         }
      }
      return teamDefs;
   }

   public TeamDefinitionSearchWidget getTeamDefWidget() {
      if (teamDefWidget == null) {
         teamDefWidget = new TeamDefinitionSearchWidget(this);
      }
      return teamDefWidget;
   }

   public ActionableItemSearchWidget getAiWidget() {
      if (aiWidget == null) {
         aiWidget = new ActionableItemSearchWidget(this);
      }
      return aiWidget;
   }

   public VersionSearchWidget getVersionWidget() {
      if (versionWidget == null) {
         versionWidget = new VersionSearchWidget(this);
      }
      return versionWidget;
   }

   public StateNameSearchWidget getStateNameWidget() {
      if (stateNameWidget == null) {
         stateNameWidget = new StateNameSearchWidget(this);
      }
      return stateNameWidget;
   }

   public HoldStateSearchWidget getHoldStateWidget() {
      if (holdStateWidget == null) {
         holdStateWidget = new HoldStateSearchWidget(this);
      }
      return holdStateWidget;
   }

   public ChangeTypeSearchWidget getChangeTypeWidget() {
      if (changeTypeWidget == null) {
         changeTypeWidget = new ChangeTypeSearchWidget(this);
      }
      return changeTypeWidget;
   }

   public PrioritySearchWidget getPriorityWidget() {
      if (priorityWidget == null) {
         priorityWidget = new PrioritySearchWidget(this);
      }
      return priorityWidget;
   }

   public UserTypeSearchWidget getUserTypeWidget() {
      if (userTypeWidget == null) {
         userTypeWidget = new UserTypeSearchWidget(this);
      }
      return userTypeWidget;
   }

   public ReviewTypeSearchWidget getReviewTypeWidget() {
      if (reviewTypeWidget == null) {
         reviewTypeWidget = new ReviewTypeSearchWidget(this);
      }
      return reviewTypeWidget;
   }

   public AttributeValuesSearchWidget getAttrValuesWidget() {
      if (attrValuesWidget == null) {
         attrValuesWidget = new AttributeValuesSearchWidget(this);
      }
      return attrValuesWidget;
   }

   @Override
   public void createToolbar(IToolBarManager toolBarManager, WorldEditor worldEditor) {
      // do nothing
   }

   public Collection<WorkItemType> getWorkItemTypes() {
      return java.util.Collections.emptyList();
   }

   @Override
   public WorldEditor getWorldEditor() {
      return worldEditor;
   }

   @Override
   public void setWorldEditor(WorldEditor worldEditor) {
      this.worldEditor = worldEditor;
   }

   protected void reportWidgetSelections(XResultData rd) {
      rd.logf("Search Parameters: \n---------------------------------------------\n\n");
      rd.logf("Title: [%s]\n", getTitleWidget().getWidget().get());
      rd.logf("Team(s): [%s]\n", teamDefWidget.getWidget().getSelectedTeamDefintions());
      Object ver = versionWidget.getWidget().getSelected();
      rd.logf("Version: [%s]\n", ver == null || "".equals("") ? "" : versionWidget.getWidget().getSelected());
      if (getStateTypeWidget() != null && !getStateTypeWidget().get().isEmpty()) {
         rd.logf("State Type: %s\n", getStateTypeWidget().get());
      }
      if (getStateNameWidget() != null && getStateNameWidget().get().size() > 0) {
         rd.logf("State Name: [%s]\n", getStateNameWidget().get());
      }
      rd.logf("Change Type: %s\n", getChangeTypeWidget().get() == null ? "" : getChangeTypeWidget().get());
      String priority = getPriorityWidget().get();
      rd.logf("Priority: [%s]\n", (Strings.isValid(priority) && !Widgets.NOT_SET.equals(priority)) ? priority : "");
      rd.logf("Hold State: [%s]\n",
         getHoldStateWidget().getSingle() == null ? "" : getHoldStateWidget().getSingle().name());
      if (getAttrValuesWidget().get().isEmpty()) {
         rd.logf("Attribute Value(s): []\n");
      } else {
         for (AttributeValue attrVal : getAttrValuesWidget().get().getAttributes()) {
            rd.logf("Attribute Value(s): Type: [%s] Value(s): [%s]\n", attrVal.getAttrType().getName(),
               Collections.toString(", ", attrVal.getValues()));
         }
      }
   }

   // For sub-class implementation
   public List<Artifact> performPostSearchFilter(List<Artifact> artifacts) {
      return artifacts;
   }

   public Collection<? extends SearchEngine> getSearchEngines() {
      return java.util.Collections.emptyList();
   }

}
