/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.ide.search;

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.query.AtsSearchUtil;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.ats.ide.world.WorldXWidgetActionPage;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;

/**
 * @author Donald G. Dunne
 */
public class AtsSearchWorkflowSearchItem extends WorldEditorParameterSearchItem {

   protected static final AtsImage IMAGE = AtsImage.SEARCH;
   private static final String TITLE = "Action Search";
   protected WorldSearchDataItem searchItem;
   private long searchId = Lib.generateId();
   protected String searchName = "";
   protected AtsSearchData savedData;

   public AtsSearchWorkflowSearchItem() {
      super(TITLE, IMAGE);
      setShortName(TITLE);
   }

   public AtsSearchWorkflowSearchItem(String name, AtsImage image) {
      super(name, image);
      setShortName(name);
   }

   public AtsSearchWorkflowSearchItem(AtsSearchWorkflowSearchItem searchItem) {
      this(searchItem, TITLE, IMAGE);
   }

   public AtsSearchWorkflowSearchItem(AtsSearchWorkflowSearchItem searchItem, String name, AtsImage image) {
      super(searchItem, image);
      setShortName(name);
   }

   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(IMAGE);
   }

   protected boolean showWorkItemWidgets() {
      return true;
   }

   protected void addWidgets() {
      if (showWorkItemWidgets()) {
         getWorkItemTypeWidget().addWidget(14);
      }
      addBaseWidgets();
   }

   protected void addBaseWidgets() {
      getTitleWidget().addWidget();
      if (isAdvanced()) {
         getAiWidget().addWidget(3);
      }
      getTeamDefWidget().addWidget(0);

      getVersionWidget().addWidget(8);
      getStateTypeWidget().addWidget();
      getStateNameWidget().addWidget();
      getHoldStateWidget().addWidget(-1);

      if (includeUserWidgets()) {
         getChangeTypeWidget().addWidget(8);
         getPriorityWidget().addWidget();
         getUserWidget().addWidget();
         getUserTypeWidget().addWidget(-1);
      } else {
         getChangeTypeWidget().addWidget(4);
         getPriorityWidget().addWidget(-1);
      }

      getAttrValuesWidget().addWidget();

   }

   protected boolean includeUserWidgets() {
      return true;
   }

   @Override
   public String getParameterXWidgetXml() {
      addWidgets();
      return super.getParameterXWidgetXml();
   }

   public AtsSearchData loadSearchData(AtsSearchData data) {
      if (searchId > 0) {
         data.setId(searchId);
      }
      data.setSearchName(searchName);
      if (getTitleWidget() != null && Strings.isValid(getTitleWidget().get())) {
         data.setTitle(getTitleWidget().get());
      }
      if (data.getStateTypes() != null) {
         data.getStateTypes().clear();
         data.getStateTypes().addAll(getStateTypeWidget().get());
      }
      if (getUserWidget() != null) {
         AtsUser user = this.getUserWidget().getSingle();
         data.setUserId(user == null ? null : user.getUserId());
      }
      if (getUserTypeWidget() != null) {
         data.setUserType(getUserTypeWidget().getSingle());
      }
      if (showWorkItemWidgets() && getWorkItemTypeWidget() != null) {
         data.getWorkItemTypes().clear();
         data.getWorkItemTypes().addAll(getWorkItemTypeWidget().get());
      } else if (!showWorkItemWidgets()) {
         data.getWorkItemTypes().clear();
         data.getWorkItemTypes().addAll(getWorkItemTypes());
      }
      if (data.getTeamDefIds() != null) {
         data.getTeamDefIds().clear();
         data.getTeamDefIds().addAll(getTeamDefWidget().getIds());
      }
      if (isAdvanced() && getAiWidget().getIds() != null) {
         data.getAiIds().clear();
         data.getAiIds().addAll(getAiWidget().getIds());
      }

      if (getVersionWidget().getSingle() != null) {
         data.setVersionId(getVersionWidget().getSingle().getId());
      }
      if (getStateNameWidget() != null && !getStateNameWidget().get().isEmpty()) {
         data.getStates().addAll(getStateNameWidget().get());
      }
      if (getChangeTypeWidget() != null && !getChangeTypeWidget().get().isEmpty()) {
         data.getChangeTypes().addAll(getChangeTypeWidget().get());
      }
      if (getPriorityWidget() != null && !getPriorityWidget().get().isEmpty()) {
         for (String str : getPriorityWidget().get().split(",")) {
            if (!str.equals(Widgets.NOT_SET)) {
               data.getPriorities().add(str);
            }
         }
      }
      if (getHoldStateWidget() != null && getHoldStateWidget().getSingle() != null) {
         data.setHoldState(getHoldStateWidget().getSingle());
      }
      if (data.getWorkItemTypes().isEmpty()) {
         for (WorkItemType type : WorkItemType.values()) {
            data.getWorkItemTypes().add(type);
         }
      }
      data.setNamespace(getNamespace());
      if (getReviewTypeWidget() != null) {
         data.setReviewType(getReviewTypeWidget().getSingle());
      }
      data.setAttrValues(getAttrValuesWidget().get());
      return data;
   }

   public void loadWidgets(AtsSearchData data) {
      try {
         searchId = data.getId();
         searchName = data.getSearchName();
         getTitleWidget().set(data);
         getStateTypeWidget().set(data);

         getChangeTypeWidget().set(data);
         getPriorityWidget().set(data);
         getUserWidget().set(data);
         getUserTypeWidget().set(data);

         if (showWorkItemWidgets()) {
            getWorkItemTypeWidget().clearAll();
            getWorkItemTypeWidget().set(data);
         }
         getVersionWidget().set(data);
         getStateNameWidget().set(data);
         getHoldStateWidget().set(data);
         getReviewTypeWidget().set(data);
         getAttrValuesWidget().set(data);

         getTeamDefWidget().set(data);
         if (isAdvanced()) {
            getAiWidget().set(data);
         }

         updateAisOrTeamDefs();

      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) {
      return searchItem.performSearchGetResults(false);
   }

   @Override
   public Collection<Artifact> performSearchAsArtifacts(SearchType searchType) {
      return searchItem.performSearchGetResultsAsArtifacts(false);
   }

   @Override
   public void setupSearch() {
      AtsSearchData data = loadSearchData(new AtsSearchData());
      searchItem = new WorldSearchDataItem(data);
   }

   public void setRestoreId(long searchId) {
      Conditions.checkExpressionFailOnTrue(searchId <= 0, "searchId must be > 0, not %d", searchId);
      this.searchId = searchId;
   }

   @Override
   public void createParametersSectionCompleted(IManagedForm managedForm, Composite mainComp) {
      if (searchId > 0) {
         AtsSearchData data = AtsApiService.get().getAtsSearchDataService().getSearch(
            AtsApiService.get().getUserService().getCurrentUser(), searchId);
         if (data != null) {
            loadWidgets(data);
            searchName = data.getSearchName();
         }
      }
   }

   public long getSearchId() {
      return searchId;
   }

   @Override
   public String getShortName() {
      if (Strings.isValid(searchName)) {
         return getShortNamePrefix() + ": " + searchName;
      }
      return super.getShortName();
   }

   @Override
   public String getShortNamePrefix() {
      return "AS";
   }

   @Override
   public AtsSearchWorkflowSearchItem copy() {
      AtsSearchWorkflowSearchItem item = new AtsSearchWorkflowSearchItem(this);
      item.setSavedData(savedData);
      return item;
   }

   @Override
   public AtsSearchWorkflowSearchItem copyProvider() {
      AtsSearchWorkflowSearchItem item = new AtsSearchWorkflowSearchItem(this);
      item.setSavedData(savedData);
      return item;
   }

   @Override
   public void createToolbar(IToolBarManager toolBarManager, WorldEditor worldEditor) {
      toolBarManager.insertBefore(WorldXWidgetActionPage.MENU_GROUP_PRE, new SaveSearchAction(this));
      toolBarManager.insertBefore(WorldXWidgetActionPage.MENU_GROUP_PRE, new SaveAsSearchAction(this));
      toolBarManager.insertBefore(WorldXWidgetActionPage.MENU_GROUP_PRE, new LoadSearchAction(this));
      toolBarManager.insertBefore(WorldXWidgetActionPage.MENU_GROUP_PRE, new DeleteSearchAction(this));
      toolBarManager.insertBefore(WorldXWidgetActionPage.MENU_GROUP_PRE, new ClearSearchAction(this));
      toolBarManager.insertBefore(WorldXWidgetActionPage.MENU_GROUP_PRE, new Separator());
      toolBarManager.insertBefore(WorldXWidgetActionPage.MENU_GROUP_PRE, new ShowSearchItemAction(this));
      toolBarManager.insertBefore(WorldXWidgetActionPage.MENU_GROUP_PRE, new Separator());
   }

   @Override
   public Result isParameterSelectionValid() {
      Result result = super.isParameterSelectionValid();
      if (result.isFalse()) {
         return result;
      }
      try {
         return new Result(true);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return new Result("Exception: " + ex.getLocalizedMessage());
      }
   }

   public String getSearchName() {
      return searchName;
   }

   public void setSearchName(String searchName) {
      this.searchName = searchName;
   }

   public void setSearchId(long searchId) {
      this.searchId = searchId;
   }

   public String getNamespace() {
      return AtsSearchUtil.ATS_QUERY_NAMESPACE;
   }

   public AtsSearchData getSavedData() {
      return savedData;
   }

   public void setSavedData(AtsSearchData savedData) {
      this.savedData = savedData;
   }

   protected boolean isAdvanced() {
      return false;
   }

   protected void addSpaceWidget(WorldEditorParameterSearchItem searchItem, String blankLabel, int beginComposite) {
      String widgetXml = String.format("<XWidget xwidgetType=\"XLabel\" displayName=\"" + blankLabel + "\" %s />",
         searchItem.getBeginComposite(beginComposite));
      searchItem.addWidgetXml(widgetXml);
   }

}
