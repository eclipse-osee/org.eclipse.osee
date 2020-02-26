/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.world;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.osee.ats.api.query.AtsSearchUserType;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.search.widget.ActionableItemSearchWidget;
import org.eclipse.osee.ats.ide.search.widget.ColorTeamSearchWidget;
import org.eclipse.osee.ats.ide.search.widget.InsertionActivitySearchWidget;
import org.eclipse.osee.ats.ide.search.widget.InsertionSearchWidget;
import org.eclipse.osee.ats.ide.search.widget.ProgramSearchWidget;
import org.eclipse.osee.ats.ide.search.widget.ReviewTypeSearchWidget;
import org.eclipse.osee.ats.ide.search.widget.StateNameSearchWidget;
import org.eclipse.osee.ats.ide.search.widget.StateTypeSearchWidget;
import org.eclipse.osee.ats.ide.search.widget.TeamDefinitionSearchWidget;
import org.eclipse.osee.ats.ide.search.widget.TitleSearchWidget;
import org.eclipse.osee.ats.ide.search.widget.UserSearchWidget;
import org.eclipse.osee.ats.ide.search.widget.UserTypeSearchWidget;
import org.eclipse.osee.ats.ide.search.widget.VersionSearchWidget;
import org.eclipse.osee.ats.ide.search.widget.WorkItemTypeSearchWidget;
import org.eclipse.osee.ats.ide.search.widget.WorkPackageSearchWidget;
import org.eclipse.osee.ats.ide.util.widgets.dialog.VersionLabelProvider;
import org.eclipse.osee.ats.ide.world.search.WorldSearchItem;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.IDynamicWidgetLayoutListener;
import org.eclipse.osee.framework.ui.skynet.widgets.util.IXWidgetOptionResolver;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetRendererItem;
import org.eclipse.osee.framework.ui.swt.KeyedImage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public abstract class WorldEditorParameterSearchItem extends WorldSearchItem implements IWorldEditorParameterProvider, IDynamicWidgetLayoutListener, IXWidgetOptionResolver {

   private TableLoadOption[] tableLoadOptions;
   protected final Map<String, XWidget> xWidgets = new HashMap<>();
   protected StringBuilder xmlSb;
   private final Pattern displayName = Pattern.compile("displayName=\"(.*?)\"");
   private String shortName = "";
   private final List<String> widgetOrder = new LinkedList<>();
   private static List<WorkItemType> GOAL_SPRINT_BACKLOG_WORKITEMTYPES =
      Arrays.asList(WorkItemType.AgileBacklog, WorkItemType.AgileSprint, WorkItemType.Goal);
   private static List<WorkItemType> TEAM_DEF_WORKITEMTYPES = null;
   private TitleSearchWidget title;
   private StateTypeSearchWidget stateType;
   private UserSearchWidget user;
   private WorkItemTypeSearchWidget workItemType;
   private TeamDefinitionSearchWidget teamDef;
   private ActionableItemSearchWidget ai;
   private VersionSearchWidget version;
   private StateNameSearchWidget stateName;
   private ProgramSearchWidget program;
   private InsertionSearchWidget insertion;
   private InsertionActivitySearchWidget insertionFeature;
   private WorkPackageSearchWidget workPackage;
   private ColorTeamSearchWidget colorTeam;
   private UserTypeSearchWidget userType;
   private ReviewTypeSearchWidget reviewType;

   public WorldEditorParameterSearchItem(String name, KeyedImage oseeImage) {
      super(name, LoadView.WorldEditor, oseeImage);
   }

   public WorldEditorParameterSearchItem(WorldSearchItem worldSearchItem, KeyedImage oseeImage) {
      super(worldSearchItem, oseeImage);
   }

   @Override
   public String getParameterXWidgetXml() {
      String xml = xmlSb.toString() + "</xWidgets>";
      return xml;
   }

   public Result isParameterSelectionValid() {
      try {
         if (getUserType() != null && getUserType().get() == AtsSearchUserType.Assignee) {
            if (getAi() != null && getAi().get() != null && !getAi().get().isEmpty() && getTeamDef() != null && getTeamDef().get() != null && !getTeamDef().get().isEmpty()) {
               return new Result("Actionable Item(s) and Team Definition(s) are not compatible selections.");
            }
         }
         if (workItemType != null && workItemType.get().isEmpty()) {
            return new Result("You must select a workflow type.");
         }
         if (teamDef != null && teamDef.get() != null && teamDef.get().isEmpty() && ai != null && ai.get().isEmpty() && isTeamDefWorkItemTypesSelected()) {
            return new Result("You must select either Actionable Item(s) or Team Definition(s).");
         }
         return Result.TrueResult;
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
      boolean sel = !Collections.setIntersection(teamDefWorkItemTypes, workItemType.get()).isEmpty();
      return sel;
   }

   @Override
   public void run(WorldEditor worldEditor, SearchType searchType, boolean forcePend) {
      boolean pend = Arrays.asList(tableLoadOptions).contains(TableLoadOption.ForcePend) || forcePend;
      worldEditor.getWorldComposite().getXViewer().setForcePend(pend);
   }

   @Override
   public String[] getWidgetOptions(XWidgetRendererItem widgetData) {
      return null;
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

   private String getDisplayName(String widgetXml) {
      Matcher matcher = displayName.matcher(widgetXml);
      if (matcher.find()) {
         return matcher.group(1);
      }
      throw new OseeArgumentException("WidgetXml must include displayName; Not found in [%s]", widgetXml);
   }

   @Override
   public void widgetCreated(XWidget widget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) {
      xWidgets.put(widget.getLabel(), widget);
      if (widget.getLabel().equals(VersionSearchWidget.VERSION)) {
         getVersion().setup(widget);
         getVersion().setupTeamDef(getTeamDef().getWidget());
      } else if (widget.getLabel().equals(StateNameSearchWidget.STATE_NAME)) {
         getStateName().setup(widget);
      } else if (widget.getLabel().equals(StateTypeSearchWidget.STATE_TYPE)) {
         getStateType().setup(widget);
         getStateType().set(StateType.Working);
      } else if (widget.getLabel().equals(ProgramSearchWidget.PROGRAM)) {
         getProgram().setup(widget);
      } else if (widget.getLabel().equals(InsertionSearchWidget.INSERTION)) {
         getInsertion().setup(widget);
         getInsertion().setProgramWidget(getProgram());
      } else if (widget.getLabel().equals(InsertionActivitySearchWidget.INSERTION_ACTIVITY)) {
         getInsertionActivity().setup(widget);
         getInsertionActivity().setInsertionWidget(getInsertion());
      } else if (widget.getLabel().equals(WorkPackageSearchWidget.WORK_PACKAGE)) {
         getWorkPackage().setup(widget);
         getWorkPackage().setInsertionActivityWidget(getInsertionActivity());
      } else if (widget.getLabel().equals(ColorTeamSearchWidget.COLOR_TEAM)) {
         getColorTeam().setup(widget);
      } else if (widget.getLabel().equals(UserSearchWidget.USER)) {
         getUser().setup(widget);
      } else if (widget.getLabel().equals(UserTypeSearchWidget.USER_TYPE)) {
         getUserType().setup(widget);
      } else if (widget.getLabel().equals(VersionSearchWidget.VERSION)) {
         getVersion().setup(widget);
      } else if (widget.getLabel().equals(ReviewTypeSearchWidget.REVIEW_TYPE)) {
         getReviewType().setup(widget);
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
   public void widgetCreating(XWidget widget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener xModListener, boolean isEditable) {
      if (widget.getLabel().equals(VersionSearchWidget.VERSION)) {
         widget.setLabelProvider(new VersionLabelProvider());
      } else if (widget.getLabel().equals(StateNameSearchWidget.STATE_NAME)) {
         widget.setUseToStringSorter(true);
      } else if (widget.getLabel().equals(ProgramSearchWidget.PROGRAM)) {
         widget.setUseToStringSorter(true);
      } else if (widget.getLabel().equals(InsertionSearchWidget.INSERTION)) {
         widget.setUseToStringSorter(true);
      } else if (widget.getLabel().equals(InsertionActivitySearchWidget.INSERTION_ACTIVITY)) {
         widget.setUseToStringSorter(true);
      } else if (widget.getLabel().equals(WorkPackageSearchWidget.WORK_PACKAGE)) {
         widget.setUseToStringSorter(true);
      } else if (widget.getLabel().equals(ColorTeamSearchWidget.COLOR_TEAM)) {
         widget.setUseToStringSorter(true);
      } else if (widget.getLabel().equals(UserSearchWidget.USER)) {
         widget.setUseToStringSorter(true);
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
      return beginComposite > 0 ? String.format(" beginComposite=\"%d\" ", beginComposite) : "";
   }

   public Map<String, XWidget> getxWidgets() {
      return xWidgets;
   }

   public TitleSearchWidget getTitle() {
      if (title == null) {
         title = new TitleSearchWidget(this);
      }
      return title;
   }

   public StateTypeSearchWidget getStateType() {
      if (stateType == null) {
         stateType = new StateTypeSearchWidget(this);
      }
      return stateType;
   }

   public UserSearchWidget getUser() {
      if (user == null) {
         user = new UserSearchWidget(this);
      }
      return user;
   }

   public boolean isReviewSearch() {
      return false;
   }

   public WorkItemTypeSearchWidget getWorkItemType() {
      if (workItemType == null) {
         workItemType = new WorkItemTypeSearchWidget(this);
         workItemType.setReviewSearch(isReviewSearch());
      }
      return workItemType;
   }

   public TeamDefinitionSearchWidget getTeamDef() {
      if (teamDef == null) {
         teamDef = new TeamDefinitionSearchWidget(this);
      }
      return teamDef;
   }

   public ActionableItemSearchWidget getAi() {
      if (ai == null) {
         ai = new ActionableItemSearchWidget(this);
      }
      return ai;
   }

   public VersionSearchWidget getVersion() {
      if (version == null) {
         version = new VersionSearchWidget(this);
      }
      return version;
   }

   public StateNameSearchWidget getStateName() {
      if (stateName == null) {
         stateName = new StateNameSearchWidget(this);
      }
      return stateName;
   }

   public ProgramSearchWidget getProgram() {
      if (program == null) {
         program = new ProgramSearchWidget(this);
      }
      return program;
   }

   public InsertionSearchWidget getInsertion() {
      if (insertion == null) {
         insertion = new InsertionSearchWidget(this);
      }
      return insertion;
   }

   public InsertionActivitySearchWidget getInsertionActivity() {
      if (insertionFeature == null) {
         insertionFeature = new InsertionActivitySearchWidget(this);
      }
      return insertionFeature;
   }

   public WorkPackageSearchWidget getWorkPackage() {
      if (workPackage == null) {
         workPackage = new WorkPackageSearchWidget(this);
      }
      return workPackage;
   }

   public ColorTeamSearchWidget getColorTeam() {
      if (colorTeam == null) {
         colorTeam = new ColorTeamSearchWidget(this);
      }
      return colorTeam;
   }

   public UserTypeSearchWidget getUserType() {
      if (userType == null) {
         userType = new UserTypeSearchWidget(this);
      }
      return userType;
   }

   public ReviewTypeSearchWidget getReviewType() {
      if (reviewType == null) {
         reviewType = new ReviewTypeSearchWidget(this);
      }
      return reviewType;
   }

   @Override
   public void createToolbar(IToolBarManager toolBarManager) {
      // do nothing
   }

}
