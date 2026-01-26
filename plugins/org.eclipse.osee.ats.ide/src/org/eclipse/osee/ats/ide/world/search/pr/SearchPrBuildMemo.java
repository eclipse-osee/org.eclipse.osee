/*******************************************************************************
 * Copyright (c) 2025 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.ats.ide.world.search.pr;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.api.query.AtsSearchUtil;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.ide.search.WorldSearchDataItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;

public class SearchPrBuildMemo extends AtsSearchPrWorkflowSearchItem {

   private static final String TITLE = "Generate Problem Report (PR) Build Memo";
   public static final String PR_NAMESPACE = AtsSearchUtil.ATS_QUERY_PR_WF_NAMESPACE;

   public SearchPrBuildMemo() {
      super(TITLE, PR_NAMESPACE, AtsImage.PROBLEM_REPORT);
   }

   public SearchPrBuildMemo(SearchPrBuildMemo searchItem) {
      super(searchItem, TITLE, PR_NAMESPACE, AtsImage.PROBLEM_REPORT);
   }

   @Override
   public void createParametersSectionCompleted(IManagedForm managedForm, Composite mainComp) {
      savedData = new AtsSearchData(TITLE);
      savedData.setWorkItemTypes(Arrays.asList(WorkItemType.TeamWorkflow));
      savedData.setStateTypes(Arrays.asList(StateType.Working));
      savedData.setTeamDefIds(Arrays.asList(DemoArtifactToken.SAW_PL_PR_TeamDef.getId()));
      searchName = getName();
      loadWidgets(savedData);
      searchItem = new WorldSearchDataItem(savedData);
      performSearch(SearchType.Search);
   }

   @Override
   public SearchPrBuildMemo copy() {
      SearchPrBuildMemo item = new SearchPrBuildMemo(this);
      item.setSavedData(savedData);
      return item;
   }

   @Override
   public SearchPrBuildMemo copyProvider() {
      SearchPrBuildMemo item = new SearchPrBuildMemo(this);
      item.setSavedData(savedData);
      return item;
   }

   @Override
   public Collection<WorkItemType> getWorkItemTypes() {
      return Arrays.asList(WorkItemType.TeamWorkflow);
   }

   @Override
   protected boolean showWorkItemWidgets() {
      return false;
   }
}
