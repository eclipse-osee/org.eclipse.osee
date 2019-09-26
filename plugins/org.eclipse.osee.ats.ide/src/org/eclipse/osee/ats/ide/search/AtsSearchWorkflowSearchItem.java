/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.search;

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.query.AtsSearchUtil;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.ats.ide.world.WorldXWidgetActionPage;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;

/**
 * @author Donald G. Dunne
 */
public class AtsSearchWorkflowSearchItem extends WorldEditorParameterSearchItem {

   private static final AtsImage IMAGE = AtsImage.SEARCH;
   private static final String TITLE = "Action Search";
   protected WorldSearchItem searchItem;
   private long searchId = Lib.generateId();
   private String searchName = "";
   protected AtsSearchData savedData;

   public AtsSearchWorkflowSearchItem() {
      super(TITLE, IMAGE);
      setShortName(TITLE);
   }

   public AtsSearchWorkflowSearchItem(String name, KeyedImage image) {
      super(name, image);
      setShortName(name);
   }

   public AtsSearchWorkflowSearchItem(AtsSearchWorkflowSearchItem searchItem) {
      this(searchItem, TITLE, IMAGE);
   }

   public AtsSearchWorkflowSearchItem(AtsSearchWorkflowSearchItem searchItem, String name, KeyedImage image) {
      super(searchItem, image);
      setShortName(name);
   }

   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(getOseeImage());
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
      getAi().addWidget(3);
      getTeamDef().addWidget(2);
      getVersion().addWidget(6);
      getStateType().addWidget();
      getStateName().addWidget();
      getUser().addWidget(6);
      getUserType().addWidget();
      if (showWorkPackageWidgets()) {
         getProgram().addWidget(8);
         getInsertion().addWidget();
         getInsertionActivity().addWidget();
         getWorkPackage().addWidget();
      }
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
         IAtsUser user = this.getUser().get();
         data.setUserId(user == null ? null : user.getUserId());
      }
      if (getUserType() != null) {
         data.setUserType(getUserType().get());
      }
      if (showWorkItemWidgets() && getWorkItemType() != null) {
         data.getWorkItemTypes().clear();
         data.getWorkItemTypes().addAll(getWorkItemType().get());
      }
      if (data.getTeamDefIds() != null) {
         data.getTeamDefIds().clear();
         data.getTeamDefIds().addAll(getTeamDef().getIds());
      }
      if (getAi().getIds() != null) {
         data.getAiIds().clear();
         data.getAiIds().addAll(getAi().getIds());
      }

      if (getVersion().get() != null) {
         data.setVersionId(getVersion().get().getId());
      }
      if (getStateName() != null && Strings.isValid(getStateName().get())) {
         data.setState(getStateName().get());
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
      if (getColorTeam() != null && Strings.isValid(getColorTeam().get())) {
         data.setColorTeam(getColorTeam().get());
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
      return data;
   }

   public void loadWidgets(AtsSearchData data) {
      try {
         searchId = data.getId();
         searchName = data.getSearchName();
         getTitle().set(data);
         getStateType().set(data);
         getUser().set(data);
         getUserType().set(data);
         if (showWorkItemWidgets()) {
            getWorkItemType().clearAll();
            getWorkItemType().set(data);
         }
         getTeamDef().set(data);
         getAi().set(data);
         getVersion().set(data);
         getStateName().set(data);
         if (showWorkPackageWidgets()) {
            getProgram().set(data);
            getInsertion().set(data);
            getInsertionActivity().set(data);
            getWorkPackage().set(data);
         }
         getColorTeam().set(data);
         getReviewType().set(data);
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
         AtsSearchData data = AtsClientService.get().getQueryService().getSearch(
            AtsClientService.get().getUserService().getCurrentUser(), searchId);
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
   public void createToolbar(IToolBarManager toolBarManager) {
      toolBarManager.insertBefore(WorldXWidgetActionPage.MENU_GROUP_PRE, new SaveSearchAction(this));
      toolBarManager.insertBefore(WorldXWidgetActionPage.MENU_GROUP_PRE, new SaveAsSearchAction(this));
      toolBarManager.insertBefore(WorldXWidgetActionPage.MENU_GROUP_PRE, new LoadSearchAction(this));
      toolBarManager.insertBefore(WorldXWidgetActionPage.MENU_GROUP_PRE, new DeleteSearchAction(this));
      toolBarManager.insertBefore(WorldXWidgetActionPage.MENU_GROUP_PRE, new ClearSearchAction(this));
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

}
