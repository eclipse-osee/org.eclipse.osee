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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;

/**
 * @author Donald G. Dunne
 */
public class AtsSearchWorkflowSearchItem extends WorldEditorParameterSearchItem {

   protected static final AtsImage IMAGE = AtsImage.SEARCH;
   private static final String TITLE = "Action Search";
   protected WorldSearchItem searchItem;
   private long searchId = Lib.generateId();
   private String searchName = "";
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

   protected boolean showWorkPackageWidgets() {
      return false;
   }

   protected void addWidgets() {
      if (showWorkItemWidgets()) {
         getWorkItemType().addWidget(14);
      }
      addBaseWidgets();
   }

   protected void addBaseWidgets() {
      getTitle().addWidget();
      if (isAdvanced()) {
         getAi().addWidget(3);
      }
      getTeamDef().addWidget(0);

      getVersion().addWidget(6);
      getStateType().addWidget();
      getStateName().addWidget(-1);

      getChangeType().addWidget(6);
      getUser().addWidget();
      getUserType().addWidget(-1);

      if (showWorkPackageWidgets()) {
         getProgram().addWidget(8);
         getInsertion().addWidget();
         getInsertionActivity().addWidget();
         getWorkPackage().addWidget();
      }
      getAttrValues().addWidget();
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
      if (getTitle() != null && Strings.isValid(getTitle().get())) {
         data.setTitle(getTitle().get());
      }
      if (data.getStateTypes() != null) {
         data.getStateTypes().clear();
         data.getStateTypes().addAll(getStateType().getTypes());
      }
      if (getUser() != null) {
         AtsUser user = this.getUser().get();
         data.setUserId(user == null ? null : user.getUserId());
      }
      if (getUserType() != null) {
         data.setUserType(getUserType().get());
      }
      if (showWorkItemWidgets() && getWorkItemType() != null) {
         data.getWorkItemTypes().clear();
         data.getWorkItemTypes().addAll(getWorkItemType().get());
      } else if (!showWorkItemWidgets()) {
         data.getWorkItemTypes().clear();
         data.getWorkItemTypes().addAll(getWorkItemTypes());
      }
      if (data.getTeamDefIds() != null) {
         data.getTeamDefIds().clear();
         data.getTeamDefIds().addAll(getTeamDef().getIds());
      }
      if (isAdvanced() && getAi().getIds() != null) {
         data.getAiIds().clear();
         data.getAiIds().addAll(getAi().getIds());
      }

      if (getVersion().get() != null) {
         data.setVersionId(getVersion().get().getId());
      }
      if (getStateName() != null && Strings.isValid(getStateName().get())) {
         data.setState(getStateName().get());
      }
      if (getChangeType() != null && Strings.isValid(getChangeType().get())) {
         data.setChangeType(getChangeType().get());
      }
      if (showWorkPackageWidgets() && getProgram() != null && getProgram().get() != null) {
         data.setProgramId(getProgram().get().getId());
      }
      if (showWorkPackageWidgets() && getInsertion() != null && getInsertion().get() != null) {
         data.setInsertionId(getInsertion().get().getId());
      }
      if (showWorkPackageWidgets() && getInsertionActivity() != null && getInsertionActivity().get() != null) {
         data.setInsertionActivityId(getInsertionActivity().get().getId());
      }
      if (showWorkPackageWidgets() && getWorkPackage() != null && getWorkPackage().get() != null) {
         data.setWorkPackageId(getWorkPackage().get().getId());
      }
      if (data.getWorkItemTypes().isEmpty()) {
         for (WorkItemType type : WorkItemType.values()) {
            data.getWorkItemTypes().add(type);
         }
      }
      data.setNamespace(getNamespace());
      if (getReviewType() != null) {
         data.setReviewType(getReviewType().getType());
      }
      data.setAttrValues(getAttrValues().get());
      return data;
   }

   public void loadWidgets(AtsSearchData data) {
      try {
         searchId = data.getId();
         searchName = data.getSearchName();
         getTitle().set(data);
         getStateType().set(data);

         getChangeType().set(data);
         getUser().set(data);
         getUserType().set(data);

         if (showWorkItemWidgets()) {
            getWorkItemType().clearAll();
            getWorkItemType().set(data);
         }
         getTeamDef().set(data);
         if (isAdvanced()) {
            getAi().set(data);
         }
         getVersion().set(data);
         getStateName().set(data);
         if (showWorkPackageWidgets()) {
            getProgram().set(data);
            getInsertion().set(data);
            getInsertionActivity().set(data);
            getWorkPackage().set(data);
         }
         getReviewType().set(data);
         getAttrValues().set(data);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) {
      return searchItem.performSearchGetResults(false);
   }

   @Override
   public void setupSearch() {
      AtsSearchData data = loadSearchData(new AtsSearchData());
      searchItem = new WorldSearchItem(data);
   }

   public void setRestoreId(long searchId) {
      Conditions.checkExpressionFailOnTrue(searchId <= 0, "searchId must be > 0, not %d", searchId);
      this.searchId = searchId;
   }

   @Override
   public void createParametersSectionCompleted(IManagedForm managedForm, Composite mainComp) {
      if (searchId > 0) {
         AtsSearchData data = AtsApiService.get().getQueryService().getSearch(
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
         return Result.TrueResult;
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
}
